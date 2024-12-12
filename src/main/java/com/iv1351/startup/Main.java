package com.iv1351.startup;

import java.sql.SQLException;

import com.iv1351.view.CLI;

// start the application
public class Main {
    public static void main(String[] args) {
        try {
            new CLI().start();
        } catch (SQLException e) {
            System.out.println("Soundgood failed to start.");
            e.printStackTrace();
        }
    }
}
