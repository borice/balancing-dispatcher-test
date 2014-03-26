package remote

import akka.actor.{Props, ActorSystem}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.slf4j.Logging
import akka.routing.FromConfig

object Main extends App with Logging {
  if (args.length < 1)
    throw new RuntimeException("Missing cmd line argument")

  args.head match {
    case "receiver" =>
      ActorSystem("systemA", ConfigFactory.load("receiver"))

    case "sender" =>
      val systemB = ActorSystem("systemB", ConfigFactory.load("sender"))
      val router = systemB.actorOf(FromConfig.props(Props[Receiver]), "receiver")
      logger.info(s"Created receiver router at ${router.path}")

      val messageCount = args(1).toInt
      val sender = systemB.actorOf(Props(classOf[Sender], router, messageCount), "sender")
      logger.info(s"Created sender at ${sender.path}")

    case _ => throw new RuntimeException("Invalid command line args specified")
  }
}
