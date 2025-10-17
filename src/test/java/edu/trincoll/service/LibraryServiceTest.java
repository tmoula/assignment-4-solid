package edu.trincoll.service;

import edu.trincoll.model.Book;
import edu.trincoll.model.BookStatus;
import edu.trincoll.model.Member;
import edu.trincoll.model.MembershipType;
import edu.trincoll.repository.BookRepository;
import edu.trincoll.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LibraryService unit tests (Mockito)")
class LibraryServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    // Will use the 2-arg constructor (bookRepository, memberRepository)
    @InjectMocks
    private LibraryService libraryService;

    private Book availableBook;
    private Member regularMember;
    private Member premiumMember;
    private Member studentMember;

    @BeforeEach
    void setUp() {
        availableBook = new Book();
        availableBook.setId(1L);
        availableBook.setIsbn("978-0-123456-78-9");
        availableBook.setTitle("Clean Code");
        availableBook.setAuthor("Robert Martin");
        availableBook.setPublicationDate(LocalDate.of(2008, 8, 1));
        availableBook.setStatus(BookStatus.AVAILABLE);

        regularMember = new Member();
        regularMember.setId(1L);
        regularMember.setName("John Doe");
        regularMember.setEmail("john@example.com");
        regularMember.setMembershipType(MembershipType.REGULAR);
        regularMember.setBooksCheckedOut(0);

        premiumMember = new Member();
        premiumMember.setId(2L);
        premiumMember.setName("Jane Smith");
        premiumMember.setEmail("jane@example.com");
        premiumMember.setMembershipType(MembershipType.PREMIUM);
        premiumMember.setBooksCheckedOut(0);

        studentMember = new Member();
        studentMember.setId(3L);
        studentMember.setName("Bob Student");
        studentMember.setEmail("bob@example.com");
        studentMember.setMembershipType(MembershipType.STUDENT);
        studentMember.setBooksCheckedOut(0);
    }
    // 1) searchBooks: author branch
    @Test
    @DisplayName("Should search books by author")
    void shouldSearchBooksByAuthor() {
        when(bookRepository.findByAuthor("Robert Martin"))
                .thenReturn(List.of(availableBook));

        var results = libraryService.searchBooks("Robert Martin", "author");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getAuthor()).isEqualTo("Robert Martin");
    }

    // 2) searchBooks: isbn branch
    @Test
    @DisplayName("Should search books by ISBN")
    void shouldSearchBooksByIsbn() {
        when(bookRepository.findByIsbn("978-0-123456-78-9"))
                .thenReturn(Optional.of(availableBook));

        var results = libraryService.searchBooks("978-0-123456-78-9", "isbn");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getIsbn()).isEqualTo("978-0-123456-78-9");
    }

    // 3) returnBook: early return when not checked out
    @Test
    @DisplayName("Should not return a book that is not checked out")
    void shouldNotReturnWhenBookNotCheckedOut() {
        availableBook.setStatus(BookStatus.AVAILABLE);
        when(bookRepository.findByIsbn(availableBook.getIsbn()))
                .thenReturn(Optional.of(availableBook));

        String msg = libraryService.returnBook(availableBook.getIsbn());

        assertThat(msg).isEqualTo("Book is not checked out");
        verify(bookRepository, never()).save(any());
        verify(memberRepository, never()).save(any());
    }

    // 4) checkoutBook: unknown membership type -> exception branch
    @Test
    @DisplayName("Should throw for unknown membership type")
    void shouldThrowForUnknownMembershipType() {
        Member weird = new Member();
        weird.setEmail("weird@example.com");
        weird.setMembershipType(null); // triggers else branch

        when(bookRepository.findByIsbn(availableBook.getIsbn()))
                .thenReturn(Optional.of(availableBook));
        when(memberRepository.findByEmail("weird@example.com"))
                .thenReturn(Optional.of(weird));

        assertThatThrownBy(() ->
                libraryService.checkoutBook(availableBook.getIsbn(), "weird@example.com"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Unknown membership type");
    }
    // ---- Coverage for legacy generateReport() paths (no ReportRegistry injected) ----
    @Test
    void generateReport_overdue_available_members() {
        // overdue
        Book overdue = new Book();
        overdue.setTitle("Over");
        overdue.setAuthor("Due");
        overdue.setIsbn("O-1");
        overdue.setPublicationDate(LocalDate.now().minusYears(1));
        overdue.setStatus(BookStatus.CHECKED_OUT);
        overdue.setCheckedOutBy("x@y.com");
        overdue.setDueDate(LocalDate.now().minusDays(3));

        when(bookRepository.findByDueDateBefore(any(LocalDate.class)))
                .thenReturn(List.of(overdue));

        // available
        when(bookRepository.countByStatus(BookStatus.AVAILABLE)).thenReturn(5L);
        // members
        when(memberRepository.count()).thenReturn(2L);

        String overdueReport = libraryService.generateReport("overdue");
        String availableReport = libraryService.generateReport("available");
        String membersReport = libraryService.generateReport("members");

        assertThat(overdueReport).contains("OVERDUE BOOKS REPORT")
                .contains("Due");
        assertThat(availableReport).isEqualTo("Available books: 5");
        assertThat(membersReport).isEqualTo("Total members: 2");
    }

    @Test
    @DisplayName("Should checkout book successfully for regular member")
    void shouldCheckoutBookForRegularMember() {
        when(bookRepository.findByIsbn(availableBook.getIsbn()))
                .thenReturn(Optional.of(availableBook));
        when(memberRepository.findByEmail(regularMember.getEmail()))
                .thenReturn(Optional.of(regularMember));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));
        when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

        String result = libraryService.checkoutBook(availableBook.getIsbn(), regularMember.getEmail());

        assertThat(result).contains("Book checked out successfully").contains("Due date");
        verify(bookRepository).save(argThat(book ->
                book.getStatus() == BookStatus.CHECKED_OUT
                        && regularMember.getEmail().equals(book.getCheckedOutBy())
                        && book.getDueDate().equals(LocalDate.now().plusDays(14))
        ));
        verify(memberRepository).save(argThat(member ->
                member.getBooksCheckedOut() == 1
        ));
    }

    @Test
    @DisplayName("Should apply correct loan period for premium member")
    void shouldApplyPremiumLoanPeriod() {
        when(bookRepository.findByIsbn(availableBook.getIsbn()))
                .thenReturn(Optional.of(availableBook));
        when(memberRepository.findByEmail(premiumMember.getEmail()))
                .thenReturn(Optional.of(premiumMember));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));
        when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

        libraryService.checkoutBook(availableBook.getIsbn(), premiumMember.getEmail());

        verify(bookRepository).save(argThat(book ->
                book.getDueDate().equals(LocalDate.now().plusDays(30))
        ));
    }

    @Test
    @DisplayName("Should enforce checkout limit for regular member")
    void shouldEnforceCheckoutLimitForRegularMember() {
        regularMember.setBooksCheckedOut(3); // at limit
        when(bookRepository.findByIsbn(availableBook.getIsbn()))
                .thenReturn(Optional.of(availableBook));
        when(memberRepository.findByEmail(regularMember.getEmail()))
                .thenReturn(Optional.of(regularMember));

        String result = libraryService.checkoutBook(availableBook.getIsbn(), regularMember.getEmail());

        assertThat(result).isEqualTo("Member has reached checkout limit");
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should not checkout unavailable book")
    void shouldNotCheckoutUnavailableBook() {
        availableBook.setStatus(BookStatus.CHECKED_OUT);
        when(bookRepository.findByIsbn(availableBook.getIsbn()))
                .thenReturn(Optional.of(availableBook));
        when(memberRepository.findByEmail(regularMember.getEmail()))
                .thenReturn(Optional.of(regularMember));

        String result = libraryService.checkoutBook(availableBook.getIsbn(), regularMember.getEmail());

        assertThat(result).isEqualTo("Book is not available");
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when book not found")
    void shouldThrowExceptionWhenBookNotFound() {
        when(bookRepository.findByIsbn(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                libraryService.checkoutBook("invalid-isbn", regularMember.getEmail()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book not found");
    }

    @Test
    @DisplayName("Should return book successfully")
    void shouldReturnBookSuccessfully() {
        availableBook.setStatus(BookStatus.CHECKED_OUT);
        availableBook.setCheckedOutBy(regularMember.getEmail());
        availableBook.setDueDate(LocalDate.now().plusDays(7));

        when(bookRepository.findByIsbn(availableBook.getIsbn()))
                .thenReturn(Optional.of(availableBook));
        when(memberRepository.findByEmail(regularMember.getEmail()))
                .thenReturn(Optional.of(regularMember));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));
        when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

        regularMember.setBooksCheckedOut(1);

        String result = libraryService.returnBook(availableBook.getIsbn());

        assertThat(result).isEqualTo("Book returned successfully");
        verify(bookRepository).save(argThat(book ->
                book.getStatus() == BookStatus.AVAILABLE
                        && book.getCheckedOutBy() == null
                        && book.getDueDate() == null
        ));
        verify(memberRepository).save(argThat(member ->
                member.getBooksCheckedOut() == 0
        ));
    }

    @Test
    @DisplayName("Should calculate late fee for regular member")
    void shouldCalculateLateFeeForRegularMember() {
        availableBook.setStatus(BookStatus.CHECKED_OUT);
        availableBook.setCheckedOutBy(regularMember.getEmail());
        availableBook.setDueDate(LocalDate.now().minusDays(5)); // 5 days late

        when(bookRepository.findByIsbn(availableBook.getIsbn()))
                .thenReturn(Optional.of(availableBook));
        when(memberRepository.findByEmail(regularMember.getEmail()))
                .thenReturn(Optional.of(regularMember));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));
        when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

        regularMember.setBooksCheckedOut(1);

        String result = libraryService.returnBook(availableBook.getIsbn());

        assertThat(result).contains("Late fee: $2.50"); // 5 * $0.50
    }

    @Test
    @DisplayName("Should not charge late fee for premium member")
    void shouldNotChargeLateFeeForPremiumMember() {
        availableBook.setStatus(BookStatus.CHECKED_OUT);
        availableBook.setCheckedOutBy(premiumMember.getEmail());
        availableBook.setDueDate(LocalDate.now().minusDays(5)); // 5 days late

        when(bookRepository.findByIsbn(availableBook.getIsbn()))
                .thenReturn(Optional.of(availableBook));
        when(memberRepository.findByEmail(premiumMember.getEmail()))
                .thenReturn(Optional.of(premiumMember));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));
        when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

        premiumMember.setBooksCheckedOut(1);

        String result = libraryService.returnBook(availableBook.getIsbn());

        assertThat(result).isEqualTo("Book returned successfully");
        assertThat(result).doesNotContain("Late fee");
    }

    @Test
    @DisplayName("Should search books by title")
    void shouldSearchBooksByTitle() {
        when(bookRepository.findByTitleContainingIgnoreCase("Clean"))
                .thenReturn(List.of(availableBook));

        var results = libraryService.searchBooks("Clean", "title");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Clean Code");
    }

    @Test
    @DisplayName("Should throw exception for invalid search type")
    void shouldThrowExceptionForInvalidSearchType() {
        assertThatThrownBy(() ->
                libraryService.searchBooks("test", "invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid search type");
    }

    @Test
    @DisplayName("Student checkout uses 21-day loan period")
    void checkoutBook_student_hasCorrectLoanPeriod() {
        when(bookRepository.findByIsbn(availableBook.getIsbn()))
                .thenReturn(Optional.of(availableBook));
        when(memberRepository.findByEmail(studentMember.getEmail()))
                .thenReturn(Optional.of(studentMember));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));
        when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

        String msg = libraryService.checkoutBook(availableBook.getIsbn(), studentMember.getEmail());

        assertTrue(msg.contains("Due date"));
        verify(bookRepository).save(argThat(book ->
                book.getStatus() == BookStatus.CHECKED_OUT
                        && studentMember.getEmail().equals(book.getCheckedOutBy())
                        && book.getDueDate().equals(LocalDate.now().plusDays(21))
        ));
    }
}
