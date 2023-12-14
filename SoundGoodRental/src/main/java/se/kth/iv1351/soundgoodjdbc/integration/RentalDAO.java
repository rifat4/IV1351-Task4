/*
 * The MIT License (MIT)
 * Copyright (c) 2020 Leif Lindbäck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction,including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so,subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package se.kth.iv1351.soundgoodjdbc.integration;

import se.kth.iv1351.soundgoodjdbc.model.Rental;
import se.kth.iv1351.soundgoodjdbc.model.RentalDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This data access object (DAO) encapsulates all rental calls in the Soundgood management
 * application. No code outside this class shall have any knowledge about the
 * database.
 */
public class RentalDAO {

    private static final String INSTRUMENT_TYPE_NAME = "name";
    private static final String INSTRUMENT_TABLE_NAME = "instruments";
    private static final String INSTRUMENT_DETAILS_TABLE_NAME = "instrument_details";
    private static final String INSTRUMENT_TYPE_TABLE_NAME = "instrument_type";
    private static final String RENTED_INSTRUMENT_TABLE_NAME = "rented_instrument";
    private static final String INSTRUMENT_PRICE = "price";
    private static final String RENTAL_END_TIME = "rental_end_time";
    private static final String NOW = "NOW()";
    private static final String RENTAL_START_TIME = "rental_start_time";
    private static final String RENTED_INSTRUMENT_K = "rented_instrument_id";
    private static final String STUDENT_K = "student_id";
    private static final String RENTAL_K = "rented_instrument_id";
    private static final String INSTRUMENT_TYPE_K = "instrument_type_id";
    private static final String INSTRUMENT_DETAILS_K = "instrument_details_id";
    private static final String INSTRUMENT_K = "instrument_id";

    Connection connection;

    private PreparedStatement findAllRentedInstrumentsStmt;
    private PreparedStatement createNewRentalStmt;
    private PreparedStatement updateEndRental;


    /**
     * Creates a new DAO object and connects to the database.
     */
    public RentalDAO() throws SoundgoodDBException {
        try {
            connectToSoundgoodDb();
            prepareStatements();
        } catch (ClassNotFoundException | SQLException exception) {
            throw new SoundgoodDBException("Could not connect to datasource.", exception);
        }
    }


    /**
     * Connects to the database and
     */
    private void connectToSoundgoodDb() throws ClassNotFoundException, SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Task4",
                "postgres", "Cb38j2zhw3A74c");
        connection.setAutoCommit(false);
    }

    /**
     * Searches for all instruments that are currently rented
     * @return a List with objects Instrument
     */
    public List<? extends RentalDTO> findAllRentedInstruments() throws SoundgoodDBException {
        String failureMsg = "Could not list instruments.";
        List<Rental> instruments = new ArrayList<>();
        try (ResultSet result = findAllRentedInstrumentsStmt.executeQuery()) {
            while (result.next()) {
                instruments.add(new Rental(result.getInt(INSTRUMENT_K),
                        result.getInt(INSTRUMENT_PRICE),
                        result.getString(INSTRUMENT_TYPE_NAME),
                        result.getInt(STUDENT_K),
                        result.getInt(RENTAL_K)));
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
        return instruments;
    }

    /**
     * Searches creates a new rental with student and instrument ID
     * @param student_id The students id
     * @param instrument_id the instrument that is to be rented
     *
     * @throws SoundgoodDBException If failed to search for available instrument.
     */
    public void createRental(int student_id, int instrument_id) throws SoundgoodDBException {
        String failureMsg = "Could not create rental.";
        ResultSet result = null;
        try{
            createNewRentalStmt.setInt(1, student_id);
            createNewRentalStmt.setInt(2, instrument_id);
            //result = createNewRentalStmt.executeQuery();
            int updatedRows = createNewRentalStmt.executeUpdate();
            if(updatedRows == 0){
                handleException(failureMsg, null);
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        } finally {
            //closeResultSet(failureMsg, result);
        }
    }

    /**
     * Updates the rental to show that it has been terminated
     *
     * @param rentalId The id of the rental
     * @throws SoundgoodDBException If failed to terminate rental with rental_Id.
     */
    public void updateRental(int rentalId) {
        String failureMsg = "Could not delete rental. ";
        try{
            updateEndRental.setInt(1, rentalId);
            int updatedRows = updateEndRental.executeUpdate();
            if(updatedRows == 0){
                handleException(failureMsg, null);
            }
            connection.commit();
        } catch (SQLException | SoundgoodDBException e) {
            throw new RuntimeException(e);
        }
    }

    private void prepareStatements() throws SQLException {
        findAllRentedInstrumentsStmt = connection.prepareStatement("SELECT i."
                + INSTRUMENT_K + ", ids." + INSTRUMENT_PRICE + ", it." + INSTRUMENT_TYPE_NAME + ", ri." + STUDENT_K
                + ", ri." + RENTED_INSTRUMENT_K
                + " FROM " + INSTRUMENT_TABLE_NAME + " i INNER JOIN " + INSTRUMENT_TYPE_TABLE_NAME
                + " it ON i." + INSTRUMENT_TYPE_K + "=it." + INSTRUMENT_TYPE_K
                + " INNER JOIN " + INSTRUMENT_DETAILS_TABLE_NAME + " ids ON i." + INSTRUMENT_DETAILS_K + "=ids." + INSTRUMENT_DETAILS_K
                + " INNER JOIN " + RENTED_INSTRUMENT_TABLE_NAME + " ri ON i." + INSTRUMENT_K + "=ri." + INSTRUMENT_K
                + " WHERE " + RENTAL_END_TIME + " IS NULL");

        createNewRentalStmt = connection.prepareStatement(
                "INSERT INTO " + RENTED_INSTRUMENT_TABLE_NAME + "("
                + RENTAL_START_TIME + ", "
                + RENTAL_END_TIME + ", "
                + STUDENT_K + ", "
                + INSTRUMENT_K + ") VALUES (NOW(), NULL, ?, ?)");

        updateEndRental = connection.prepareStatement(
                "UPDATE " + RENTED_INSTRUMENT_TABLE_NAME + " SET " + RENTAL_END_TIME + "=" + NOW
                + " WHERE " + RENTED_INSTRUMENT_K + " = (?) AND " + RENTAL_END_TIME + " IS NULL" );
    }

    private void handleException(String failureMsg, Exception cause) throws SoundgoodDBException {
        String completeFailureMsg = failureMsg;
        try {
            connection.rollback();
        } catch (SQLException rollbackExc) {
            completeFailureMsg = completeFailureMsg +
                    ". Also failed to rollback transaction because of: " + rollbackExc.getMessage();
        }
        if (cause != null) {
            throw new SoundgoodDBException(completeFailureMsg, cause);
        } else {
            throw new SoundgoodDBException(completeFailureMsg);
        }
    }


}
