package ng.martG.library.service;

import ng.martG.library.config.LibraryProperties;
import ng.martG.library.exception.LibraryException;
import ng.martG.library.model.*;
import ng.martG.library.repository.BookRepository;
import ng.martG.library.repository.LoanRepository;
import ng.martG.library.repository.ReservationRepository;
import ng.martG.library.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final FineCalculator fineCalculator;
    private final LibraryProperties properties;

    public LoanService(LoanRepository loanRepository,
                       BookRepository bookRepository,
                       UserRepository userRepository,
                       ReservationRepository reservationRepository,
                       FineCalculator fineCalculator,
                       LibraryProperties properties) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.fineCalculator = fineCalculator;
        this.properties = properties;
    }

    // ------------------------------------------------------------ issue

    @Transactional
    public Loan issueBook(Long userId, Long bookId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new LibraryException("Account not found."));
        // Row lock: nobody else can change this book until we finish.
        Book book = bookRepository.findByIdForUpdate(bookId)
                .orElseThrow(() -> new LibraryException("Book not found."));
        LocalDate today = LocalDate.now();

        if (loanRepository.existsByUserIdAndFineStatus(userId, FineStatus.UNPAID)) {
            throw new LibraryException("You have an unpaid fine. Please pay it at the library desk before borrowing again.");
        }
        if (loanRepository.existsByUserIdAndStatusAndDueDateBefore(userId, LoanStatus.ISSUED, today)) {
            throw new LibraryException("You have an overdue book. Please return it before borrowing another.");
        }
        if (loanRepository.countByUserIdAndStatus(userId, LoanStatus.ISSUED) >= properties.getMaxActiveLoans()) {
            throw new LibraryException("You have reached the limit of " + properties.getMaxActiveLoans()
                    + " books at a time.");
        }
        if (loanRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, LoanStatus.ISSUED)) {
            throw new LibraryException("You already have a copy of this book.");
        }
        if (book.getAvailableQuantity() <= 0) {
            throw new LibraryException("No copies are available right now. You can reserve this book instead.");
        }

        // Fairness: free copies go to the people at the front of the reservation line first.
        List<Reservation> queue = reservationRepository
                .findByBookIdAndStatusOrderByReservedAtAscIdAsc(bookId, ReservationStatus.ACTIVE);
        Reservation mine = queue.stream()
                .filter(r -> r.getUser().getId().equals(userId))
                .findFirst()
                .orElse(null);
        int placeInLine = (mine == null) ? queue.size() : queue.indexOf(mine);
        if (placeInLine >= book.getAvailableQuantity()) {
            throw new LibraryException("The free copies are being held for members who reserved this book earlier.");
        }
        if (mine != null) {
            mine.setStatus(ReservationStatus.FULFILLED);
        }

        book.setAvailableQuantity(book.getAvailableQuantity() - 1);

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setIssueDate(today);
        loan.setDueDate(today.plusDays(properties.getLoanDays()));
        loan.setStatus(LoanStatus.ISSUED);
        return loanRepository.save(loan);
    }

    // ----------------------------------------------------------- return

    @Transactional
    public Loan returnBook(Long loanId, Long userId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LibraryException("Loan not found."));
        if (!loan.getUser().getId().equals(userId)) {
            throw new LibraryException("That loan does not belong to you.");
        }
        if (loan.getStatus() != LoanStatus.ISSUED) {
            throw new LibraryException("This book has already been returned.");
        }

        Book book = bookRepository.findByIdForUpdate(loan.getBook().getId())
                .orElseThrow(() -> new LibraryException("Book not found."));

        LocalDate today = LocalDate.now();
        BigDecimal fine = fineCalculator.calculate(loan.getDueDate(), today);

        loan.setReturnDate(today);
        loan.setStatus(LoanStatus.RETURNED);
        if (fine.signum() > 0) {
            loan.setFineAmount(fine);
            loan.setFineStatus(FineStatus.UNPAID);
        }
        book.setAvailableQuantity(Math.min(book.getTotalQuantity(), book.getAvailableQuantity() + 1));
        return loan;
    }

    // ------------------------------------------------------------ fines

    @Transactional
    public Loan markFinePaid(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LibraryException("Loan not found."));
        if (loan.getFineStatus() != FineStatus.UNPAID) {
            throw new LibraryException("This loan has no unpaid fine.");
        }
        loan.setFineStatus(FineStatus.PAID);
        loan.setFinePaidAt(LocalDateTime.now());
        return loan;
    }

    public BigDecimal totalFines(FineStatus status) {
        BigDecimal total = loanRepository.sumFineAmountByStatus(status);
        return total == null ? BigDecimal.ZERO.setScale(2) : total;
    }

    // ---------------------------------------------------------- queries

    public List<Loan> loansForUser(Long userId) {
        return loanRepository.findByUserIdOrderByIssueDateDescIdDesc(userId);
    }

    public List<Loan> issuedLoans() {
        return loanRepository.findByStatusOrderByDueDateAsc(LoanStatus.ISSUED);
    }

    public List<Loan> loansWithFines() {
        return loanRepository.findByFineStatusNotOrderByIdDesc(FineStatus.NONE);
    }

    public Set<Long> issuedBookIdsFor(Long userId) {
        return new HashSet<>(loanRepository.findBookIdsByUserAndStatus(userId, LoanStatus.ISSUED));
    }
}
