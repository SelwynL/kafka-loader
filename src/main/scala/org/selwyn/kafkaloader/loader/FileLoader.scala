package org.selwyn.kafkaloader.loader

import scala.io.Source
import scala.util.Try

object FileLoader {

  def asString(fileName: String): Either[Throwable, String] =
    Try(Source.fromFile(fileName).getLines().mkString("\n")).toEither
}
