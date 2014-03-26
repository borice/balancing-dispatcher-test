package remote

import akka.actor._
import akka.routing.{BalancingPool, FromConfig, RandomPool}
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy.{Restart, Escalate}

class Receiver extends Actor with ActorLogging {
  import Protocol._
  import Worker._

  val mySupervisorStrategy = OneForOneStrategy() {
    case _: ActorInitializationException => Escalate
    case _: DeathPactException => Escalate
    case e: Throwable =>
      self ! WorkFailed
      Restart
  }

//  val workerPool = context.actorOf(
//    BalancingPool(4).props(Props[Worker]),
//    name = "workerPool"
//  )

  val workerPool = context.actorOf(FromConfig.props(Props[Worker]), "workerPool")

  var receivedCount = 0
  var timestamp = 0L

  override def receive = {
    case m @ Message(i) =>
      if (receivedCount == 0)
        timestamp = System.currentTimeMillis

      receivedCount += 1

      if (receivedCount % 1000 == 0) {
        val newTimestamp = System.currentTimeMillis
        val delta = newTimestamp - timestamp
        timestamp = newTimestamp
        log.info(f"${self.path}: delta = $delta%,d ms, received = $receivedCount%,d")
      }

      workerPool ! m

    case WorkDone(i) =>
      log.info("Work {} is done", i)

    case WorkFailed =>
      log.warning("Some work failed!")

    case NoMoreMessages =>
      log.info(s"Sender signaled NoMoreMessages; Received $receivedCount messages")
  }
}
