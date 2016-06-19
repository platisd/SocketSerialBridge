package sockets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.util.Vector;

/**
 * A simple server that handles web sockets of multiple clients. Each new user is connected on a separate thread.
 * Data from all of them, are collected in a common data structure and by using the write(String) function, a message
 * is broadcasted to all of them.
 * 
 * @author platisd
 */
public class MultiSocketServer extends SocketServer {
	private Vector<PrintWriter> connectedSockets = new Vector<PrintWriter>(); //contains the printwriters to the different clients
	
	/**
	 * The default constructor of the MultiSocketServer. The server uses the default port
	 * (serverPort) to listen for incoming connections and can serve multiple users.
	 */
	public MultiSocketServer() {
		super();
	}

	/**
	 * Constructor of the MultiSocketServer in case you want to provide a specific
	 * port for the server to listen for incoming connections.
	 * @param the port number for the server to listen for connections.
	 */
	public MultiSocketServer(int serverPort){
		super(serverPort);
	}
	
	/**
	 * Broadcasts the packet (as a string) to the connected clients.
	 * @param the packet to be broadcasted to the client(s), as a string.
	 */
	@Override
	public void write(String input){
		if (isConnected()){
			for (PrintWriter p : connectedSockets){
				p.println(input);
			}
		}
	}

	@Override
	public void run(){ //this will run in parallel to the main thread
		try {
			System.out.println("Opened port " + getPort() + " and waiting for multiple clients");
			socket = server.accept(); //wait until a user is connected
			System.out.println("Got a user connection!");
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true); //out will now write to the particular socket
			out.println("Hi, you are connected"); //welcome message sent to the connected client
			connectedSockets.add(out); //save it so we can broadcast to it later
			new Thread(this).start(); //start a new thread and wait for another connection
			BufferedReader reader = new BufferedReader(new InputStreamReader((socket.getInputStream())));
			String input;
			while ((input = reader.readLine()) != null) {
				//System.out.println("Bridge received from socket: " + input); //uncomment if you want to print whatever is being received
				//write("You wrote: " + input); //uncomment if you want to echo back to the client whatever is being received
				put(input); //save the data in order to be transmitted to the serial port

			}
		} catch (BindException e) {
			System.out.println("Port already in use. Start another server with a different"
					+ " port using the setPort(port) command");
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
