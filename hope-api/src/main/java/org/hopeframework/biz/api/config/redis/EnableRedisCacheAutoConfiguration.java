package org.hopeframework.biz.api.config.redis;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * redis缓存配置
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
@Configuration
public class EnableRedisCacheAutoConfiguration extends CachingConfigurerSupport {

	/**
	 * Redis connection configuration.
	 */
	@Configuration
	@ConditionalOnClass(GenericObjectPool.class)
	protected static class RedisConnectionConfiguration {

		private final RedisProperties properties;

		private final RedisSentinelConfiguration sentinelConfiguration;

		private final RedisClusterConfiguration clusterConfiguration;

		public RedisConnectionConfiguration(RedisProperties properties,
				ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider,
				ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider) {
			this.properties = properties;
			this.sentinelConfiguration = sentinelConfigurationProvider.getIfAvailable();
			this.clusterConfiguration = clusterConfigurationProvider.getIfAvailable();
		}

		@Bean(name = "redisConnectionFactory")
		@Primary
		public JedisConnectionFactory redisConnectionFactory() throws UnknownHostException {
			return applyProperties(createJedisConnectionFactory());
		}

		protected final JedisConnectionFactory applyProperties(JedisConnectionFactory factory) {
			factory.setHostName(this.properties.getHost());
			factory.setPort(this.properties.getPort());
			if (this.properties.getPassword() != null) {
				factory.setPassword(this.properties.getPassword());
			}
			factory.setDatabase(this.properties.getDatabase());
			return factory;
		}

		protected final RedisSentinelConfiguration getSentinelConfig() {

			RedisProperties.Sentinel sentinelProperties = this.properties.getSentinel();

			// 判断是否已添加集群配置信息
			if (this.sentinelConfiguration != null) {

				// 取得master节点
				RedisNode master = (RedisNode) this.sentinelConfiguration.getMaster();

				// master节点存在，并且是配置的master节点，则使用该配置
				if (master != null && StringUtils.hasText(master.getHost())
						&& master.getHost().equals(sentinelProperties.getMaster())) {

					return this.sentinelConfiguration;

				}
			}

			if (sentinelProperties != null && StringUtils.hasText(sentinelProperties.getMaster())) {
				RedisSentinelConfiguration config = new RedisSentinelConfiguration();
				config.master(sentinelProperties.getMaster());
				config.setSentinels(createSentinels(sentinelProperties));
				return config;
			}
			return null;
		}

		/**
		 * Create a {@link RedisClusterConfiguration} if necessary.
		 * 
		 * @return {@literal null} if no cluster settings are set.
		 */
		protected final RedisClusterConfiguration getClusterConfiguration() {
			if (this.clusterConfiguration != null) {
				return this.clusterConfiguration;
			}
			if (this.properties.getCluster() == null) {
				return null;
			}
			RedisProperties.Cluster clusterProperties = this.properties.getCluster();
			RedisClusterConfiguration config = new RedisClusterConfiguration(clusterProperties.getNodes());

			if (clusterProperties.getMaxRedirects() != null) {
				config.setMaxRedirects(clusterProperties.getMaxRedirects());
			}
			return config;
		}

		private List<RedisNode> createSentinels(RedisProperties.Sentinel sentinel) {
			List<RedisNode> nodes = new ArrayList<RedisNode>();
			for (String node : sentinel.getNodes()) {
				try {
					String[] parts = StringUtils.split(node, ":");
					Assert.state(parts.length == 2, "Must be defined as 'host:port'");
					nodes.add(new RedisNode(parts[0], Integer.valueOf(parts[1])));
				} catch (RuntimeException ex) {
					throw new IllegalStateException("Invalid redis sentinel " + "property '" + node + "'", ex);
				}
			}
			return nodes;
		}

		private JedisConnectionFactory createJedisConnectionFactory() {

			JedisPoolConfig poolConfig = this.properties.getJedis().getPool() != null ? jedisPoolConfig() : new JedisPoolConfig();

			if (getSentinelConfig() != null) {
				return new JedisConnectionFactory(getSentinelConfig(), poolConfig);
			}
			if (getClusterConfiguration() != null) {
				return new JedisConnectionFactory(getClusterConfiguration(), poolConfig);
			}
			return new JedisConnectionFactory(poolConfig);
		}

		private JedisPoolConfig jedisPoolConfig() {
			JedisPoolConfig config = new JedisPoolConfig();
			RedisProperties.Pool props = this.properties.getJedis().getPool();
			config.setMaxTotal(props.getMaxActive());
			config.setMaxIdle(props.getMaxIdle());
			config.setMinIdle(props.getMinIdle());
			config.setMaxWaitMillis(props.getMaxWait().getSeconds() * 1000);
			return config;
		}

	}

	@Bean(name = "cloudRedisTemplate")
	@Qualifier(value = "redisConnectionFactory")
	public RedisTemplate<String, Object> voiceRedisTemplate(JedisConnectionFactory connectionFactory) {

		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(connectionFactory);
		// 设置序列化类
		FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);

		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(fastJsonRedisSerializer);

		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(fastJsonRedisSerializer);

		redisTemplate.afterPropertiesSet();

		return redisTemplate;

	}

	@Bean
	public KeyGenerator wiselyKeyGenerator() {
		return new KeyGenerator() {
			@Override
			public Object generate(Object target, Method method, Object... params) {
				StringBuilder sb = new StringBuilder();
				sb.append(target.getClass().getName());
				sb.append(method.getName());
				for (Object obj : params) {
					sb.append(obj.toString());
				}
				return sb.toString();
			}
		};

	}

	@Bean
	@Primary
	public CacheManager cacheManager(JedisConnectionFactory redisConnectionFactory) {
		RedisCacheManager cacheManager =  RedisCacheManager.create(redisConnectionFactory);
		return cacheManager;
	}

	
	/**
	 * Access缓存管理（0分钟过期）
	 * 
	 * @param  redisConnectionFactory
	 * @return
	 */
	@Bean(name = "cacheManagerAccess")
	public CacheManager cacheManagerAccess(JedisConnectionFactory redisConnectionFactory) {

		RedisCacheManager cacheManager = RedisCacheManager.create(redisConnectionFactory);

		return cacheManager;
	}

}