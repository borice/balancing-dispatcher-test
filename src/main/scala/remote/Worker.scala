package remote

import akka.actor.{Actor, ActorLogging}
import scala.util.Random

object Worker {
  case class WorkDone(id: Int)
  case object WorkFailed
}

class Worker extends Actor with ActorLogging {
  import Protocol._
  import Worker._

  val rnd = new Random()

  log.info("Worker {} started", self.path)

  override def postRestart(reason: Throwable) = log.warning("postRestart: {}: reason was: {}", self.path, reason.getMessage)

  override def receive = {
    case Message(i) =>
      // simulate doing some work
      val delay = rnd.nextInt(7) * 1000
      log.info(f"${self.path}: Doing work on id $i%,d for $delay%,d ms")
      Thread.sleep(delay)
      if (i % 9 == 0)
        throw new Exception(s"${self.path} I did it on purpose to test the supervisor strategy")
      sender ! WorkDone(i)

  }
}
