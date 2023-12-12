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

package se.kth.iv1351.soundgoodjdbc.view;

/**
 * Defines all commands that can be performed by a user of the chat application.
 */
public enum Command {
    /**
    * Lists all instruments that are currently rented out.
    */
    RENTED_INSTRUMENTS,

    /**
     * Lists all available instruments
     * the command available_instruments [INSTRUMENT NAME]
     * lists all available instruments of type [INSTRUMENT NAME]
     */
    AVAILABLE_INSTRUMENTS,

    /**
     * Student i rents instrument j (example "rent_instrument 3 3")
     */
    RENT_INSTRUMENT,

    /**
     * Terminates rental i, where i is "rental_id (example terminate_rental 3)"
     * 3 has to be an active rental
     */
    TERMINATE_RENTAL,

    /**
     * Lists all commands.
     */
    HELP,

    /**
     * Leave the chat application.
     */
    QUIT,
    /**
     * None of the valid commands above was specified.
     */
    ILLEGAL_COMMAND
}
