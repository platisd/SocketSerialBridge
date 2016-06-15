package serial;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import bridge.DataStream;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class SerialHandler implements  SerialPortEventListener, DataStream {
	private boolean portOpen = false; //if the port has been successfully opened this is true 
	final private String defaultPort = "/dev/ttyACM0"; //the default serial port (linux)
	private SerialPort serialPort;
	private String packetDelimiter = "*";
	private long serialTimeout = 10; //the amount of time we shall wait for a "packet" to finish
	private BlockingQueue<String> socketData = new PriorityBlockingQueue<String>();

	public SerialHandler(){
		initSerial(defaultPort);
	}

	public SerialHandler(String port) {
		initSerial(port);
	}

	private void initSerial(String port){
		serialPort = new SerialPort(port); 
		try {
			portOpen = false;
			serialPort.openPort();//Open port
			portOpen = true;
			System.out.println("Opened serial port: " + port);
			serialPort.setParams(
					SerialPort.BAUDRATE_9600,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
			int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;//Prepare mask
			serialPort.setEventsMask(mask);//Set mask
			serialPort.addEventListener(this);//Add SerialPortEventListener
		}catch (SerialPortException ex) {
			System.out.println(ex);
		}	
	}

	public String[] getAvailablePorts() {
		return SerialPortList.getPortNames();
	}

	public void write(String s) {
		try {
			if (isConnected()) serialPort.writeString(s);
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}

	public String getData(){
		try {
			return socketData.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isConnected(){
		return portOpen;
	}

	public void close(){
		try {
			serialPort.closePort();
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setTimeout(long serialTimeout){
		this.serialTimeout = serialTimeout;
	}
	
	public String toString(){
		return "serial handler";
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		if(event.isRXCHAR()){//If data is available
			if(event.getEventValue()>0){//Check bytes count in the input buffer
				//Read data, if bytes available 
				try {
					//	System.out.print(serialPort.readString());
					String incomingCharacter = serialPort.readString(1); //read just one character/byte from the stream
					long currentTime = System.currentTimeMillis(); //log down the current time
					String incomingPacket = ""; //here the incoming packet will be stored as a string
					while (!incomingCharacter.equals(packetDelimiter) && //the incoming character is not the delimiter
							currentTime - System.currentTimeMillis() < serialTimeout){ //AND we have not timed-out
						if (incomingCharacter != null){
							incomingPacket += incomingCharacter; //don't include the delimiter mainly so we don't get packages of just the delimiter
						}
						incomingCharacter = serialPort.readString(1); //read the next character
					}
					if (!incomingPacket.equals(packetDelimiter)){ //if the packet doesn't just contain only the delimiter
						//System.out.println("Bridge received from serial: " + incomingPacket); //print out the packet that is about to be saved
						socketData.put(incomingPacket); //add the packet to the list to be transmitted via the socket
					}
				}catch (SerialPortException ex) {
					System.out.println(ex);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		else if(event.isCTS()){//If CTS line has changed state
			if(event.getEventValue() == 1){//If line is ON
				System.out.println("CTS - ON");
			}
			else {
				System.out.println("CTS - OFF");
			}
		}
		else if(event.isDSR()){///If DSR line has changed state
			if(event.getEventValue() == 1){//If line is ON
				System.out.println("DSR - ON");
			}
			else {
				System.out.println("DSR - OFF");
			}
		}
	}

}
