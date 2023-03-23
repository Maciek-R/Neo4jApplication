package com.example.converter

import com.example.converter.Xxx.TestClass
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

object Xxx {

  case class TestClass(
      int1: Int,
      optInt1: Option[Int],
      str1: String,
      optStr1: Option[String],
      bool: Boolean,
      optBool: Option[Boolean]
  )

  implicit val testClassFromMap: FromMap[TestClass] =
    FromMap.generic(FromValue.gen)
}

class FromMapSpec extends AnyFlatSpec with Matchers with EitherValues {

  it should "create TestClass from map of fields" in {

    val testClass =
      TestClass(123, Some(456), "str1", Some("optStr1"), true, Some(false))

    val map = Map(
      "int1" -> 123,
      "optInt1" -> 456,
      "str1" -> "str1",
      "optStr1" -> "optStr1",
      "bool" -> true,
      "optBool" -> false
    )

    FromMap.apply[TestClass].fromMap(map).value shouldBe testClass
  }
}
