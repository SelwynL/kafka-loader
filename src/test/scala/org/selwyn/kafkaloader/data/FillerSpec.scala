package org.selwyn.kafkaloader.data

import org.scalatest.{Matchers, WordSpecLike}

class FillerSpec extends WordSpecLike with Matchers {

  "Filler" must {

    "fill INT sequence incrementally" in {
      val size = 10

      val expected = Array(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
      val actual   = Filler.int(size, "").toArray

      actual.length should be(size)
      actual should equal(expected)
    }

    "fill UUID set without duplicates" in {
      val size = 5000

      val actual = Filler.uuid(size, "")

      // since this returns a set, if the requested size is the result size then each is guaranteed unique
      actual.size should be(size)
    }

    "fill ENUM sequence in config order" in {
      val size = 5

      val expected = Seq("create", "delete", "update", "create", "delete")
      val actual   = Filler.enum(size, "create,delete,update")

      actual.size should be(size)
      actual should equal(expected)
    }

    "fill ENUM sequence with duplicate value in config" in {
      val size = 5

      val expected = Seq("create", "delete", "update", "create", "delete")
      val actual   = Filler.enum(size, "create,delete,create,update")

      actual.size should be(size)
      actual should equal(expected)
    }

    "fill ENUM sequence with extra spaces in config" in {
      val size = 5

      val expected = Seq("create", "delete", "update", "create", "delete")
      val actual   = Filler.enum(size, "create,delete, create  ,update")

      actual.size should be(size)
      actual should equal(expected)
    }

    "fill ENUM sequence without config" in {
      val size = 5

      val expected = Seq("", "", "", "", "")
      val actual   = Filler.enum(size, "")

      actual.size should be(size)
      actual should equal(expected)
    }
  }
}
