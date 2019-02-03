package org.selwyn.kafkaloader.data

object ClassName {
  type ClassName = String

  def apply[T](clazz: Class[T]): ClassName =
    clazz.getName
}
