package com.example.label

import akka.actor.Actor

trait LabelStorage {

  type Label <: LabelLike

  def add(label: Label)

  def retrieve(): Vector[Label]

  def remove(label: Label)

  trait LabelLike {
    def name: String
  }

}

trait LabelStorageActor extends Actor {
  self: LabelStorage =>

  def label(str: String): Label

  override def receive = {
    case AddLabel(str) => {
      add(label(str))
    }
    case GetLabels() => println(Thread.currentThread().getId + " sender => " + sender + " " + retrieve()); sender ! retrieve()
    case x => println("ooops" + x)
  }
}

sealed trait LabelMessage
case class AddLabel(str: String) extends LabelMessage
case class GetLabels() extends LabelMessage

