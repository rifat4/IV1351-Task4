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
import se.kth.iv1351.soundgoodjdbc.model.InstrumentDTO;
import se.kth.iv1351.soundgoodjdbc.model.InstrumentException;

/**
 * This data access object (DAO) encapsulates all instrument calls in the Soundgood management
 * application. No code outside this class shall have any knowledge about the
 * database.
 */
public class InstrumentDAO {
    private static final String INSTRUMENT_TYPE_NAME = "name";
    private static final String INSTRUMENT_TABLE_NAME = "instruments";
    private static final String RENTED_INSTRUMENT_TABLE_NAME = "rented_instrument";
    private static final String INSTRUMENT_DETAILS_TABLE_NAME = "instrument_details";
    private static final String INSTRUMENT_TYPE_TABLE_NAME = "instrument_type";
    private static final String INSTRUMENT_BRAND = "brand";
    private static final String INSTRUMENT_PRICE = "price";
    private static final String INSTRUMENT_TYPE_K = "instrument_type_id";
    private static final String INSTRUMENT_DETAILS_K = "instrument_details_id";
    private static final String INSTRUMENT_K = "instrument_id";
    private static final String RENTAL_END_TIME = "rental_end_time";

    private Connection connection;
    private PreparedStatement findAvailableInstrumentOfTypeStmt;
    private PreparedStatement findAllAvailableInstrumentsStmt;
    private PreparedStatement findAllAvailableInstrumentsForUpdateStmt;
    private PreparedStatement findInstrumentWithIdStmt;

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
     * Connects to the database and
     */
    private void connectToSoundgoodDb() throws ClassNotFoundException, SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Task4",
                "postgres", "Cb38j2zhw3A74c");
        connection.setAutoCommit(false);
    }

    /**
     * Searches for all instruments of specific type
     *
     * @param instrumentType The instrument type
     * @return A list with all inistruments whose holder has the specified type,
     *         the list is empty if there are no such instruments.
     * @throws SoundgoodDBException If failed to search for instruments.
     */
    public List<Instrument> findAvailableInstrumentsOfType(String instrumentType) throws SoundgoodDBException {
        String failureMsg = "Could not search for specified instrument.";
        ResultSet result = null;
        List<Instrument> instruments = new ArrayList<>();
        try {
            findAvailableInstrumentOfTypeStmt.setString(1, instrumentType);
            result = findAvailableInstrumentOfTypeStmt.executeQuery();
            while (result.next()) {
                instruments.add(new Instrument(result.getString(INSTRUMENT_BRAND),
                        result.getString(INSTRUMENT_TYPE_NAME),
                        result.getInt(INSTRUMENT_PRICE),
                        result.getInt(INSTRUMENT_K)));
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        } finally {
            closeResultSet(failureMsg, result);
        }
        return instruments;
    }

    /**
     * @return A list with all existing instruments. The list is empty if there are no
     *         instruments.
     * @throws SoundgoodDBException If failed to search for instruments.
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
                            result.getInt(INSTRUMENT_K)));
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
                            result.getInt(INSTRUMENT_K)));
                }
                connection.commit();
            } catch (SQLException sqle) {
                handleException(failureMsg, sqle);
            }
        }
        return instruments;
    }

    public InstrumentDTO findInstrument(int instrumentId) throws SoundgoodDBException {
        String failureMsg = "Could not search for specified instrument.";
        ResultSet result = null;
        InstrumentDTO instrument = null;
        try {
            findInstrumentWithIdStmt.setInt(1, instrumentId);
            result = findInstrumentWithIdStmt.executeQuery();
            result.next();
            instrument = new Instrument(result.getString(INSTRUMENT_BRAND),
                    result.getString(INSTRUMENT_TYPE_NAME),
                    result.getInt(INSTRUMENT_PRICE),
                    result.getInt(INSTRUMENT_K));
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        } finally {
            closeResultSet(failureMsg, result);
        }
        return instrument;
    }

    private void prepareStatements() throws SQLException {

        findAvailableInstrumentOfTypeStmt = connection.prepareStatement(
                "SELECT t1." + INSTRUMENT_K + ", " + INSTRUMENT_TYPE_NAME + ", " + INSTRUMENT_BRAND + ", " + INSTRUMENT_PRICE +
                        " FROM " + INSTRUMENT_TABLE_NAME + " AS t1 " +
                        " LEFT JOIN " + INSTRUMENT_TYPE_TABLE_NAME + " AS it ON t1." + INSTRUMENT_TYPE_K + " = it." + INSTRUMENT_TYPE_K +
                        " LEFT JOIN " + INSTRUMENT_DETAILS_TABLE_NAME + " AS ids ON t1." + INSTRUMENT_DETAILS_K + " = ids." + INSTRUMENT_DETAILS_K +
                        " LEFT JOIN " + RENTED_INSTRUMENT_TABLE_NAME + " AS ri ON t1." + INSTRUMENT_K + " = ri." + INSTRUMENT_K +
                        " AND ri." + RENTAL_END_TIME + " IS NULL " +
                        " WHERE ri." + INSTRUMENT_K + " IS NULL AND LOWER(it." + INSTRUMENT_TYPE_NAME + ") = LOWER((?));");

        findAllAvailableInstrumentsStmt = connection.prepareStatement(
                "SELECT t1." + INSTRUMENT_K + ", " + INSTRUMENT_TYPE_NAME + ", " + INSTRUMENT_BRAND + ", " + INSTRUMENT_PRICE +
                        " FROM " + INSTRUMENT_TABLE_NAME + " AS t1 " +
                        " LEFT JOIN " + INSTRUMENT_TYPE_TABLE_NAME + " AS it ON t1." + INSTRUMENT_TYPE_K + " = it." + INSTRUMENT_TYPE_K +
                        " LEFT JOIN " + INSTRUMENT_DETAILS_TABLE_NAME + " AS ids ON t1." + INSTRUMENT_DETAILS_K + " = ids." + INSTRUMENT_DETAILS_K +
                        " LEFT JOIN " + RENTED_INSTRUMENT_TABLE_NAME + " AS ri ON t1." + INSTRUMENT_K + " = ri." + INSTRUMENT_K +
                        " AND ri." + RENTAL_END_TIME + " IS NULL " +
                        " WHERE ri." + INSTRUMENT_K + " IS NULL;");


        findAllAvailableInstrumentsForUpdateStmt = connection.prepareStatement(
                "WITH cte AS (" +
                        " SELECT t1." + INSTRUMENT_K + ", " + INSTRUMENT_TYPE_NAME + ", " + INSTRUMENT_BRAND + ", " + INSTRUMENT_PRICE +
                        " FROM " + INSTRUMENT_TABLE_NAME + " AS t1 " +
                        " LEFT JOIN " + INSTRUMENT_TYPE_TABLE_NAME + " AS it ON t1." + INSTRUMENT_TYPE_K + " = it." + INSTRUMENT_TYPE_K +
                        " LEFT JOIN " + INSTRUMENT_DETAILS_TABLE_NAME + " AS ids ON t1." + INSTRUMENT_DETAILS_K + " = ids." + INSTRUMENT_DETAILS_K +
                        " LEFT JOIN " + RENTED_INSTRUMENT_TABLE_NAME + " AS ri ON t1." + INSTRUMENT_K + " = ri." + INSTRUMENT_K +
                        " AND ri." + RENTAL_END_TIME + " IS NULL " +
                        " WHERE ri." + INSTRUMENT_K + " IS NULL" +
                        ")" +
                        " SELECT * FROM cte" +
                        " FOR UPDATE;");

        findInstrumentWithIdStmt = connection.prepareStatement(
                "WITH cte AS (\n" +
                        "    SELECT t1.instrument_id, it.name, ids.brand, ids.price\n" +
                        "    FROM instruments AS t1\n" +
                        "    LEFT JOIN instrument_type AS it ON t1.instrument_type_id = it.instrument_type_id\n" +
                        "    LEFT JOIN instrument_details AS ids ON t1.instrument_details_id = ids.instrument_details_id\n" +
                        "    LEFT JOIN rented_instrument AS ri ON t1.instrument_id = ri.instrument_id AND ri.rental_end_time IS NULL\n" +
                        "    WHERE ri.instrument_id IS NULL AND t1.instrument_id = (?)\n" +
                        ")\n" +
                        "SELECT *\n" +
                        "FROM cte\n" +
                        "FOR UPDATE;");

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

    private void closeResultSet(String failureMsg, ResultSet result) throws SoundgoodDBException {
        try {
            result.close();
        } catch (Exception e) {
            throw new SoundgoodDBException(failureMsg + " Could not close result set.", e);
        }
    }


}
