package sparta.bunny.domain.Favorites.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.bunny.domain.stores.entity.Store;
import sparta.bunny.domain.user.entity.User;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "store_id")
	private Store store;

	public Favorite(User user, Store store) {
		this.user = user;
		this.store = store;
	}
}
