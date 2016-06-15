# SocketSerialBridge
A simple program written in Java to to have a 2-way bridge between web sockets and a serial connection. It uses the [Java Simple Serial Connector](https://code.google.com/archive/p/java-simple-serial-connector/) library instead of the RXTX library. I found it simpler to install. The most common use case for this, is a Raspberry Pi, connected to the Internet and an Arduino. It relays messages from the websocket connection to the serial and vice versa.


### License
I am including the JSSC library in the [/lib](lib) folder for convenience. It is covered by the LGPL license instead. The rest is under the MIT license.
