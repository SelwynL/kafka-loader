package org.selwyn.kafkaloader

import com.typesafe.scalalogging.LazyLogging
import org.selwyn.kafkaloader.kafka.SchemaRegistry

object KafkaloaderMain extends App with LazyLogging {

  val result = for {
    client <- SchemaRegistry.getClient("http://localhost:8081")
    schema <- SchemaRegistry.getSchema(client, "users")
  } yield schema

  logger.info(s"Result: $result")
}
