package edu.trincoll.service;

import edu.trincoll.model.BookStatus;
import edu.trincoll.repository.BookRepository;
import edu.trincoll.service.report.AvailabilityReportGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityReportGeneratorTest {

    @Mock
    BookRepository bookRepository;

    @Test
    void countsAvailable() {
        when(bookRepository.countByStatus(BookStatus.AVAILABLE)).thenReturn(7L);
        var gen = new AvailabilityReportGenerator(bookRepository);
        assertEquals("Available books: 7", gen.generateReport());
    }
}
