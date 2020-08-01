package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
    private final int SERVICE_MSG_FREQ = 110_000;
    private final int HIST_MSG_CNT     = 100;

    @FXML
    TextArea textArea;

    @FXML
    TextField msgField, loginField, infoField;

    @FXML
    HBox msgPanel, authPanel, infoPanel;

    @FXML
    PasswordField passField;

    @FXML
    ListView<String> clientsList;

    private Network network;
    private String  nickname;
    private String  login;

    private Thread fSendingServiceMsgThread;

    private File fHistoryFile;

    public void setAuthenticated(boolean authenticated, Boolean aIsNeedNewAuth)
    {
        if (!aIsNeedNewAuth) return;

        infoPanel.setVisible(false);
        infoPanel.setManaged(false);

        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);

        msgPanel.setVisible(authenticated);
        msgPanel.setManaged(authenticated);

        clientsList.setVisible(authenticated);
        clientsList.setManaged(authenticated);

        if (!authenticated)
        {
            nickname = "";
        }
    }

    public void setConnectionClosed()
    {
        infoField.setText("Время на авторизацию истекло. Соединение прервано.");
        infoField.setStyle("-fx-text-inner-color: red;");
        infoField.setEditable(false);

        infoPanel.setVisible(true);
        infoPanel.setManaged(true);

        authPanel.setVisible(false);
        authPanel.setManaged(false);

        msgPanel.setVisible(false);
        msgPanel.setManaged(false);

        clientsList.setVisible(false);
        clientsList.setManaged(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createServiceThread();
        setAuthenticated(false, true);
        clientsList.setOnMouseClicked(this::clientClickHandler);
        createNetwork();
        network.connect();
    }

    public void sendAuth() {
        network.sendAuth(loginField.getText(), passField.getText());
        login = loginField.getText();
        loginField.clear();
        passField.clear();
    }

    public void sendMsg() {
        if (network.sendMsg(msgField.getText())) {
            msgField.clear();
            msgField.requestFocus();
        }
    }

    public void sendExit() {
        network.sendMsg("/end");
    }

    public void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
            alert.showAndWait();
        });
    }

    public void closeClient()
    {
        Stage stage = (Stage) textArea.getScene().getWindow();
        stage.close();
    }

    public void createNetwork() {
        network = new Network();
        network.setCallOnException(args -> showAlert(args[0].toString()));

        network.setCallOnCloseConnection(args ->
                {
                    setAuthenticated(false, (Boolean) args[0]);
                    fSendingServiceMsgThread.interrupt();
                });

        network.setCallOnAuthenticated(args ->
                {
                    setAuthenticated(true, true);
                    nickname = args[0].toString();
                    fSendingServiceMsgThread.start();

                    fHistoryFile = new File("history_" + login + ".txt");
                    getMsgHistory();
                });

        network.setCallOnMsgReceived(args -> {
            String msg = args[0].toString();
            if (msg.startsWith("/clients "))
            {
                String[] tokens = msg.split("\\s");
                Platform.runLater(() ->
                {
                    clientsList.getItems().clear();
                    for (int i = 1; i < tokens.length; i++)
                    {
                        if (!nickname.equals(tokens[i]))
                        {
                            clientsList.getItems().add(tokens[i]);
                        }
                    }
                });
            }
            else if (msg.startsWith("/exception "))
            {
                showAlert(msg.substring(11));
            }
            else if (msg.startsWith("/chnickok "))
            {
                String[] tokens = msg.split("\\s");
                String vOldNick = tokens[1];
                String vNewNick = tokens[2];
                String vMsgText = msg.substring(10 + vOldNick.length() + 1 + vNewNick.length() + 1) + "\n";

                if (nickname.equals(vOldNick)) nickname = vNewNick;

                addMessage(vMsgText);
            }
            else
            {
                addMessage(msg + "\n");
            }
        });

        network.setCallOnTimeout(args -> setConnectionClosed());
    }

    private void getMsgHistory()
    {
        try(BufferedReader vFileReader = new BufferedReader(new FileReader(fHistoryFile)))
        {
            // file -> ArrayList
            String vStr;
            ArrayList<String> vStrList = new ArrayList<>();
            while ((vStr = vFileReader.readLine()) != null)
            {
                vStrList.add(vStr + "\n");
            }

            // ArrayList -> textArea
            int idxStart = vStrList.size() - HIST_MSG_CNT;
            for (int i = Math.max(idxStart, 0); i < vStrList.size(); i++)
            {
                textArea.appendText(vStrList.get(i));
            }
        }
        catch (IOException e)
        {
            System.out.println("Ошибка чтения из файла");
            e.printStackTrace();
        }
    }

    private void addMessage(String aMsg)
    {
        textArea.appendText(aMsg);

        try (BufferedWriter vFileWriter = new BufferedWriter(new FileWriter(fHistoryFile, true)))
        {
            vFileWriter.write(aMsg);
        }
        catch (IOException e)
        {
            System.out.println("Ошибка записи в файл");
            e.printStackTrace();
        }
    }

    private void createServiceThread()
    {
        fSendingServiceMsgThread = new Thread(() ->
        {
            try
            {
                while (!Thread.currentThread().isInterrupted())
                {
                    System.out.println("/timeoutavoid");
                    network.sendMsg("/timeoutavoid");
                    Thread.sleep(SERVICE_MSG_FREQ);
                }
            }
            catch (InterruptedException e)
            {
                System.out.println("Поток был остановлен.");
            }

    });
        fSendingServiceMsgThread.setName("Thread_Qwerty1");
        fSendingServiceMsgThread.setDaemon(true);
    }

    private void clientClickHandler(MouseEvent event) {
        if (event.getClickCount() == 2) {
            String nickname = clientsList.getSelectionModel().getSelectedItem();
            msgField.setText("/w " + nickname + " ");
            msgField.requestFocus();
            msgField.selectEnd();
        }
    }
}