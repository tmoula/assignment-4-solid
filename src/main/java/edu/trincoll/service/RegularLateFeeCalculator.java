//Taha
package edu.trincoll.service;

import org.springframework.stereotype.Component;

@Component
public class RegularLateFeeCalculator implements LateFeeCalculator {
    @Override
    public double calculateLateFee(long daysLate) {
        return daysLate * 0.50;
    }
}