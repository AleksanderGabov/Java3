package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ScatteringByteChannel;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MyServer {
    private final int PORT         = 8191;
    private final int TIMEOUT_AUTH = 120_000;

    private Map<String, ClientHandler> clients;
    private AuthService authService;
    private TDbConnection fDbConnection;

    public AuthService getAuthService() {
        return authService;
    }

    public MyServer() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            authService = new BaseAuthService();
            authService.start();
            clients = new HashMap<>();

            fDbConnection = new TDbConnection();

            while (true) {
                System.out.println("Сервер ожидает подключения");
                Socket socket = server.accept();
                socket.setSoTimeout(TIMEOUT_AUTH);
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
            }
        }
        catch (IOException e)
        {
            System.out.println("Ошибка в работе сервера");
            e.printStackTrace();
        }
        finally
        {
            if (authService   != null) authService.stop();
            if (fDbConnection != null) fDbConnection.closeConnection();
        }
    }

    public synchronized boolean isNickBusy(String nick) {
        return clients.containsKey(nick);
    }

    public synchronized void broadcastMsg(String msg) {
        for (ClientHandler o : clients.values()) {
            o.sendMsg(msg);
        }
    }

    public synchronized void broadcastMsg(String from, String msg) {
        broadcastMsg(formatMessage(from, msg));
    }

    public synchronized void sendMsgToClient(String from, String to, String msg)
    {
        if (clients.containsKey(to)) {
            clients.get(to).sendMsg(formatMessage(from, msg));
        } else {
            clients.get(from).sendMsg(String.format("/exception Client '%s' does not exist", to));
        }
    }

    public synchronized boolean changeNickname(String aOldNick, String aNewNick)
    {
        try
        {
            fDbConnection.executeSelect(String.format("select count(1) as cnt from t_user tu where tu.nickname = '%s'", aNewNick));
            if (fDbConnection.getResultSet().getInt("cnt") == 0)
            {
                if (fDbConnection.executeUpdate(String.format(
                                  "update t_user "
                                + "   set nickname = '%s' "
                                + " where nickname = '%s' "
                        , aNewNick
                        , aOldNick)) > 0 )
                {
                    broadcastMsg(String.format("/chnickok %s %s Server: Ник \"%s\" изменен на \"%s\""
                            , aOldNick
                            , aNewNick
                            , aOldNick
                            , aNewNick));

                    // Обновление ника в списке клиентов
                    ClientHandler vClient = clients.get(aOldNick);
                    vClient.setName(aNewNick);
                    clients.put(aNewNick, vClient);
                    clients.remove(aOldNick);
                    broadcastClients();

                    return true;
                }
                else
                {
                    sendMsgToClient("Server", aOldNick, "При попытке изменения ника возникла ошибка");
                    return false;
                }
            }
            else
            {
                sendMsgToClient("Server", aOldNick, String.format("Ник \"%s\" уже существует", aNewNick));
                return false;
            }
        }
        catch (SQLException e)
        {
            System.out.println("Ошибка получения данных из ResultSet");
            e.printStackTrace();
            return false;
        }
    }

    public synchronized void unsubscribe(ClientHandler o) {
        clients.remove(o.getName());
        broadcastClients();
    }

    public synchronized void subscribe(ClientHandler o) {
        clients.put(o.getName(), o);
        broadcastClients();
    }

    private String formatMessage(String from, String msg) {
        return from + ": " + msg;
    }

    public synchronized void broadcastClients() {
        StringBuilder builder = new StringBuilder("/clients ");
        for (String nick : clients.keySet()) {
            builder.append(nick).append(' ');
        }
        broadcastMsg(builder.toString());
    }
}
