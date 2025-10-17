package edu.trincoll.service;

import org.springframework.stereotype.Component;

import edu.trincoll.model.MembershipType;

@Component
public class CheckoutPolicyFactory {
    
    public CheckoutPolicy getPolicyFor(MembershipType type) {
        return switch (type) {
            case REGULAR -> new RegularCheckoutPolicy();
            case PREMIUM -> new PremiumCheckoutPolicy();
            case STUDENT -> new StudentCheckoutPolicy();
        };
    }
}