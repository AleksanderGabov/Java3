package client;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Network implements Closeable {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private Boolean fIsNeedNewAuth = true;

    private Callback callOnMsgReceived;
    private Callback callOnAuthenticated;
    private Callback callOnException;
    private Callback callOnCloseConnection;
    private Callback callOnTimeout;

    public void setCallOnMsgReceived(Callback callOnMsgReceived) {
        this.callOnMsgReceived = callOnMsgReceived;
    }

    public void setCallOnAuthenticated(Callback callOnAuthenticated) {
        this.callOnAuthenticated = callOnAuthenticated;
    }

    public void setCallOnException(Callback callOnException) {
        this.callOnException = callOnException;
    }

    public void setCallOnCloseConnection(Callback callOnCloseConnection) {
        this.callOnCloseConnection = callOnCloseConnection;
    }

    public void setCallOnTimeout(Callback callOnTimeout)
    {
        this.callOnTimeout = callOnTimeout;
    }

    public void sendAuth(String login, String password) {
        try {
            connect();
            out.writeUTF("/auth " + login + " " + password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        if (socket != null && !socket.isClosed()) {
            return;
        }

        try {
            socket = new Socket("localhost", 8190);
            in     = new DataInputStream (socket.getInputStream ());
            out    = new DataOutputStream(socket.getOutputStream());
            Thread clientListenerThread = new Thread(() -> {
                try {
                    // Ожидание авторизации
                    while (true)
                    {
                        String msg = in.readUTF();

                        // Проверка входящего сообщения на успешную авторизацию
                        if (msg.startsWith("/authok "))
                        {
                            callOnAuthenticated.callback(msg.split("\\s")[1]);
                            break;
                        }

                        // Проверка входящего сообщения на превышение таймаута
                        if (msg.startsWith("/timeout"))
                        {
                            fIsNeedNewAuth = false;
                            throw new TTimeoutException();
                        }
                        callOnException.callback(msg);
                    }

                    // Ожидание входящих сообщений
                    while (true)
                    {
                        String msg = in.readUTF();
                        if (msg.equals("/end")) break;
                        callOnMsgReceived.callback(msg);
                    }
                }
                catch (IOException e)
                {
                    callOnException.callback("Соединение с сервером разорвано");
                }
                catch (TTimeoutException e)
                {
                    callOnTimeout.callback();
                }
                finally
                {
                    close();
                }
            });
            clientListenerThread.setDaemon(true);
            clientListenerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean sendMsg(String msg) {
        if (out == null) {
            callOnException.callback("Соединение с сервером не установлено");
        }

        try {
            out.writeUTF(msg);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void close() {
        callOnCloseConnection.callback(fIsNeedNewAuth);
        close(in, out, socket);
    }

    private void close(Closeable... objects) {
        for (Closeable o : objects) {
            try {
                o.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class TTimeoutException extends Exception {}
}

