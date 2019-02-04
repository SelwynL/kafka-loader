package org.selwyn.kafkaloader.data

import java.util.UUID
import scala.annotation.tailrec
import scala.collection.Set

object Filler {

  type FillerFunction = (Int, String) => Iterable[Any]

  /**
    * Fills a Set with "n" strings containing UUID values
    */
  val uuid: FillerFunction = (n: Int, append: String) => {
    @tailrec
    def iter(generated: Set[String]): Set[String] = {
      val currentSize = generated.size
      if (currentSize == n) generated
      else {
        val diff = n - currentSize
        iter(generated ++: List.fill(diff)(s"${UUID.randomUUID.toString}$append").toSet)
      }
    }

    iter(Set.empty[String])
  }

  /**
    * Fills a Seq with "n" strings all deriving their value from an element in the comma-separated "config"
    */
  val enum: FillerFunction = (n: Int, config: String) => {
    val initialEnumValues: Set[String] = config.split(",").toSet[String].map(_.trim)

    @tailrec
    def iter(values: List[String], generated: Seq[String]): Seq[String] = {
      if (generated.size == n) generated
      else {
        values match {
          case head :: tail => iter(tail, generated :+ head)
          case Nil          => iter(initialEnumValues.toList, generated)
        }
      }
    }

    iter(initialEnumValues.toList, Seq.empty[String])
  }

  /**
    * Fills a Range of integers from 0 to "n"
    */
  val int: FillerFunction = (n: Int, _: String) => 0 until n
}
