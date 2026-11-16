package ng.martG.library.controller;

import ng.martG.library.model.AppUser;
import ng.martG.library.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Admin: dashboard, issued books, fines, members, contact messages. */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DashboardService dashboardService;
    private final LoanService loanService;
    private final MemberService memberService;
    private final ContactService contactService;

    public AdminController(DashboardService dashboardService,
                           LoanService loanService,
                           MemberService memberService,
                           ContactService contactService) {
        this.dashboardService = dashboardService;
        this.loanService = loanService;
        this.memberService = memberService;
        this.contactService = contactService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalTitles", dashboardService.totalTitles());
        model.addAttribute("totalMembers", dashboardService.totalMembers());
        model.addAttribute("issuedNow", dashboardService.issuedNow());
        model.addAttribute("overdueNow", dashboardService.overdueNow());
        model.addAttribute("unpaidFines", dashboardService.unpaidFines());
        model.addAttribute("collectedFines", dashboardService.collectedFines());
        model.addAttribute("openMessages", dashboardService.openMessages());
        return "admin/dashboard";
    }

    // ---- issued books and due dates
    @GetMapping("/loans")
    public String issuedLoans(Model model) {
        model.addAttribute("loans", loanService.issuedLoans());
        return "admin/loans";
    }

    // ---- fines
    @GetMapping("/fines")
    public String fines(Model model) {
        model.addAttribute("loans", loanService.loansWithFines());
        model.addAttribute("unpaidFines", dashboardService.unpaidFines());
        model.addAttribute("collectedFines", dashboardService.collectedFines());
        return "admin/fines";
    }

    @PostMapping("/fines/{loanId}/pay")
    public String markFinePaid(@PathVariable Long loanId, RedirectAttributes redirectAttributes) {
        loanService.markFinePaid(loanId);
        redirectAttributes.addFlashAttribute("success", "Fine marked as paid.");
        return "redirect:/admin/fines";
    }

    // ---- members
    @GetMapping("/members")
    public String members(Model model) {
        model.addAttribute("members", memberService.listMembers());
        return "admin/members";
    }

    @GetMapping("/members/{id}")
    public String memberDetail(@PathVariable Long id, Model model) {
        AppUser member = memberService.get(id);
        model.addAttribute("member", member);
        model.addAttribute("loans", loanService.loansForUser(id));
        return "admin/member-detail";
    }

    @PostMapping("/members/{id}/toggle")
    public String toggleMember(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        AppUser member = memberService.toggleEnabled(id);
        redirectAttributes.addFlashAttribute("success", member.getFullName()
                + (member.isEnabled() ? " can log in again." : " has been disabled and can no longer log in."));
        return "redirect:/admin/members";
    }

    // ---- contact messages
    @GetMapping("/messages")
    public String messages(Model model) {
        model.addAttribute("messages", contactService.findAll());
        return "admin/messages";
    }

    @PostMapping("/messages/{id}/resolve")
    public String resolveMessage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        contactService.markResolved(id);
        redirectAttributes.addFlashAttribute("success", "Message marked as resolved.");
        return "redirect:/admin/messages";
    }
}
