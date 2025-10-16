package edu.trincoll.service;

import org.springframework.stereotype.Component;

@Component("studentLateFeeCalculator")
public class StudentLateFeeCalculator implements LateFeeCalculator {
    private final double ratePerDay = 0.25; // discounted

    @Override
    public double calculateLateFee(long daysLate) {
        if (daysLate <= 0) return 0.0;
        return daysLate * ratePerDay;
    }
}
