package ng.martG.library.repository;

import ng.martG.library.model.Reservation;
import ng.martG.library.model.ReservationStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /** The waiting line for a book, first come first served. */
    @EntityGraph(attributePaths = {"user"})
    List<Reservation> findByBookIdAndStatusOrderByReservedAtAscIdAsc(Long bookId, ReservationStatus status);

    @EntityGraph(attributePaths = {"book", "user"})
    List<Reservation> findByUserIdOrderByReservedAtDesc(Long userId);

    boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, ReservationStatus status);

    @Query("select r.book.id from Reservation r where r.user.id = :userId and r.status = :status")
    List<Long> findBookIdsByUserAndStatus(@Param("userId") Long userId, @Param("status") ReservationStatus status);

    void deleteByBookId(Long bookId);
}
