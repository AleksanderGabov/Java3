package server;

import java.sql.*;

public class TDbConnection
{
    private Connection fDbConnection;
    private Statement  fStatement;
    private ResultSet  fResultSet;

    public TDbConnection()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            fDbConnection = DriverManager.getConnection("jdbc:sqlite:Java3_Lesson3.db");
            fStatement    = fDbConnection.createStatement();

            System.out.println("Соединение с БД установлено");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Ошибка подключения драйвера БД");
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            System.out.println("Ошибка соединения с БД");
            e.printStackTrace();
        }
    }

    public void executeSelect(String aQuery)
    {
        try
        {
            fResultSet = fStatement.executeQuery(aQuery);
        }
        catch (SQLException e)
        {
            System.out.println("Ошибка выполнения SQL-выражения");
            e.printStackTrace();
        }
    }

    public int executeUpdate(String aQuery)
    {
        try
        {
            return fStatement.executeUpdate(aQuery);
        }
        catch (SQLException e)
        {
            System.out.println("Ошибка выполнения выражения SQL");
            e.printStackTrace();
            return -1;
        }
    }

    public void closeConnection()
    {
        try
        {
            fDbConnection.close();
            fResultSet   .close();
            fStatement   .close();
            System.out.println("Соединение с БД закрыто");
        }
        catch (SQLException e)
        {
            System.out.println("Ошибка закрытия соединения с БД");
            e.printStackTrace();
        }
    }

    public ResultSet getResultSet() { return fResultSet; }
}
