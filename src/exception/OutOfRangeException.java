package exception;

public class OutOfRangeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public OutOfRangeException(){
		super();
	}
	public OutOfRangeException(String mess){
		super(mess);
	}
}
