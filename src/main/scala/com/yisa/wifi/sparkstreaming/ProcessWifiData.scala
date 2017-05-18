package com.yisa.wifi.sparkstreaming

import java.util.Properties
import java.util.UUID

import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yisa.wifi.manager.KafkaManager
import com.yisa.wifi.manager.KafkaSink
import com.yisa.wifi.model.WifiInfo

import kafka.serializer.StringDecoder
import org.apache.commons.cli.Options
import org.apache.commons.cli.Option
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.MissingOptionException
import org.apache.commons.cli.PosixParser
import com.yisa.wifi.zookeeper.ZookeeperUtil
import java.util.logging.SimpleFormatter
import java.text.SimpleDateFormat
import java.util.Date

object ProcessWifiData {
  def main(args: Array[String]) {

    //获取参数值
    var cmd: CommandLine = null
    val options: Options = new Options()
    
    try {
      var kafkaServer: Option = new Option("kafka_server", true, "Need parameter: kafka_server")
      kafkaServer.setRequired(true)
      options.addOption(kafkaServer)
      
      var groupId: Option = new Option("group_id", true, "Need parameter: kafka_server")
      groupId.setRequired(true)
      options.addOption(groupId)
      
      var topics:Option = new Option("topic_get", true, "Need parameter: topic_get")
      topics.setRequired(true)
      options.addOption(topics)
      
      var kafkaTopic: Option = new Option("topic_set", true, "Need parameter: topic_set")
      kafkaTopic.setRequired(true)
      options.addOption(kafkaTopic)
      
      var interval: Option = new Option("interval", true, "Need parameter: interval")
      interval.setRequired(true)
      options.addOption(interval)
      
      var parser: PosixParser = new PosixParser()
      cmd = parser.parse(options, args)
    } catch {
      case e: MissingOptionException => {
        println(e)
        System.exit(1)
      }
    }
    
    //配置参数
    var kafkaAddr = cmd.getOptionValue("kafka_server")
    var groupId = cmd.getOptionValue("group_id")
    var topics = cmd.getOptionValue("topic_get")
    var kafkaTopic = cmd.getOptionValue("topic_set")
    var interval = cmd.getOptionValue("interval")
    println("kafkaAddr:"+kafkaAddr+"**groupId:"+groupId+"**topics:"+topics+"**kafkaTopic:"+kafkaTopic)

    //配置spark
    var sparkConf = new SparkConf().setAppName("ProcessWifiData")//.setMaster("local")
    sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
//    sparkConf.set("spark.ui.port", "14047")

    var sparkContext = new SparkContext(sparkConf)
    var streamingContext = new StreamingContext(sparkContext, Seconds(interval.toInt))

    //配置producer
    val kafkaProducer: Broadcast[KafkaSink[String, String]] = {
      val kafkaConfigs = {
        var properties = new Properties()
        properties.setProperty("bootstrap.servers", kafkaAddr)
        properties.setProperty("key.serializer", classOf[StringSerializer].getName)
        properties.setProperty("value.serializer", classOf[StringSerializer].getName)
        properties
      }
      streamingContext.sparkContext.broadcast(KafkaSink[String, String](kafkaConfigs))
    }

    val topicsSet = topics.split(",").toSet
    val kafakParams = Map[String, String](
      "metadata.broker.list" -> kafkaAddr,
      "group.id" -> groupId,
      "auto.offset.reset" -> "largest")

    //从kafka中取数据
    val km = new KafkaManager(kafakParams)
    val messages = km.createDirectStream[String, String, StringDecoder, StringDecoder](streamingContext, kafakParams, topicsSet)

    val flume = messages.foreachRDD(rdd => {
      if (!rdd.isEmpty()) {
        var wifiInfos_RDD = rdd.map(line_data => {
          var line = line_data._2.replace("type", "value_type")
          println("line:" + line)
          var wifiInfo: WifiInfo = null
          try {
            val gson = new Gson
            val mapType = new TypeToken[WifiInfo] {}.getType
            wifiInfo = gson.fromJson[WifiInfo](line, mapType)
          } catch {
            case e: Exception => {
              println("json数据接收异常" + line)
            }
          }
          if (wifiInfo == null) {
            println("json数据接收异常" + line)
            throw new Exception("json数据接收异常")
          }

          wifiInfo.equId = wifiInfo.equId.replaceAll("-", "")
          wifiInfo

        })
        var wifiAccount = wifiInfos_RDD.count()

        if (wifiAccount > 0) {
          //过滤掉type=3的数据
          var wifiInfo_data_without2 = wifiInfos_RDD.filter(_.value_type != 3)
          //处理数据
          var wifiInfos_process_data = wifiInfo_data_without2.map(x => {
            var macOrImsi: String = ""
            var cap: String = ""
            if (x.value_type == 1) {
              x.mac = x.mac.replaceAll("-", "")
              macOrImsi = x.mac
            } else {
              x.imsi = x.imsi.replaceAll("-", "")
              macOrImsi = x.imsi
            }
            if (x.captureTime != 0L) {
              cap = (x.captureTime + "").substring(0, (x.captureTime + "").length() - 2)
            }
            var sBuilder = new StringBuilder()
            sBuilder.append(x.equId).append("_").append(macOrImsi).append("_").append(cap)
            var key = sBuilder.toString()

            (key, x)
          })
          

          wifiInfos_process_data.reduceByKey((x, y) => (x))
            .foreach(data => {
              if (data != null) {
                println("################################")

                val gsonKa = new Gson
                println("data:" + data)
                var kafka_data = data._2

                var kafkaData = gsonKa.toJson(kafka_data).replace("value_type", "type")
                println("入kafka数据：" + kafkaData)
                kafkaProducer.value.send(kafkaTopic, kafkaData)

                if (kafka_data.value_type == 2) {
                  var imei_data: WifiInfo = kafka_data
                  imei_data.value_type = 3
                  var imeiData = gsonKa.toJson(imei_data).replace("value_type", "type")
                  println("入kafka数据--type=3：" + imeiData)
                  kafkaProducer.value.send(kafkaTopic, imeiData)
                }
              }
            })

          val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
          var nowDate = format.format(new Date())
          println(nowDate + " rdd长度：" + wifiAccount)
          //更新offset
          km.updateZKOffsets(rdd)
        }
      } else {
        val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var nowDate = format.format(new Date())
        println(nowDate + " rdd长度：" + 0)
      }
    })

    streamingContext.start();
    streamingContext.awaitTermination();
  }
}