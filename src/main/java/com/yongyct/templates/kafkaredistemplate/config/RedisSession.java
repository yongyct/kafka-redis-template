package com.yongyct.templates.kafkaredistemplate.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Bean that contains a {@link redisTemplate} to carry out redis-related
 * operations. More methods to be added depending on use cases.
 * 
 * @author tommy.yong
 *
 */
public class RedisSession {
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	public String get(String key) {
		return redisTemplate.opsForValue().get(key);
	}
	
	public void set(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}
	
	public void del(String key) {
		redisTemplate.delete(key);
	}

}
