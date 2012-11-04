package com.example.label

import spray.routing._
import spray.http._
import akka.pattern.ask
import akka.util.Timeout
import akka.util.duration._
import spray.httpx.encoding.{NoEncoding, Gzip}
import akka.actor.ActorRef
import akka.dispatch.Await
import spray.http.HttpHeaders.`Cache-Control`

trait LabelService extends HttpService {
  implicit val timeout: Timeout
  def persistenceActor : ActorRef

  val plainLabelRoute =
    pathPrefix("label" / PathElement) {
      appDomain =>
        path("") {
          post {
            (decodeRequest(Gzip) | decodeRequest(NoEncoding)) {
              entity(as[String]) {
                lbl =>
                  detachTo(singleRequestServiceActor) {
                    persistenceActor ! AddLabel(lbl)
                    complete(StatusCodes.OK, "ok")
                  }
              }
            }
          } ~ get {
              respondWithHeaders(`Cache-Control`(CacheDirectives.`no-cache`)) {
                complete {
                  Await.result((persistenceActor ? GetLabels()), 10 second).toString()
                }
            }
          }
        }
    }
}