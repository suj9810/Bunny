package sparta.bunny.domain.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sparta.bunny.domain.review.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
	List<Image> findAllByReviewId(Long reviewId);
}
