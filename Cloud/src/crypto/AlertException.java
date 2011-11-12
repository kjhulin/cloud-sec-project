package crypto;

/**
 * Class: AlertException.java - Custom Alert Exception
 * Author: Donald Talkington - dst071000@utdallas.edu
 * Project: Dropbox API with Symmetric Searchable Encryption
 * Date: November 11th, 2011
 * Version: 1.0
 * @author dtalk
 */
public class AlertException extends Exception
{
	private static final long serialVersionUID = 1L;
	private String message;
	
	/**
	 * AlertException default constructor
	 * calls super class and sets message to unknown
	 */
	public AlertException()
	{
		super();
		message = "unknown";
	}
	
	/**
	 * AlertException constructor
	 * calls super class and sets message to msg
	 * @param msg String error message
	 */
	public AlertException(String msg)
	{
		super(msg);
		message = msg;
	}
	
	/**
	 * AlertException getError method
	 * @return String error message
	 */
	public String getError()
	{return message;}
}
