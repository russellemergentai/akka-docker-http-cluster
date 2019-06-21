package com.example.cluster.playground.api

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshallers.sprayjson._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import com.example.cluster.playground.node.processor.ProcessorResponseJsonProtocol._
import com.example.cluster.playground.node.Node.{Request}
import com.example.cluster.playground.node.processor.Response

import scala.concurrent.Future
import scala.concurrent.duration._

trait NodeRoutes extends SprayJsonSupport {

  implicit def system: ActorSystem

  def node: ActorRef

  implicit lazy val timeout = Timeout(5.seconds)

  // http://localhost:8001/process/request/11

  lazy val processRoutes: Route = pathPrefix("process") {
    concat(
      pathPrefix("request") {
        concat(
          path(IntNumber) { n =>
            pathEnd {
              concat(
                get {
                  val processFuture: Future[Response] = (node ? Request(n)).mapTo[Response]
                  onSuccess(processFuture) { response =>
                    complete(StatusCodes.OK, response)
                  }
                }
              )
            }
          }
        )
      }
    )
  }
}
