package com.example.cluster.playground

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.example.cluster.playground.api.NodeRoutes
import com.example.cluster.playground.node.Node
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Server extends App with NodeRoutes {

  implicit val system: ActorSystem = ActorSystem("cluster-playground")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val config: Config = ConfigFactory.load()
  // referring to config in the application.conf via docker-compose.yml
  val address = config.getString("http.ip")
  val port = config.getInt("http.port")
  val nodeId = config.getString("clustering.ip")

  // server starts a node actor
  // node itself runs a processor actor
  // node also runs a processorRouter actor from config, which hooks into clustering and round-robin rooting
  val node: ActorRef = system.actorOf(Node.props(nodeId), "node")

  lazy val routes: Route = processRoutes

  Http().bindAndHandle(routes, address, port)
  println(s"Node $nodeId is listening at http://$address:$port")

  Await.result(system.whenTerminated, Duration.Inf)

}
