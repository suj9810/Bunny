package sparta.bunny.domain.review.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sparta.bunny.domain.review.entity.OwnerComment;
import sparta.bunny.domain.review.entity.Review;

public interface OwnerCommentRepository extends JpaRepository<OwnerComment, Long> {
	List<OwnerComment> findAllByReviewIdIn(List<Long> reviewIds);

	Optional<OwnerComment> findByReviewId(Long reviewId);

	List<Long> review(Review review);
}
