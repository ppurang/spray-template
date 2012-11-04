package com

import typesafe.config.Config
import akka.actor.ActorSystem

/**
 * Created with IntelliJ IDEA.
 * User: ppurang
 * Date: 11/4/12
 * Time: 1:54 PM
 * To change this template use File | Settings | File Templates.
 */
package object example {

  trait ConfigContainer {
    def givenConfig: Config
  }

  trait ActorSystemContainer {
    def givenActorSystem : ActorSystem
  }

}
