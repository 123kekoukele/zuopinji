package com.hadluo.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@ConditionalOnProperty(prefix = "redis", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RedisConfig {

	@Bean
	public RedisConnectionFactory redisConnectionFactory(RedisProperties props) {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
		config.setHostName(props.getHost());
		config.setPort(props.getPort());
		config.setDatabase(props.getDatabase());
		if (StringUtils.isNotBlank(props.getPassword())) {
			config.setPassword(RedisPassword.of(props.getPassword()));
		}
		LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
		factory.afterPropertiesSet();
		return factory;
	}

	@Bean
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(redisConnectionFactory);
		template.afterPropertiesSet();
		return template;
	}
}
