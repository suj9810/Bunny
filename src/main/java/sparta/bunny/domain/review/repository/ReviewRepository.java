package sparta.bunny.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sparta.bunny.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
