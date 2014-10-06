package exception;

public class WrongTypeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public WrongTypeException(){
		super();
	}
	public WrongTypeException(String mess){
		super(mess);
	}
}
