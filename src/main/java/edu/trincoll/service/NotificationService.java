package edu.trincoll.service;

import java.time.LocalDate;

import edu.trincoll.model.Book;
import edu.trincoll.model.Member;

public interface NotificationService {
    void sendCheckoutNotification(Member member, Book book, LocalDate dueDate);
    void sendReturnNotification(Member member, Book book, double lateFee);
}