package sparta.bunny.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sparta.bunny.domain.menu.entity.Menu;
import sparta.bunny.domain.menu.entity.MenuImage;

/**
 * 메뉴 이미지 Repository
 */
@Repository
public interface MenuImageRepository extends JpaRepository<MenuImage, Long> {
	void deleteByMenu(Menu menu);
}
