package edu.trincoll.service;

import edu.trincoll.model.Member;

public class StudentCheckoutPolicy implements CheckoutPolicy {
    
    @Override
    public int getMaxBooks() {
        return 5;
    }
    
    @Override
    public int getLoanPeriodDays() {
        return 21;
    }
    
    @Override
    public boolean canCheckout(Member member) {
        return member.getBooksCheckedOut() < getMaxBooks();
    }
}