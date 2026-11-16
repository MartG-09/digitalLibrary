package ng.martG.library.service;

import ng.martG.library.model.FineStatus;
import ng.martG.library.model.LoanStatus;
import ng.martG.library.model.Role;
import ng.martG.library.repository.BookRepository;
import ng.martG.library.repository.ContactMessageRepository;
import ng.martG.library.repository.LoanRepository;
import ng.martG.library.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Numbers shown on the admin dashboard. */
@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final ContactMessageRepository messageRepository;
    private final LoanService loanService;

    public DashboardService(BookRepository bookRepository,
                            UserRepository userRepository,
                            LoanRepository loanRepository,
                            ContactMessageRepository messageRepository,
                            LoanService loanService) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
        this.messageRepository = messageRepository;
        this.loanService = loanService;
    }

    public long totalTitles() { return bookRepository.count(); }

    public long totalMembers() { return userRepository.countByRole(Role.USER); }

    public long issuedNow() { return loanRepository.countByStatus(LoanStatus.ISSUED); }

    public long overdueNow() {
        return loanRepository.countByStatusAndDueDateBefore(LoanStatus.ISSUED, LocalDate.now());
    }

    public BigDecimal unpaidFines() { return loanService.totalFines(FineStatus.UNPAID); }

    public BigDecimal collectedFines() { return loanService.totalFines(FineStatus.PAID); }

    public long openMessages() { return messageRepository.countByResolvedFalse(); }
}
