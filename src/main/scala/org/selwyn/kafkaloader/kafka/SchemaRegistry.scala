package org.selwyn.kafkaloader.kafka

import com.typesafe.scalalogging.LazyLogging
import io.confluent.kafka.schemaregistry.client.rest.RestService
import org.apache.avro.Schema
import org.apache.avro.Schema.Parser

import scala.util.Try

object SchemaRegistry extends LazyLogging {

  def getSchema(url: String, subject: String): Either[Throwable, Schema] = {
    logger.info(s"Getting schema from registry url '$url' with subject '$subject'")
    Try(new RestService(url).getLatestVersion(s"$subject-value")).toEither.map(restSchema =>
      new Parser().parse(restSchema.getSchema))
  }
}
