package se.kth.iv1351.bankjdbc.integration;

import se.kth.iv1351.bankjdbc.model.Rental;
import se.kth.iv1351.bankjdbc.model.RentalDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RentalDAO {

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
    private static final String RENTED_INSTRUMENT_FK = "rented_instrument_id";
    private static final String STUDENT_FK = "student_id";
    private static final String NOW = "NOW()";

    Connection connection;

    private PreparedStatement findAllRentedInstrumentsStmt;
    private PreparedStatement createNewRentalStmt;
    private PreparedStatement terminateRentalStmt;



    public RentalDAO() throws SoundgoodDBException {
        try {
            connectToBankDB();
            prepareStatements();
        } catch (ClassNotFoundException | SQLException exception) {
            throw new SoundgoodDBException("Could not connect to datasource.", exception);
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

    public List<? extends RentalDTO> findAllRentedInstruments() throws SoundgoodDBException {
        String failureMsg = "Could not list instruments.";
        List<Rental> instruments = new ArrayList<>();
        try (ResultSet result = findAllRentedInstrumentsStmt.executeQuery()) {
            while (result.next()) {
                instruments.add(new Rental(result.getInt(INSTRUMENT_FK),
                        result.getInt(INSTRUMENT_PRICE),
                        result.getString(INSTRUMENT_TYPE_NAME),
                        result.getInt(STUDENT_FK)));
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
        return instruments;
    }

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

    public void terminateRental(int rentalId) {
        String failureMsg = "Could not delete rental. ";
        try{
            terminateRentalStmt.setInt(1, rentalId);
            int updatedRows = terminateRentalStmt.executeUpdate();
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
                + INSTRUMENT_FK + ", ids." + INSTRUMENT_PRICE + ", it." + INSTRUMENT_TYPE_NAME + ", ri." + STUDENT_FK
                + " FROM " + INSTRUMENT_TABLE_NAME + " i INNER JOIN " + INSTRUMENT_TYPE_TABLE_NAME
                + " it ON i." + INSTRUMENT_TYPE_FK + "=it." + INSTRUMENT_TYPE_FK
                + " INNER JOIN " + INSTRUMENT_DETAILS_TABLE_NAME + " ids ON i." + INSTRUMENT_DETAILS_FK + "=ids." + INSTRUMENT_DETAILS_FK
                + " INNER JOIN " + RENTED_INSTRUMENT_TABLE_NAME + " ri ON i." + INSTRUMENT_FK + "=ri." + INSTRUMENT_FK
                + " WHERE " + RENTAL_END_TIME + " IS NULL");

        createNewRentalStmt = connection.prepareStatement(
                "INSERT INTO " + RENTED_INSTRUMENT_TABLE_NAME + "("
                + RENTAL_START_TIME + ", "
                + RENTAL_END_TIME + ", "
                + STUDENT_FK + ", "
                + INSTRUMENT_FK + ") VALUES (NOW(), NULL, ?, ?)");

        terminateRentalStmt = connection.prepareStatement(
                "UPDATE " + RENTED_INSTRUMENT_TABLE_NAME + " SET " + RENTAL_END_TIME + "=" + NOW
                + " WHERE " + RENTED_INSTRUMENT_FK + " = (?)");
    }

    private void closeResultSet(String failureMsg, ResultSet result) throws SoundgoodDBException {
        try {
            result.close();
        } catch (Exception e) {
            throw new SoundgoodDBException(failureMsg + " Could not close result set.", e);
        }
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


}
