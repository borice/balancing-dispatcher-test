balancing-dispatcher-test
=========================

Testing the Akka BalancingPool routee with remotely deployed router.

To run, configure your IDE to run the remote.Main class as follows:  
* pass cmd line argument "receiver" to start the receiver (**must be started first**)
* pass cmd line argumets "sender N" to start the sender and send N messages to receiver
