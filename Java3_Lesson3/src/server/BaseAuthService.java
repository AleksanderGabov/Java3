package server;

import java.sql.*;

public class BaseAuthService implements AuthService {
    private Connection fDbConnection;

    @Override
    public void start()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            fDbConnection = DriverManager.getConnection("jdbc:sqlite:Java3_Lesson3.db");

            System.out.println("Сервис аутентификации запущен");
            System.out.flush();
        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void stop()
    {
        try
        {
            fDbConnection.close();
            System.out.println("Сервис аутентификации остановлен");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String getNickByLoginPass(String login, String pass)
    {
        Statement vStmnt = null;
        try
        {
            vStmnt = fDbConnection.createStatement();
            ResultSet vRs = vStmnt.executeQuery(String.format(
                      "select tu.id, tu.nm, tu.nickname "
                    + "  from t_user tu "
                    + " where tu.login = '%s' "
                    + "   and tu.pass  = '%s' "
                    , login
                    , pass));

            if (vRs.next())
            {
                return vRs.getString("nickname");
            }
            else
            {
                return null;
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            try
            {
                assert vStmnt != null;
                vStmnt.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
}
