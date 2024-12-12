package com.iv1351.integration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBCon {
    private static final String URL = "jdbc:postgresql://localhost:5432/soundGoodSem4";
    private static final String USER = "postgres";
    private static final String PASSWORD = "0795";

    private final Connection connection;

    // constructor creates a connection to database and disables autocommit
    public DBCon() throws SQLException {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
        connection.setAutoCommit(false);
    }

    // return the connection
    public Connection getConnection() {
        return connection;
    }

    // close the connection
    public void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    // commit the transaction
    public void commit() throws SQLException {
        if (connection != null) {
            connection.commit();
        }
    }

    // rollback the transaction
    public void rollback() throws SQLException {
        if (connection != null) {
            connection.rollback();
        }
    }
}
