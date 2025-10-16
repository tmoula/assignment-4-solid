package edu.trincoll.service;

import org.springframework.stereotype.Component;

@Component("premiumLateFeeCalculator")
public class PremiumLateFeeCalculator implements LateFeeCalculator {
    @Override
    public double calculateLateFee(long daysLate) {
        return 0.0;
    }
}
