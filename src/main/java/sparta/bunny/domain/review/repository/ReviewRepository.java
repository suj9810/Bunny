package sparta.bunny.domain.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import sparta.bunny.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	Page<Review> findByStoresId(Long storeId, Pageable pageable);
}
