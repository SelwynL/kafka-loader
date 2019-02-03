package org.selwyn.kafkaloader.kafka

import org.selwyn.kafkaloader.avro.AvroCodec
import org.selwyn.kafkaloader.data.ClassName
import org.selwyn.kafkaloader.data.ClassName.ClassName

import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.{KafkaProducer, Producer, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.StringSerializer
import io.circe.Json
import io.confluent.kafka.serializers.KafkaAvroSerializer
import java.util.Properties
import scala.util.Try

object Kafka {

  val serializerProperties: Map[String, ClassName] = Map(
    "value.serializer" -> ClassName(classOf[StringSerializer]),
    "key.serializer"   -> ClassName(classOf[KafkaAvroSerializer])
  )

  def producerClient(properties: Properties): Either[Throwable, KafkaProducer[String, GenericRecord]] = {
    serializerProperties.foreach((kv: (String, ClassName)) => properties.setProperty(kv._1, kv._2))
    Try(new KafkaProducer[String, GenericRecord](properties)).toEither
  }

  def produce(client: Producer[String, GenericRecord])(topic: String, avroCodec: AvroCodec)(
      key: String,
      payload: Json): Either[Throwable, RecordMetadata] =
    avroCodec
      .decodeJson(payload)
      .flatMap(record => Try(client.send(new ProducerRecord(topic, key, record)).get).toEither)
}
