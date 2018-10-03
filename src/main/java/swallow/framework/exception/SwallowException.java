package swallow.framework.exception;

/**
 * swallow框架构 异常
 * @author aohanhe
 *
 */
public class SwallowException extends Exception{

	private static final long serialVersionUID = 2744853534788968021L;
	
	public SwallowException(String message,Throwable ex) {
		super(message,ex);
	}
	
	public SwallowException(String message) {
		super(message);
	}

}
