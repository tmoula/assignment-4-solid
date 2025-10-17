Refactoring Report: SOLID Principles
Single Responsibility Principle (SRP)
Violation
The original LibraryService class had multiple responsibilities:

Book operations (checkout, return, availability checking)
Member operations (update checkout counts)
Notifications (sending checkout/return messages)
Search operations (finding books by title, author, ISBN)
Reporting (generating overdue, available, and member reports)

This violates SRP because a class should have only one reason to change. The original LibraryService would need to change if:

Book checkout rules changed
Notification format changed
Search algorithms changed
Report formats changed

Our Solution
We extracted separate services, each with a single responsibility:

BookService - manages book state and operations
MemberService - manages member state and operations
NotificationService - handles all notifications
BookSearchService - performs search operations
ReportGenerator - generates various reports

Code Example
Before:
javapublic class LibraryService {
    public String checkoutBook(String isbn, String memberEmail) {
        // Find book
        Book book = bookRepository.findByIsbn(isbn)...
        
        // Find member
        Member member = memberRepository.findByEmail(memberEmail)...
        
        // Check availability
        if (book.getStatus() != BookStatus.AVAILABLE) {
            return "Book is not available";
        }
        
        // Calculate limits
        int maxBooks;
        int loanPeriodDays;
        if (member.getMembershipType() == MembershipType.REGULAR) {
            maxBooks = 3;
            loanPeriodDays = 14;
        }
        
        // Update book
        book.setStatus(BookStatus.CHECKED_OUT);
        bookRepository.save(book);
        
        // Update member
        member.setBooksCheckedOut(member.getBooksCheckedOut() + 1);
        memberRepository.save(member);
        
        // Send notification
        System.out.println("Sending email to: " + member.getEmail());
        System.out.println("Subject: Book checked out");
    }
}
After:
java@Service
public class BookService {
    private final BookRepository bookRepository;

    public void checkoutBook(Book book, Member member, int loanPeriodDays) {
        // Only book-related operations
        book.setCheckedOutBy(member.getEmail());
        book.setDueDate(LocalDate.now().plusDays(loanPeriodDays));
        book.setAvailable(false);
        bookRepository.save(book);
    }

    public void returnBook(Book book) {
        // Only book-related operations
        book.setCheckedOutBy(null);
        book.setDueDate(null);
        book.setAvailable(true);
        bookRepository.save(book);
    }

    public boolean isAvailable(Book book) {
        return book.isAvailable();
    }
}

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public void incrementCheckoutCount(Member member) {
        member.setBooksCheckedOut(member.getBooksCheckedOut() + 1);
        memberRepository.save(member);
    }

    public void decrementCheckoutCount(Member member) {
        member.setBooksCheckedOut(member.getBooksCheckedOut() - 1);
        memberRepository.save(member);
    }
}
Why This Is Better

Focused responsibility: Each service has one clear purpose
Independent testing: Services can be unit tested in isolation with mocks
Easier maintenance: Changes to book operations don't affect member operations
Better reusability: Services can be composed in different ways
Clear boundaries: Team members can work on different services without conflicts


Open-Closed Principle (OCP)
Violation
The original code used if-else statements to handle different membership types:
javaif (member.getMembershipType() == MembershipType.REGULAR) {
    maxBooks = 3;
    loanPeriodDays = 14;
} else if (member.getMembershipType() == MembershipType.PREMIUM) {
    maxBooks = 10;
    loanPeriodDays = 30;
} else if (member.getMembershipType() == MembershipType.STUDENT) {
    maxBooks = 5;
    loanPeriodDays = 21;
}
This violates OCP because:

Adding a new membership type (e.g., FACULTY, SENIOR) requires modifying existing code
The same if-else logic appears in multiple places (checkout logic, late fee calculation)
The class is not closed for modification when requirements change

Our Solution
We implemented the Strategy pattern with CheckoutPolicy interface:
javapublic interface CheckoutPolicy {
    int getMaxBooks();
    int getLoanPeriodDays();
    boolean canCheckout(Member member);
}
Each membership type has its own policy implementation:
javapublic class RegularCheckoutPolicy implements CheckoutPolicy {
    @Override
    public int getMaxBooks() { return 3; }
    
    @Override
    public int getLoanPeriodDays() { return 14; }
    
    @Override
    public boolean canCheckout(Member member) {
        return member.getBooksCheckedOut() < getMaxBooks();
    }
}

public class PremiumCheckoutPolicy implements CheckoutPolicy {
    @Override
    public int getMaxBooks() { return 10; }
    
    @Override
    public int getLoanPeriodDays() { return 30; }
    
    @Override
    public boolean canCheckout(Member member) {
        return member.getBooksCheckedOut() < getMaxBooks();
    }
}

public class StudentCheckoutPolicy implements CheckoutPolicy {
    @Override
    public int getMaxBooks() { return 5; }
    
    @Override
    public int getLoanPeriodDays() { return 21; }
    
    @Override
    public boolean canCheckout(Member member) {
        return member.getBooksCheckedOut() < getMaxBooks();
    }
}
A factory selects the appropriate policy:
java@Component
public class CheckoutPolicyFactory {
    public CheckoutPolicy getPolicyFor(MembershipType type) {
        return switch (type) {
            case REGULAR -> new RegularCheckoutPolicy();
            case PREMIUM -> new PremiumCheckoutPolicy();
            case STUDENT -> new StudentCheckoutPolicy();
        };
    }
}
Code Example
Before:
java// In LibraryService.checkoutBook()
int maxBooks;
int loanPeriodDays;

if (member.getMembershipType() == MembershipType.REGULAR) {
    maxBooks = 3;
    loanPeriodDays = 14;
} else if (member.getMembershipType() == MembershipType.PREMIUM) {
    maxBooks = 10;
    loanPeriodDays = 30;
} else if (member.getMembershipType() == MembershipType.STUDENT) {
    maxBooks = 5;
    loanPeriodDays = 21;
}

if (member.getBooksCheckedOut() >= maxBooks) {
    return "Member has reached checkout limit";
}
After:
java// In refactored LibraryService.checkoutBook()
CheckoutPolicy policy = checkoutPolicyFactory.getPolicyFor(member.getMembershipType());

if (!policy.canCheckout(member)) {
    return "Member has reached checkout limit";
}

int loanPeriodDays = policy.getLoanPeriodDays();
bookService.checkoutBook(book, member, loanPeriodDays);
Why This Is Better

Open for extension: Add new membership types by creating new policy classes
Closed for modification: Existing code doesn't change when adding new types
Eliminates duplication: Policy logic centralized in one place per type
Testability: Each policy can be tested independently
Runtime flexibility: Policies can be selected dynamically or configured externally


Liskov Substitution Principle (LSP)
How Our Refactoring Supports LSP
While the original code didn't have explicit inheritance violations, our refactoring enables proper substitutability:

CheckoutPolicy implementations: Any CheckoutPolicy can be substituted without breaking the system

java   CheckoutPolicy policy = getPolicyFor(memberType);
   // Works with ANY policy implementation
   int days = policy.getLoanPeriodDays();

NotificationService implementations: Can swap between email, SMS, or mock implementations

java   NotificationService notificationService;  // Could be Email, SMS, Push, etc.
   notificationService.sendCheckoutNotification(member, book, dueDate);

All strategies are interchangeable: Each concrete policy behaves consistently with the contract defined by the interface

Why This Matters

Polymorphic behavior: Code works with abstractions, not concrete types
Flexible implementations: Can replace strategies without affecting clients
Consistent contracts: All implementations honor the same behavioral expectations


Interface Segregation Principle (ISP)
Violation
The original monolithic LibraryService would force clients to depend on methods they don't need:
java// A controller that only needs search functionality
// must still depend on the entire LibraryService
public class BookSearchController {
    private final LibraryService libraryService;  // Too much!
    
    public List<Book> search(String term) {
        return libraryService.searchBooks(term, "title");
        // But this class also has access to:
        // - checkoutBook()
        // - returnBook()
        // - generateReport()
        // ... which it doesn't need!
    }
}
Our Solution
We created focused, segregated interfaces and services:
java// Search functionality separated
public interface BookSearchService {
    List<Book> searchByTitle(String title);
    List<Book> searchByAuthor(String author);
    Optional<Book> searchByIsbn(String isbn);
}

// Now clients depend only on what they need
public class BookSearchController {
    private final BookSearchService searchService;  // Only search methods!
    
    public List<Book> search(String term) {
        return searchService.searchByTitle(term);
    }
}

// Another client needs only notifications
public class AutomatedReminderJob {
    private final NotificationService notificationService;  // Only notifications!
    
    public void sendOverdueReminders() {
        // Uses only notification methods
    }
}
Code Example
Before:
java// Fat interface - forces clients to depend on everything
public class LibraryService {
    public String checkoutBook(String isbn, String memberEmail) { }
    public String returnBook(String isbn) { }
    public List<Book> searchBooks(String term, String type) { }
    public String generateReport(String reportType) { }
}

// Every client depends on ALL methods, even if they only need one
After:
java// Segregated interfaces - clients depend only on what they use
public interface BookSearchService {
    List<Book> searchByTitle(String title);
    List<Book> searchByAuthor(String author);
}

public interface NotificationService {
    void sendCheckoutNotification(Member member, Book book, LocalDate dueDate);
    void sendReturnNotification(Member member, Book book, double lateFee);
}

public interface ReportGenerator {
    String generateOverdueReport();
    String generateAvailabilityReport();
}

// Each client depends on minimal interface
Why This Is Better

Minimal dependencies: Clients depend only on methods they actually use
Reduced coupling: Changes to unused functionality don't force recompilation
Clearer contracts: Interface purpose is obvious from its focused methods
Better testing: Mock only the methods actually needed
Prevents interface pollution: Interfaces don't grow bloated with unrelated methods


Dependency Inversion Principle (DIP)
Violation
The original code depended directly on concrete implementations for notifications:
java// High-level module depends on low-level detail (System.out.println)
public String checkoutBook(String isbn, String memberEmail) {
    // ... business logic ...
    
    // Direct dependency on console output
    System.out.println("Sending email to: " + member.getEmail());
    System.out.println("Subject: Book checked out");
    System.out.println("Message: You have checked out " + book.getTitle());
    
    return "Book checked out successfully";
}
This violates DIP because:

High-level business logic depends on low-level I/O mechanism
Cannot swap notification mechanism (email, SMS, push) without changing code
Impossible to test without seeing console output
Tightly coupled to a specific implementation detail

Our Solution
We created a NotificationService interface and depend on the abstraction:
javapublic interface NotificationService {
    void sendCheckoutNotification(Member member, Book book, LocalDate dueDate);
    void sendReturnNotification(Member member, Book book, double lateFee);
}

@Service
public class EmailNotificationService implements NotificationService {
    @Override
    public void sendCheckoutNotification(Member member, Book book, LocalDate dueDate) {
        // Actual email sending implementation
        // For now, can still use System.out.println
        System.out.println("Sending email to: " + member.getEmail());
        System.out.println("Subject: Book checked out");
        System.out.println("Message: You have checked out " + book.getTitle());
        System.out.println("Due date: " + dueDate);
    }
    
    @Override
    public void sendReturnNotification(Member member, Book book, double lateFee) {
        System.out.println("Sending email to: " + member.getEmail());
        System.out.println("Subject: Book returned");
        System.out.println("Message: You have returned " + book.getTitle());
        if (lateFee > 0) {
            System.out.println("Late fee: $" + String.format("%.2f", lateFee));
        }
    }
}
Code Example
Before:
javapublic class LibraryService {
    // Depends on concrete implementation detail
    public String checkoutBook(String isbn, String memberEmail) {
        // ... business logic ...
        
        // Hardcoded notification mechanism
        System.out.println("Sending email to: " + member.getEmail());
        System.out.println("Subject: Book checked out");
    }
}
After:
javapublic class LibraryService {
    private final NotificationService notificationService;  // Abstraction!
    
    public LibraryService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    public String checkoutBook(String isbn, String memberEmail) {
        // ... business logic ...
        
        // Depend on abstraction, not implementation
        notificationService.sendCheckoutNotification(member, book, dueDate);
    }
}
Alternative Implementations
Now we can easily create different implementations:
java// SMS notifications
@Service
public class SmsNotificationService implements NotificationService {
    @Override
    public void sendCheckoutNotification(Member member, Book book, LocalDate dueDate) {
        // Send SMS instead of email
        smsGateway.send(member.getPhone(), "Book checked out: " + book.getTitle());
    }
}

// Mock for testing
public class MockNotificationService implements NotificationService {
    private List<String> sentNotifications = new ArrayList<>();
    
    @Override
    public void sendCheckoutNotification(Member member, Book book, LocalDate dueDate) {
        sentNotifications.add("Checkout: " + book.getTitle());
    }
    
    public List<String> getSentNotifications() {
        return sentNotifications;
    }
}
Why This Is Better

Abstraction over implementation: High-level modules depend on abstractions
Easy substitution: Swap notification mechanisms without changing business logic
Testability: Use mock implementations in tests
Configuration flexibility: Choose implementation via dependency injection
Decoupled modules: Changes to notification implementation don't affect business logic
Future-proof: Can add push notifications, Slack messages, etc. without touching existing code


Summary: SOLID Principles Applied
PrincipleOriginal ProblemSolutionBenefitSRPOne class doing book, member, notification, search, and report operationsExtracted BookService, MemberService, NotificationService, BookSearchService, ReportGeneratorEach class has one reason to change; easier to maintain and testOCPIf-else statements for membership types; must modify code to add new typesStrategy pattern with CheckoutPolicy interface and factoryAdd new membership types without modifying existing codeLSPNo inheritance violations, but no polymorphism eitherAll strategies properly substitutable; consistent contractsFlexible, interchangeable implementationsISPMonolithic service forces clients to depend on unused methodsSegregated interfaces for search, notification, reportingClients depend only on methods they useDIPDirect dependency on System.out.printlnDepend on NotificationService abstractionEasy to swap implementations; testable with mocks
Testing Benefits
The refactored code is now highly testable:
java@Test
public void testCheckoutBook() {
    // Mock dependencies
    BookRepository bookRepo = mock(BookRepository.class);
    NotificationService notifications = mock(NotificationService.class);
    CheckoutPolicy policy = mock(CheckoutPolicy.class);
    
    // Test BookService in isolation
    BookService bookService = new BookService(bookRepo);
    
    when(policy.getLoanPeriodDays()).thenReturn(14);
    
    Book book = new Book();
    Member member = new Member();
    
    bookService.checkoutBook(book, member, 14);
    
    verify(bookRepo).save(book);
    assertFalse(book.isAvailable());
}
Conclusion
The refactored code demonstrates all five SOLID principles in a real-world library management context. Each principle contributes to a more maintainable, testable, and extensible system:

Easier to understand: Each class has a clear, focused purpose
Easier to test: Services can be tested in isolation with mocks
Easier to extend: New features can be added without modifying existing code
Easier to maintain: Changes are localized to specific services
More flexible: Implementations can be swapped via dependency injection
RetryClaude does not have the ability to run the code it generates yet.Claude can make mistakes. Please double-check responses. Sonnet 4.5

