package com.example.cluster.playground.node

import akka.actor.{Actor, ActorRef, Props}
import akka.routing.FromConfig
import com.example.cluster.playground.node.Node.{Request}
import com.example.cluster.playground.node.cluster.ClusterManager
import com.example.cluster.playground.node.processor.Processor
import com.example.cluster.playground.node.processor.Processor.Compute

object Node {

  sealed trait NodeMessage

  case class Request(n: Int)

  def props(nodeId: String) = Props(new Node(nodeId))
}

class Node(nodeId: String) extends Actor {

  val processor: ActorRef = context.actorOf(Processor.props(nodeId), "processor")
  val processorRouter: ActorRef = context.actorOf(FromConfig.props(Props.empty), "processorRouter")
  val clusterManager: ActorRef = context.actorOf(ClusterManager.props(nodeId), "clusterManager")

  override def receive: Receive = {
    // node passes on any requests to the cluster round-robin processrouter, which has routes down to Processor
    // 'forward' in this case means that the Process actor will see the 'sender()' to be the http Server,
    // to ensure the RESPONSE GOES BACK TO THE HTTP LAYER and complete the future
    case Request(value) => processorRouter forward Compute(value)
  }
}
