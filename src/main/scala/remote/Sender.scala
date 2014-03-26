package remote

import akka.actor._

object Sender {
  private case object Send
}

class Sender(receiver: ActorRef, messageCount: Int) extends Actor with ActorLogging {
  import Sender._
  import Protocol._

  var messagesSent = 0

  log.info(s"Sending $messageCount messages")

  self ! Send

  override def receive = {
    case Send if messagesSent < messageCount =>
      messagesSent += 1
      receiver ! Message(messagesSent)
      self ! Send

    case _ =>
      receiver ! NoMoreMessages
      log.info("Done sending")
  }
}
