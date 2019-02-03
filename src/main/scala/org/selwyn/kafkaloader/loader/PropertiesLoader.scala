package org.selwyn.kafkaloader.loader
import java.io.StringReader
import java.util.Properties

import scala.util.Try

object PropertiesLoader {

  def create(str: String): Either[Throwable, Properties] =
    Try {
      val p = new Properties()
      p.load(new StringReader(str))
      p
    }.toEither

  def load(fileName: String): Either[Throwable, Properties] =
    for {
      str        <- FileLoader.asString(fileName)
      properties <- create(str)
    } yield properties

  def getProperty(properties: Properties, key: String): Either[Throwable, String] =
    Try(properties.getProperty(key)).toEither
}
