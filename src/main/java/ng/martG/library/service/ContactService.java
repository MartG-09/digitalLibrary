package ng.martG.library.service;

import ng.martG.library.dto.ContactForm;
import ng.martG.library.exception.LibraryException;
import ng.martG.library.model.AppUser;
import ng.martG.library.model.ContactMessage;
import ng.martG.library.repository.ContactMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ContactService {

    private final ContactMessageRepository repository;

    public ContactService(ContactMessageRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ContactMessage submit(AppUser user, ContactForm form) {
        ContactMessage message = new ContactMessage();
        message.setUser(user);
        message.setName(user.getFullName());
        message.setEmail(user.getEmail());
        message.setSubject(form.getSubject().trim());
        message.setMessage(form.getMessage().trim());
        return repository.save(message);
    }

    public List<ContactMessage> findAll() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public void markResolved(Long id) {
        ContactMessage message = repository.findById(id)
                .orElseThrow(() -> new LibraryException("Message not found."));
        message.setResolved(true);
    }
}
