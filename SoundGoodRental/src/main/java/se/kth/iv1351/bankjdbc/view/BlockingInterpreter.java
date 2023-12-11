/*
 * The MIT License
 *
 * Copyright 2017 Leif Lindbäck <leifl@kth.se>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
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

package se.kth.iv1351.bankjdbc.view;

import java.util.List;
import java.util.Scanner;

import se.kth.iv1351.bankjdbc.controller.Controller;
import se.kth.iv1351.bankjdbc.model.InstrumentDTO;
import se.kth.iv1351.bankjdbc.model.RentalDTO;

/**
 * Reads and interprets user commands. This command interpreter is blocking, the user
 * interface does not react to user input while a command is being executed.
 */
public class BlockingInterpreter {
    private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private Controller ctrl;
    private boolean keepReceivingCmds = false;

    /**
     * Creates a new instance that will use the specified controller for all operations.
     * 
     * @param ctrl The controller used by this instance.
     */
    public BlockingInterpreter(Controller ctrl) {
        this.ctrl = ctrl;
    }

    /**
     * Stops the commend interpreter.
     */
    public void stop() {
        keepReceivingCmds = false;
    }

    /**
     * Interprets and performs user commands. This method will not return until the
     * UI has been stopped. The UI is stopped either when the user gives the
     * "quit" command, or when the method <code>stop()</code> is called.
     */
    public void handleCmds() {
        keepReceivingCmds = true;
        while (keepReceivingCmds) {
            try {
                CmdLine cmdLine = new CmdLine(readNextLine());
                switch (cmdLine.getCmd()) {
                    case HELP:
                        for (Command command : Command.values()) {
                            if (command == Command.ILLEGAL_COMMAND) {
                                continue;
                            }
                            System.out.println(command.toString().toLowerCase());
                        }
                        break;
                    case QUIT:
                        keepReceivingCmds = false;
                        break;
                    case AVAILABLE_INSTRUMENTS:
                        List<? extends InstrumentDTO> instruments = null;
                        if (cmdLine.getParameter(0).equals("")) {
                            instruments = ctrl.getAllAvailableInstruments();
                        } else {
                            instruments = ctrl.getAllAvailableInstrumentType(cmdLine.getParameter(0));
                        }
                        for (InstrumentDTO instrument : instruments) {
                            System.out.println("Instrument Brand: " + instrument.getInstrumentBrand() + ", "
                                             + "Instrument Type: " + instrument.getInstrumentType() + ", "
                                             + "Instrument Price: " + instrument.getInstrumentPrice() + ", "
                                             + "Instrument ID: " + instrument.getInstrumentID());
                        }
                        break;
                    case RENTED_INSTRUMENTS:
                        List<? extends RentalDTO> rentals = null;
                        if (cmdLine.getParameter(0).equals("")) {
                            rentals = ctrl.getAllRentedInstruments();
                        } else {
                            //rentals = ctrl.getAllAvailableInstrumentType(cmdLine.getParameter(0));
                        }
                        for (RentalDTO rental : rentals) {
                            System.out.println("Instrument ID: " + rental.getInstrumentId() + ", "
                                    + "Instrument Price: " + rental.getInstrumentPrice() + ", "
                                    + "Instrument Type: " + rental.getInstrumentType() + ", "
                                    + "Student ID: " + rental.getStudentId() + ", "
                                    + " Rental ID: " + rental.getRentalId());
                        }

                        break;
                    case RENT_INSTRUMENT:
                        if(cmdLine.getParameter(0).equals("")){
                            System.out.println("Bad input give parameter student id followed by instrument id");
                        } else {
                            int student_id = Integer.parseInt(cmdLine.getParameter(0));
                            int instrument_id = Integer.parseInt(cmdLine.getParameter(1));
                            ctrl.rentInstrument(student_id, instrument_id);
                        }
                        break;
                    case TERMINATE_RENTAL:
                        if(cmdLine.getParameter(0).equals("")){
                            System.out.println("Bad input give parameter rental_id");
                        } else {
                            int rental_id = Integer.parseInt(cmdLine.getParameter(0));
                            ctrl.terminateRental(rental_id);
                        }
                        break;
                    default:
                        System.out.println("illegal command");
                }
            } catch (Exception e) {
                System.out.println("Operation failed");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String readNextLine() {
        System.out.print(PROMPT);
        return console.nextLine();
    }
}
