package bridge;

import serial.SerialHandler;
import wifi.WifiServer;

public class BridgeMain {

	public static void main(String[] args) {
		SerialHandler serial = new SerialHandler();
		WifiServer wifiServer = new WifiServer();
		new Thread(wifiServer).start(); //start the server on a separate thread
		new Thread(new StreamBridge(serial, wifiServer)).start(); //from serial to wifi
		new Thread(new StreamBridge(wifiServer, serial)).start(); //from wifi to serial
	}

}
