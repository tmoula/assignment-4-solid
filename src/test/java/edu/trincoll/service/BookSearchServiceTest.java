package edu.trincoll.service;

import edu.trincoll.model.Book;
import edu.trincoll.repository.BookRepository;
import edu.trincoll.service.BookSearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Taha
@ExtendWith(MockitoExtension.class)
class BookSearchServiceTest {

    @Mock
    BookRepository bookRepository;
    @InjectMocks
    BookSearchService service;

    @Test
    void searchByTitle_delegatesToRepo() {
        when(bookRepository.findByTitleContainingIgnoreCase("java"))
                .thenReturn(List.of(new Book()));
        var res = service.searchByTitle("java");
        assertEquals(1, res.size());
        verify(bookRepository).findByTitleContainingIgnoreCase("java");
    }

    @Test
    void searchByIsbn_wrapsOptional() {
        var book = new Book();
        when(bookRepository.findByIsbn("123")).thenReturn(Optional.of(book));
        var res = service.searchByIsbn("123");
        assertTrue(res.isPresent());
    }
}
