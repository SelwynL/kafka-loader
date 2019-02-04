package org.selwyn.kafkaloader.data

import org.selwyn.kafkaloader.data.Filler.FillerFunction

import enumeratum.{Enum, EnumEntry}
import scala.collection.immutable

sealed abstract class DataType(override val entryName: String, val func: FillerFunction) extends EnumEntry

case object DataType extends Enum[DataType] {
  val values: immutable.IndexedSeq[DataType] = findValues

  case object Int  extends DataType("INT", Filler.int)
  case object UUID extends DataType("UUID", Filler.uuid)
  case object Enum extends DataType("ENUM", Filler.enum)
}
