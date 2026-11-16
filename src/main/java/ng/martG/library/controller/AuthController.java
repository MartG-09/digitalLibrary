package ng.martG.library.controller;

import jakarta.validation.Valid;
import ng.martG.library.dto.RegisterForm;
import ng.martG.library.exception.LibraryException;
import ng.martG.library.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final MemberService memberService;

    public AuthController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            memberService.register(form);
        } catch (LibraryException e) {
            result.reject("register.failed", e.getMessage());
            return "register";
        }
        redirectAttributes.addFlashAttribute("success", "Account created. You can log in now.");
        return "redirect:/login";
    }
}
