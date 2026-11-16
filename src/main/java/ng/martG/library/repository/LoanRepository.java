package ng.martG.library.repository;

import ng.martG.library.model.FineStatus;
import ng.martG.library.model.Loan;
import ng.martG.library.model.LoanStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @EntityGraph(attributePaths = {"book", "user"})
    List<Loan> findByStatusOrderByDueDateAsc(LoanStatus status);

    @EntityGraph(attributePaths = {"book", "user"})
    List<Loan> findByUserIdOrderByIssueDateDescIdDesc(Long userId);

    @EntityGraph(attributePaths = {"book", "user"})
    List<Loan> findByFineStatusNotOrderByIdDesc(FineStatus status);

    long countByStatus(LoanStatus status);

    long countByStatusAndDueDateBefore(LoanStatus status, LocalDate date);

    long countByUserIdAndStatus(Long userId, LoanStatus status);

    long countByBookIdAndStatus(Long bookId, LoanStatus status);

    boolean existsByBookId(Long bookId);

    boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, LoanStatus status);

    boolean existsByUserIdAndFineStatus(Long userId, FineStatus fineStatus);

    boolean existsByUserIdAndStatusAndDueDateBefore(Long userId, LoanStatus status, LocalDate date);

    @Query("select l.book.id from Loan l where l.user.id = :userId and l.status = :status")
    List<Long> findBookIdsByUserAndStatus(@Param("userId") Long userId, @Param("status") LoanStatus status);

    /** Returns null when there are no matching rows. */
    @Query("select sum(l.fineAmount) from Loan l where l.fineStatus = :status")
    BigDecimal sumFineAmountByStatus(@Param("status") FineStatus status);
}
