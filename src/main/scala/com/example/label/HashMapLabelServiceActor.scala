package com.example.label

import akka.actor.{Props, ActorRef, Actor}
import com.example.{ActorSystemContainer, ConfigContainer}
import akka.util.Timeout
import akka.util.duration._

abstract class HashMapLabelServiceActor extends Actor with LabelService with ConfigContainer with ActorSystemContainer {

  implicit val timeout: Timeout = Timeout(givenConfig.getInt("future.timeout") seconds)

  private trait CompositeActor extends LabelStorageActor with HashMapLabelStorage

  val persistenceActor : ActorRef = givenActorSystem.actorOf(Props(new CompositeActor {
    def label(str: String) = Label(str)
  }), "hashmaplabelservice")


  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  override def receive = runRoute {
    plainLabelRoute
  }
}


trait HashMapLabelStorage extends LabelStorage {
  private var storage = Vector[Label]()

  override def add(label: Label) = {
    storage = storage :+ label
  }

  override def retrieve(): Vector[Label] = {
    storage
  }

  override def remove(label: Label) = storage = storage.filterNot(_ == label)

  case class Label(name: String) extends LabelLike

}
