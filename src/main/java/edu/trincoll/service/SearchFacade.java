package edu.trincoll.service;

import edu.trincoll.model.Book;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchFacade {
    private final BookSearchService bookSearchService;

    public SearchFacade(BookSearchService bookSearchService) {
        this.bookSearchService = bookSearchService;
    }

    public List<Book> search(String searchTerm, String searchType) {
        if (searchType == null) {
            throw new IllegalArgumentException("Invalid search type");
        }
        switch (searchType.toLowerCase()) {
            case "title":
                return bookSearchService.searchByTitle(searchTerm);
            case "author":
                return bookSearchService.searchByAuthor(searchTerm);
            case "isbn":
                return bookSearchService.searchByIsbn(searchTerm)
                        .map(List::of)
                        .orElse(List.of());
            default:
                throw new IllegalArgumentException("Invalid search type");
        }
    }
}
