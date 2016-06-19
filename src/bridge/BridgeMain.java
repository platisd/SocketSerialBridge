package bridge;

import serial.SerialHandler;
import sockets.MultiSocketServer;
import sockets.SocketServer;

/**
 * The main class which spawns a serial connection and a socket server (using the default ports in each case).
 * Then using the StreamBridge class, data from serial are transmitted to the sockets and vice versa in two separate threads.
 * If you need to connect more than one clients and broadcast the messages/packets from the serial connection to all of them
 * Then use the MultiSocketServer class (in comments). It is used similarly, with the difference that it accepts multiple clients.
 * The input from the various clients is accumulated in a common data structure among each instance of MultiSocketServer and
 * instead of writing to a single client, the messages are broadcasted to all of them.
 * 
 * @author platisd
 */
public class BridgeMain {

	public static void main(String[] args) {
		SerialHandler serial = new SerialHandler();
		SocketServer wifiServer = new SocketServer();
		new Thread(wifiServer).start(); //start the server on a separate thread
		new Thread(new StreamBridge(serial, wifiServer)).start(); //from serial to wifi
		new Thread(new StreamBridge(wifiServer, serial)).start(); //from wifi to serial
//		MultiSocketServer multi = new MultiSocketServer(8089); //initialize a server that accepts multiple clients on a different port
//		new Thread(multi).start(); //start the server on a separate thread
//		new Thread(new StreamBridge(serial, multi)).start(); //from serial to the multiple server broadcast
//		new Thread(new StreamBridge(multi, serial)).start(); //from any of the clients to serial
	}

}
