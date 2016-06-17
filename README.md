# SocketSerialBridge
A simple program written in Java to to have a 2-way bridge between web sockets and a serial connection. It uses the [Java Simple Serial Connector](https://code.google.com/archive/p/java-simple-serial-connector/) library instead of the RXTX library. I found it simpler to use and install. The most common use case for this, is a Raspberry Pi, connected to the Internet and an Arduino. It relays messages as strings from the websocket connection to the serial and vice versa. You can easily modify this to handle bytes.

### How to run
The easiest way I have found to run this, is extract the project as a runnable JAR through eclipse and then then run it with `java -jar yourJarName.jar`

### Class diagram
Here is a (rather abstract) class diagram, that should give you an overview of how things are structured and run.
![class diagram](http://i.imgur.com/WUWaJ3T.png)

### Troubleshooting
When I first tried to run the .jar in the Raspberry Pi, I kept getting an UnsatisfiedLinkError. This was solved easily, please have a look at this [issue](https://github.com/scream3r/java-simple-serial-connector/issues/93).

### License
I am including the JSSC library in the [/lib](lib) folder for convenience. It is covered by the LGPL license instead. The rest is under the MIT license.
