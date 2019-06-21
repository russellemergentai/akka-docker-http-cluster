package com.example.cluster.playground.node.processor

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}


case class Response(nodeId: String, result: Int)

object ProcessorResponseJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol{
  implicit val processorResponse: RootJsonFormat[Response] = jsonFormat2(Response)
}
