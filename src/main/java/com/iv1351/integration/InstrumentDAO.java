package com.iv1351.integration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.iv1351.model.Instrument;

// InstrumentDAO handles database operations for instruments
public class InstrumentDAO {
    private final Connection connection;

    // constructor initalizes connection
    public InstrumentDAO(Connection connection) {
        this.connection = connection;
    }

    // query database for available instruments of a specified type
    public List<Instrument> getAllInstrumentsByType(String type) throws SQLException {
        String query = """
                    SELECT i.id, i.brand, i.rental_price
                    FROM instrument i
                    JOIN instrument_type it ON i.instrument_type_id = it.id
                    WHERE LOWER(it.instrument_type) = LOWER(?)
                        AND i.id NOT IN (
                            SELECT r.instrument_id
                            FROM rental r
                            WHERE r.terminated = FALSE
                            AND CURRENT_DATE BETWEEN r.start_date::DATE AND r.end_date::DATE
                        );
                """;
        // create array list to store instruments returned from query
        List<Instrument> instruments = new ArrayList<>();
        // prepare statement to execute query
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // insert user input type into query
            stmt.setString(1, type);
            // execute query and store results in result set
            try (ResultSet result = stmt.executeQuery()) {
                // iterate through each row in results, create instrument object out of columns
                // and add to array list
                while (result.next()) {
                    instruments.add(new Instrument(
                            result.getInt("id"),
                            result.getString("brand"),
                            result.getBigDecimal("rental_price")));
                }
            }
        }
        return instruments;
    }
}
