package sparta.bunny.domain.menu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sparta.bunny.domain.menu.entity.Menu;

/**
 * The interface Menu repository.
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

	/**
	 * Find all by store id list.
	 *
	 * @param storeId the store id
	 * @return the list
	 */
	List<Menu> findAllByStoreId(Long storeId);

	List<Menu> findMenusWithOptionsAndImagesByStoreId(Long storeId);

}

