package serial;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import bridge.DataStream;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 * Handles a serial connection using the JSSC library. This code was largely adopted from an example on the
 * JSSC documentation. Uses a BlockingQueue to store the incoming data, that are split into packets as
 * defined by the packetDelimiter.
 * 
 * @author platisd
 */
public class SerialHandler implements  SerialPortEventListener, DataStream {
	private boolean portOpen = false; //if the port has been successfully opened this is true 
	final private String defaultPort = "/dev/ttyACM0"; //the default serial port (linux)
	private SerialPort serialPort;
	private String packetDelimiter = "*"; //the character/byte that will designate when a "packet" from the serial port is complete
	private long serialTimeout = 10; //the amount of time we shall wait for a "packet" to finish
	private BlockingQueue<String> serialData = new PriorityBlockingQueue<String>(); //FIFO data structure to save the incoming data

	/**
	 * The default constructor for the SerialHandler class which tries to connect to the default
	 * serial port, as defined by defaultPort.
	 */
	public SerialHandler(){
		initSerial(defaultPort);
	}

	/**
	 * A SerialHandler class constructor that allows the user to specify the serial port
	 * they want to connect to.
	 * @param The serial port to initiate a connection to, e.g /dev/ttyACM0, COM3 etc.
	 */
	public SerialHandler(String port) {
		initSerial(port);
	}

	/**
	 * Initializes the serial port connection and sets the various parameters.
	 * @param The serial port to initiate a connection to, e.g. /dev/ttyACM0, COM3 etc.
	 */
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

	/**
	 * Presents the serial ports that are available, as a string array.
	 * @return string array with the available serial ports
	 */
	public static String[] getAvailablePorts() {
		return SerialPortList.getPortNames();
	}

	public void write(String s) {
		try {
			if (isConnected()) serialPort.writeString(s);
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}

	public String read(){
		try {
			return serialData.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Indicates whether a successful connection to a serial port has been made.
	 * @return true if we have successfully established a connection to the serial port, false otherwise.
	 */
	public boolean isConnected(){
		return portOpen;
	}

	/**
	 * Closes the serial port and disables listening to incoming data from it.
	 */
	public void close(){
		try {
			serialPort.closePort();
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the amount of time to wait for a package to arrive.
	 * @param amount of time to wait for a package to arrive in milliseconds.
	 */
	public void setTimeout(long serialTimeout){
		this.serialTimeout = serialTimeout;
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		if(event.isRXCHAR()){//If data is available
			if(event.getEventValue()>0){//Check bytes count in the input buffer
				//Read data, if bytes available 
				try {
					long startTime = System.currentTimeMillis(); //log down the current time before entering the loop
					String incomingPacket = ""; //here the incoming packet will be stored as a string
					String incomingCharacter = ""; //initializing an empty incoming character variable
					do{
						incomingCharacter = serialPort.readString(1); //read just one character/byte from the stream
						if (incomingCharacter != null){
							incomingPacket += incomingCharacter;
						}
					}while(!incomingCharacter.equals(packetDelimiter) && //the incoming character is not the delimiter
							System.currentTimeMillis() - startTime < serialTimeout); //AND we have run out of time
					if (!incomingPacket.equals(packetDelimiter)){ //if the packet doesn't just contain only the delimiter
						//System.out.println("Bridge received from serial: " + incomingPacket); //print out the packet that is about to be saved
						serialData.put(incomingPacket); //add the packet to the list to be transmitted via the socket
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
