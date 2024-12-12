package com.iv1351.view;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.iv1351.controller.Controller;
import com.iv1351.model.Instrument;

// command line interface for Soundgood
public class CLI {
    private final Controller controller;
    private final Scanner scanner = new Scanner(System.in);

    // constructor instantiates the controller
    public CLI() throws SQLException {
        controller = new Controller();
    }

    // starts the command line interface
    public void start() {
        while (true) {
            // display menu
            System.out.println("\nSoundgood rental system.");
            System.out.println("1. List instruments");
            System.out.println("2. Rent instrument");
            System.out.println("3. Terminate rental");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");
            try {
                int choice = scanner.nextInt();
                // clear input
                scanner.nextLine();
                switch (choice) {
                    case 1 -> listInstruments();
                    case 2 -> rentInstrument();
                    case 3 -> terminateRental();
                    case 4 -> exit();
                    default -> System.out.println("Invalid choice");
                }
            } catch (Exception e) {
                System.out.println("Invalid input");
            }
        }
    }

    // list instruments available for rent
    private void listInstruments() {
        System.out.print("Enter instrument type: ");
        String type = scanner.nextLine();
        try {
            // create a list of available instruments of specified type
            List<Instrument> instruments = controller.listAvailableInstruments(type);
            if (instruments.isEmpty()) {
                System.out.println("Instrument type is not available.");
            } else {
                System.out.println("Available Instruments:");
                for (Instrument i : instruments) {
                    System.out.printf("Instrument ID: %d, Brand: %s, Price: %.2f%n",
                            i.getId(), i.getBrand(), i.getRentalPrice().doubleValue());
                }
            }
        } catch (SQLException e) {
            System.out.println("Error listing instruments: " + e.getMessage());
        }
    }

    // rent an instrument
    private void rentInstrument() {
        System.out.print("Enter student ID: ");
        int studentId = scanner.nextInt();
        System.out.print("Enter instrument ID: ");
        int instrumentId = scanner.nextInt();
        System.out.print("Enter rental duration (1-12 months): ");
        int months = scanner.nextInt();

        // Delegate duration validation to the controller
        try {
            if (months < 1 || months > 12) {
                System.out.println("Invalid rental duration. Valid duration is between 1 and 12.");
                return;
            }
            controller.rentInstrument(studentId, instrumentId, months);
            System.out.println("Instrument rented successfully.");
        } catch (SQLException e) {
            System.out.println("Error renting instrument: " + e.getMessage());
        }
    }

    // terminates an ongoing rental
    private void terminateRental() {
        System.out.print("Enter student ID: ");
        int studentId = scanner.nextInt();
        System.out.print("Enter instrument ID: ");
        int instrumentId = scanner.nextInt();
        try {
            boolean success = controller.terminateRental(studentId, instrumentId);
            if (success) {
                System.out.println("Rental terminated successfully.");
            } else {
                System.out.println("No active rental found for the given IDs.");
            }
        } catch (SQLException e) {
            System.out.println("Error terminating rental: " + e.getMessage());
        }
    }

    // exits the program
    private void exit() {
        try {
            controller.disconnect();
        } catch (SQLException e) {
            System.out.println("Error disconnecting: " + e.getMessage());
        }
        System.out.println("Exiting program.");
        System.exit(0);
    }
}
