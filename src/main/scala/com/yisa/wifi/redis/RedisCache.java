package com.yisa.wifi.redis;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.yisa.wifi.util.EquLocaMySQLHelper;
import com.yisa.wifi.util.RedisHelper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * Redis缓存初始化工具类
 * 
 * @author DangT
 * @date 2015年9月22日 上午9:30:18
 * @version V1.0
 */
public class RedisCache {
	
//	private static int preRefreshDate = 0;

	/**
	 * Redis数据库索引-设备和位置对应关系缓存
	 */
	public static final int DT_DB_INDEX_DEVICE_LOCATION = 4;

	/**
	 * 初始化Redis缓存
	 *
	 * @throws Exception
	 */
	public static void init() throws Exception {

		Connection conn = null;
		Jedis jedis = null;
		try {
			conn = EquLocaMySQLHelper.openConnection();

			// 删除原有缓存
			jedis = RedisHelper.getJedis();
			jedis.select(DT_DB_INDEX_DEVICE_LOCATION);
			jedis.flushDB();

			// 缓存数据
			addDeviceLocationRelToRedis(conn, jedis);
		} catch (Exception e) {
			throw e;
		} finally {
			EquLocaMySQLHelper.close(conn);
			RedisHelper.returnResource(jedis);
		}

	}

	/**
	 * 缓存设备和位置对应关系
	 *
	 * @param conn
	 * @param jedis
	 * @throws Exception
	 */
	private static void addDeviceLocationRelToRedis(Connection conn, Jedis jedis)
			throws Exception {

		System.out.println("开始设备和位置对应关系缓存...");

		String sql = "select equ_id,location_id from wifi_location";
		ResultSet rs = null;
		try {
			// 执行语句，得到结果集
			rs = EquLocaMySQLHelper.query(conn, sql);

			Pipeline p = jedis.pipelined();
			p.multi();
			int count = 0;
			while (rs.next()) {
				count++;
				// Key:equ_id + "_" + location_id, Value:location_id
				p.set(rs.getString(1) + "_" + rs.getString(2), rs.getString(2));
			}
			p.exec();
			p.sync();

			System.out.println("设备和位置对应关系缓存建立完毕，共计：" + count + "条");
		} catch (Exception e) {
			throw e;
		} finally {
			EquLocaMySQLHelper.close(rs);
		}

	}

	/**
	 * 根据WiFi采集设备编号查找Location
	 *
	 * @param equId
	 * @return
	 * @throws Exception
	 */
	public static List<String> getLocationIds(String equId) throws Exception {

		List<String> locationIds = new ArrayList<String>();
		
//		 //每天重建一次设置与位置对应关系
//		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
//		Date date = new Date(); 
//		int nowDate = Integer.valueOf(format.format(date));
//		if(nowDate >  preRefreshDate){
//			System.out.println("开始重建设备和位置对应关系");
//			init();
//			preRefreshDate = nowDate;
//		}

		Jedis jedis = null;
		try {
			jedis = RedisHelper.getJedis();
			jedis.select(DT_DB_INDEX_DEVICE_LOCATION);
			Set<String> keys = jedis.keys(equId + "_*");
			for (String key : keys) {
				locationIds.add(jedis.get(key));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			RedisHelper.returnResource(jedis);
		}
		return locationIds;

	}

}
