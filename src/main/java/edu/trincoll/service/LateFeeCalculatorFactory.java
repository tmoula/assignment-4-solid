package edu.trincoll.service;

//Edited by Taha
import edu.trincoll.model.MembershipType;
import org.springframework.stereotype.Component;

@Component
public class LateFeeCalculatorFactory {

    private final RegularLateFeeCalculator regularLateFeeCalculator;
    private final PremiumLateFeeCalculator premiumLateFeeCalculator;
    private final StudentLateFeeCalculator studentLateFeeCalculator;

    public LateFeeCalculatorFactory(RegularLateFeeCalculator regularLateFeeCalculator,
                                    PremiumLateFeeCalculator premiumLateFeeCalculator,
                                    StudentLateFeeCalculator studentLateFeeCalculator) {
        this.regularLateFeeCalculator = regularLateFeeCalculator;
        this.premiumLateFeeCalculator = premiumLateFeeCalculator;
        this.studentLateFeeCalculator = studentLateFeeCalculator;
    }

    public LateFeeCalculator getCalculatorFor(MembershipType membershipType) {
        return switch (membershipType) {
            case REGULAR -> regularLateFeeCalculator;
            case PREMIUM -> premiumLateFeeCalculator;
            case STUDENT -> studentLateFeeCalculator;
        };
    }
}
