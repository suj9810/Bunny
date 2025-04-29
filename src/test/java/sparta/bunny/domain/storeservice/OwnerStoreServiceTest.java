package sparta.bunny.domain.storeservice;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import sparta.bunny.domain.menu.entity.Menu;
import sparta.bunny.domain.menu.repository.MenuRepository;
import sparta.bunny.domain.stores.dto.request.StoreRequestDto;
import sparta.bunny.domain.stores.dto.response.StoreResponseDto;
import sparta.bunny.domain.stores.dto.response.StoreWithMenuResponseDto;
import sparta.bunny.domain.stores.entity.Category;
import sparta.bunny.domain.stores.entity.Store;
import sparta.bunny.domain.stores.exception.StoreException;
import sparta.bunny.domain.stores.repository.StoreRepository;
import sparta.bunny.domain.stores.service.OwnerStoreService;
import sparta.bunny.domain.user.entity.User;

@DisplayName("OwnerStoreService 단위 테스트")
class OwnerStoreServiceTest {

	@InjectMocks
	private OwnerStoreService ownerStoreService;

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private MenuRepository menuRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("가게 등록 성공")
	void createStore_success() {
		// given
		User owner = createUser(1L);
		StoreRequestDto request = createStoreRequestDto("롯데리아", Category.FAST_FOOD);

		given(storeRepository.countByUser(owner)).willReturn(0L);
		given(storeRepository.save(any(Store.class)))
			.willAnswer(invocation -> {
				Store store = invocation.getArgument(0);
				ReflectionTestUtils.setField(store, "id", 1L);
				return store;
			});

		// when
		ownerStoreService.createStore(request, owner);

		// then
		verify(storeRepository).save(any(Store.class));
	}

	@Test
	@DisplayName("가게 수정 성공")
	void updateStore_success() {
		// given
		Long storeId = 1L;
		User owner = createUser(1L);
		Store store = createStore(owner, "롯데리아", Category.FAST_FOOD);
		ReflectionTestUtils.setField(store, "id", storeId);

		StoreRequestDto request = createStoreRequestDto("맘스터치", Category.FAST_FOOD);

		given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

		// when
		ownerStoreService.updateStore(storeId, request, owner);

		// then
		assertThat(store.getStoreName()).isEqualTo("맘스터치");
		assertThat(store.getOpenTime()).isEqualTo(request.getOpenTime());
		assertThat(store.getCloseTime()).isEqualTo(request.getCloseTime());
		assertThat(store.getMinOrderPrice()).isEqualTo(request.getMinOrderPrice());
	}

	@Test
	@DisplayName("가게 폐업 처리 성공")
	void closeStore_success() {
		// given
		Long storeId = 1L;
		User owner = createUser(1L);
		Store store = createStore(owner, "롯데리아", Category.FAST_FOOD);
		ReflectionTestUtils.setField(store, "id", storeId);

		given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

		// when
		ownerStoreService.closeStore(storeId, true, owner);

		// then
		assertThat(store.getIsClosed()).isTrue();
	}

	@Test
	@DisplayName("내 가게 전체 조회 성공 (카테고리 필터링 가능, 페이징 적용)")
	void getStoresByCategory_success() {
		// given
		User owner = createUser(1L);
		Store store = createStore(owner, "롯데리아", Category.FAST_FOOD);
		ReflectionTestUtils.setField(store, "id", 1L);

		Pageable pageable = PageRequest.of(0, 10);
		Page<Store> storePage = new PageImpl<>(List.of(store), pageable, 1);

		given(storeRepository.findAllByUserAndCategoryName(owner, Category.FAST_FOOD, pageable))
			.willReturn(storePage);

		// when
		Page<StoreResponseDto> response = ownerStoreService.getStoresByCategory(owner, Category.FAST_FOOD, pageable);

		// then
		assertThat(response.getContent()).hasSize(1);
		assertThat(response.getContent().get(0).getStoreName()).isEqualTo("롯데리아");
	}

	@Test
	@DisplayName("단일 가게 조회 성공 (메뉴 포함)")
	void getStoreWithMenus_success() {
		// given
		Long storeId = 1L;
		User owner = createUser(1L);
		Store store = createStore(owner, "롯데리아", Category.FAST_FOOD);
		ReflectionTestUtils.setField(store, "id", storeId);

		Menu menu = Menu.builder()
			.name("불고기버거세트")
			.price(15000)
			.build();

		given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
		given(menuRepository.findMenusWithOptionsAndImagesByStoreId(storeId))
			.willReturn(List.of(menu));

		// when
		StoreWithMenuResponseDto response = ownerStoreService.getStoreWithMenus(storeId, owner);

		// then
		assertThat(response.getStoreName()).isEqualTo("롯데리아");
		assertThat(response.getMenu()).hasSize(1);
		assertThat(response.getMenu().get(0).getName()).isEqualTo("불고기버거세트");
	}

	@Test
	@DisplayName("가게 등록 제한 초과 시 예외 발생")
	void createStore_storeLimitExceeded() {
		// given
		User owner = createUser(1L);
		StoreRequestDto request = createStoreRequestDto("롯데리아", Category.FAST_FOOD);

		given(storeRepository.countByUser(owner)).willReturn(3L);

		// when & then
		assertThatThrownBy(() -> ownerStoreService.createStore(request, owner))
			.isInstanceOf(StoreException.class)
			.hasMessageContaining("STORE_LIMIT_EXCEEDED");
	}

	// ======= 테스트 보조 메서드 =======

	private User createUser(Long id) {
		return User.builder().id(id).build();
	}

	private StoreRequestDto createStoreRequestDto(String name, Category category) {
		return StoreRequestDto.builder()
			.storeName(name)
			.openTime("08:00")
			.closeTime("23:00")
			.minOrderPrice(12000)
			.notice("불고기 버거 최고")
			.categoryName(category)
			.build();
	}

	private Store createStore(User owner, String name, Category category) {
		return Store.builder()
			.user(owner)
			.storeName(name)
			.openTime("08:00")
			.closeTime("23:00")
			.minOrderPrice(12000)
			.isClosed(false)
			.categoryName(category)
			.build();
	}
}
