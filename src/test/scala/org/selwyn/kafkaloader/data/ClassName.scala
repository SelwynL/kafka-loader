package org.selwyn.kafkaloader.data

import org.scalatest.{Matchers, WordSpecLike}

// Test class for ClassName
case class MyClassMock()

class ClassNameSpec extends WordSpecLike with Matchers {

  "ClassName" must {

    "get string value of class" in {
      ClassName(classOf[MyClassMock]) should be("org.selwyn.kafkaloader.data.MyClassMock")
    }
  }
}
