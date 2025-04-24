package sparta.bunny.domain.review.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sparta.bunny.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	@Query("SELECT r FROM Review r " + "LEFT JOIN FETCH r.images " + "WHERE r.store.id = :storeId "
		+ "AND r.rating BETWEEN :min AND :max")
	Page<Review> findByStoreIdAndRatingBetween(@Param("storeId") Long storeId, @Param("min") Integer min,
		@Param("max") Integer max, Pageable pageable);

	Optional<Review> findById(Long reviewId);
}
