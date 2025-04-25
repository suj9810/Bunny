package sparta.bunny.domain.review.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.bunny.common.audit.BaseEntity;
import sparta.bunny.domain.order.entity.Order;
import sparta.bunny.domain.review.code.ReviewExceptionCode;
import sparta.bunny.domain.review.exception.ReviewException;
import sparta.bunny.domain.stores.entity.Store;
import sparta.bunny.domain.user.entity.User;

@Entity
@Getter
@Table(name = "reviews")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private Integer rating;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id")
	private Store store;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ReviewImage> reviewImages = new ArrayList<>();

	/**
	 * 리뷰 작성자 확인
	 * @param user user
	 */
	public void validateOwner(User user) {
		if (!user.getId().equals(this.getUser().getId())) {
			throw new ReviewException(ReviewExceptionCode.NOT_OWNER_OF_REVIEW);
		}
	}
}
