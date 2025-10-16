package edu.trincoll.service;

import edu.trincoll.model.Member;

public class RegularCheckoutPolicy implements CheckoutPolicy {
    
    @Override
    public int getMaxBooks() {
        return 3;
    }
    
    @Override
    public int getLoanPeriodDays() {
        return 14;
    }
    
    @Override
    public boolean canCheckout(Member member) {
        return member.getBooksCheckedOut() < getMaxBooks();
    }
}