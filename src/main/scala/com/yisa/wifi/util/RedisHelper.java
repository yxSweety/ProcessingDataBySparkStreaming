package com.yisa.wifi.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yisa.wifi.zookeeper.ZKUtil;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

/**
 * Redis操作接口
 * 
 * @author DangT
 * @date 2015年12月16日 上午11:31:43
 * @version V1.0
 */
public class RedisHelper {

	// sentinel名称
	private static String SENTINEL_NAME ;
	private static String SENTINEL_HOST ;
	private static int SENTINEL_PORT;

	private static Set<String> set = new HashSet<String>();
	
	
	static{
		ZKUtil sample = new ZKUtil();
		sample.createConnection();

		SENTINEL_NAME = sample.readData("/yisaconfig/cpa/sentinel_name");
		SENTINEL_HOST = sample.readData("/yisaconfig/cpa/sentinel_host");
		SENTINEL_PORT = Integer.valueOf(sample.readData("/yisaconfig/cpa/sentinel_port"));
		

		sample.releaseConnection();
	}

	static {
		String[] hosts = SENTINEL_HOST.split(",");
		for (String host : hosts) {
			set.add(String.valueOf(new HostAndPort(host, SENTINEL_PORT)));
		}
	}

	private static JedisSentinelPool pool = new JedisSentinelPool(SENTINEL_NAME, set);
 
	/**
	 * 获取Jedis实例
	 *
	 * @return
	 */
	public static Jedis getJedis() throws Exception {

		return pool.getResource();

	}

	/**
	 * 返还到连接池
	 * 
	 * @param pool
	 * @param redis
	 */
	public static void returnResource(Jedis jedis) {

		if (jedis != null) {
			pool.returnResource(jedis);
		} else {
			pool.returnBrokenResource(jedis);
		}

	}

	/**
	 * 保存HashMap类型的值
	 *
	 * @param key
	 * @param map
	 * @throws Exception 
	 */
	public static void hmset(String key, Map<String, String> map) throws Exception {
		
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.hmset(key, map);
		} catch (Exception e) {
			throw e;
		} finally {
			returnResource(jedis);
		}
		
	}
	
	/**
	 * 保存HashMap类型的值
	 *
	 * @param jedis
	 * @param key
	 * @param map
	 */
	public static void hmset(Jedis jedis, String key, Map<String, String> map) {
		
		jedis.hmset(key, map);
		
	}
	
	/**
	 * 获取数据HashMap类型的值
	 *
	 * @param key
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	public static List<String> hmget(String key, String... fields) throws Exception {

		List<String> list = new ArrayList<String>();
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			list = jedis.hmget(key, fields);
		} catch (Exception e) {
			throw e;
		} finally {
			returnResource(jedis);
		}
		return list;

	}

	/**
	 * 获取数据HashMap类型的值
	 *
	 * @param jedis
	 * @param key
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	public static List<String> hmget(Jedis jedis, String key, String... fields) throws Exception {

		return jedis.hmget(key, fields);

	}

	public static void set(Jedis jedis, String key, String value, int daysToExpired) throws Exception {

		jedis.set(key, value);
		if (daysToExpired != -1) {
			jedis.expire(key, daysToExpired * 24 * 60 * 60);
		}

	}

	public static String get(Jedis jedis, String key) throws Exception {

		return jedis.get(key);

	}

	public static void incr(Jedis jedis, String key) throws Exception {

		jedis.incr(key);

	}

	public static Set<String> getKeys(Jedis jedis, String pattern) throws Exception {

		return jedis.keys(pattern);

	}

	public static boolean isKeyExist(Jedis jedis, String key) throws Exception {

		return jedis.exists(key);

	}

}