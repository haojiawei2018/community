package org.hopeframework.core.cache;

import org.hopeframework.core.constant.RedisConstans;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 访问accesskey,id缓存
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
@Component
public class AccessDao {

	@CachePut(cacheNames = RedisConstans.CACHE_NAME_ACCESS, key = "T(org.hopeframework.core.constant.RedisConstans).CACHE_NAME_ACCESS +#accessInfo.userNo", cacheManager = "cacheManagerAccess")
	public AccessInfo putRedis(AccessInfo accessInfo) {
		return accessInfo;
	}

	@Cacheable(cacheNames = RedisConstans.CACHE_NAME_ACCESS, key = "T(org.hopeframework.core.constant.RedisConstans).CACHE_NAME_ACCESS +#p0")
	public AccessInfo get(String userNo) {
		return null;
	}

	/**
	 * 固定 ACCESS信息放于redis
	 *
	 * @param accessInfo
	 * @return
	 */
	@CachePut(cacheNames = RedisConstans.CACHE_NAME_ACCESS, key = "T(org.hopeframework.core.constant.RedisConstans).CACHE_NAME_ACCESS +#accessInfo.userNo")
	public AccessInfo putRedisFix(AccessInfo accessInfo) {
		return accessInfo;
	}
}
