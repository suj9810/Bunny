package sparta.bunny.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sparta.bunny.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByUserEmail(String email);

	Optional<User> findByUserEmail(String email);
}
