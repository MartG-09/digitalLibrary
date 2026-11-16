package ng.martG.library.service;

import ng.martG.library.dto.BookForm;
import ng.martG.library.exception.LibraryException;
import ng.martG.library.model.Book;
import ng.martG.library.model.LoanStatus;
import ng.martG.library.repository.BookRepository;
import ng.martG.library.repository.LoanRepository;
import ng.martG.library.repository.ReservationRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;

    public BookService(BookRepository bookRepository,
                       LoanRepository loanRepository,
                       ReservationRepository reservationRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<Book> search(String query, String category) {
        String q = query == null ? "" : query.trim();
        String c = category == null ? "" : category.trim();
        return bookRepository.search(q, c);
    }

    public List<String> categories() {
        return bookRepository.findAllCategories();
    }

    public List<Book> findAll() {
        return bookRepository.findAll(Sort.by("title"));
    }

    public Book get(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new LibraryException("Book not found."));
    }

    @Transactional
    public Book create(BookForm form) {
        String isbn = normalizeIsbn(form.getIsbn());
        if (bookRepository.existsByIsbn(isbn)) {
            throw new LibraryException("A book with ISBN " + isbn + " already exists.");
        }
        Book book = new Book();
        copyFields(book, form, isbn);
        book.setTotalQuantity(form.getQuantity());
        book.setAvailableQuantity(form.getQuantity());
        return bookRepository.save(book);
    }

    @Transactional
    public Book update(Long id, BookForm form) {
        Book book = bookRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new LibraryException("Book not found."));
        String isbn = normalizeIsbn(form.getIsbn());
        bookRepository.findByIsbn(isbn).ifPresent(other -> {
            if (!other.getId().equals(id)) {
                throw new LibraryException("Another book already uses ISBN " + isbn + ".");
            }
        });

        long issued = loanRepository.countByBookIdAndStatus(id, LoanStatus.ISSUED);
        if (form.getQuantity() < issued) {
            throw new LibraryException("Quantity cannot be lower than the " + issued
                    + " copies that are currently issued.");
        }
        copyFields(book, form, isbn);
        book.setTotalQuantity(form.getQuantity());
        book.setAvailableQuantity((int) (form.getQuantity() - issued));
        return book;
    }

    @Transactional
    public void delete(Long id) {
        Book book = get(id);
        if (loanRepository.existsByBookId(id)) {
            throw new LibraryException("This book has loan history and cannot be deleted. "
                    + "Set its quantity to 0 to take it off the shelf instead.");
        }
        reservationRepository.deleteByBookId(id);
        bookRepository.delete(book);
    }

    private void copyFields(Book book, BookForm form, String isbn) {
        book.setTitle(form.getTitle().trim());
        book.setAuthor(form.getAuthor().trim());
        book.setIsbn(isbn);
        book.setCategory(form.getCategory().trim());
    }

    private String normalizeIsbn(String raw) {
        String isbn = raw.replace("-", "").replace(" ", "").toUpperCase();
        if (isbn.length() != 10 && isbn.length() != 13) {
            throw new LibraryException("ISBN must have 10 or 13 characters (hyphens are ignored).");
        }
        return isbn;
    }
}
