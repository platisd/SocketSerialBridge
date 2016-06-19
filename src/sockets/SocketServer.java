package sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import bridge.DataStream;

/**
 * A simple server that handles web sockets. One user at a time. If you want to handle more than one,
 * then use the MultiSocketServer instead.
 * 
 * @author platisd
 */
public class SocketServer implements Runnable, DataStream{
	protected ServerSocket server;
	protected int serverPort = 8088; //the default port this server instance will listen to
	protected Socket socket;	
	protected PrintWriter out;
	protected boolean socketInitialized = false; //if this is false, it means that there is not connection atm
	private BlockingQueue<String> socketData = new PriorityBlockingQueue<String>(); //FIFO data structure to save the incoming data


	/**
	 * The default constructor of the SocketServer. The server uses the default port
	 * (serverPort) to listen for incoming connections.
	 */
	public SocketServer() {
		init();
	}
	
	/**
	 * Constructor of the SocketServer in case you want to provide a specific
	 * port for the server to listen for incoming connections.
	 * @param the port number for the server to listen for connections.
	 */
	public SocketServer(int serverPort){
		this.serverPort = serverPort;
		init();
	}

	/**
	 * 
	 */
	private void init() {
		try {
			server = new ServerSocket(serverPort);  //initialize a new connection (if port already in use an error will be thrown)
			System.out.println("Opened port " + serverPort + " and waiting");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(String s){
		if (isConnected()) out.println(s);
	}

	/**
	 * Indicates whether a user is connected to the socket or not.
	 * @return true if a user has been connected to the socket, false otherwise.
	 */
	public boolean isConnected(){
		return socketInitialized;
	}

	public String read(){
		try {
			return socketData.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * The method that saves/adds a string to the BlockingQueue data structure
	 * @param The string to be added in the BlockingQueue data structure
	 */
	protected void put(String input){
		try {
			socketData.put(input);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run(){ //this will run in parallel to the main thread
		try {
			while (true) {
				socketInitialized = false;
				socket = server.accept(); //wait until a user is connected
				socketInitialized = true;
				System.out.println("Got a user connection!");
				out = new PrintWriter(socket.getOutputStream(), true); //out will now write to the particular socket
				write("Hi, you are connected"); //welcome message sent to the connected client
				BufferedReader reader = new BufferedReader(new InputStreamReader((socket.getInputStream())));
				String input;
				while ((input = reader.readLine()) != null) {
					//System.out.println("Bridge received from socket: " + input); //uncomment if you want to print whatever is being received
					//write("You wrote: " + input); //uncomment if you want to echo back to the client whatever is being received
					put(input); //save the data in order to be transmitted to the serial port
				}
			}
		} catch (BindException e) {
			System.out.println("Port already in use. Start another server with a different"
					+ " port using the setPort(port) command");
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
