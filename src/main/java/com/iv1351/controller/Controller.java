package com.iv1351.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.iv1351.integration.DBCon;
import com.iv1351.integration.InstrumentDAO;
import com.iv1351.integration.RentDAO;
import com.iv1351.model.Instrument;

// controller acts as a mediator between the view and the integration
public class Controller {
    private final InstrumentDAO instrumentDAO;
    private final RentDAO rentDAO;
    private final DBCon dbCon;

    // constructor instantiates the DAOs with connections to the database
    public Controller() throws SQLException {
        dbCon = new DBCon();
        instrumentDAO = new InstrumentDAO(dbCon.getConnection());
        rentDAO = new RentDAO(dbCon.getConnection());
    }

    // handle exceptions by rolling back the transaction and throwing a new
    // exception
    private void handleException(String failureMsg, SQLException cause) throws SQLException {
        try {
            dbCon.rollback();
        } catch (SQLException rollbackExc) {
            failureMsg = failureMsg + ". Also failed to rollback transaction because of: " + rollbackExc.getMessage();
        }
        if (cause != null) {
            throw new SQLException(failureMsg, cause);
        } else {
            throw new SQLException(failureMsg);
        }
    }

    // check if an instrument is available for rent
    public boolean isInstrumentAvailable(int instrumentId) throws SQLException {
        List<Integer> rentedInstrumentIds = rentDAO.fetchActiveRentalInstrumentIds();
        return !rentedInstrumentIds.contains(instrumentId);
    }

    // list available instruments of a specified type
    public List<Instrument> listAvailableInstruments(String type) throws SQLException {
        List<Instrument> allInstruments = instrumentDAO.getAllInstrumentsByType(type); // Get all instruments of the
                                                                                       // type
        List<Instrument> availableInstruments = new ArrayList<>();
        for (Instrument instrument : allInstruments) {
            if (isInstrumentAvailable(instrument.getId())) {
                availableInstruments.add(instrument);
            }
        }
        return availableInstruments;
    }

    // rent an instrument for a student for a specified number of months
    public void rentInstrument(int studentId, int instrumentId, int months) throws SQLException {
        try {
            checkExistingRental(studentId, instrumentId);
            checkRentalLimit(studentId);
            checkInstrumentAvailability(instrumentId);
            rentDAO.createRental(studentId, instrumentId, months);
            dbCon.commit();
        } catch (SQLException e) {
            handleException("Failed to rent instrument", e);
        }
    }

    // check if a student already has an active rental for a specified instrument
    private void checkExistingRental(int studentId, int instrumentId) throws SQLException {
        int existingRentalId = rentDAO.findActiveRentalId(studentId, instrumentId);
        if (existingRentalId > 0) {
            throw new SQLException("Student already has an active rental for this instrument.");
        }
    }

    // check if a student has reached the rental limit of 2 instruments
    private void checkRentalLimit(int studentId) throws SQLException {
        if (rentDAO.countActiveRentals(studentId) >= 2) {
            throw new SQLException("Student is already renting the maximum of 2 instruments.");
        }
    }

    // check if an instrument is available for rent
    private void checkInstrumentAvailability(int instrumentId) throws SQLException {
        if (!isInstrumentAvailable(instrumentId)) {
            throw new SQLException("Instrument is not available.");
        }
    }

    // terminate a rental for a student for a specified instrument
    public boolean terminateRental(int studentId, int instrumentId) throws SQLException {
        try {
            // check if the student has an active rental for specified instrument to be
            // terminated
            int rentalId = rentDAO.findActiveRentalId(studentId, instrumentId);
            // if an id is returned rental exists and can be terminated
            if (rentalId > 0) {
                rentDAO.terminateRental(rentalId);
                dbCon.commit();
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            handleException("Failed to terminate rental", e);
            return false;
        }
    }

    // disconnect from the database
    public void disconnect() throws SQLException {
        dbCon.disconnect();
    }
}
