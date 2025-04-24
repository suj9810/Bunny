package sparta.bunny.domain.stores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.bunny.domain.stores.entity.Store;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    //단일 가게 조회 (폐업한 가게는 빼고)
    Optional<Store> findByIdAndIsClosedFalse(Long id);

    // 전체 가게 조회(폐업한 가게는 빼고)
    List<Store> findAllByIsClosedFalse();

    // 카테고리별 가게 조회(폐업한 가게는 빼고)
    List<Store> findAllByCategoryNameAndIsClosedFalse(String categoryName);

    Optional<Store> findById(Long Id);
}
