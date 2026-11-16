package ng.martG.library.service;

import ng.martG.library.exception.LibraryException;
import ng.martG.library.model.*;
import ng.martG.library.repository.BookRepository;
import ng.martG.library.repository.LoanRepository;
import ng.martG.library.repository.ReservationRepository;
import ng.martG.library.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              BookRepository bookRepository,
                              UserRepository userRepository,
                              LoanRepository loanRepository) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
    }

    @Transactional
    public Reservation reserve(Long userId, Long bookId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new LibraryException("Account not found."));
        Book book = bookRepository.findByIdForUpdate(bookId)
                .orElseThrow(() -> new LibraryException("Book not found."));

        if (book.getAvailableQuantity() > 0) {
            throw new LibraryException("Copies are available right now, so you can issue this book directly.");
        }
        if (loanRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, LoanStatus.ISSUED)) {
            throw new LibraryException("You already have this book issued.");
        }
        if (reservationRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, ReservationStatus.ACTIVE)) {
            throw new LibraryException("You have already reserved this book.");
        }

        Reservation reservation = new Reservation();
        reservation.setBook(book);
        reservation.setUser(user);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public void cancel(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new LibraryException("Reservation not found."));
        if (!reservation.getUser().getId().equals(userId)) {
            throw new LibraryException("That reservation does not belong to you.");
        }
        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new LibraryException("Only active reservations can be cancelled.");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
    }

    public List<Reservation> reservationsForUser(Long userId) {
        return reservationRepository.findByUserIdOrderByReservedAtDesc(userId);
    }

    public Set<Long> reservedBookIdsFor(Long userId) {
        return new HashSet<>(reservationRepository.findBookIdsByUserAndStatus(userId, ReservationStatus.ACTIVE));
    }
}
