package org.selwyn.kafkaloader

import com.typesafe.scalalogging.LazyLogging
import org.selwyn.kafkaloader.kafka.SchemaRegistry

object KafkaloaderMain extends App with LazyLogging {

  logger.info(s"Result: ${SchemaRegistry.getSchema("http://localhost:8081", "users")}")
}
