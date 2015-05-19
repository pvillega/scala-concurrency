package com.example

import akka.actor.{Actor, ActorLogging, Props}

class PongActor(name: String) extends Actor with ActorLogging {
  import PongActor._

  def receive = {
  	case PingActor.PingMessage(text) => 
  	  log.info(s"In $name - received message: $text")
  	  sender() ! PongMessage(name)
  }	
}

object PongActor {
  def props(name: String) = Props(new PongActor(name))
  case class PongMessage(text: String)
}
