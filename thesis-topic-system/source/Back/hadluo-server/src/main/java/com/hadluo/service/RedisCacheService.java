package com.hadluo.service;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.hadluo.config.RedisProperties;
import com.hadluo.utils.RedisCacheKeys;

@Service
public class RedisCacheService {

	private static final Logger log = LoggerFactory.getLogger(RedisCacheService.class);

	@Autowired
	private RedisProperties redisProperties;

	@Autowired(required = false)
	private StringRedisTemplate stringRedisTemplate;

	public <T> T getOrLoad(String key, Class<T> clazz, Supplier<T> loader, long ttlSeconds) {
		if (!isAvailable()) {
			return loader.get();
		}
		try {
			String json = stringRedisTemplate.opsForValue().get(key);
			if (StringUtils.isNotBlank(json)) {
				return JSON.parseObject(json, clazz);
			}
		} catch (Exception e) {
			log.warn("Redis read failed, key={}, fallback to DB: {}", key, e.getMessage());
		}
		T value = loader.get();
		put(key, value, ttlSeconds);
		return value;
	}

	public <T> T getOrLoad(String key, Type type, Supplier<T> loader, long ttlSeconds) {
		if (!isAvailable()) {
			return loader.get();
		}
		try {
			String json = stringRedisTemplate.opsForValue().get(key);
			if (StringUtils.isNotBlank(json)) {
				return JSON.parseObject(json, type);
			}
		} catch (Exception e) {
			log.warn("Redis read failed, key={}, fallback to DB: {}", key, e.getMessage());
		}
		T value = loader.get();
		put(key, value, ttlSeconds);
		return value;
	}

	public void put(String key, Object value, long ttlSeconds) {
		if (!isAvailable() || value == null || ttlSeconds <= 0) {
			return;
		}
		try {
			stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(value), ttlSeconds, TimeUnit.SECONDS);
		} catch (Exception e) {
			log.warn("Redis write failed, key={}: {}", key, e.getMessage());
		}
	}

	public void evictByPrefix(String prefix) {
		if (!isAvailable() || StringUtils.isBlank(prefix)) {
			return;
		}
		try {
			Set<String> keys = stringRedisTemplate.keys(prefix + "*");
			if (keys != null && !keys.isEmpty()) {
				stringRedisTemplate.delete(keys);
			}
		} catch (Exception e) {
			log.warn("Redis evict failed, prefix={}: {}", prefix, e.getMessage());
		}
	}

	public void evictConfigCache() {
		evictByPrefix(RedisCacheKeys.CONFIG_INFO);
		evictByPrefix(RedisCacheKeys.CONFIG_LIST);
	}

	public void evictMenuCache() {
		evictByPrefix(RedisCacheKeys.MENU_LIST);
	}

	public void evictDashboardCache() {
		evictByPrefix(RedisCacheKeys.DASHBOARD_STATS);
	}

	private boolean isAvailable() {
		return redisProperties.isEnabled() && stringRedisTemplate != null;
	}
}
