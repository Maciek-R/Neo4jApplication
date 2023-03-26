package com.example.converter

import magnolia1.{CaseClass, Magnolia, Monadic}
import org.scalacheck.{Arbitrary, Gen}

import scala.language.experimental.macros

trait ArbitraryDerivation {
  type Typeclass[T] = Arbitrary[T]

  def join[T](caseClass: CaseClass[Arbitrary, T]): Arbitrary[T] = {
    Arbitrary {
      caseClass.constructMonadic { param =>
        param.typeclass.arbitrary
      }
    }
  }

  implicit private val monadicGen: Monadic[Gen] = new Monadic[Gen] {
    override def point[A](value: A): Gen[A] = Gen.const(value)

    override def map[A, B](from: Gen[A])(fn: A => B): Gen[B] = from.map(fn)

    override def flatMap[A, B](from: Gen[A])(fn: A => Gen[B]): Gen[B] = from.flatMap(fn)
  }

  implicit def gen[T]: Arbitrary[T] = macro Magnolia.gen[T]
}
