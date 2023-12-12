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

package se.kth.iv1351.soundgoodjdbc.model;

/**
 * An account in the bank.
 */
public class Instrument implements InstrumentDTO {
    private int instrumentPrice;
    private String instrumentType;
    private String instrumentBrand;
    private int instrumentID;


    /**
     * Creates an instrument with same attributes as an instrument in the database
     * @param instrumentBrand  Brand of instrument.
     * @param instrumentType Type of instrument
     * @param instrumentPrice  Rental price of the instrument.
     */
    public Instrument(String instrumentBrand, String instrumentType, int instrumentPrice, int instrumentID) {
        this.instrumentBrand = instrumentBrand;
        this.instrumentType = instrumentType;
        this.instrumentPrice = instrumentPrice;
        this.instrumentID = instrumentID;
    }

    /**
     * @return Brand of instrument
     */
    public String getInstrumentBrand() {
        return instrumentBrand;
    }

    /**
     * @return Price of instrument
     */
    public int getInstrumentPrice() {
        return instrumentPrice;
    }

    /**
     * @return Type of instrument
     */
    public String getInstrumentType() {
        return instrumentType;
    }

    /**
     * @return The unique id of instrument
     */
    public int getInstrumentID() {return instrumentID;}

}
