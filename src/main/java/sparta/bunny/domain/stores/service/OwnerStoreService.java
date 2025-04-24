package sparta.bunny.domain.stores.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.bunny.domain.menu.dto.response.MenuResponse;
import sparta.bunny.domain.menu.dto.response.MenuOptionResponse;
import sparta.bunny.domain.menu.entity.Menu;
import sparta.bunny.domain.menu.repository.MenuRepository;
import sparta.bunny.domain.stores.code.StoreExceptionCode;
import sparta.bunny.domain.stores.dto.StoreRequestDto;
import sparta.bunny.domain.stores.dto.StoreResponseDto;
import sparta.bunny.domain.stores.dto.StoreWithMenuResponseDto;
import sparta.bunny.domain.stores.entity.Store;
import sparta.bunny.domain.stores.exception.StoreException;
import sparta.bunny.domain.stores.repository.StoreRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerStoreService {

    private final StoreRepository storeRepository;
    private MenuRepository menuRepository;

    // 가게 등록
    @Transactional
    public void createStore(StoreRequestDto requestDto) {
        Store store = Store.builder()
                .storeName(requestDto.getStoreName())
                .openTime(requestDto.getOpenTime())
                .closeTime(requestDto.getCloseTime())
                .minOrderPrice(requestDto.getMinOrderPrice())
                .isClosed(false)
                .categoryName(requestDto.getCategoryName())
                .build();
        storeRepository.save(store);
    }

    // 가게 수정
    @Transactional
    public void updateStore(Long storeId, StoreRequestDto requestDto) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new StoreException(StoreExceptionCode.STORE_NOT_FOUND));
        store.updateStore(
                requestDto.getStoreName(),
                requestDto.getOpenTime(),
                requestDto.getCloseTime(),
                requestDto.getMinOrderPrice(),
                requestDto.getNotice(),
                requestDto.getCategoryName()
        );
    }

    //가게 폐업 처리
    @Transactional
    public void closeStore(Long storeId, boolean closure) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreExceptionCode.STORE_NOT_FOUND));
        if (closure) {
            store.close();  // 폐업 처리
        } else {
            store.reopen();  // 폐업 해제 처리
        }
    }

    // 전체 가게 조회(폐업한 가게 제외)
    @Transactional(readOnly = true)
    public List<StoreResponseDto> getAllStores(String categoryName, int page, int size) {
        if (categoryName != null) {
            return storeRepository.findAllByCategoryNameAndIsClosedFalse(categoryName).stream()
                    .map(store -> new StoreResponseDto(
                            store.getId(),
                            store.getStoreName(),
                            store.getOpenTime(),
                            store.getCloseTime(),
                            store.getMinOrderPrice(),
                            store.getNotice(),
                            store.getIsClosed(),
                            store.getCategoryName()
                    ))
                    .collect(Collectors.toList());
        } else {
            return storeRepository.findAllByIsClosedFalse().stream()
                    .map(store -> new StoreResponseDto(
                            store.getId(),
                            store.getStoreName(),
                            store.getOpenTime(),
                            store.getCloseTime(),
                            store.getMinOrderPrice(),
                            store.getNotice(),
                            store.getIsClosed(),
                            store.getCategoryName()
                    ))
                    .collect(Collectors.toList());
        }
    }

//    // 단일 가게 조회(메뉴 포함)
//    @Transactional(readOnly = true)
//    public StoreWithMenuResponseDto getStoreWithMenus(Long storeId) {
//        Store store = storeRepository.findByStoreId(storeId).orElseThrow(() -> new StoreException(StoreExceptionCode.STORE_NOT_FOUND));
//        List<Menu> menus = menuRepository.findAllByStoreId(storeId);
//
//        List<MenuResponse> menuResponses = menus.stream()
//                .map(menu -> MenuResponse.builder()
//                        .menuId(menu.getId())
//                        .name(menu.getName())
//                        .description(menu.getDescription())
//                        .price(menu.getPrice())
//                        .imageUrl(menu.getImageUrl())
//                        .status(menu.getStatus().name())
//                        .options(menu.getOptions().stream()
//                                .map(MenuOptionResponse::of)
//                                .collect(Collectors.toList()))
//                        .build())
//                .collect(Collectors.toList());
//
//        return StoreWithMenuResponseDto.builder()
//                .id(store.getId())
//                .storeName(store.getStoreName())
//                .openTime(store.getOpenTime())
//                .closeTime(store.getCloseTime())
//                .minOrderPrice(store.getMinOrderPrice())
//                .notice(store.getNotice())
//                .isClosed(store.getIsClosed())
//                .categoryName(store.getCategoryName())
//                .menu(menuResponses)
//                .build();
//    }

}
