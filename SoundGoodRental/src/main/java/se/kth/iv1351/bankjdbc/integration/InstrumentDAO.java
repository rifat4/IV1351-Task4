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

package se.kth.iv1351.bankjdbc.integration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import se.kth.iv1351.bankjdbc.model.Instrument;
import se.kth.iv1351.bankjdbc.model.InstrumentDTO;

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
    private PreparedStatement createHolderStmt;
    private PreparedStatement findHolderPKStmt;
    private PreparedStatement createAccountStmt;
    private PreparedStatement findFreeInstrumentOfTypeStmt;
    private PreparedStatement findAccountByAcctNoStmt;
    private PreparedStatement findAccountByAcctNoStmtLockingForUpdate;
    private PreparedStatement findAllFreeInstrumentsStmt;
    private PreparedStatement deleteAccountStmt;
    private PreparedStatement changeBalanceStmt;

    /**
     * Constructs a new DAO object connected to the bank database.
     */
    public InstrumentDAO() throws SoundgoodDBException {
        try {
            connectToBankDB();
            prepareStatements();
        } catch (ClassNotFoundException | SQLException exception) {
            throw new SoundgoodDBException("Could not connect to datasource.", exception);
        }
    }

    /**
     * Creates a new account.
     *
     * @param account The account to create.
     * @throws SoundgoodDBException If failed to create the specified account.
     */
    public void createAccount(InstrumentDTO account) throws SoundgoodDBException {
        String failureMsg = "Could not create the account: " + account;
        int updatedRows = 0;
        try {
            int holderPK = findHolderPKByName(account.getInstrumentType());
            if (holderPK == 0) {
                createHolderStmt.setString(1, account.getInstrumentType());
                updatedRows = createHolderStmt.executeUpdate();
                if (updatedRows != 1) {
                    handleException(failureMsg, null);
                }
                holderPK = findHolderPKByName(account.getInstrumentType());
            }

            createAccountStmt.setInt(1, createAccountNo());
            createAccountStmt.setInt(2, account.getInstrumentPrice());
            createAccountStmt.setInt(3, holderPK);
            updatedRows = createAccountStmt.executeUpdate();
            if (updatedRows != 1) {
                handleException(failureMsg, null);
            }

            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
    }

    /**
     * Searches for the account with the specified account number.
     *
     * @param acctNo        The account number.
     * @param lockExclusive If true, it will not be possible to perform UPDATE
     *                      or DELETE statements on the selected row in the
     *                      current transaction. Also, the transaction will not
     *                      be committed when this method returns. If false, no
     *                      exclusive locks will be created, and the transaction
     *                      will be committed when this method returns.
     * @return The account with the specified account number, or <code>null</code>
     *         if there is no such account.
     * @throws SoundgoodDBException If failed to search for the account.
     */
    public Instrument findAccountByAcctNo(String acctNo, boolean lockExclusive)
            throws SoundgoodDBException {
        PreparedStatement stmtToExecute;
        if (lockExclusive) {
            stmtToExecute = findAccountByAcctNoStmtLockingForUpdate;
        } else {
            stmtToExecute = findAccountByAcctNoStmt;
        }

        String failureMsg = "Could not search for specified account.";
        ResultSet result = null;
        try {
            stmtToExecute.setString(1, acctNo);
            result = stmtToExecute.executeQuery();
            if (result.next()) {
                return new Instrument(result.getString(INSTRUMENT_BRAND),
                        result.getString(INSTRUMENT_TYPE_NAME),
                        result.getInt(INSTRUMENT_PRICE),
                        result.getInt(INSTRUMENT_FK));
            }
            if (!lockExclusive) {
                connection.commit();
            }
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        } finally {
            closeResultSet(failureMsg, result);
        }
        return null;
    }

    /**
     * Searches for all accounts whose holder has the specified name.
     *
     * @param instrumentType The account holder's name
     * @return A list with all accounts whose holder has the specified name,
     *         the list is empty if there are no such account.
     * @throws SoundgoodDBException If failed to search for accounts.
     */
    public List<Instrument> findFreeInstrumentsOfType(String instrumentType) throws SoundgoodDBException {
        String failureMsg = "Could not search for specified accounts.";
        ResultSet result = null;
        List<Instrument> accounts = new ArrayList<>();
        try {
            findFreeInstrumentOfTypeStmt.setString(1, instrumentType);
            result = findFreeInstrumentOfTypeStmt.executeQuery();
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
     * Retrieves all existing accounts.
     *
     * @return A list with all existing accounts. The list is empty if there are no
     *         accounts.
     * @throws SoundgoodDBException If failed to search for accounts.
     */
    public List<Instrument> findAllFreeInstruments(boolean forUpdate) throws SoundgoodDBException {
        String failureMsg = "Could not list instruments.";
        List<Instrument> instruments = new ArrayList<>();
        if(forUpdate){
            try (ResultSet result = findAllFreeInstrumentsStmt.executeQuery()) {
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
            try (ResultSet result = findAllFreeInstrumentsStmt.executeQuery()) {
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

    private void connectToBankDB() throws ClassNotFoundException, SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/soundgoodtestview2",
                "postgres", "Cb38j2zhw3A74c");
        // connection =
        // DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb",
        // "mysql", "mysql");
        connection.setAutoCommit(false);
    }

    private void prepareStatements() throws SQLException {
        createHolderStmt = connection.prepareStatement("INSERT INTO " + INSTRUMENT_DETAILS
                + "(" + INSTRUMENT_TYPE_NAME + ") VALUES (?)");

        createAccountStmt = connection.prepareStatement("INSERT INTO " + INSTRUMENT_TABLE_NAME
                + "(" + INSTRUMENT_BRAND + ", " + INSTRUMENT_PRICE + ", "
                + INSTRUMENT_TYPE_FK + ") VALUES (?, ?, ?)");

        /**
        findFreeInstrumentOfType = connection.prepareStatement("SELECT ids." + INSTRUMENT_BRAND
                + ", ids." + INSTRUMENT_PRICE + ", it." + INSTRUMENT_TYPE_NAME + " from "
                + INSTRUMENT_TABLE_NAME + " i INNER JOIN "
                + INSTRUMENT_DETAILS + " ids ON i." + INSTRUMENT_TYPE_FK
                + " = ids." + INSTRUMENT_TYPE_FK + " WHERE h." + INSTRUMENT_TYPE_NAME + " = ?");
         **/

        findFreeInstrumentOfTypeStmt = connection.prepareStatement("SELECT ids." + INSTRUMENT_BRAND
                + ", ids." + INSTRUMENT_PRICE + ", it." + INSTRUMENT_TYPE_NAME + " from " + INSTRUMENT_TABLE_NAME +
                " i INNER JOIN " + INSTRUMENT_DETAILS_TABLE_NAME + " ids ON i." + INSTRUMENT_DETAILS_FK + "=ids." +
                INSTRUMENT_DETAILS_FK + " INNER JOIN " + INSTRUMENT_TYPE_TABLE_NAME + " it ON i." + INSTRUMENT_TYPE_FK
        + "=it" + INSTRUMENT_TYPE_FK);

        findAllFreeInstrumentsStmt = connection.prepareStatement("SELECT ids." + INSTRUMENT_BRAND
                + ", ids." + INSTRUMENT_PRICE + ", it." + INSTRUMENT_TYPE_NAME + ", i." + INSTRUMENT_FK + " from " + INSTRUMENT_TABLE_NAME +
                " i INNER JOIN " + INSTRUMENT_DETAILS_TABLE_NAME + " ids ON i." + INSTRUMENT_DETAILS_FK + "=ids." +
                INSTRUMENT_DETAILS_FK + " INNER JOIN " + INSTRUMENT_TYPE_TABLE_NAME + " it ON i." + INSTRUMENT_TYPE_FK
                + "=it." + INSTRUMENT_TYPE_FK + " LEFT JOIN " + RENTED_INSTRUMENT_TABLE_NAME + " ri ON i." +
                INSTRUMENT_FK + "=ri." + INSTRUMENT_FK + " WHERE " + RENTAL_END_TIME + " IS NULL AND " +
                RENTAL_START_TIME + " IS NULL ");

        changeBalanceStmt = connection.prepareStatement("UPDATE " + INSTRUMENT_TABLE_NAME
                + " SET " + INSTRUMENT_PRICE + " = ? WHERE " + INSTRUMENT_BRAND + " = ? ");

        deleteAccountStmt = connection.prepareStatement("DELETE FROM " + INSTRUMENT_TABLE_NAME
                + " WHERE " + INSTRUMENT_BRAND + " = ?");
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

    private int createAccountNo() {
        return (int) Math.floor(Math.random() * Integer.MAX_VALUE);
    }

    private int findHolderPKByName(String holderName) throws SQLException {
        ResultSet result = null;
        findHolderPKStmt.setString(1, holderName);
        result = findHolderPKStmt.executeQuery();
        if (result.next()) {
            return result.getInt(HOLDER_PK_COLUMN_NAME);
        }
        return 0;
    }

}
