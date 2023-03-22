package com.example.converter

import com.example.converter.Xxx.TestClass
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

object Xxx {

  case class TestClass(
      int1: Int,
      optInt1: Option[Int],
      str1: String,
      optStr1: Option[String]
  )

  implicit val testClassFromMap: FromMap[TestClass] =
    FromMap.generic(FromValue.gen)
}

class FromMapSpec extends AnyFlatSpec with Matchers {

  it should "create TestClass from map of fields" in {

    val testClass = TestClass(123, Some(456), "str1", Some("optStr1"))

    val map = Map(
      "int1" -> 123,
      "optInt1" -> 456,
      "str1" -> "str1",
      "optStr1" -> "optStr1"
    )

    FromMap.apply[TestClass].fromMap(map) shouldBe testClass
  }
}
