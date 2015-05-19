package com.perevillega

import org.scalatest.{Matchers, FunSuite}

class FutureSamplesSpec extends FunSuite with Matchers {

  test("A sample test using FunSuite to refresh syntax") {
    FutureSamples.justForTestPurposes should be(2)
  }

}
