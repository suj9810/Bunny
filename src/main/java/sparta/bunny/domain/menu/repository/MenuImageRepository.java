package sparta.bunny.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sparta.bunny.domain.menu.entity.MenuImage;

/**
 * Menu Image Repository
 */
@Repository
public interface MenuImageRepository extends JpaRepository<MenuImage, Long> {
}
