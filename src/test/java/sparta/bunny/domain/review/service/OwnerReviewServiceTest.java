package sparta.bunny.domain.review.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.domain.auth.jwt.UserDetailsImpl;
import sparta.bunny.domain.order.entity.Order;
import sparta.bunny.domain.review.dto.request.OwnerCommentCreateRequestDto;
import sparta.bunny.domain.review.dto.response.OwnerCommentCreateResponse;
import sparta.bunny.domain.review.entity.OwnerComment;
import sparta.bunny.domain.review.entity.Review;
import sparta.bunny.domain.review.repository.OwnerCommentRepository;
import sparta.bunny.domain.review.repository.ReviewRepository;
import sparta.bunny.domain.stores.entity.Category;
import sparta.bunny.domain.stores.entity.Store;
import sparta.bunny.domain.user.entity.User;
import sparta.bunny.domain.user.entity.UserRole;

@ExtendWith(MockitoExtension.class)
class OwnerReviewServiceTest {

	@Mock
	private OwnerCommentRepository ownerCommentRepository;
	@Mock
	private ReviewRepository reviewRepository;

	@InjectMocks
	private OwnerReviewService ownerReviewService;

	private User owner;
	private User user;
	private Store store;
	private Order order;
	private UserDetailsImpl ownerUserDetails;
	private UserDetailsImpl userDetails;
	private Review review;

	@BeforeEach
	void setUp() {
		owner = User.builder()
			.email("user")
			.id(1L)
			.password("1234")
			.nickname("owner")
			.userRole(UserRole.OWNER)
			.userNumber("1234")
			.isDeleted(false)
			.build();

		user = User.builder()
			.email("owner")
			.id(2L)
			.password("1234")
			.nickname("user")
			.userRole(UserRole.USER)
			.userNumber("1234")
			.isDeleted(false)
			.build();

		store = Store.builder()
			.user(owner)
			.storeName("가게 이름")
			.openTime("21:00")
			.closeTime("11:00")
			.minOrderPrice(1)
			.notice("공지")
			.isClosed(false)
			.categoryName(Category.SNACK)
			.build();
		ReflectionTestUtils.setField(store, "id", 1L);

		order = new Order(owner, store);
		ReflectionTestUtils.setField(order, "id", 1L);

		ownerUserDetails = new UserDetailsImpl(owner);
		userDetails = new UserDetailsImpl(user);

		review = Review.builder()
			.content("댓글내용")
			.rating(1)
			.user(owner)
			.order(order)
			.store(store)
			.build();
		ReflectionTestUtils.setField(review, "id", 1L);

	}

	@Test
	@DisplayName("사장 리뷰 작성 테스트 성공")
	void saveOwnerCommentTest_success() {
		// given
		OwnerComment ownerComment = OwnerComment.builder()
			.review(review)
			.content("사장 댓글")
			.build();
		ReflectionTestUtils.setField(ownerComment, "id", 1L);
		OwnerCommentCreateRequestDto request = new OwnerCommentCreateRequestDto(1L, "사장 리뷰");

		given(reviewRepository.findById(1L)).willReturn(Optional.of(review));
		given(ownerCommentRepository.save(any())).willReturn(ownerComment);

		// when
		CommonResponse<OwnerCommentCreateResponse> response = ownerReviewService.saveOwnerComment(
			request, ownerUserDetails);

		// then
		assertEquals(1L, response.getData().getOwnerReviewId());
	}

}
