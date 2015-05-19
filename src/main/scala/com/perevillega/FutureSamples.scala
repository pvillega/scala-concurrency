package com.perevillega

import scala.concurrent.{Promise, Await, Future}
import scala.concurrent.duration._
import concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Random}

/**
 * Trying futures in Scala
 */
object FutureSamples extends App {

  def justForTestPurposes = 2

  val random = new Random()

  // defining basic future
  val f: Future[String] = Future { "Hello world!" }
  val fResult = Await.result(f, 1 minute)
  println(s"Value $fResult")

  // using onSuccess
  val f2 = Future { "Another result" }
  f2.onSuccess{ case result => println(s"OnSuccess $result")}

  // using onComplete
  val f3 = Future { if(random.nextBoolean()) "All ok" else throw new Exception("Error!")  }
  f3.onComplete{
    case Success(result) => println(s"OnComplete $result")
    case Failure(ex) => println(s"OnComplete ${ex.getMessage}")
  }

  // composing futures with map
  val f4 = Future {"I don't like Strings" }
  f4.map(_.getBytes.sum).onSuccess{ case result => println(s"Composed with map $result")}

  // composing futures via flatMap
  val f5 = Future {"I don't like Strings" }
  val f6 = (x: String) => Future { x.hashCode }
  f5.flatMap(f6(_)).onSuccess{ case result => println(s"Composed with FlatMap $result")}

  // composing via for comprehension (same as flatmap)
  val f7 = Future {"I don't like Strings" }
  val f8 = (x: String) => Future { x.hashCode }
  val result = for {
    m <- f7
    s <- f8(m)
  } yield s
  result.map{ case r => println(s"Composed with For comprehension $r") }

  // Future is success biased, now using a failure detector
  val f9 = Future{ throw new Exception("Failed!") }
  f9.onFailure{ case ex => println(s"OnFailure ${ex.getMessage}")}

  // we can project a future as a failed future and operate on it
  val f10 = Future{ throw new Exception("Failed!") }
  f10.failed.map{ case ex => println(s"Failed projection ${ex.getMessage}")}

  // we can give fallback values in case of error
  val f11 = Future{ throw new Exception("Failed!") }
  f11.fallbackTo(Future("Fallback!")).map{ case result => println(s"Fallback value $result")}

  // we can also try to recover errors, like with Try
  val f12 = Future{ throw new Exception("Failed!") }
  f12.recoverWith{ case ex => Future("Recovered")}.map{ case result => println(s"Recovered value $result")}


  // Promises - a link ot the Future
  // This promise will contain a String at some point in the future
  val p1 = Promise[String]()
  // we can read that value via the Future linked to the promise
  val p1f1 = p1.future
  //then we can complete the promise
  p1.success("Promise successful")
  // while at the same time we can operate with the future
  p1f1.map(r => println(s"Promise p1 $r"))


  // a more standard use case would be this
  def goodPromise(): Future[String] = {
    val p = Promise[String]()
    Future {
      println("Starting the goodPromise.")
      Thread.sleep(20)
      p.success("aha!")
    }
    p.future
  }
  val p2f2 = goodPromise()
  p2f2.map(r => println(s"Good Promise $r"))


  // we can also fail promises
  def badPromise(): Future[String] = {
    val p = Promise[String]()
    Future {
      println("Starting the badPromise.")
      Thread.sleep(20)
      p.failure(new Exception("I am a bad promise"))
    }
    p.future
  }
  val p3f3 = badPromise()
  p3f3.onFailure{ case ex => println(s"Bad Promise ${ex.getMessage}") }


}
