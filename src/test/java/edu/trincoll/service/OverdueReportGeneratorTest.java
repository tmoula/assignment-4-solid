package edu.trincoll.service;

import edu.trincoll.model.Book;
import edu.trincoll.repository.BookRepository;
import edu.trincoll.service.report.OverdueReportGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OverdueReportGeneratorTest {

    @Mock
    BookRepository bookRepository;

    @Test
    void generatesOverdueReport() {
        var b = new Book();
        b.setTitle("T1"); b.setAuthor("A1");
        b.setDueDate(LocalDate.now().minusDays(1)); b.setCheckedOutBy("x@y.com");

        when(bookRepository.findByDueDateBefore(any(LocalDate.class)))
                .thenReturn(List.of(b));

        var gen = new OverdueReportGenerator(bookRepository);
        var out = gen.generateReport();

        assertTrue(out.contains("OVERDUE BOOKS REPORT"));
        assertTrue(out.contains("T1"));
    }
}
