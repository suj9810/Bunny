package sparta.bunny.domain.Favorites.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sparta.bunny.domain.Favorites.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

	// 즐겨찾기한 가게 조회
	boolean existsByUserIdAndStoreId(Long userId, Long storeId);

	// 즐겨찾기 취소
	void deleteByUserIdAndStoreId(Long userId, Long storeId);
}
