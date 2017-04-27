package io.liquidsoftware

import java.util

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import collection.JavaConversions._

/**
  * Created by erikdreyer on 4/27/17.
  */
object Main {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("spark_gps_consumer").setMaster("local[2]")
    val sparkCtx = new StreamingContext(conf, Seconds(2))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "GPS.1",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (true: java.lang.Boolean)
    )

    val gpsStream = KafkaUtils.createDirectStream[String, String](
      sparkCtx,
      PreferConsistent,
      Subscribe[String, String](List("gps"), kafkaParams)
    )
    gpsStream


    sparkCtx.start()
    sparkCtx.awaitTermination()
  }

}
