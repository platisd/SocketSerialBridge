package bridge;

/**
 * An abstract class that should be implemented by classes that are handling a data stream and want to be
 * connected to each other. Each class should provide a getter (getData()) and a setter (write(String s)) for
 * reading and writing data to it.
 * The DataStream classes should save data in a thread-safe FIFO data structure.
 * 
 * @author platisd
 */
public interface DataStream {

	/**
	 * Allows reading packets from the stream
	 * @return the oldest packet that has not been parsed yet 
	 */
	public String read();

	/**
	 * Allows writing packets (as strings) to the stream
	 * @param the packet to be written to the stream, as a string
	 */
	public void write(String s);
}
