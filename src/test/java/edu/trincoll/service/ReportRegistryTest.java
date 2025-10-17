package edu.trincoll.service;

import edu.trincoll.repository.BookRepository;
import edu.trincoll.repository.MemberRepository;
import edu.trincoll.service.report.ReportGenerator;
import edu.trincoll.service.report.ReportRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportRegistryTest {

    @Mock private BookRepository bookRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private SearchFacade searchFacade;
    @Mock private ReportRegistry reportRegistry;
    @Mock private ReportGenerator generator;

    @Test
    void testGenerateReportUsesRegistryWhenInjected() {
        when(reportRegistry.get("custom")).thenReturn(generator);
        when(generator.generateReport()).thenReturn("From registry");

        // Explicitly construct LibraryService with all dependencies, including the mocked registry
        LibraryService service =
                new LibraryService(bookRepository, memberRepository, searchFacade, reportRegistry);

        String result = service.generateReport("custom");

        assertThat(result).isEqualTo("From registry");
        verify(reportRegistry).get("custom");
    }

    @Test
    void resolvesByTypeOrThrows() {
        ReportGenerator foo = new ReportGenerator() {
            @Override public String getType() { return "foo"; }
            @Override public String generateReport() { return "ok"; }
        };

        ReportRegistry reg = new ReportRegistry(List.of(foo));
        assertEquals("ok", reg.get("foo").generateReport());
        assertThrows(IllegalArgumentException.class, () -> reg.get("nope"));
    }
}
