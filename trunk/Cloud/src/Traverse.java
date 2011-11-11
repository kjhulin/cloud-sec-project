import java.io.File;
import java.util.regex.Pattern;

//TODO - Javadoc Comments

public class Traverse
{
	//Regular expression to match file extension
	private final static String regex = "^.*(.SSE2){1,1}$";
	//Original File object 
	private File file;
	
	//Constructor
	public Traverse(File file)
	{this.file = file;}
	//Start Method - Begins Recursive Traverse
	public void start() throws AlertException
	{traverse(file);}
	
	//Traverse - Recursive Method
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
