package ng.martG.library.controller;

import ng.martG.library.config.LibraryProperties;
import ng.martG.library.model.AppUser;
import ng.martG.library.model.FineStatus;
import ng.martG.library.model.Loan;
import ng.martG.library.service.LoanService;
import ng.martG.library.service.MemberService;
import ng.martG.library.service.ReservationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

/** A member's own loans, fines and reservations. */
@Controller
@RequestMapping("/my")
public class MyLibraryController {

    private final LoanService loanService;
    private final ReservationService reservationService;
    private final MemberService memberService;
    private final LibraryProperties properties;

    public MyLibraryController(LoanService loanService,
                               ReservationService reservationService,
                               MemberService memberService,
                               LibraryProperties properties) {
        this.loanService = loanService;
        this.reservationService = reservationService;
        this.memberService = memberService;
        this.properties = properties;
    }

    @GetMapping("/loans")
    public String myLoans(Model model, Principal principal) {
        AppUser user = memberService.requireByEmail(principal.getName());
        List<Loan> loans = loanService.loansForUser(user.getId());
        BigDecimal unpaid = loans.stream()
                .filter(l -> l.getFineStatus() == FineStatus.UNPAID)
                .map(Loan::getFineAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("loans", loans);
        model.addAttribute("unpaidTotal", unpaid);
        return "my/loans";
    }

    @PostMapping("/loans/{id}/return")
    public String returnBook(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        AppUser user = memberService.requireByEmail(principal.getName());
        Loan loan = loanService.returnBook(id, user.getId());
        if (loan.getFineStatus() == FineStatus.UNPAID) {
            redirectAttributes.addFlashAttribute("error", "\"" + loan.getBook().getTitle()
                    + "\" returned late. A fine of " + properties.getCurrencySymbol() + loan.getFineAmount()
                    + " has been recorded. Please pay it at the library desk.");
        } else {
            redirectAttributes.addFlashAttribute("success",
                    "\"" + loan.getBook().getTitle() + "\" returned. Thank you!");
        }
        return "redirect:/my/loans";
    }

    @GetMapping("/reservations")
    public String myReservations(Model model, Principal principal) {
        AppUser user = memberService.requireByEmail(principal.getName());
        model.addAttribute("reservations", reservationService.reservationsForUser(user.getId()));
        return "my/reservations";
    }

    @PostMapping("/reservations/{id}/cancel")
    public String cancelReservation(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        AppUser user = memberService.requireByEmail(principal.getName());
        reservationService.cancel(id, user.getId());
        redirectAttributes.addFlashAttribute("success", "Reservation cancelled.");
        return "redirect:/my/reservations";
    }
}
