package com.example

import akka.actor.{Props, ActorSystem}
import label.HashMapLabelServiceActor
import spray.can.server.HttpServer
import spray.io._
import com.typesafe.config.ConfigFactory


object Boot extends App {
  // we need an ActorSystem to host our application in
  val system = ActorSystem("demo")

  // every spray-can HttpServer (and HttpClient) needs an IOBridge for low-level network IO
  // (but several servers and/or clients can share one)
  val ioBridge = new IOBridge(system).start()

 // def config = system.settings.config
 lazy val config = ConfigFactory.load()

  // create and start our service actor
  val service = config.getString("label.service.type") match {
    case "hash" =>  system.actorOf(Props(new HashMapLabelServiceActor {
       override def givenActorSystem = system
       override def givenConfig = config
    }), "hash-map-label-service")
    case x => throw new AssertionError("Can't launch a service for " + x)
  }

  // create and start the spray-can HttpServer, telling it that
  // we want requests to be handled by our singleton service actor
  val httpServer = system.actorOf(
    Props(new HttpServer(ioBridge, SingletonHandler(service))),
    name = "http-server"
  )

  // a running HttpServer can be bound, unbound and rebound
  // initially to need to tell it where to bind to
  httpServer ! HttpServer.Bind("localhost", 8080)

  // finally we drop the main thread but hook the shutdown of
  // our IOBridge into the shutdown of the applications ActorSystem
  system.registerOnTermination {
    ioBridge.stop()
  }
}