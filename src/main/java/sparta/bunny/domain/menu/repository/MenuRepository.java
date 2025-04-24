package sparta.bunny.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sparta.bunny.domain.menu.entity.Menu;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
}
