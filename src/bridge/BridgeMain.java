package bridge;

import serial.SerialHandler;
import sockets.SocketServer;

/**
 * The main class which spawns a serial connection and a socket server (using the default ports in each case).
 * Then using the StreamBridge class, data from serial are transmitted to the sockets and vice versa in two separate threads.
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
	}

}
