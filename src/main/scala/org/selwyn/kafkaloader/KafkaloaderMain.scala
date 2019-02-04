package org.selwyn.kafkaloader

import org.selwyn.kafkaloader.avro.AvroCodec
import org.selwyn.kafkaloader.kafka.{Kafka, SchemaRegistry}
import org.selwyn.kafkaloader.loader.{FileLoader, PropertiesLoader}
import com.typesafe.scalalogging.LazyLogging
import io.circe.Json
import io.circe.parser._
import org.apache.kafka.clients.producer.RecordMetadata
import org.selwyn.kafkaloader.data.DataField
import org.selwyn.kafkaloader.data.DataType

object KafkaloaderMain extends App with LazyLogging {

  val kafkaPropertiesFile     = "etc/kafka/local-kafka.properties"
  val templateJsonPayloadFile = "etc/payload/user.json"

  val count = 10

  val fields: Seq[DataField] = Seq(
    DataField("id", DataType.Int, ""),
    DataField("level", DataType.Enum, "WRITE,READ_ONLY"),
    DataField("email", DataType.UUID, "@gmail.com")
  )

  val produceFunction: Either[Throwable, (String, Json) => Either[Throwable, RecordMetadata]] = for {
    properties   <- PropertiesLoader.load(kafkaPropertiesFile)
    topic        <- PropertiesLoader.getProperty(properties, "topic.subscriptions")
    schemaRegUrl <- PropertiesLoader.getProperty(properties, "schema.registry.url")
    client       <- SchemaRegistry.getClient(schemaRegUrl)
    schema       <- SchemaRegistry.getSchema(client, topic)
    producer     <- Kafka.producerClient(properties)
  } yield Kafka.produce(topic, new AvroCodec(schema))(producer)

  val jsonPayloadTemplate: Either[Throwable, Json] = for {
    jsonString <- FileLoader.asString(templateJsonPayloadFile)
    json       <- parse(jsonString)
  } yield json

  logger.info(s"Json Payload Template: $jsonPayloadTemplate")
  logger.info(s"Produce Function: $produceFunction")
}
