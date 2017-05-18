package com.yisa.wifi.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;


import com.yisa.wifi.zookeeper.ZKUtil;
 
/**
 * 数据库连接池
 * 
 * @author liliwei
 * @date 2015年9月18日 下午1:11:02
 * @version V1.0
 */
public class EquLocaJDBCConnectionSinglen {

	private static Connection conn = null;
	private static String DRIVER = "com.mysql.jdbc.Driver";

	/**
	 * 获得连接
	 *
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException {

		if (conn == null || conn.isClosed()) {
			synchronized (EquLocaJDBCConnectionSinglen.class) {
				if (conn == null || conn.isClosed()) {

					ZKUtil sample = new ZKUtil();
					sample.createConnection();

					String jdbc = sample.readData("/yisaconfig/cpa/jdbcUrl");
					String USERNAME = sample.readData("/yisaconfig/cpa/jdbcUser");
					String PASSWORD = sample.readData("/yisaconfig/cpa/jdbcPassword");

					try {
						Class.forName(DRIVER);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					conn = DriverManager.getConnection(jdbc, USERNAME, PASSWORD);
					sample.releaseConnection();
				}
			}
		}

		return conn;

	}

	/**
	 * 关闭连接池
	 * 
	 * @throws SQLException
	 */
	public static void close() throws SQLException {

		conn.close();

	}

}
