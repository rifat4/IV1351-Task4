package se.kth.iv1351.soundgoodjdbc.model;

import java.util.List;

public class Rental implements RentalDTO {

    private int instrumentId;
    private int instrumentPrice;
    private String instrumentType;
    private int studentId;
    private int rentalId;

    public Rental(int instrumentId, int instrumentPrice, String instrumentType, int studentId, int rentalId) {
        this.instrumentId = instrumentId;
        this.instrumentPrice = instrumentPrice;
        this.instrumentType = instrumentType;
        this.studentId = studentId;
        this.rentalId = rentalId;
    }

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
        throw new RejectedException("Instrument is not free to be rented " + this);
    }


    @Override
    public int getInstrumentId() {
        return instrumentId;
    }

    @Override
    public int getInstrumentPrice() {
        return instrumentPrice;
    }

    @Override
    public String getInstrumentType() {
        return instrumentType;
    }

    @Override
    public int getStudentId() {
        return studentId;
    }

    @Override
    public int getRentalId() {
        return rentalId;
    }
}
