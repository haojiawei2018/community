package org.hopeframework.core.cache;

import org.hopeframework.core.constant.RedisConstans;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 基础数据缓存信息
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
@Component
public class BasicDataDao {

	/**
	 * 业务异常数据加入到redis中
	 *
	 * @param systemBasicData
	 */
	@CachePut(cacheNames = RedisConstans.CACHE_NAME_BASIC_DATA_BUSINESS, key = "T(org.hopeframework.core.constant.RedisConstans).CACHE_NAME_BASIC_DATA_BUSINESS +#p0.confKey")
	public SystemBasicData putBusiness(SystemBasicData systemBasicData) {
		return systemBasicData;
	}

	/**
	 * IP白名单加入到redis中
	 *
	 * @param systemBasicData
	 */
	@CachePut(cacheNames = RedisConstans.CACHE_NAME_BASIC_DATA_IPLIMIT, key = "T(org.hopeframework.core.constant.RedisConstans).CACHE_NAME_BASIC_DATA_IPLIMIT +#p0.confKey")
	public SystemBasicData putIplimit(SystemBasicData systemBasicData) {
		return systemBasicData;
	}

	/**
	 * 获取redis中业务异常数据
	 *
	 * @param confKey
	 * @return
	 */
	@Cacheable(cacheNames = RedisConstans.CACHE_NAME_BASIC_DATA_BUSINESS, key = "T(org.hopeframework.core.constant.RedisConstans).CACHE_NAME_BASIC_DATA_BUSINESS +#p0")
	public SystemBasicData getBusiness(String confKey) {
		return null;
	}

	/**
	 * 获取redis中IP白名单
	 *
	 * @param confKey
	 * @return
	 */
	@Cacheable(cacheNames = RedisConstans.CACHE_NAME_BASIC_DATA_IPLIMIT, key = "T(org.hopeframework.core.constant.RedisConstans).CACHE_NAME_BASIC_DATA_IPLIMIT +#p0")
	public SystemBasicData getIplimit(String confKey) {
		return null;
	}
}
