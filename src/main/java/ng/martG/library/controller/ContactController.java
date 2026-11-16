package ng.martG.library.controller;

import jakarta.validation.Valid;
import ng.martG.library.dto.ContactForm;
import ng.martG.library.model.AppUser;
import ng.martG.library.service.ContactService;
import ng.martG.library.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class ContactController {

    private final ContactService contactService;
    private final MemberService memberService;

    public ContactController(ContactService contactService, MemberService memberService) {
        this.contactService = contactService;
        this.memberService = memberService;
    }

    @GetMapping("/contact")
    public String form(Model model) {
        model.addAttribute("form", new ContactForm());
        return "contact";
    }

    @PostMapping("/contact")
    public String submit(@Valid @ModelAttribute("form") ContactForm form,
                         BindingResult result,
                         Principal principal,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "contact";
        }
        AppUser user = memberService.requireByEmail(principal.getName());
        contactService.submit(user, form);
        redirectAttributes.addFlashAttribute("success", "Thanks! Your message has been sent to the librarian.");
        return "redirect:/contact";
    }
}
