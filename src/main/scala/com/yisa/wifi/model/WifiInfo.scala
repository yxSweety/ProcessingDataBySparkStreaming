package com.yisa.wifi.model

case class WifiInfo(
  var uuid:String,
  /**
   * 1:mac,2:imsi,3:imei，4:区号
   */
  var value_type:Int,
   
  /**
   * mac
   */
  var mac: String,
  
  /**
   * imsi
   */
  var imsi: String,
  
  /**
   * imei
   */
  var imei: String,
  
  /**
   * 区号
   */
  var areaCode: String,
  
  var cid: String,
  var lac: String,
  
  /**
   * 网桥
   */
  var switchBoard: String,
  
  /**
   * nodeid
   */
	var nodeid:String,
	
	/**
	 * kafka中取出的equId
	 */
	var equId_first:String,
	
	/**
	 * 抓拍时间(20170509091800)
	 */
	var captureTime:Long,
	
	/**
	 * 处理后的equId(nodeid_equId_first)
	 */
	var equId:String,
	
	/**
	 * locationId
	 */
	var locationId:String,
	
	/**
	 * 创建时间-入库时间(20170509091800)
	 */
	var createTime:Long
)