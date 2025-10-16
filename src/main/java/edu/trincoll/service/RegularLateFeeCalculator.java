package edu.trincoll.service;

public interface LateFeeCalculator {
    /**
     * Calculate fee for given number of days late.
     * @param daysLate non-negative number of days late
     * @return monetary late fee (e.g. in dollars)
     */
    double calculateLateFee(long daysLate);
}
