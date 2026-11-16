package ng.martG.library.service;

import ng.martG.library.dto.RegisterForm;
import ng.martG.library.exception.LibraryException;
import ng.martG.library.model.AppUser;
import ng.martG.library.model.Role;
import ng.martG.library.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AppUser register(RegisterForm form) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new LibraryException("The two passwords do not match.");
        }
        return createUser(form.getFullName(), form.getEmail(), form.getPassword(), Role.USER);
    }

    @Transactional
    public AppUser createUser(String fullName, String email, String rawPassword, Role role) {
        String cleanEmail = email.trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(cleanEmail)) {
            throw new LibraryException("An account with this email already exists.");
        }
        AppUser user = new AppUser();
        user.setFullName(fullName.trim());
        user.setEmail(cleanEmail);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        return userRepository.save(user);
    }

    public AppUser requireByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new LibraryException("Account not found."));
    }

    public AppUser get(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new LibraryException("Member not found."));
    }

    public List<AppUser> listMembers() {
        return userRepository.findByRoleOrderByFullNameAsc(Role.USER);
    }

    /** Admin switch: a disabled member can no longer log in. */
    @Transactional
    public AppUser toggleEnabled(Long id) {
        AppUser member = get(id);
        if (member.getRole() != Role.USER) {
            throw new LibraryException("Only member accounts can be enabled or disabled here.");
        }
        member.setEnabled(!member.isEnabled());
        return member;
    }
}
