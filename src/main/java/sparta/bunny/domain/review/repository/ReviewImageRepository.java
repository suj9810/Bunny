package sparta.bunny.domain.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sparta.bunny.domain.review.entity.ReviewImage;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
	List<ReviewImage> findAllByReviewId(Long reviewId);
}
