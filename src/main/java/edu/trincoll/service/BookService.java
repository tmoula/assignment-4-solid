package edu.trincoll.service;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import edu.trincoll.model.Book;
import edu.trincoll.model.Member;
import edu.trincoll.repository.BookRepository;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void checkoutBook(Book book, Member member, int loanPeriodDays) {
        book.setCheckedOutBy(member.getEmail());
        book.setDueDate(LocalDate.now().plusDays(loanPeriodDays));
        book.setAvailable(false);
        bookRepository.save(book);
    }

    public void returnBook(Book book) {
        book.setCheckedOutBy(null);
        book.setDueDate(null);
        book.setAvailable(true);
        bookRepository.save(book);
    }

    public boolean isAvailable(Book book) {
        return book.isAvailable();
    }
}
