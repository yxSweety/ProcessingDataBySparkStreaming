package com.yisa.wifi.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


/**
 * MySQL工具类
 * 
 * @author DangT
 * @date 2015年6月17日 上午9:28:52
 * @version V1.0
 */
public class EquLocaMySQLHelper {

	private static final String UPDATE_EQU_SQL = "update wifi_equ set dwbh = ?,name = ?, address = ?, lon = ?, lat = ? where equ_id = ?";
	private static final String SAVE_EQU_SQL = "insert into wifi_equ(equ_id, dwbh, name, address, lon, lat, flag) values(?, ?, ?, ?, ?, ?, ?)";
	
	private static PreparedStatement pstmt = null;

	/**
	 * 获取数据库连接
	 *
	 * @return
	 * @throws Exception
	 */
	public static Connection openConnection() throws Exception {

		try {
			return EquLocaJDBCConnectionSinglen.getConnection();
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * 执行查询操作
	 *
	 * @param conn
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public static ResultSet query(Connection conn, String sql) throws Exception {

		pstmt = conn.prepareStatement(sql);
		return pstmt.executeQuery();

	}


	
	

	
	/**
	 * 关闭资源连接
	 *
	 * @param conn
	 * @throws Exception
	 */
	public static void close(Connection conn) throws Exception {

		try {
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			throw e;
		}

	}
	
	/**
	 * 关闭资源连接
	 *
	 * @param pstmt
	 * @throws Exception
	 */
	public static void close(PreparedStatement pstmt) throws Exception {
		
		try {
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
		} catch (SQLException e) {
			throw e;
		}
		
	}

	/**
	 * 关闭资源连接
	 *
	 * @param rs
	 * @throws Exception
	 */
	public static void close(ResultSet rs) throws Exception {

		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
		} catch (SQLException e) {
			throw e;
		}

	}

}
