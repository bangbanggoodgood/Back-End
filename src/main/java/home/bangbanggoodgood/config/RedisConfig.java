package home.bangbanggoodgood.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisConfig {

    // Redis 서버에 연결하는 Factory 설정
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // LettuceConnectionFactory를 사용하여 Redis 서버와 연결 설정
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", 6379));  // Redis 호스트와 포트를 설정
    }

    // RedisTemplate 설정 (redisConnectionFactory를 통해 Redis와 연결)
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);  // RedisConnectionFactory 주입
        template.setKeySerializer(new StringRedisSerializer());  // Key 직렬화
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());  // Value 직렬화
        return template;
    }
}
