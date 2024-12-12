package com.iv1351.integration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RentDAO {
    private PreparedStatement createRentalStmt;
    private PreparedStatement terminateRentalStmt;
    private PreparedStatement countRentalsStmt;
    private PreparedStatement findRentedInstrumentsStmt;
    private PreparedStatement findActiveRentalIdStmt;

    private final Connection connection;

    // constructor initalizes connection and prepares statements
    public RentDAO(Connection connection) throws SQLException {
        this.connection = connection;
        prepareStatements();
    }

    private void prepareStatements() throws SQLException {
        // statement to insert a new rental into the database
        createRentalStmt = connection.prepareStatement("""
                    INSERT INTO rental (start_date, end_date, student_id, instrument_id, terminated)
                    VALUES (DATE_TRUNC('second', NOW()), ?, ?, ?, FALSE)
                """);
        // statement to mark a rental as terminated
        terminateRentalStmt = connection.prepareStatement("""
                    UPDATE rental
                    SET terminated = TRUE
                    WHERE id = ?
                """);
        // statement to count active rentals for a student
        countRentalsStmt = connection.prepareStatement("""
                    SELECT COUNT(*)
                    FROM rental
                    WHERE student_id = ?
                        AND terminated = FALSE
                        AND CURRENT_DATE BETWEEN start_date::DATE AND end_date::DATE
                """);
        // statement to find instrument ids that are currently rented
        findRentedInstrumentsStmt = connection.prepareStatement("""
                    SELECT instrument_id
                    FROM rental
                    WHERE terminated = FALSE
                        AND CURRENT_DATE BETWEEN start_date::DATE AND end_date::DATE
                """);
        // statement to find the rental id of an active rental for a given student and
        // instrument
        findActiveRentalIdStmt = connection.prepareStatement("""
                    SELECT id
                    FROM rental
                    WHERE student_id = ?
                        AND instrument_id = ?
                        AND terminated = FALSE
                        AND CURRENT_DATE BETWEEN start_date::DATE AND end_date::DATE
                    LIMIT 1
                """);
    }

    // create a new rental
    public void createRental(int studentId, int instrumentId, int months) throws SQLException {
        // insert rental into database with start date as current date and end date as
        // current date plus specified months
        java.time.LocalDate endDate = java.time.LocalDate.now().plusMonths(months);
        createRentalStmt.setDate(1, java.sql.Date.valueOf(endDate));
        createRentalStmt.setInt(2, studentId);
        createRentalStmt.setInt(3, instrumentId);
        createRentalStmt.executeUpdate();
    }

    // terminate a rental
    public int terminateRental(int rentalId) throws SQLException {
        terminateRentalStmt.setInt(1, rentalId);
        return terminateRentalStmt.executeUpdate();
    }

    // count active rentals for a student
    public int countActiveRentals(int studentId) throws SQLException {
        countRentalsStmt.setInt(1, studentId);
        try (ResultSet result = countRentalsStmt.executeQuery()) {
            if (result.next()) {
                return result.getInt(1);
            } else {
                return 0;
            }
        }
    }

    // get a list of instrument ids that are currently rented
    public List<Integer> fetchActiveRentalInstrumentIds() throws SQLException {
        try (ResultSet result = findRentedInstrumentsStmt.executeQuery()) {
            // create array list to store instrument ids
            List<Integer> rentedInstrumentIds = new ArrayList<>();
            // iterate through each row in results and add instrument id to array list
            while (result.next()) {
                rentedInstrumentIds.add(result.getInt("instrument_id"));
            }
            return rentedInstrumentIds;
        }
    }

    // return rental id if student has an active rental for specified instrument
    public int findActiveRentalId(int studentId, int instrumentId) throws SQLException {
        findActiveRentalIdStmt.setInt(1, studentId);
        findActiveRentalIdStmt.setInt(2, instrumentId);
        try (ResultSet result = findActiveRentalIdStmt.executeQuery()) {
            if (result.next()) {
                return result.getInt("id");
            }
        }
        return -1;
    }
}
