package com.yisa.wifi.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

/**
 * 读取配置文件工具类
 * 
 * @author DangT
 * @date 2015年10月15日 下午5:52:56
 * @version V1.0
 */
public class ConfigUtil {

	private static Logger log = Logger.getLogger(ConfigUtil.class);
	
	private static CompositeConfiguration config = new CompositeConfiguration();

	static {
		try {
//			ResourceBundle rb = ResourceBundle.getBundle("conf-init");
//			String confNames = rb.getString("conf_names"); // 获取默认加载的配置文件名称
			String confNames = "/yisa_oe/config/cpa/wifi_conf.properties";
			for (String confName : confNames.split(";")) {
				if (confName.endsWith("properties")) {					
					config.addConfiguration(new PropertiesConfiguration(confName));
				} else if (confName.endsWith("xml")) {
					config.addConfiguration(new XMLConfiguration(confName));
				} else {
					log.error("不支持的文件类型：" + confName);
				}
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取配置文件中Key的前缀为prefix的配置信息或者XML配置文件中指定Key节点下的配置信息
	 * 例：
	 * 	Properties配置文件中有如下配置：
	 * 	test.name = test
	 *	test.password = 111
	 *
	 *	subset("test")返回信息如下：
	 *	name = test
     *	password = 111
	 *
	 * 例：
	 * XML配置文件中有如下配置：
	 * <root>
	 *		<name type='type'>zhaipuhong</name>
	 *		<gender>male</gender>
	 *		<birthday>
	 *			<year>1970</year>
	 *			<month>12</month>
	 *			<day>17</day>
	 *		</birthday>
	 *	</root>
	 *
	 *	subset("birthday")返回信息如下：
	 *	year = 1970
     *	month = 12
     *	day = 17
     *
	 * @param prefix
	 * @return
	 */
	public static Configuration subset(String prefix) {

		return config.subset(prefix);

	}

	/**
	 * 判断配置信息是否为空
	 *
	 * @return
	 */
	public static boolean isEmpty() {

		return config.isEmpty();

	}

	/**
	 * 判断配置文件中是否包含指定的key
	 *
	 * @param key
	 * @return
	 */
	public static boolean containsKey(String key) {

		return config.containsKey(key);

	}

	/**
	 * 添加属性配置信息
	 *
	 * @param key
	 * @param value
	 */
	public static void addProperty(String key, Object value) {

		config.addProperty(key, value);

	}

	/**
	 * 设置属性的值
	 *
	 * @param key
	 * @param value
	 */
	public static void setProperty(String key, Object value) {

		config.setProperty(key, value);

	}

	/**
	 * 从配置信息中删除指定Key的配置
	 *
	 * @param key
	 */
	public static void clearProperty(String key) {

		config.clearProperty(key);

	}

	/**
	 * 清空配置信息
	 */
	public static void clear() {

		config.clear();

	}

	/**
	 * 从配置文件中获取Key的前缀为prefix的Key
	 * 例：
	 * 	配置文件中有如下配置：
	 * 	test.name = test
	 *	test.password = 111
	 *
	 *	getKeys("test")返回信息如下：
	 *	[test.name, test.password]
	 *
	 * @param prefix
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getKeys(String prefix) {

		List<String> list = new ArrayList<String>();
		Iterator<String> iterator = config.getKeys(prefix);
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		return list;

	}

	/**
	 * 从配置文件中获取所有的Key
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getKeys() {

		List<String> list = new ArrayList<String>();
		Iterator<String> iterator = config.getKeys();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		return list;

	}

	public static Properties getProperties(String key) {

		return config.getProperties(key);

	}

	public static boolean getBoolean(String key) {

		return config.getBoolean(key);

	}

	public static boolean getBoolean(String key, boolean defaultValue) {

		return config.getBoolean(key, defaultValue);

	}

	public static Boolean getBoolean(String key, Boolean defaultValue) {

		return config.getBoolean(key, defaultValue);

	}

	public static byte getByte(String key) {

		return config.getByte(key);

	}

	public static byte getByte(String key, byte defaultValue) {

		return config.getByte(key, defaultValue);

	}

	public static Byte getByte(String key, Byte defaultValue) {

		return config.getByte(key, defaultValue);

	}

	public static double getDouble(String key) {

		return config.getDouble(key);

	}

	public static double getDouble(String key, double defaultValue) {

		return config.getDouble(key, defaultValue);

	}

	public static Double getDouble(String key, Double defaultValue) {

		return config.getDouble(key, defaultValue);

	}

	public static float getFloat(String key) {

		return config.getFloat(key);

	}

	public static float getFloat(String key, float defaultValue) {

		return config.getFloat(key, defaultValue);

	}

	public static Float getFloat(String key, Float defaultValue) {

		return config.getFloat(key, defaultValue);

	}

	public static int getInt(String key) {

		return config.getInt(key);

	}

	public static int getInt(String key, int defaultValue) {

		return config.getInt(key, defaultValue);

	}

	public static Integer getInteger(String key, Integer defaultValue) {

		return config.getInteger(key, defaultValue);

	}

	public static long getLong(String key) {

		return config.getLong(key);

	}

	public static long getLong(String key, long defaultValue) {

		return config.getLong(key, defaultValue);

	}

	public static Long getLong(String key, Long defaultValue) {

		return config.getLong(key, defaultValue);

	}

	public static short getShort(String key) {

		return config.getShort(key);

	}

	public static short getShort(String key, short defaultValue) {

		return config.getShort(key, defaultValue);

	}

	public static Short getShort(String key, Short defaultValue) {

		return config.getShort(key, defaultValue);

	}

	public static BigDecimal getBigDecimal(String key) {

		return config.getBigDecimal(key);

	}

	public static BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {

		return config.getBigDecimal(key, defaultValue);

	}

	public static BigInteger getBigInteger(String key) {

		return config.getBigInteger(key);

	}

	public static BigInteger getBigInteger(String key, BigInteger defaultValue) {

		return config.getBigInteger(key, defaultValue);

	}

	public static String getString(String key) {

		return config.getString(key);

	}

	public static String getString(String key, String defaultValue) {

		return config.getString(key, defaultValue);

	}

	/**
	 * 获取指定Key的配置信息并按指定分隔符进行分隔(默认分隔符：,)
	 * 例：
	 * 	配置文件中有如下配置：
	 * 	test.array = test1,test2
	 *
	 *	getStringArray("test.array")返回信息如下：
	 *	[test1, test2]
	 *
	 * @param key
	 * @return
	 */
	public static String[] getStringArray(String key) {

		return config.getStringArray(key);

	}

	/**
	 * 获取指定Key的配置信息并按指定分隔符进行分隔(默认分隔符：,)
	 * 例：
	 * 	配置文件中有如下配置：
	 * 	test.array = test1,test2
	 *
	 *	getList("test.array")返回信息如下：
	 *	[test1, test2]
	 *
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getList(String key) {

		return config.getList(key);

	}

	/**
	 * 获取指定Key的配置信息并按指定分隔符进行分隔(默认分隔符：,)，没有获取到配置信息则返回defaultValue
	 * 例：
	 * 	配置文件中有如下配置：
	 * 	test.array = test1,test2
	 *
	 *	getList("test.array")返回信息如下：
	 *	[test1, test2]
	 *
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getList(String key, List<String> defaultValue) {

		return config.getList(key, defaultValue);

	}

}
