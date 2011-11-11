//TODO - Javadoc comments

public class AlertException extends Exception
{
	private static final long serialVersionUID = 1L;
	private String message;
	
	public AlertException()
	{
		super();
		message = "unknown";
	}
	
	public AlertException(String msg)
	{
		super(msg);
		message = msg;
	}
	
	public String getError()
	{return message;}
}
