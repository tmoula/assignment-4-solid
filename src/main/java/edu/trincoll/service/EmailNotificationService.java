package edu.trincoll.service;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import edu.trincoll.model.Book;
import edu.trincoll.model.Member;

@Service
public class EmailNotificationService implements NotificationService {
    
    @Override
    public void sendCheckoutNotification(Member member, Book book, LocalDate dueDate) {
        // For now, using System.out.println
        // In production, this would send an actual email
        System.out.println("CHECKOUT NOTIFICATION:");
        System.out.println("To: " + member.getEmail());
        System.out.println("Book: " + book.getTitle() + " by " + book.getAuthor());
        System.out.println("Due Date: " + dueDate);
        System.out.println("---");
    }
    
    @Override
    public void sendReturnNotification(Member member, Book book, double lateFee) {
        // For now, using System.out.println
        // In production, this would send an actual email
        System.out.println("RETURN NOTIFICATION:");
        System.out.println("To: " + member.getEmail());
        System.out.println("Book Returned: " + book.getTitle());
        if (lateFee > 0) {
            System.out.println("Late Fee: $" + String.format("%.2f", lateFee));
        } else {
            System.out.println("Returned on time - no late fee");
        }
        System.out.println("---");
    }
}