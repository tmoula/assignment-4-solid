package edu.trincoll.service;

import edu.trincoll.model.Book;
import edu.trincoll.model.BookStatus;
import edu.trincoll.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class LibraryFacadeIT {

    @Autowired LibraryFacade facade;
    @Autowired BookRepository bookRepository;

    @Test
    void availableReport_worksAgainstH2() {
        Book b = new Book();
        b.setTitle("X");
        b.setAuthor("Y");
        b.setStatus(BookStatus.AVAILABLE);

        b.setIsbn("ISBN-123");
        b.setPublicationDate(java.time.LocalDate.now());

        bookRepository.save(b);

        String out = facade.generateReport("available");
        assertTrue(out.startsWith("Available books: "));
    }
}

