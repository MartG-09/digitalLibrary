package ng.martG.library.controller;

import jakarta.validation.Valid;
import ng.martG.library.dto.BookForm;
import ng.martG.library.exception.LibraryException;
import ng.martG.library.model.Book;
import ng.martG.library.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Admin: add, edit and delete books. */
@Controller
@RequestMapping("/admin/books")
public class AdminBookController {

    private final BookService bookService;

    public AdminBookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "admin/books";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new BookForm());
        model.addAttribute("bookId", null);
        model.addAttribute("categories", bookService.categories());
        return "admin/book-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") BookForm form,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        model.addAttribute("bookId", null);
        model.addAttribute("categories", bookService.categories());
        if (result.hasErrors()) {
            return "admin/book-form";
        }
        try {
            Book book = bookService.create(form);
            redirectAttributes.addFlashAttribute("success", "\"" + book.getTitle() + "\" added to the catalogue.");
        } catch (LibraryException e) {
            result.reject("book.failed", e.getMessage());
            return "admin/book-form";
        }
        return "redirect:/admin/books";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Book book = bookService.get(id);
        model.addAttribute("form", BookForm.from(book));
        model.addAttribute("bookId", id);
        model.addAttribute("categories", bookService.categories());
        return "admin/book-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") BookForm form,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        model.addAttribute("bookId", id);
        model.addAttribute("categories", bookService.categories());
        if (result.hasErrors()) {
            return "admin/book-form";
        }
        try {
            Book book = bookService.update(id, form);
            redirectAttributes.addFlashAttribute("success", "\"" + book.getTitle() + "\" updated.");
        } catch (LibraryException e) {
            result.reject("book.failed", e.getMessage());
            return "admin/book-form";
        }
        return "redirect:/admin/books";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Book deleted.");
        return "redirect:/admin/books";
    }
}
