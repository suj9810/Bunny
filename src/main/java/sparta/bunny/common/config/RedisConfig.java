package sparta.bunny.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import sparta.bunny.domain.cart.entity.Cart;

@Configuration
public class RedisConfig {

	@Bean
	public RedisTemplate<String, Cart> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Cart> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		// 직렬화 설정 (Cart 저장 가능하게)
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

		return template;
	}
}
