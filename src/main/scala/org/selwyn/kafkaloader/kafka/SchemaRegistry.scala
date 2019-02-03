package org.selwyn.kafkaloader.kafka

import com.typesafe.scalalogging.LazyLogging
import io.confluent.kafka.schemaregistry.client.{CachedSchemaRegistryClient, SchemaRegistryClient}
import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import scala.util.Try

object SchemaRegistry extends LazyLogging {

  def getClient(url: String): Either[Throwable, SchemaRegistryClient] =
    Try(new CachedSchemaRegistryClient(url, 100)).toEither

  def getSchema(client: SchemaRegistryClient, subject: String): Either[Throwable, Schema] = {
    logger.info(s"Getting schema from registry with subject '$subject'")
    Try(client.getLatestSchemaMetadata(s"$subject-value")).toEither.map(metaData =>
      new Parser().parse(metaData.getSchema))
  }
}
