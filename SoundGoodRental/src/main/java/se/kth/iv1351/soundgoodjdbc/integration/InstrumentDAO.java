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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import se.kth.iv1351.soundgoodjdbc.model.Instrument;

/**
 * This data access object (DAO) encapsulates all database calls in the bank
 * application. No code outside this class shall have any knowledge about the
 * database.
 */
public class InstrumentDAO {
    private static final String INSTRUMENT_DETAILS = "instrument_details";
    private static final String HOLDER_PK_COLUMN_NAME = "holder_id";
    private static final String INSTRUMENT_TYPE_NAME = "name";
    private static final String INSTRUMENT_TABLE_NAME = "instruments";
    private static final String INSTRUMENT_BRAND = "brand";
    private static final String INSTRUMENT_PRICE = "price";
    private static final String INSTRUMENT_DETAILS_TABLE_NAME = "instrument_details";
    private static final String INSTRUMENT_TYPE_TABLE_NAME = "instrument_type";
    private static final String INSTRUMENT_TYPE_FK = "instrument_type_id";
    private static final String INSTRUMENT_DETAILS_FK = "instrument_details_id";
    private static final String RENTED_INSTRUMENT_TABLE_NAME = "rented_instrument";
    private static final String INSTRUMENT_FK = "instrument_id";
    private static final String RENTAL_END_TIME = "rental_end_time";
    private static final String RENTAL_START_TIME = "rental_start_time";
    private static final String STUDENT_FK = "student_id";

    private Connection connection;
    private PreparedStatement findAvailableInstrumentOfTypeStmt;
    private PreparedStatement findAllAvailableInstrumentsStmt;
    private PreparedStatement findAllAvailableInstrumentsForUpdateStmt;
    private PreparedStatement findCountOfRentedInstrumentsForStudentStmt;

    /**
     * Constructs a new DAO object connected to the bank database.
     */
    public InstrumentDAO() throws SoundgoodDBException {
        try {
            connectToSoundgoodDb();
            prepareStatements();
        } catch (ClassNotFoundException | SQLException exception) {
            throw new SoundgoodDBException("Could not connect to datasource.", exception);
        }
    }

    /**
     * Searches for all accounts whose holder has the specified name.
     *
     * @param instrumentType The account holder's name
     * @return A list with all accounts whose holder has the specified name,
     *         the list is empty if there are no such account.
     * @throws SoundgoodDBException If failed to search for accounts.
     */
    public List<Instrument> findAvailableInstrumentsOfType(String instrumentType) throws SoundgoodDBException {
        String failureMsg = "Could not search for specified instrument.";
        ResultSet result = null;
        List<Instrument> accounts = new ArrayList<>();
        try {
            findAvailableInstrumentOfTypeStmt.setString(1, instrumentType);
            result = findAvailableInstrumentOfTypeStmt.executeQuery();
            while (result.next()) {
                accounts.add(new Instrument(result.getString(INSTRUMENT_BRAND),
                        result.getString(INSTRUMENT_TYPE_NAME),
                        result.getInt(INSTRUMENT_PRICE),
                        result.getInt(INSTRUMENT_FK)));
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        } finally {
            closeResultSet(failureMsg, result);
        }
        return accounts;
    }

    /**
     * @return A list with all existing accounts. The list is empty if there are no
     *         accounts.
     * @throws SoundgoodDBException If failed to search for accounts.
     */
    public List<Instrument> findAllAvailableInstruments(boolean forUpdate) throws SoundgoodDBException {
        String failureMsg = "Could not list instruments.";
        List<Instrument> instruments = new ArrayList<>();
        if(forUpdate){
            try (ResultSet result = findAllAvailableInstrumentsForUpdateStmt.executeQuery()) {
                while (result.next()) {
                    instruments.add(new Instrument(result.getString(INSTRUMENT_BRAND),
                            result.getString(INSTRUMENT_TYPE_NAME),
                            result.getInt(INSTRUMENT_PRICE),
                            result.getInt(INSTRUMENT_FK)));
                }
            } catch (SQLException sqle) {
                handleException(failureMsg, sqle);
            }
        } else {
            try (ResultSet result = findAllAvailableInstrumentsStmt.executeQuery()) {
                while (result.next()) {
                    instruments.add(new Instrument(result.getString(INSTRUMENT_BRAND),
                            result.getString(INSTRUMENT_TYPE_NAME),
                            result.getInt(INSTRUMENT_PRICE),
                            result.getInt(INSTRUMENT_FK)));
                }
                connection.commit();
            } catch (SQLException sqle) {
                handleException(failureMsg, sqle);
            }
        }
        return instruments;
    }

    /**
     * Commits the current transaction.
     * 
     * @throws SoundgoodDBException If unable to commit the current transaction.
     */
    public void commit() throws SoundgoodDBException {
        try {
            connection.commit();
        } catch (SQLException e) {
            handleException("Failed to commit", e);
        }
    }

    private void connectToSoundgoodDb() throws ClassNotFoundException, SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/soundgoodtestview2",
                "postgres", "Cb38j2zhw3A74c");
        connection.setAutoCommit(false);
    }

    private void prepareStatements() throws SQLException {

        /**
        findAvailableInstrumentOfType = connection.prepareStatement("SELECT ids." + INSTRUMENT_BRAND
                + ", ids." + INSTRUMENT_PRICE + ", it." + INSTRUMENT_TYPE_NAME + " from "
                + INSTRUMENT_TABLE_NAME + " i INNER JOIN "
                + INSTRUMENT_DETAILS + " ids ON i." + INSTRUMENT_TYPE_FK
                + " = ids." + INSTRUMENT_TYPE_FK + " WHERE h." + INSTRUMENT_TYPE_NAME + " = ?");
         **/



        findAvailableInstrumentOfTypeStmt = connection.prepareStatement("SELECT t1.instrument_id, it.name, ids.brand, ids.price\n" +
                "FROM instruments AS t1\n" +
                "LEFT JOIN instrument_type AS it ON t1.instrument_type_id = it.instrument_type_id\n" +
                "LEFT JOIN instrument_details AS ids ON t1.instrument_details_id = ids.instrument_details_id\n" +
                "LEFT JOIN rented_instrument AS ri ON t1.instrument_id = ri.instrument_id AND ri.rental_end_time IS NULL\n" +
                "WHERE ri.instrument_id IS NULL AND LOWER(it.name) = LOWER((?));\n");


        /**
        findAllAvailableInstrumentsStmt = connection.prepareStatement("SELECT ids." + INSTRUMENT_BRAND
                + ", ids." + INSTRUMENT_PRICE + ", it." + INSTRUMENT_TYPE_NAME + ", i." + INSTRUMENT_FK + " from " + INSTRUMENT_TABLE_NAME +
                " i INNER JOIN " + INSTRUMENT_DETAILS_TABLE_NAME + " ids ON i." + INSTRUMENT_DETAILS_FK + "=ids." +
                INSTRUMENT_DETAILS_FK + " INNER JOIN " + INSTRUMENT_TYPE_TABLE_NAME + " it ON i." + INSTRUMENT_TYPE_FK
                + "=it." + INSTRUMENT_TYPE_FK + " LEFT JOIN " + RENTED_INSTRUMENT_TABLE_NAME + " ri ON i." +
                INSTRUMENT_FK + "=ri." + INSTRUMENT_FK + " WHERE " + RENTAL_END_TIME + " IS NULL AND " +
                RENTAL_START_TIME + " IS NULL ");
         **/

        findAllAvailableInstrumentsStmt = connection.prepareStatement("SELECT t1.instrument_id, it.name, ids.brand, ids.price\n" +
                "FROM instruments AS t1\n" +
                "LEFT JOIN instrument_type AS it ON t1.instrument_type_id = it.instrument_type_id\n" +
                "LEFT JOIN instrument_details AS ids ON t1.instrument_details_id = ids.instrument_details_id\n" +
                "LEFT JOIN rented_instrument AS ri ON t1.instrument_id = ri.instrument_id AND ri.rental_end_time IS NULL\n" +
                "WHERE ri.instrument_id IS NULL;\n");

        findAllAvailableInstrumentsForUpdateStmt = connection.prepareStatement("WITH cte AS (\n" +
                "  SELECT t1.instrument_id, it.name, ids.brand, ids.price\n" +
                "  FROM instruments AS t1\n" +
                "  LEFT JOIN instrument_type AS it ON t1.instrument_type_id = it.instrument_type_id\n" +
                "  LEFT JOIN instrument_details AS ids ON t1.instrument_details_id = ids.instrument_details_id\n" +
                "  LEFT JOIN rented_instrument AS ri ON t1.instrument_id = ri.instrument_id AND ri.rental_end_time IS NULL\n" +
                "  WHERE ri.instrument_id IS NULL\n" +
                ")\n" +
                "SELECT * FROM cte\n" +
                "FOR UPDATE");

        findCountOfRentedInstrumentsForStudentStmt = connection.prepareStatement(
                "SELECT COUNT(*) AS count FROM rented_instrument ri\n" +
                 "WHERE ri.student_id = 1 AND rental_end_time IS NULL");
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
            throw new SoundgoodDBException(failureMsg, cause);
        } else {
            throw new SoundgoodDBException(failureMsg);
        }
    }

    private void closeResultSet(String failureMsg, ResultSet result) throws SoundgoodDBException {
        try {
            result.close();
        } catch (Exception e) {
            throw new SoundgoodDBException(failureMsg + " Could not close result set.", e);
        }
    }
}
