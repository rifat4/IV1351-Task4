package se.kth.iv1351.soundgoodjdbc.model;

import java.util.List;

public class Rental implements RentalDTO {

    private final int instrumentId;
    private final int instrumentPrice;
    private final String instrumentType;
    private final int studentId;
    private final int rentalId;

    /**
     * Creates an instrument with same attributes as an instrument in the database
     * @param instrumentId  ID of instrument
     * @param instrumentType Type of instrument
     * @param instrumentPrice  Rental price of the instrument.
     * @param studentId ID of student
     * @param rentalId ID of rental
     */
    public Rental(int instrumentId, int instrumentPrice, String instrumentType, int studentId, int rentalId) {
        this.instrumentId = instrumentId;
        this.instrumentPrice = instrumentPrice;
        this.instrumentType = instrumentType;
        this.studentId = studentId;
        this.rentalId = rentalId;
    }

    /**
     * Creates an instrument with same attributes as an instrument in the database
     * @param instrumentId  ID of instrument
     * @param studentId ID of student
     * @throws RejectedException if instrument is not available
     */
    public Rental(int studentId, int instrumentId, List<? extends InstrumentDTO> freeInstruments) throws RejectedException {
        for (InstrumentDTO instrument : freeInstruments) {
            if (instrument.getInstrumentID() == instrumentId) {
                this.instrumentId = instrumentId;
                this.instrumentPrice = instrument.getInstrumentPrice();
                this.instrumentType = instrument.getInstrumentType();
                this.studentId = studentId;
                this.rentalId = -1;
                return;
            }
        }
        throw new RejectedException("Instrument is not available to be rented " + this);
    }

    /**
     * @return Brand of instrument ID
     */
    @Override
    public int getInstrumentId() {
        return instrumentId;
    }

    /**
     * @return price of instrument
     */
    @Override
    public int getInstrumentPrice() {
        return instrumentPrice;
    }

    /**
     * @return Type of instrument
     */
    @Override
    public String getInstrumentType() {
        return instrumentType;
    }

    /**
     * @return ID of student
     */
    @Override
    public int getStudentId() {
        return studentId;
    }

    /**
     * @return ID of rental
     */
    @Override
    public int getRentalId() {
        return rentalId;
    }
}
