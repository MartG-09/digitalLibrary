package ng.martG.library.repository;

import ng.martG.library.model.AppUser;
import ng.martG.library.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<AppUser> findByRoleOrderByFullNameAsc(Role role);

    long countByRole(Role role);
}
