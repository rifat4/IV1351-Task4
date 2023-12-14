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

package se.kth.iv1351.soundgoodjdbc.controller;

import java.util.ArrayList;
import java.util.List;

import se.kth.iv1351.soundgoodjdbc.integration.InstrumentDAO;
import se.kth.iv1351.soundgoodjdbc.integration.SoundgoodDBException;
import se.kth.iv1351.soundgoodjdbc.integration.RentalDAO;
import se.kth.iv1351.soundgoodjdbc.model.*;

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
     * Lists all non rented instruments in the schoolDB
     *
     * @return A list containing all available instruments, if the list is empty there are no
     * available instruments.
     * @throws InstrumentException If unable to retrieve accounts.
     * value true if view is gotten for update
     */
    public List<? extends InstrumentDTO> getAllAvailableInstruments() throws InstrumentException {
        try {
            return instrumentDb.findAllAvailableInstruments(false);
        } catch (Exception e) {
            throw new InstrumentException("Unable to list available instruments.", e);
        }
    }

    /**
     * Lists all non rented instruments in the schoolDB
     *
     * @return A list containing all instruments that are currently rented out, if the list is empty there are no
     * rented instruments.
     * @throws InstrumentException If unable to retrieve accounts.
     *
     */
    public List<? extends RentalDTO> getAllRentedInstruments() throws InstrumentException {
        try {
            return rentalDb.findAllRentedInstruments();
        } catch(Exception e){
            throw new InstrumentException("Unable to list all rented instruments.", e);
        }
    }

    /**
     * Terminates a rental
     *
     * @throws RejectedException If unable to terminate rental.
     *
     */
    public void terminateRental(int rentalId) throws RejectedException {
        try {
            rentalDb.updateRental(rentalId);
        } catch(Exception e){
            throw new RejectedException("Unable to terminate rental. ", e);
        }
    }

    /**
     * Lists all instruments of a specific type
     * 
     * @param instrumentType The type of instrument
     * @return A list of all available instruments of that type
     * @throws InstrumentException If unable to list of instruments
     */
    public List<? extends InstrumentDTO> getAllAvailableInstrumentType(String instrumentType) throws InstrumentException {
        if (instrumentType == null) {
            return new ArrayList<>();
        }
        try {
            return instrumentDb.findAvailableInstrumentsOfType(instrumentType);
        } catch (Exception e) {
            throw new InstrumentException("Could not search for account.", e);
        }
    }

    /**
     * Student rents instrument
     *
     * @param studentId the id of the student
     * @param instrumentId the id of the instrument to be rented
     * @throws RejectedException If unable to rent instrument.
     */
    public void rentInstrument(int studentId, int instrumentId) throws RejectedException, SoundgoodDBException {
        List<? extends InstrumentDTO> availableInstruments;
        String failureMsg = "Failed to create rental: ";
        try {
            availableInstruments = instrumentDb.findAllAvailableInstruments(true);
            RentalDTO rental = new Rental(studentId, instrumentId, availableInstruments);
            rentalDb.createRental(rental.getStudentId(), rental.getInstrumentId());
        } catch (SoundgoodDBException | RejectedException e) {
            instrumentDb.commit();
            throw new RejectedException(failureMsg,e);
        }
    }

}
