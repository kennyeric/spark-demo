

import org.apache.kafka.clients.producer._

import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._

object StreamingFromKafka {  
  def main(args: Array[String]) {
    if (args.length < 4) {
      System.err.println("Usage: StreamingFromKafka <zkQuorum> <group> <topics> <numThreads>")
    }
    
    val Array(zkQuorum, group, topics, numThreads) = args
    val conf = new SparkConf().setAppName("streaming kafka demo")
    val ssc = new StreamingContext(conf, Seconds(2))

    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
    val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap).map(_._2)
    val words = lines.flatMap(_.split(" "))
    val pairs = words.map(word => (word, 1L))
    val wordCounts = pairs.reduceByKeyAndWindow(_ + _, _ - _, Minutes(10), Seconds(2), 2)

    wordCounts.print()

    ssc.start()
    ssc.awaitTermination()    
  }
}

