// Taha
package edu.trincoll.service;

import edu.trincoll.model.Book;
import edu.trincoll.service.report.ReportRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AI Collaboration Summary:
 *
 * Team Members and Contributions:
 * - Daniel: TODOs 1, 2, 3
 * - Varvara:   TODOs 4, 5
 * - Taha Moula:  TODOs 6, 7, 8
 *
 * AI Tools Used: ChatGPT
 *
 * How AI Helped:
 * - Proposed the SearchFacade + BookSearchService split (SRP/ISP) and a safe adapter in LibraryService to keep legacy tests passing.
 * - Suggested ReportGenerator strategy with a ReportRegistry selector (OCP/LSP), plus unit tests for each generator and the registry.
 * - Helped design LibraryFacade and an H2-backed @SpringBootTest integration test.
 * - Debugged build issues (constructor wiring, NullPointer in tests, JaCoCo gaps) and showed how to enable/inspect the H2 console.
 *
 * What We Learned:
 * - SRP/ISP: Small, focused services are trivial to mock and compose.
 * - OCP: Strategies + a small registry/factory remove if/else and make adding behaviors non-breaking.
 * - DIP: Depending on abstractions (interfaces) + constructor injection makes testing and substitution easy.
 * - Testing: A thin Facade + strategies are easy to unit test; one Spring Boot IT proves persistence/coordination end-to-end.
 */
@Component
public class LibraryFacade {
    private final BookSearchService bookSearchService;
    private final SearchFacade searchFacade;
    private final ReportRegistry reportRegistry;

    public LibraryFacade(BookSearchService bookSearchService,
                         SearchFacade searchFacade,
                         ReportRegistry reportRegistry) {
        this.bookSearchService = bookSearchService;
        this.searchFacade = searchFacade;
        this.reportRegistry = reportRegistry;
    }

    // Simplified APIs for controllers
    public List<Book> searchByTitle(String title) { return bookSearchService.searchByTitle(title); }
    public List<Book> searchByAuthor(String author) { return bookSearchService.searchByAuthor(author); }
    public List<Book> search(String term, String type) { return searchFacade.search(term, type); }
    public String generateReport(String type) { return reportRegistry.get(type).generateReport(); }
}

