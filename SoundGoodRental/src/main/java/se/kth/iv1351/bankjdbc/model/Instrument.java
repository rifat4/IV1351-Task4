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

package se.kth.iv1351.bankjdbc.model;

/**
 * An account in the bank.
 */
public class Instrument implements InstrumentDTO {
    private int instrumentPrice;
    private String instrumentType;
    private String instrumentBrand;

    private int instrumentID;

    /**
     * Creates an account for the specified holder with the balance zero. The account
     * number is unspecified.
     *
     * @param instrumentType The account holder's holderName.
     * @param bankDB     The DAO used to store updates to the database.
     */
    public Instrument(String instrumentType) {
        this(null, instrumentType, 0, 0);
    }

    /**
     * Creates an account for the specified holder with the specified balance. The
     * account number is unspecified.
     *
     * @param instrumentType The account holder's holderName.
     * @param balance    The initial balance.
     */
    public Instrument(String instrumentType, int balance, int instrumentID) {
        this(null, instrumentType, balance, instrumentID);
    }

    /**
     * Creates an account for the specified holder with the specified instrumentPrice and account
     * number.
     *
     * @param instrumentBrand     The account number.
     * @param instrumentType The account holder's instrumentType.
     * @param instrumentPrice    The initial instrumentPrice.
     */
    public Instrument(String instrumentBrand, String instrumentType, int instrumentPrice, int instrumentID) {
        this.instrumentBrand = instrumentBrand;
        this.instrumentType = instrumentType;
        this.instrumentPrice = instrumentPrice;
        this.instrumentID = instrumentID;
    }

    /**
     * @return The account number.
     */
    public String getInstrumentBrand() {
        return instrumentBrand;
    }

    /**
     * @return The balance.
     */
    public int getInstrumentPrice() {
        return instrumentPrice;
    }

    /**
     * @return The holder's name.
     */
    public String getInstrumentType() {
        return instrumentType;
    }

    public int getInstrumentID() {return instrumentID;}

    /**
     * Deposits the specified amount.
     *
     * @param amount The amount to deposit.
     * @throws InstrumentException If the specified amount is negative, or if unable to
     *                          perform the update.
     */
    public void deposit(int amount) throws RejectedException {
        if (amount < 0) {
            throw new RejectedException("Tried to deposit negative value, illegal value: "
                                        + amount + ", account: " + this);
        }
        instrumentPrice = instrumentPrice + amount;
    }

    /**
     * Withdraws the specified amount.
     *
     * @param amount The amount to withdraw.
     * @throws InstrumentException If the specified amount is negative, if the amount
     *                          is larger than the balance, or if unable to perform
     *                          the update.
     */
    public void withdraw(int amount) throws RejectedException {
        if (amount < 0) {
            throw new RejectedException("Tried to withdraw negative value, illegal value: "
                                        + amount + ", account: " + this);
        }
        if (instrumentPrice - amount < 0) {
            throw new RejectedException("Overdraft attempt, illegal value: " + amount
                                        + ", account: " + this);
        }
        instrumentPrice = instrumentPrice - amount;
    }

    /**
     * @return A string representation of all fields in this object.
     */
    @Override
    public String toString() {
        StringBuilder stringRepresentation = new StringBuilder();
        stringRepresentation.append("Account: [");
        stringRepresentation.append("account number: ");
        stringRepresentation.append(instrumentBrand);
        stringRepresentation.append(", holder: ");
        stringRepresentation.append(instrumentType);
        stringRepresentation.append(", balance: ");
        stringRepresentation.append(instrumentPrice);
        stringRepresentation.append("]");
        return stringRepresentation.toString();
    }
}
