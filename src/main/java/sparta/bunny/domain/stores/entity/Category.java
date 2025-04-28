package sparta.bunny.domain.stores.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Category {
	KOREAN,         // 한식
	WESTERN,        // 양식
	JAPANESE,       // 일식
	CHINESE,        // 중식
	CAFE,           // 카페
	FAST_FOOD,      // 패스트푸드
	SNACK,          // 분식
	PORK_CUTLET,    // 돈까스
	SUSHI,          // 회
	LUNCH_BOX,      // 도시락
	CHICKEN,        // 치킨
	PIZZA,          // 피자
	ASIAN,          // 아시안
	JOKBAL,         // 족발
	BOSSAM,         // 보쌈
	LATE_NIGHT;     // 야식

	@JsonProperty("category_name")
	public String toJson() {
		return this.name();
	}
}
