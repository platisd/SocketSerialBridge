package bridge;

public interface DataStream {
	public String getData(); //a method to allow the other functions to get the data
	public void write(String s); //a method to allow the other functions to write data through it
}
