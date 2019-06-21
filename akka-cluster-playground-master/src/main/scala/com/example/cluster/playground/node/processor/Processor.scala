package com.example.cluster.playground.node.processor

import akka.actor.{Actor, Props}

import scala.annotation.tailrec

object Processor {

  sealed trait ProcessorMessage

  case class Compute(n: Int) extends ProcessorMessage

  def props(nodeId: String) = Props(new Processor(nodeId))
}

class Processor(nodeId: String) extends Actor {
  import Processor._

  @tailrec final def computeHelper(x: Int, prev: Int = 0, next: Int = 1): Int = x match {
    case 0 => prev
    case 1 => next
    case _ => computeHelper(x - 1, next, next + prev)
  }

  override def receive: Receive = {
    case Compute(value) => {
      val replyTo = sender()
      replyTo ! Response(nodeId, computeHelper(value))
    }
  }
}
