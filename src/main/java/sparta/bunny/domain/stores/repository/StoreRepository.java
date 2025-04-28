package sparta.bunny.domain.stores.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import sparta.bunny.domain.stores.entity.Category;
import sparta.bunny.domain.stores.entity.Store;
import sparta.bunny.domain.user.entity.User;

public interface StoreRepository extends JpaRepository<Store, Long> {

	// 모든 가게 조회(사장님이여서 폐업 상관없이 자기 가게 전체 조회)
	Page<Store> findAllByUser(User user, Pageable pageable);

	// 카테고리별로 전체 가게 조회(사장님이여서 폐업 상관없이 자기 가게 전체 조회)
	Page<Store> findAllByUserAndCategoryName(User user, Category categoryName, Pageable pageable);

	// keyword 가 들어간 가게 이름 찾기
	@EntityGraph(attributePaths = {"menus"})
	List<Store> findByStoreNameContaining(String keyword);

	// 카테고리별로 전체 가게 조회(사용자 부분에서 사용, 폐업한 가게 제외)
	Page<Store> findAllByCategoryNameAndIsClosedFalse(Category categoryName, Pageable pageable);

	// 폐업하지 않은 모든 가게 조회(사용자 부분에서 사용)
	Page<Store> findAllByIsClosedFalse(Pageable pageable);

	// 가게 수
	long countByUser(User user);

}