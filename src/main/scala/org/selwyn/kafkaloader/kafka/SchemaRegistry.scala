package org.selwyn.kafkaloader.kafka

import com.typesafe.scalalogging.LazyLogging
import io.confluent.kafka.schemaregistry.client.rest.RestService
import org.apache.avro.Schema

import scala.util.{Failure, Success, Try}

object SchemaRegistry extends LazyLogging{

  def getSchema(url: String, subject: String): Either[Throwable, Schema] =
    Try(new RestService(url).getLatestVersion(subject)) match {
      case Success(schema: Schema) =>
        logger.debug(s"Found schema for '$subject'")
        Right(schema)
      case Failure(exception: Throwable) =>
        logger.error(s"Unable to get schema from registry url '$url' with subject '$subject': ${exception.getMessage}")
        Left(exception)
    }
}
