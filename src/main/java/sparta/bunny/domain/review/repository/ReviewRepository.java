package sparta.bunny.domain.review.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sparta.bunny.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	Optional<Review> getReviewById(Long id);
}
