package sparta.bunny.domain.review.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.common.response.CommonResponses;
import sparta.bunny.common.service.FileService;
import sparta.bunny.domain.auth.jwt.UserDetailsImpl;
import sparta.bunny.domain.order.entity.Order;
import sparta.bunny.domain.order.repository.OrderRepository;
import sparta.bunny.domain.review.dto.request.ReviewCreateRequest;
import sparta.bunny.domain.review.dto.response.ReviewCreateResponse;
import sparta.bunny.domain.review.dto.response.ReviewFindResponse;
import sparta.bunny.domain.review.entity.Review;
import sparta.bunny.domain.review.repository.OwnerCommentRepository;
import sparta.bunny.domain.review.repository.ReviewImageRepository;
import sparta.bunny.domain.review.repository.ReviewRepository;
import sparta.bunny.domain.stores.entity.Store;
import sparta.bunny.domain.user.entity.User;
import sparta.bunny.domain.user.entity.UserRole;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

	@Mock
	private ReviewRepository reviewRepository;
	@Mock
	private OwnerCommentRepository ownerCommentRepository;
	@Mock
	private OrderRepository orderRepository;
	@Mock
	private ReviewImageRepository reviewImageRepository;
	@Mock
	private FileService fileService;

	@InjectMocks
	private ReviewService reviewService;

	private User user;
	private Store store;
	private Order order;
	private UserDetailsImpl userDetails;
	private Review review;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.email("email")
			.id(1L)
			.password("1234")
			.nickname("nickname")
			.userRole(UserRole.USER)
			.userNumber("1234")
			.isDeleted(false)
			.build();

		store = Store.builder()
			.user(user)
			.storeName("가게 이름")
			.openTime("21:00")
			.closeTime("11:00")
			.minOrderPrice(1)
			.notice("공지")
			.isClosed(false)
			.categoryName("한식")
			.build();
		ReflectionTestUtils.setField(store, "id", 1L);

		order = new Order(user, store);
		ReflectionTestUtils.setField(order, "id", 1L);

		userDetails = new UserDetailsImpl(user);

		review = Review.builder()
			.content("댓글내용")
			.rating(1)
			.user(user)
			.order(order)
			.store(store)
			.build();
		ReflectionTestUtils.setField(review, "id", 1L);

	}

	@Nested
	@DisplayName("리뷰 저장 테스트")
	class saveReviewTest {

		@Test
		@DisplayName("리뷰 저장 테스트 성공")
		void saveReview_success() throws IOException {
			// given
			ReviewCreateRequest request = new ReviewCreateRequest("댓글 내용", 1, 1L, null);

			given(orderRepository.findById(request.getOrderId())).willReturn(Optional.of(order));
			given(reviewRepository.save(any())).willReturn(review);

			// when
			CommonResponse<ReviewCreateResponse> reviewCreateResponseCommonResponse = reviewService.saveReview(request,
				userDetails);

			// then
			verify(reviewRepository, times(1)).save((any(Review.class)));
		}

	}

	@Test
	@DisplayName("리뷰 불러오기 테스트 성공")
	void getReviewByStoreId_success() {
		// given

		Long storeId = 1L;
		Integer minRating = 1;
		Integer maxRating = 5;
		Pageable pageable = PageRequest.of(0, 10);

		Review review2 = Review.builder()
			.content("댓글내용2")
			.rating(1)
			.user(user)
			.order(order)
			.store(store)
			.build();
		ReflectionTestUtils.setField(review2, "id", 2L);

		List<Review> reviews = List.of(
			review,
			review2
		);

		Page<Review> reviewPage = new PageImpl<>(reviews, pageable, reviews.size());

		given(reviewRepository.findByStoreIdAndRatingBetween(storeId, minRating, maxRating, pageable)).willReturn(
			reviewPage);
		given(ownerCommentRepository.findAllByReviewIdIn(any())).willReturn(List.of());

		// when
		CommonResponses<ReviewFindResponse> responses = reviewService.getReviewsByStoreId(1L, pageable, 1, 5);

		// then
		assertThat(responses).isNotNull();
		assertThat(responses.getResult().getContent()).hasSize(2);
		assertThat(responses.getResult().getContent().get(0).getContent()).isEqualTo("댓글내용");
		assertThat(responses.getResult().getContent().get(1).getContent()).isEqualTo("댓글내용2");

		verify(reviewRepository, times(1)).findByStoreIdAndRatingBetween(storeId, minRating, maxRating, pageable);
		verify(ownerCommentRepository, times(1)).findAllByReviewIdIn(any());
	}
}
