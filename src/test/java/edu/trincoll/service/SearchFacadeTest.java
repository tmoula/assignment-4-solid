package edu.trincoll.service;

import edu.trincoll.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SearchFacade Unit Tests")
class SearchFacadeTest {

    @Mock
    private BookSearchService bookSearchService;

    @InjectMocks
    private SearchFacade searchFacade;

    private Book book;

    @BeforeEach
    void setup() {
        book = new Book();
        book.setTitle("Mock Book");
    }

    @Test
    void testSearchDelegatesToBookSearchService() {
        when(bookSearchService.searchByTitle("Mock")).thenReturn(List.of(book));
        List<Book> result = searchFacade.search("Mock", "title");
        assertThat(result).hasSize(1);
        verify(bookSearchService).searchByTitle("Mock");
    }

    // 👇 Add this new test at the bottom of the class
    @Test
    void search_throwsOnNullType() {
        assertThatThrownBy(() -> searchFacade.search("x", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid search type");
    }
}
