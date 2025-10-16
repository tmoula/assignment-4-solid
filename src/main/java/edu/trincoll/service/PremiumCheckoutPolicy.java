package edu.trincoll.service;

import edu.trincoll.model.Member;

public class PremiumCheckoutPolicy implements CheckoutPolicy {
    
    @Override
    public int getMaxBooks() {
        return 10;
    }
    
    @Override
    public int getLoanPeriodDays() {
        return 30;
    }
    
    @Override
    public boolean canCheckout(Member member) {
        return member.getBooksCheckedOut() < getMaxBooks();
    }
}