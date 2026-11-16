package ng.martG.library.controller;

import jakarta.servlet.http.HttpServletRequest;
import ng.martG.library.config.LibraryProperties;
import ng.martG.library.exception.LibraryException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;

/** Things every page needs, plus one place to turn business-rule errors into a banner. */
@ControllerAdvice
public class GlobalControllerAdvice {

    private final LibraryProperties properties;

    public GlobalControllerAdvice(LibraryProperties properties) {
        this.properties = properties;
    }

    @ModelAttribute("currency")
    public String currency() {
        return properties.getCurrencySymbol();
    }

    @ModelAttribute("finePerDay")
    public BigDecimal finePerDay() {
        return properties.getFinePerDay();
    }

    @ModelAttribute("loanDays")
    public int loanDays() {
        return properties.getLoanDays();
    }

    @ModelAttribute("today")
    public LocalDate today() {
        return LocalDate.now();
    }

    /**
     * Any LibraryException thrown from a button/action (issue, return, reserve, delete...)
     * lands here: show the message as a red banner on the page the user came from.
     */
    @ExceptionHandler(LibraryException.class)
    public String handleLibraryException(LibraryException ex,
                                         HttpServletRequest request,
                                         RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        String target = "/";
        String referer = request.getHeader("Referer");
        if (referer != null) {
            try {
                URI uri = URI.create(referer);
                if (uri.getRawPath() != null && uri.getRawPath().startsWith("/")) {
                    target = uri.getRawPath() + (uri.getRawQuery() != null ? "?" + uri.getRawQuery() : "");
                }
            } catch (IllegalArgumentException ignored) {
                // bad Referer header: fall back to home
            }
        }
        return "redirect:" + target;
    }
}
