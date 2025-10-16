package edu.trincoll.service;

import com.example.library.model.MembershipType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LateFeeCalculatorFactory {

    private final Map<MembershipType, LateFeeCalculator> strategies;

    public LateFeeCalculatorFactory(
            LateFeeCalculator regularLateFeeCalculator,
            LateFeeCalculator premiumLateFeeCalculator,
            LateFeeCalculator studentLateFeeCalculator
    ) {
        // Construct a small map. Uses the Component names above or type injection ordering.
        this.strategies = Map.of(
            MembershipType.REGULAR, regularLateFeeCalculator,
            MembershipType.PREMIUM, premiumLateFeeCalculator,
            MembershipType.STUDENT, studentLateFeeCalculator
        );
    }

    public LateFeeCalculator getCalculatorFor(MembershipType membershipType) {
        return strategies.getOrDefault(membershipType, strategies.get(MembershipType.REGULAR));
    }
}
