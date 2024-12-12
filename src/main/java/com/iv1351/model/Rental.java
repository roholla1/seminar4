package com.iv1351.model;

import java.time.LocalDateTime;

// rental model, not used
public class Rental {
    private final int studentId;
    private final int instrumentId;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final boolean terminated;

    public Rental(int studentId, int instrumentId, LocalDateTime startDate, LocalDateTime endDate, boolean terminated) {
        this.studentId = studentId;
        this.instrumentId = instrumentId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.terminated = terminated;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getInstrumentId() {
        return instrumentId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public boolean isTerminated() {
        return terminated;
    }
}
