package ng.martG.library.dto;

import jakarta.validation.constraints.*;
import ng.martG.library.model.Book;

public class BookForm {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title is too long")
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 150, message = "Author is too long")
    private String author;

    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^[0-9Xx\\- ]{10,17}$", message = "ISBN must be 10 or 13 digits (hyphens allowed)")
    private String isbn;

    @NotBlank(message = "Category is required")
    @Size(max = 60, message = "Category is too long")
    private String category;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Max(value = 10000, message = "Quantity is too large")
    private Integer quantity;

    public static BookForm from(Book book) {
        BookForm form = new BookForm();
        form.setTitle(book.getTitle());
        form.setAuthor(book.getAuthor());
        form.setIsbn(book.getIsbn());
        form.setCategory(book.getCategory());
        form.setQuantity(book.getTotalQuantity());
        return form;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
