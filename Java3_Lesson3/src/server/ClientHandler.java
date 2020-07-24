package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientHandler {
    private MyServer         myServer;
    private Socket           socket;
    private DataInputStream  in;
    private DataOutputStream out;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String aName)
    {
        this.name = aName;
    }

    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.myServer = myServer;
            this.socket   = socket;
            this.in       = new DataInputStream (socket.getInputStream ());
            this.out      = new DataOutputStream(socket.getOutputStream());
            this.name     = "";
            new Thread(() -> {
                try {
                    authentication();
                    readMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Проблемы при создании обработчика клиента");
        }
    }

    public void authentication() throws IOException
    {
        while (true)
        {
            try
            {
                String str = in.readUTF();
                if (str.startsWith("/auth"))
                {
                    String[] parts = str.split("\\s");
                    String nick = myServer.getAuthService().getNickByLoginPass(parts[1], parts[2]);
                    if (nick != null)
                    {
                        if (!myServer.isNickBusy(nick))
                        {
                            sendMsg("/authok " + nick);
                            name = nick;
                            myServer.broadcastMsg(name + " зашел в чат");
                            myServer.subscribe(this);
                            return;
                        }
                        else
                        {
                            sendMsg("Учетная запись уже используется");
                        }
                    }
                    else
                    {
                        sendMsg("Неверные логин/пароль");
                    }
                }
            }
            catch (SocketTimeoutException e)
            {
                System.out.println("Превышен таймаут на авторизацию.");
                sendMsg("/timeout");
            }
        }
    }

    public void readMessages() throws IOException {
        while (true) {
            String strFromClient = in.readUTF();
            System.out.println("от " + name + ": " + strFromClient);

            String vPrefix = strFromClient.split("\\s")[0];

            switch (vPrefix)
            {
                case "/end"         : return;   // Выход из чата
                case "/timeoutavoid": continue; // Избежание отключения по таймауту
                case "/chnick"      :           // Изменение ника
                    {
                        String vNewNick = strFromClient.split("\\s")[1];
                        if (myServer.changeNickname(name, vNewNick)) name = vNewNick;
                    } break;
                case "/w"           :           // Отправка сообщения конкретному клиенту
                    {
                        String[] tokens = strFromClient.split("\\s");
                        String nick = tokens[1];
                        String msg = strFromClient.substring(4 + nick.length());
                        myServer.sendMsgToClient(name, nick, msg);
                    } break;
                default: myServer.broadcastMsg(name, strFromClient);
            }
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        myServer.unsubscribe(this);
        myServer.broadcastMsg(name + " вышел из чата");
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
