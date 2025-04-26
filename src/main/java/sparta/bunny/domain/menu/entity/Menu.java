package sparta.bunny.domain.menu.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.bunny.common.audit.BaseEntity;
import sparta.bunny.domain.menu.dto.request.MenuOptionRequest;
import sparta.bunny.domain.menu.dto.request.MenuUpdateRequest;
import sparta.bunny.domain.menu.enums.Status;
import sparta.bunny.domain.stores.entity.Store;

@Entity
@Table(name = "menus")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(nullable = false)
	private Integer price;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status = Status.ACTIVE;

	@OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<MenuOption> options = new ArrayList<>();

	@OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<MenuImage> images = new ArrayList<>();

	public void updateMenu(MenuUpdateRequest request) {
		this.name = request.getName();
		this.description = request.getDescription();
		this.price = request.getPrice();
		this.status = request.getStatus();
		this.options.clear();
		for (MenuOptionRequest optionRequest : request.getOptions()) {
			MenuOption option = MenuOption.builder()
				.name(optionRequest.getName())
				.price(optionRequest.getPrice())
				.menu(this)
				.build();
			this.options.add(option);
		}
	}

	public void changeStatus(Status status) {
		this.status = status;
	}
}
