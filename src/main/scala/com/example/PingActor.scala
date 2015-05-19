package com.example

import akka.actor.SupervisorStrategy.{Escalate, Stop, Restart}
import akka.actor.{OneForOneStrategy, Actor, ActorLogging, Props}
import scala.concurrent.duration._

class PingActor extends Actor with ActorLogging {
  import PingActor._

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: NullPointerException     => Restart
      case _: IllegalArgumentException => Stop
      case _: Exception                => Escalate
    }

  var counter = 0
  val pongActor = context.actorOf(PongActor.props("pong"), "pongActor")

  def initialStatus: Receive = {
    case _ =>
      log.info("In PingActor - starting ping-pong")
      pongActor ! PingMessage("ping")
      context.become(communicating)
  }

  def communicating: Receive = {
  	case PongActor.PongMessage(text) =>
  	  log.info("In PingActor - received message: {}", text)
  	  counter += 1

      if(counter == 3) {
        context.unbecome()
        log.info("Reset!")
      }

      if(counter >= 10) context.system.shutdown()
      else sender() ! PingMessage("ping")
  }

  def receive: Receive = initialStatus
}

object PingActor {
  val props = Props[PingActor]
  case object Initialize
  case class PingMessage(text: String)
}