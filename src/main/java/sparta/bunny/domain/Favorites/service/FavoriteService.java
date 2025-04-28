package sparta.bunny.domain.Favorites.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sparta.bunny.domain.Favorites.code.FavoriteExceptionCode;
import sparta.bunny.domain.Favorites.entity.Favorite;
import sparta.bunny.domain.Favorites.exception.FavoriteException;
import sparta.bunny.domain.Favorites.repository.FavoriteRepository;
import sparta.bunny.domain.stores.entity.Store;
import sparta.bunny.domain.stores.repository.StoreRepository;
import sparta.bunny.domain.user.entity.User;
import sparta.bunny.domain.user.repository.UserRepository;

@Service
public class FavoriteService {

	private final FavoriteRepository favoriteRepository;
	private final StoreRepository storeRepository;
	private final UserRepository userRepository;

	@Autowired
	public FavoriteService(FavoriteRepository favoriteRepository, StoreRepository storeRepository,
		UserRepository userRepository) {
		this.favoriteRepository = favoriteRepository;
		this.storeRepository = storeRepository;
		this.userRepository = userRepository;
	}

	// 가게 즐겨찾기 추가
	@Transactional
	public void addFavorite(Long userId, Long storeId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new FavoriteException(FavoriteExceptionCode.STORE_NOT_FOUND));

		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new FavoriteException(FavoriteExceptionCode.STORE_NOT_FOUND));

		if (!favoriteRepository.existsByUserIdAndStoreId(userId, storeId)) {
			Favorite favorite = new Favorite(user, store);
			favoriteRepository.save(favorite);
		} else {
			throw new FavoriteException(FavoriteExceptionCode.FAVORITE_ALREADY_EXISTS);
		}
	}

	// 가게 즐겨찾기 삭제
	@Transactional
	public void removeFavorite(Long userId, Long storeId) {
		if (favoriteRepository.existsByUserIdAndStoreId(userId, storeId)) {
			favoriteRepository.deleteByUserIdAndStoreId(userId, storeId);
		} else {
			throw new FavoriteException(FavoriteExceptionCode.FAVORITE_NOT_FOUND);
		}
	}
}
