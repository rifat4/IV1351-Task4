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

package se.kth.iv1351.bankjdbc.controller;

import java.util.ArrayList;
import java.util.List;

import se.kth.iv1351.bankjdbc.integration.InstrumentDAO;
import se.kth.iv1351.bankjdbc.integration.SoundgoodDBException;
import se.kth.iv1351.bankjdbc.integration.RentalDAO;
import se.kth.iv1351.bankjdbc.model.*;

/**
 * This is the application's only controller, all calls to the model pass here.
 * The controller is also responsible for calling the DAO. Typically, the
 * controller first calls the DAO to retrieve data (if needed), then operates on
 * the data, and finally tells the DAO to store the updated data (if any).
 */
public class Controller {
    private final InstrumentDAO instrumentDb;
    private final RentalDAO rentalDb;

    /**
     * Creates a new instance, and retrieves a connection to the database.
     * 
     * @throws SoundgoodDBException If unable to connect to the database.
     */
    public Controller() throws SoundgoodDBException {
        instrumentDb = new InstrumentDAO();
        rentalDb = new RentalDAO();
    }

    /**
     * Creates a new account for the specified account holder.
     * 
     * @param holderName The account holder's name.
     * @throws InstrumentException If unable to create account.
     */
    public void createAccount(String holderName) throws InstrumentException {
        String failureMsg = "Could not create account for: " + holderName;

        if (holderName == null) {
            throw new InstrumentException(failureMsg);
        }

        try {
            instrumentDb.createAccount(new Instrument(holderName));
        } catch (Exception e) {
            throw new InstrumentException(failureMsg, e);
        }
    }

    /**
     * Lists all non rented instruments in the schoolDB
     * 
     * @return A list containing all free instruments, if the list is empty there are no
     * free instruments.
     * @throws InstrumentException If unable to retrieve accounts.
     *
     * forUpdate = true if view is gotten for update
     */
    public List<? extends InstrumentDTO> getAllFreeInstruments(boolean forUpdate) throws InstrumentException {
        try {
            return instrumentDb.findAllFreeInstruments(forUpdate);
        } catch (Exception e) {
            throw new InstrumentException("Unable to list free instruments.", e);
        }
    }

    public List<? extends RentalDTO> getAllRentedInstruments() throws InstrumentException {
        try {
            return rentalDb.findAllRentedInstruments();
        } catch(Exception e){
            throw new InstrumentException("Unable to list all rented instruments.", e);
        }
    }

    public void terminateRental(int rentalId) throws RejectedException {
        String failureMsg = "Could not terminate rental: ";
        try {
            rentalDb.terminateRental(rentalId);
        } catch(Exception e){
            throw new RejectedException("Unable to terminate rental. ", e);
        }
    }

    /**
     * Lists all accounts owned by the specified account holder.
     * 
     * @param instrumentType The holder who's accounts shall be listed.
     * @return A list with all accounts owned by the specified holder. The list is
     *         empty if the holder does not have any accounts, or if there is no
     *         such holder.
     * @throws InstrumentException If unable to retrieve the holder's accounts.
     */
    public List<? extends InstrumentDTO> getAllAvailableInstrumentType(String instrumentType) throws InstrumentException {
        if (instrumentType == null) {
            return new ArrayList<>();
        }

        try {
            return instrumentDb.findFreeInstrumentsOfType(instrumentType);
        } catch (Exception e) {
            throw new InstrumentException("Could not search for account.", e);
        }
    }

    public void rentInstrument(int studentId, int instrumentId) throws InstrumentException {
        List<? extends InstrumentDTO> freeInstruments = getAllFreeInstruments(true);
        try {
            instrumentDb.findAllFreeInstruments(true);
            RentalDTO rental = new Rental(studentId, instrumentId, freeInstruments);
            rentalDb.createRental(rental.getStudentId(), rental.getInstrumentId());
        } catch (SoundgoodDBException | RejectedException e) {
            throw new RuntimeException(e);
        }
    }


    private void commitOngoingTransaction(String failureMsg) throws InstrumentException {
        try {
            instrumentDb.commit();
        } catch (SoundgoodDBException bdbe) {
            throw new InstrumentException(failureMsg, bdbe);
        }
    }
}
