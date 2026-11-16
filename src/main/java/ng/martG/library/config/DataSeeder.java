package ng.martG.library.config;

import ng.martG.library.model.AppUser;
import ng.martG.library.model.Book;
import ng.martG.library.model.Role;
import ng.martG.library.repository.BookRepository;
import ng.martG.library.repository.UserRepository;
import ng.martG.library.service.MemberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/** Runs once at start-up: makes sure an admin exists and adds a few demo books. */
@Component
public class DataSeeder implements CommandLineRunner {

    private final LibraryProperties properties;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final MemberService memberService;

    public DataSeeder(LibraryProperties properties,
                      UserRepository userRepository,
                      BookRepository bookRepository,
                      MemberService memberService) {
        this.properties = properties;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.memberService = memberService;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmailIgnoreCase(properties.getAdminEmail())) {
            AppUser admin = memberService.createUser(properties.getAdminName(), properties.getAdminEmail(),
                    properties.getAdminPassword(), Role.ADMIN);
            System.out.println("Created admin account: " + admin.getEmail());
        }
        if (properties.isSeedSampleBooks() && bookRepository.count() == 0) {
            List<Book> books = List.of(
                    book("Clean Code", "Robert C. Martin", "9780132350884", "Programming", 3),
                    book("Effective Java", "Joshua Bloch", "9780134685991", "Programming", 2),
                    book("Head First Java", "Kathy Sierra and Bert Bates", "9780596009205", "Programming", 2),
                    book("Things Fall Apart", "Chinua Achebe", "9780385474542", "Fiction", 3),
                    book("Half of a Yellow Sun", "Chimamanda Ngozi Adichie", "9781400095209", "Fiction", 2),
                    book("Purple Hibiscus", "Chimamanda Ngozi Adichie", "9781616202415", "Fiction", 1),
                    book("Americanah", "Chimamanda Ngozi Adichie", "9780307455925", "Fiction", 2),
                    book("1984", "George Orwell", "9780451524935", "Fiction", 2),
                    book("A Brief History of Time", "Stephen Hawking", "9780553380163", "Science", 2),
                    book("Sapiens", "Yuval Noah Harari", "9780062316097", "History", 2),
                    book("Atomic Habits", "James Clear", "9780735211292", "Self-Help", 3),
                    book("The Alchemist", "Paulo Coelho", "9780062315007", "Fiction", 2));
            bookRepository.saveAll(books);
            System.out.println("Added " + books.size() + " sample books.");
        }
    }

    private Book book(String title, String author, String isbn, String category, int quantity) {
        Book b = new Book();
        b.setTitle(title);
        b.setAuthor(author);
        b.setIsbn(isbn);
        b.setCategory(category);
        b.setTotalQuantity(quantity);
        b.setAvailableQuantity(quantity);
        return b;
    }
}
