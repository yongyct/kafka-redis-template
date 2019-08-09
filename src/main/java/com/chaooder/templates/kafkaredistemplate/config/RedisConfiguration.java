package com.chaooder.templates.kafkaredistemplate.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configurations for Redis connection with the application
 * 
 * @author tommy.yong
 *
 */
@Configuration
public class RedisConfiguration {

	@Value("${redis.instances}")
	private String[] instances;
	@Value("${redis.password}")
	private String password;

	/**
	 * Picks up any implemented connection factory depending on whether
	 * {@code redis.iscluster} is true or false
	 */
	@Autowired
	private JedisConnectionFactory jedisConnectionFactory;

	/**
	 * 
	 * Conditional Redis Cluster Connection Bean
	 * 
	 * @return redis cluster connection factory
	 */
	@Bean
	@ConditionalOnProperty("redis.iscluster")
	JedisConnectionFactory jedisClusterConnectionFactory() {

		RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration();

		for (String instance : instances) {
			String host = instance.split(":")[0];
			int port = Integer.parseInt(instance.split(":")[1]);
			RedisNode node = new RedisNode(host, port);
			clusterConfig.addClusterNode(node);
		}

		RedisPassword redisPassword = RedisPassword.of(password);
		clusterConfig.setPassword(redisPassword);

		return new JedisConnectionFactory(clusterConfig);
	}

	/**
	 * 
	 * Conditional bean for redis standalone connection
	 * 
	 * TODO: Clean up to use & test
	 * {@code @ConditionalOnProperty("redis.iscluster", havingValue = "false")}
	 * 
	 * @return redis standalone connection factory
	 */
	@Bean
	@ConditionalOnMissingBean(name = "jedisClusterConnectionFactory")
	JedisConnectionFactory jedisStandaloneConnectionFactory() {

		RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration();

		String instance = instances[0];
		String host = instance.split(":")[0];
		int port = Integer.parseInt(instance.split(":")[1]);
		RedisPassword redisPassword = RedisPassword.of(password);

		standaloneConfig.setHostName(host);
		standaloneConfig.setPort(port);
		standaloneConfig.setPassword(redisPassword);

		return new JedisConnectionFactory(standaloneConfig);
	}

	/**
	 * Bean for executing redis operations
	 * 
	 * @param <K>
	 * @param <V>
	 * @return redis template for {@link RedisSession} to use to carry out redis
	 *         operations
	 */
	@Bean
	public <K, V> RedisTemplate<K, V> redisTemplate() {

		RedisTemplate<K, V> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		return redisTemplate;

	}

}
