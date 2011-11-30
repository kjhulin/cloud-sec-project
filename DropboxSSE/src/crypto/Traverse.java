package crypto;
import java.io.File;
import java.util.regex.Pattern;

/**
 * Class: Traverse.java - Traverse files and folders
 * Author: Donald Talkington - dst071000@utdallas.edu
 * Project: Dropbox API with Symmetric Searchable Encryption
 * Date: November 11th, 2011
 * Version: 1.0
 * @author dtalk
 */
public class Traverse
{
	/**Regular expression to match file extension*/
	private final static String regex = "^.*(.SSE2){1,1}$";
	/**Original File object*/
	private File file;

	/**
	 * Traverse constructor
	 * @param file File object that points to original source
	 */
	public Traverse(File file)
	{this.file = file;}
	/**
	 * Traverse start method
	 * Begins recursive traversal starting from original source
	 * @throws AlertException Thrown for errors and exceptions
	 */
	public void start() throws AlertException
	{traverse(file);}

	/**
	 * Traverse traverse method - recursive method
	 * If directory, retrieve file listing and make recursive call
	 * If file, look for .SSE2 extensions (keyword files) and insert paths into SQL table
	 * @param f File object that represents current file level
	 * @throws AlertException Thrown for errors and exceptions
	 */
	public void traverse(File f) throws AlertException
	{
		if(f.isDirectory())
                {
			File[] files = f.listFiles();
			for(File x : files)
				traverse(x);
		}
		else if(f.isFile() && Pattern.matches(regex, f.getName()))
		{
			String path = f.getAbsolutePath();
			path = path.substring(0, path.length() - 5);

			if(!SQL.setDocumentID(path))
				throw new AlertException("traverse: unable to insert path");
		}
	}
}
