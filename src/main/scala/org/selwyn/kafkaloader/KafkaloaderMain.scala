package org.selwyn.kafkaloader

import org.selwyn.kafkaloader.avro.AvroCodec
import org.selwyn.kafkaloader.kafka.{Kafka, SchemaRegistry}
import org.selwyn.kafkaloader.loader.PropertiesLoader

import com.typesafe.scalalogging.LazyLogging
import io.circe.Json
import org.apache.kafka.clients.producer.RecordMetadata

object KafkaloaderMain extends App with LazyLogging {

  val kafkaPropertiesFile = "etc/kafka/local-kafka.properties"

  val produceFunction: Either[Throwable, (String, Json) => Either[Throwable, RecordMetadata]] = for {
    properties   <- PropertiesLoader.load(kafkaPropertiesFile)
    topic        <- PropertiesLoader.getProperty(properties, "topic.subscriptions")
    schemaRegUrl <- PropertiesLoader.getProperty(properties, "schema.registry.url")
    client       <- SchemaRegistry.getClient(schemaRegUrl)
    schema       <- SchemaRegistry.getSchema(client, topic)
    producer     <- Kafka.producerClient(properties)
  } yield Kafka.produce(topic, new AvroCodec(schema))(producer)

  logger.info(s"Produce Function: $produceFunction")
}
