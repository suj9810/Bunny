package sparta.bunny.domain.menu.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import sparta.bunny.domain.stores.entity.Category;
import sparta.bunny.domain.stores.entity.Store;
import sparta.bunny.domain.stores.repository.StoreRepository;
import sparta.bunny.domain.user.entity.User;

/**
 * 메뉴 서비스 단위 테스트
 */
@DisplayName("MenuService 단위 테스트")
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

	@Test
	@DisplayName("메뉴 생성 성공 - 이미지 포함")
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
			.categoryName(Category.패스트푸드)
			.build();
		ReflectionTestUtils.setField(store, "id", storeId);

		MockMultipartFile mockFile = new MockMultipartFile(
			"files", // name
			"image.jpg", // originalFilename
			"image/jpeg", // contentType
			"test image content".getBytes() // content
		);

		MenuCreateRequest request = MenuCreateRequest.builder()
			.storeId(storeId)
			.description("그럼요 당연하죠 네네치킨")
			.name("네네 파닭")
			.price(30000)
			.options(List.of(MenuOptionRequest.builder()
				.name("파 추가")
				.price(1000)
				.build()))
			.files(List.of(mockFile)) // 빈 리스트로 삽입
			.build();

		given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
		given(menuRepository.save(any(Menu.class))).willAnswer(invocation -> invocation.getArgument(0));
		given(fileService.uploadAndCreateEntities(anyList(), anyString(), any())).willReturn(List.of(
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

	@DisplayName("메뉴 생성 실패 - 존재하지 않는 Store ID")
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

		given(storeRepository.findById(nonExistStoreId)).willReturn(Optional.empty());
		//when
		MenuException thrown = assertThrows(MenuException.class, () -> {
			menuService.saveMenu(request, 1L);
		});
		//then
		assertEquals(MenuExceptionCode.NOT_FOUND_STORE, thrown.getResponseCode());
	}

	@DisplayName("메뉴 생성 실패 - 유저가 매장 주인이 아님")
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
			.categoryName(Category.패스트푸드)
			.build();
		ReflectionTestUtils.setField(store, "id", storeId);

		MenuCreateRequest request = MenuCreateRequest.builder()
			.storeId(storeId)
			.description("그럼요 당연하죠 네네치킨")
			.name("네네 파닭")
			.price(30000)
			.build();

		given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

		// when
		MenuException thrown = assertThrows(MenuException.class, () -> {
			menuService.saveMenu(request, loginUserId);
		});

		// then
		assertEquals(MenuExceptionCode.NOT_OWNER_OF_STORE, thrown.getResponseCode());
	}

	@DisplayName("이미지 업로드 및 링크 성공")
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
			.categoryName(Category.패스트푸드)
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

		given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
		given(fileService.uploadAndCreateEntities(anyList(), anyString(), any())).willReturn(List.of(menuImage));
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
