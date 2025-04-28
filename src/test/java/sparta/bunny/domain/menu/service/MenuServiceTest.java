package sparta.bunny.domain.menu.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.common.service.FileService;
import sparta.bunny.domain.menu.code.MenuExceptionCode;
import sparta.bunny.domain.menu.dto.request.MenuCreateRequest;
import sparta.bunny.domain.menu.dto.request.MenuOptionRequest;
import sparta.bunny.domain.menu.dto.response.MenuResponse;
import sparta.bunny.domain.menu.entity.Menu;
import sparta.bunny.domain.menu.entity.MenuImage;
import sparta.bunny.domain.menu.exception.MenuException;
import sparta.bunny.domain.menu.repository.MenuImageRepository;
import sparta.bunny.domain.menu.repository.MenuRepository;
import sparta.bunny.domain.stores.entity.Store;
import sparta.bunny.domain.stores.repository.StoreRepository;
import sparta.bunny.domain.user.entity.User;

/**
 * The type Menu service test.
 */
class MenuServiceTest {

	@InjectMocks
	private MenuService menuService;

	@Mock
	private MenuRepository menuRepository;

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private MenuImageRepository menuImageRepository;

	@Mock
	private FileService fileService;

	/**
	 * Sets up.
	 */
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	/**
	 * 메뉴 생성 성공
	 *
	 * @throws Exception the exception
	 */
	@Test
	void saveMenu_success() throws Exception {
		//given
		Long loginUserId = 1L;
		Long storeId = 100L;

		User storeOwner = User.builder().id(loginUserId).build();
		Store store = Store.builder()
			.user(storeOwner)
			.storeName("네네치킨")
			.openTime("09:00")
			.closeTime("22:00")
			.minOrderPrice(10000)
			.isClosed(false)
			.categoryName("치킨")
			.build();
		ReflectionTestUtils.setField(store, "id", storeId);

		MenuCreateRequest request = MenuCreateRequest.builder()
			.storeId(storeId)
			.description("그럼요 당연하죠 네네치킨")
			.name("네네 파닭")
			.price(30000)
			.options(List.of(MenuOptionRequest.builder()
				.name("파 추가")
				.price(1000)
				.build()))
			.files(Collections.emptyList()) // 빈 리스트로 삽입
			.build();

		when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
		when(menuRepository.save(any(Menu.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(fileService.uploadAndCreateEntities(anyList(), anyString(), any())).thenReturn(List.of(
			MenuImage.builder().imgUrl("http://example.com/image1.jpg").build()));
		//when
		var response = menuService.saveMenu(request, loginUserId);
		//then
		assertThat(response.getMessage()).isEqualTo("메뉴 생성 성공");
		assertThat(response.getData().getName()).isEqualTo("네네 파닭");
		assertThat(response.getData().getPrice()).isEqualTo(30000);
		assertThat(response.getData().getOptions()).hasSize(1);
		assertThat(response.getData().getImageUrl()).isEqualTo("http://example.com/image1.jpg");

		verify(menuRepository, times(1)).save(any(Menu.class));
		verify(menuImageRepository, times(1)).saveAll(anyList());
		verify(fileService, times(1)).uploadAndCreateEntities(anyList(), eq("menu-images"), any());
	}

	/**
	 * 테스트: 존재하지 않는 Store ID를 저장 시 NOT_FOUND_STORE 예외 발생 검증
	 */
	@Test
	void saveMenu_storeNotFound_throwsMenuException() {
		//given
		Long nonExistStoreId = 999L; // 존재하지 않는 ID
		MenuCreateRequest request = MenuCreateRequest.builder()
			.storeId(nonExistStoreId)
			.description("그럼요 당연하죠 네네치킨")
			.name("네네 파닭")
			.price(30000)
			.build();

		when(storeRepository.findById(nonExistStoreId)).thenReturn(Optional.empty());
		//when
		MenuException thrown = assertThrows(MenuException.class, () -> {
			menuService.saveMenu(request, 1L);
		});
		//then
		assertEquals(MenuExceptionCode.NOT_FOUND_STORE, thrown.getResponseCode());
	}

	/**
	 * Save menu user not store owner throws not owner of store exception.
	 */
	@Test
	void saveMenu_userNotStoreOwner_throwsNotOwnerOfStoreException() {
		// given
		Long loginUserId = 999L;
		Long storeId = 100L;

		User storeOwner = User.builder().id(100L).build();
		Store store = Store.builder()
			.user(storeOwner)
			.storeName("네네치킨")
			.openTime("09:00")
			.closeTime("22:00")
			.minOrderPrice(10000)
			.isClosed(false)
			.categoryName("치킨")
			.build();
		ReflectionTestUtils.setField(store, "id", storeId);

		MenuCreateRequest request = MenuCreateRequest.builder()
			.storeId(storeId)
			.description("그럼요 당연하죠 네네치킨")
			.name("네네 파닭")
			.price(30000)
			.build();

		when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

		// when
		MenuException thrown = assertThrows(MenuException.class, () -> {
			menuService.saveMenu(request, loginUserId);
		});

		// then
		assertEquals(MenuExceptionCode.NOT_OWNER_OF_STORE, thrown.getResponseCode());
	}

	/**
	 * Test image upload and linking success.
	 *
	 * @throws IOException the io exception
	 */
	@Test
	void testImageUploadAndLinkingSuccess() throws IOException {
		// given
		Long loginUserId = 1l;
		Long storeId = 1L;

		User storeOwner = User.builder().id(loginUserId).build();
		Store store = Store.builder()
			.user(storeOwner)
			.storeName("네네치킨")
			.openTime("09:00")
			.closeTime("22:00")
			.minOrderPrice(10000)
			.isClosed(false)
			.categoryName("치킨")
			.build();
		ReflectionTestUtils.setField(store, "id", storeId);

		String fileContent = "file content";
		MockMultipartFile file = new MockMultipartFile(
			"file", "image.jpg", "image.jpeg",
			new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8)));

		MenuCreateRequest request = MenuCreateRequest.builder()
			.storeId(storeId)
			.description("그럼요 당연하죠 네네치킨")
			.name("네네 스노윙 어니언")
			.price(25000)
			.options(List.of())
			.files(List.of(file))
			.build();

		MenuImage menuImage = MenuImage.builder()
			.imgUrl("http://example.com/image1.jpg")
			.build();

		when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
		when(fileService.uploadAndCreateEntities(anyList(), anyString(), any())).thenReturn(List.of(menuImage));
		// when
		CommonResponse<MenuResponse> response = menuService.saveMenu(request, loginUserId);
		// then
		assertThat(response.getMessage()).isEqualTo("메뉴 생성 성공");
		assertThat(response.getData().getImageUrl()).isEqualTo("http://example.com/image1.jpg");

		verify(menuRepository, times(1)).save(any(Menu.class));
		verify(menuImageRepository, times(1)).saveAll(anyList());
		verify(fileService, times(1)).uploadAndCreateEntities(anyList(), eq("menu-images"), any());
	}
}
