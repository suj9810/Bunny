package sparta.bunny.domain.storeservice;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import sparta.bunny.domain.menu.entity.Menu;
import sparta.bunny.domain.menu.repository.MenuRepository;
import sparta.bunny.domain.stores.code.StoreExceptionCode;
import sparta.bunny.domain.stores.dto.response.StoreResponseDto;
import sparta.bunny.domain.stores.dto.response.StoreWithMenuResponseDto;
import sparta.bunny.domain.stores.entity.Category;
import sparta.bunny.domain.stores.entity.Store;
import sparta.bunny.domain.stores.exception.StoreException;
import sparta.bunny.domain.stores.repository.StoreRepository;
import sparta.bunny.domain.stores.service.UserStoreService;

@ExtendWith(MockitoExtension.class)
class UserStoreServiceTest {

	@InjectMocks
	private UserStoreService userStoreService;

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private MenuRepository menuRepository;

	private Pageable pageable;

	@BeforeEach
	void setUp() {
		pageable = PageRequest.of(0, 10);
	}

	@Test
	@DisplayName("전체 가게 조회 성공 (폐업한 가게 제외)")
	void findAllStores_success() {
		// given
		Store store = Store.builder()
			.storeName("롯데리아")
			.openTime(LocalTime.of(10, 0).toString())
			.closeTime(LocalTime.of(22, 0).toString())
			.minOrderPrice(15000)
			.notice("불고기 버거 짱맛있다.")
			.isClosed(false)
			.categoryName(Category.FAST_FOOD)
			.build();

		Page<Store> storePage = new PageImpl<>(List.of(store), pageable, 1);

		given(storeRepository.findAllByIsClosedFalse(any(Pageable.class))).willReturn(storePage);

		// when
		Page<StoreResponseDto> response = userStoreService.getStores(null, pageable);

		// then
		assertThat(response.getContent()).hasSize(1);
		StoreResponseDto dto = response.getContent().get(0);
		assertThat(dto.getStoreName()).isEqualTo("롯데리아");
		assertThat(dto.getMinOrderPrice()).isEqualTo(12000);
		assertThat(dto.getNotice()).isEqualTo("불고기버거 짱맛있다.");
		assertThat(dto.getCategoryName()).isEqualTo(Category.FAST_FOOD);
	}

	@Test
	@DisplayName("카테고리별 가게 조회 성공 (폐업한 가게 제외)")
	void findAllStores_byCategory_success() {
		// given
		Store store = Store.builder()
			.storeName("롯데리아")
			.openTime(LocalTime.of(11, 0).toString())
			.closeTime(LocalTime.of(23, 0).toString())
			.minOrderPrice(17000)
			.notice("불고기버거 짱맛있다.")
			.isClosed(false)
			.categoryName(Category.FAST_FOOD)
			.build();

		Page<Store> storePage = new PageImpl<>(List.of(store), pageable, 1);

		given(storeRepository.findAllByCategoryNameAndIsClosedFalse(eq(Category.FAST_FOOD), any(Pageable.class)))
			.willReturn(storePage);

		// when
		Page<StoreResponseDto> response = userStoreService.getStores(Category.FAST_FOOD, pageable);

		// then
		assertThat(response.getContent()).hasSize(1);
		StoreResponseDto dto = response.getContent().get(0);
		assertThat(dto.getStoreName()).isEqualTo("롯데리아");
		assertThat(dto.getCategoryName()).isEqualTo(Category.FAST_FOOD);
	}

	@Test
	@DisplayName("단일 가게 조회 성공 (폐업 X, 메뉴 포함)")
	void findStoreDetail_success() {
		// given
		Long storeId = 1L;
		Store store = Store.builder()
			.storeName("롯데리아")
			.openTime(LocalTime.of(10, 0).toString())
			.closeTime(LocalTime.of(22, 0).toString())
			.minOrderPrice(15000)
			.notice("불고기버거 짱맛있다.")
			.isClosed(false)
			.categoryName(Category.FAST_FOOD)
			.build();

		Menu menu = Menu.builder()
			.name("불고기버거 세트")
			.price(15000)
			.build();

		given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
		given(menuRepository.findAllByStoreId(storeId)).willReturn(List.of(menu));

		// when
		StoreWithMenuResponseDto response = userStoreService.getStoreWithMenus(storeId);

		// then
		assertThat(response.getStoreName()).isEqualTo("롯데리아");
		assertThat(response.getMenu()).hasSize(1);
		assertThat(response.getMenu().get(0).getName()).isEqualTo("불고기버거 세트");
		assertThat(response.getMenu().get(0).getPrice()).isEqualTo(15000);
	}

	@Test
	@DisplayName("폐업한 가게 조회 실패")
	void findStoreDetail_closedStore_throwsStoreException() {
		// given
		Long storeId = 1L;
		Store store = Store.builder()
			.storeName("폐업")
			.isClosed(true)
			.build();

		given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

		// when
		StoreException thrown = assertThrows(StoreException.class, () -> {
			userStoreService.getStoreWithMenus(storeId);
		});

		// then
		assertEquals(StoreExceptionCode.STORE_NOT_FOUND, thrown.getResponseCode());
	}
}
