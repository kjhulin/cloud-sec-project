package crypto;
import java.sql.*;
import java.util.Vector;

/**
 * Class: SQL.java - SQLite commands
 * Author: Donald Talkington - dst071000@utdallas.edu
 * Project: Dropbox API with Symmetric Searchable Encryption
 * Date: November 11th, 2011
 * Version: 1.0
 * @author dtalk
 */
public class SQL
{
	private static final String SQL_CLASS = "org.sqlite.JDBC";
	private static final String SQL_FILE = "jdbc:sqlite:SSE2.DB";
	
	private static final String CREATE_DOCUMENT_TABLE = "create table if not exists document(id INTEGER PRIMARY KEY AUTOINCREMENT, path TEXT UNIQUE)";
	private static final String DROP_DOCUMENT_TABLE = "drop table if exists document";
	private static final String INSERT_PREP_DOCUMENT_PATH = "insert into document(path) values (?)";
	private static final String SELECT_ALL_DOCUMENT = "select * from document";
	private static final String SELECT_PREP_DOCUMENT_PATH = "select * from document where path=?";
	private static final String SELECT_PREP_DOCUMENT_ID = "select * from document where id=?";
	private static final String SELECT_MAX_DOCUMENT_ID = "select max(id) from document";
	
	private static final String CREATE_WORD_TABLE = "create table if not exists word(word TEXT, id INTEGER, FOREIGN KEY (id) REFERENCES document(id))";
	private static final String DROP_WORD_TABLE = "drop table if exists word";
	private static final String INSERT_PREP_WORD = "insert into word(word,id) values (?,?)";
	private static final String SELECT_ALL_WORD = "select * from word";
	private static final String SELECT_DIST_WORD = "select distinct word from word";
	private static final String SELECT_PREP_WORD_WORD = "select id from word where word=?";
	
	private static final String CREATE_LOOKUP_TABLE = "create table if not exists lookup(value TEXT, id INTEGER, FOREIGN KEY (id) REFERENCES document(id))";
	private static final String DROP_LOOKUP_TABLE = "drop table if exists lookup";
	private static final String INSERT_PREP_LOOKUP = "insert into lookup(value,id) values (?,?)";
	private static final String SELECT_ALL_LOOKUP = "select * from lookup";
	private static final String SELECT_MAX_LOOKUP_VALUE_LEN = "select max(len) from (select length(value) as 'len' from lookup)";
	private static final String SELECT_COUNT_LOOKUP_VALUE = "select id, count(value) as 'words' from lookup group by id";
	private static final String SELECT_PREP_LOOKUP_VALUE = "select * from lookup where value=?";
	private static final String SELECT_PREP_LOOKUP = "select path from document where id in (select distinct id from lookup where value in (";
	
	/**
	 * SQL selectLookup method - used to search or lookup trapdoors
	 * @param traps Vector that contains Strings with HEX representation of trapdoors
	 * @return Vector that contains Strings with paths to documents that matched the supplied trapdoors
	 * @throws AlertException Thrown for SQLExceptions, IOExceptions
	 */
	public static final Vector<String> selectLookup(final Vector<String> traps) throws AlertException
	{
		Vector<String> paths = new Vector<String>();
		
		StringBuilder query = new StringBuilder(SELECT_PREP_LOOKUP);
		
		int count = traps.size();
		for(int i = 0; i < count; i++)
		{
			if((i+1) != count)
			{query.append("?,");}
			else
			{query.append("?))");}
		}
		
		Connection conn = null;
		ResultSet rs = null;
		try
		{
			Class.forName(SQL_CLASS);
		    conn = DriverManager.getConnection(SQL_FILE);
		    PreparedStatement prep = conn.prepareStatement(query.toString());
		    for(int i = 0; i < count; i++)
		    {prep.setString((i+1), traps.get(i));}
		    rs = prep.executeQuery();
		    
		    String path;
		    while(rs.next())
		    {
		    	path = rs.getString("path");
		    	paths.add(path);
		    }
		}
		catch(SQLException | ClassNotFoundException e)
		{throw new AlertException("selectLookup: unable to lookup values");}
		finally
		{
			try
			{
				if(rs != null)
					rs.close();
				if(conn != null)
					conn.close();
			}
			catch(SQLException e){}
		}
	    
	    return paths;
	}
	
	/**
	 * SQL checkLookupValue method - used to check if lookup table contains value
	 * @param val String value with HEX representation
	 * @return Integer that indicates the number of time the HEX representation appears in the table
	 * @throws AlertException Thrown for SQLExceptions, IOExceptions
	 */
	public static final int checkLookupValue(final String val) throws AlertException
	{
		int count = 0;
		
		Connection conn = null;
		ResultSet rs = null;
		try
		{
			Class.forName(SQL_CLASS);
		    conn = DriverManager.getConnection(SQL_FILE);
		    PreparedStatement prep = conn.prepareStatement(SELECT_PREP_LOOKUP_VALUE);
		    prep.setString(1, val);
		    rs = prep.executeQuery();
		    
		    while(rs.next())
		    {count++;}
		}
		catch(SQLException | ClassNotFoundException e)
		{throw new AlertException("checkLookupValue: unable to check value");}
		finally
		{
			try
			{
				if(rs != null)
					rs.close();
				if(conn != null)
					conn.close();
			}
			catch(SQLException e){}
		}
	    
	    return count;
	}	
	
	/**
	 * SQL getLookupCount - used to determine the number listings for each document
	 * @return Vector of Strings that represent command separated list (id,number of listings)
	 * @throws AlertException Thrown for SQLException, IOException
	 */
	public static final Vector<String> getLookupCount() throws AlertException
	{
		Vector<String> results = new Vector<String>();
		
		Connection conn = null;
		ResultSet rs = null;
		try
		{
			Class.forName(SQL_CLASS);
		    conn = DriverManager.getConnection(SQL_FILE);
		    Statement state = conn.createStatement();
		    rs = state.executeQuery(SELECT_COUNT_LOOKUP_VALUE);
		    
		    int count;
		    int id;
		    while(rs.next())
		    {
		    	count = rs.getInt("words");
		    	id = rs.getInt("id");
		    	results.add(id + "," + count);
		    }
		}
		catch(SQLException | ClassNotFoundException e)
		{throw new AlertException("getLookupCount: unable to count values");}
		finally
		{
			try
			{
				if(rs != null)
					rs.close();
				if(conn != null)
					conn.close();
			}
			catch(SQLException e){}
		}
	    
	    return results;
	}	
	
	/**
	 * SQL getDocumentsWord method - used to document ids associated with a word
	 * @param w - String represents the word
	 * @return Vector of Integers which indicated document ids associated with the provided word
	 * @throws AlertException Thrown for SQLException, IOException
	 */
	public static final Vector<Integer> getDocumentsWord(final String w) throws AlertException
	{
		Vector<Integer> ids = new Vector<Integer>();
		
		Connection conn = null;
		ResultSet rs = null;
		try
		{
			Class.forName(SQL_CLASS);
		    conn = DriverManager.getConnection(SQL_FILE);
		    PreparedStatement prep = conn.prepareStatement(SELECT_PREP_WORD_WORD);
	    	prep.setString(1, w);
	    	rs = prep.executeQuery();
		    
		    int id;
		    while(rs.next())
		    {
		    	id = rs.getInt("id");
		    	ids.add(id);
		    }
		}
		catch(SQLException | ClassNotFoundException e)
		{throw new AlertException("getDocumentsWord: unable to select ids");}
		finally
		{
			try
			{
				if(rs != null)
					rs.close();
				if(conn != null)
					conn.close();
			}
			catch(SQLException e){}
		}
	    
	    return ids;
	}	
	
	/**
	 * SQL getDistinctWords method - used to create list of distinct words
	 * @return Vector of Strings that represent the non-duplicate set of words
	 * @throws AlertException Thrown for SQLException, IOExceptions
	 */
	public static final Vector<String> getDistinctWords() throws AlertException
	{
		Vector<String> words = new Vector<String>();
		
		Connection conn = null;
		ResultSet rs = null;
		try
		{
			Class.forName(SQL_CLASS);
		    conn = DriverManager.getConnection(SQL_FILE);
		    Statement state = conn.createStatement();
		    rs = state.executeQuery(SELECT_DIST_WORD);
		    
		    String w;
		    while(rs.next())
		    {
		    	w = rs.getString("word");
		    	words.add(w);
		    }
		}
		catch(SQLException | ClassNotFoundException e)
		{throw new AlertException("getDistinctWords: unable to select words");}
		finally
		{
			try
			{
				if(rs != null)
					rs.close();
				if(conn != null)
					conn.close();
			}
			catch(SQLException e){}
		}
	    
	    return words;
	}
	
	/**
	 * SQL setLookupValue method - used to insert trapdoor and associated document id
	 * @param val String with HEX representation of trapdoor
	 * @param id Integer that indicates associated document id
	 * @return Boolean result that indicates if insertion into the table was successful
	 */
	public static final boolean setLookupValue(final String val, final int id)
	{
		boolean result = true;
		
		Connection conn = null;
	    try
	    {
	    	Class.forName(SQL_CLASS);
		    conn = DriverManager.getConnection(SQL_FILE);
	    	PreparedStatement prep = conn.prepareStatement(INSERT_PREP_LOOKUP);
	    	prep.setString(1, val);
	    	prep.setInt(2, id);
	    	prep.executeUpdate();
	    }
	    catch(SQLException | ClassNotFoundException e)
	    {result = false;}
	    finally
	    {
	    	if(conn != null)
	    	{
	    		try
	    		{conn.close();}
	    		catch(SQLException e){}
	    	}
	    }
	    
	    return result;
	}
	
	/**
	 * SQL setWord method - used to insert word and associated document id
	 * @param w String with character representation of word
	 * @param id Integer that indicates associated document id
	 * @return Boolean result that indicates if insertion into the table was successful
	 */
	public static final boolean setWord(final String w, final int id)
	{
		boolean result = true;
		
		Connection conn = null;
	    try
	    {
	    	Class.forName(SQL_CLASS);
		    conn = DriverManager.getConnection(SQL_FILE);
	    	PreparedStatement prep = conn.prepareStatement(INSERT_PREP_WORD);
	    	prep.setString(1, w);
	    	prep.setInt(2, id);
	    	prep.executeUpdate();
	    }
	    catch(SQLException | ClassNotFoundException e)
	    {result = false;}
	    finally
	    {
	    	if(conn != null)
	    	{
	    		try
	    		{conn.close();}
	    		catch(SQLException e){}
	    	}
	    }
	    
	    return result;
	}
	
	/**
	 * SQL setDocumentID method - used to insert document path
	 * @param path String that represents absolute document path
	 * @return Boolean result that indicates if insertion into the table was successful
	 */
	public static final boolean setDocumentID(final String path)
	{
		boolean result = true;
		
		Connection conn = null;
	    try
	    {
	    	Class.forName(SQL_CLASS);
		    conn = DriverManager.getConnection(SQL_FILE);
	    	PreparedStatement prep = conn.prepareStatement(INSERT_PREP_DOCUMENT_PATH);
	    	prep.setString(1, path);
	    	prep.executeUpdate();
	    }
	    catch(SQLException | ClassNotFoundException e)
	    {result = false;}
	    finally
	    {
	    	if(conn != null)
	    	{
	    		try
	    		{conn.close();}
	    		catch(SQLException e){}
	    	}
	    }
	    
	    return result;
	}
	
	/**
	 * SQL getDocumentID method - used to retrieve document id for particular absolute document path
	 * @param path String that contains absolute path to document
	 * @return Integer that indicates the associated document id
	 * @throws AlertException Thrown for SQLExceptions, IOExceptions
	 */
	public static final int getDocumentID(final String path) throws AlertException
	{
		int result = -1;
		
		Connection conn = null;
		ResultSet rs = null;
		try
		{
			Class.forName(SQL_CLASS);
		    conn = DriverManager.getConnection(SQL_FILE);
		    PreparedStatement prep = conn.prepareStatement(SELECT_PREP_DOCUMENT_PATH);
		    prep.setString(1, path);
		    rs = prep.executeQuery();
		    
		    while(rs.next())
		    {result = rs.getInt("id");}
		}
		catch(SQLException | ClassNotFoundException e)
		{throw new AlertException("getDocumentID: unable to select document id");}
		finally
		{
			try
			{
				if(rs != null)
					rs.close();
				if(conn != null)
					conn.close();
			}
			catch(SQLException e){}
		}
	    
	    return result;
	}
	
	/**
	 * SQL getDocumentPath method - used to retrieve document path for associated document id
	 * @param id - Integer that indicates the associated document id
	 * @return String that contains absolute path to document
	 * @throws AlertException Thrown for SQLExceptions, IOExceptions
	 */
	public static final String getDocumentPath(final int id) throws AlertException
	{
		String result = null;
		
		Connection conn = null;
		ResultSet rs = null;
		try
		{
			Class.forName(SQL_CLASS);
		    conn = DriverManager.getConnection(SQL_FILE);
		    PreparedStatement prep = conn.prepareStatement(SELECT_PREP_DOCUMENT_ID);
		    prep.setInt(1, id);
		    rs = prep.executeQuery();
		    
		    while(rs.next())
		    {result = rs.getString("path");}
		}
		catch(SQLException | ClassNotFoundException e)
		{throw new AlertException("getDocumentID: unable to select document id");}
		finally
		{
			try
			{
				if(rs != null)
					rs.close();
				if(conn != null)
					conn.close();
			}
			catch(SQLException e){}
		}
	    
	    return result;
	}
	
	/**
	 * SQL getMaxLookupValue method - used to determine the maximum length of all lookup values
	 * @return Integer that represents maximum length in bytes (values are in HEX)
	 * @throws AlertException Thrown for SQLException, IOException
	 */
	public static final int getMaxLookupValue() throws AlertException
	{
		int result = -1;
		
		Connection conn = null;
		ResultSet rs = null;
		try
		{
			Class.forName(SQL_CLASS);
		    conn = DriverManager.getConnection(SQL_FILE);
		    Statement state = conn.createStatement();
		    rs = state.executeQuery(SELECT_MAX_LOOKUP_VALUE_LEN);
		    
		    while(rs.next())
		    {result = rs.getInt("max(len)");}
		    
		    result = (int) Math.ceil((double)(result / 2.0));
		}
		catch(SQLException | ClassNotFoundException e)
		{throw new AlertException("getMaxLookupValue: unable to select max value");}
		finally
		{
			try
			{
				if(rs != null)
					rs.close();
				if(conn != null)
					conn.close();
			}
			catch(SQLException e){}
		}
	    
	    return result;
	}
	
	/**
	 * SQL getMaxDocumentID method - used to determine maximum associated document id
	 * @return Integer that represents the maximum document id
	 * @throws AlertException Thrown for SQLException, IOException
	 */
	public static final int getMaxDocumentID() throws AlertException
	{
		int result = -1;
		
		Connection conn = null;
		ResultSet rs = null;
		try
		{
			Class.forName(SQL_CLASS);
		    conn = DriverManager.getConnection(SQL_FILE);
		    
		    Statement state = conn.createStatement();
		    rs = state.executeQuery(SELECT_MAX_DOCUMENT_ID);
		    while(rs.next())
		    {result = rs.getInt("max(id)");}
		}
		catch(SQLException | ClassNotFoundException e)
		{throw new AlertException("getMaxDocumentID: unable to select max id");}
		finally
		{
			try
			{
				if(rs != null)
					rs.close();
				if(conn != null)
					conn.close();
			}
			catch(SQLException e){}
		}
		
	    return result;
	}

	/**
	 * SQL dropWord method - used to drop and create empty word table
	 * @return Boolean that indicates if the drop and create operations were successful
	 */
	public static final boolean dropWord()
	{
		boolean result = true;
		
		Connection conn = null;
		try
		{
			Class.forName(SQL_CLASS);
			conn = DriverManager.getConnection(SQL_FILE);
			Statement state = conn.createStatement();
			result = state.executeUpdate(DROP_WORD_TABLE) == 0 ? true : false;
			result = result && state.executeUpdate(CREATE_WORD_TABLE) == 0 ? true : false;
		}
		catch(SQLException | ClassNotFoundException e)
		{result = false;}
		finally
		{
			if(conn != null)
			{
				try
				{conn.close();}
				catch(SQLException e){}
			}
		}
		
		return result;
	}
	
	/**
	 * SQL dropDatabase method - drops all database tables
	 * @return Boolean that indicates if the drop operations were successful
	 */
	public static final boolean dropDatabase()
	{
		boolean result = true;
		
		Connection conn = null;
		try
		{
			Class.forName(SQL_CLASS);
			conn = DriverManager.getConnection(SQL_FILE);
			Statement state = conn.createStatement();
			result = state.executeUpdate(DROP_LOOKUP_TABLE) == 0 ? true : false;
			result = result && state.executeUpdate(DROP_WORD_TABLE) == 0 ? true : false;
			result = result && state.executeUpdate(DROP_DOCUMENT_TABLE) == 0 ? true : false;
		}
		catch(SQLException | ClassNotFoundException e)
		{result = false;}
		finally
		{
			if(conn != null)
			{
				try
				{conn.close();}
				catch(SQLException e){}
			}
		}
		
		return result;
	}
	
	/**
	 * SQL createDatabase method - used to create empty database tables
	 * @return Boolean that indicates if the create operations were completed successfully
	 */
	public static final boolean createDatabase()
	{
		boolean result = true;
		
		Connection conn = null;
		try
		{
			Class.forName(SQL_CLASS);
			conn = DriverManager.getConnection(SQL_FILE);
			Statement state = conn.createStatement();
			result = state.executeUpdate(CREATE_DOCUMENT_TABLE) == 0 ? true : false;
			result = result && state.executeUpdate(CREATE_WORD_TABLE) == 0 ? true : false;
			result = result && state.executeUpdate(CREATE_LOOKUP_TABLE) == 0 ? true : false;
		}
		catch(SQLException | ClassNotFoundException e)
		{result = false;}
		finally
		{
			if(conn != null)
			{
				try
				{conn.close();}
				catch(SQLException e){}
			}
		}
		
		return result;
	}
	
	/**
	 * SQL showDatabase method - used to display the contents of all tables
	 * @throws Exception Thrown for SQLException, IOException
	 */
	public static void showDatabase() throws Exception
	{
		System.out.println("SHOW DATABASE");
		Class.forName(SQL_CLASS);
		Connection conn = DriverManager.getConnection(SQL_FILE);
		Statement state = conn.createStatement();
		
		ResultSet rs = state.executeQuery(SELECT_ALL_DOCUMENT);
	    while (rs.next())
	    {
	    	System.out.print("id = " + rs.getInt("id") + ", ");
	    	System.out.println("path = " + rs.getString("path"));
	    }
	    rs.close();
	    
	    rs = state.executeQuery(SELECT_ALL_WORD);
	    while (rs.next())
	    {
	    	System.out.print("id = " + rs.getString("word") + ", ");
	    	System.out.println("path = " + rs.getInt("id"));
	    }
	    rs.close();
	    
	    rs = state.executeQuery(SELECT_ALL_LOOKUP);
	    while (rs.next())
	    {
	    	System.out.print("id = " + rs.getString("value") + ", ");
	    	System.out.println("path = " + rs.getInt("id"));
	    }
	    rs.close();
	    
	    conn.close();
	}
	
	/**
	 * SQL main method - used for test cases
	 * @param args no arguments are required and should not be supplied
	 * @throws Exception inappropriate catch-all case
	 */
	public static void main(String[] args) throws Exception
	{
//		dropDatabase();
		createDatabase();
		showDatabase();
//		
//		setDocumentID("/test/a/b/c");
//		setDocumentID("/a/b/c/d");
//		setDocumentID("/testing");
//		
//		System.out.println(getDocumentID("/test/a/b/c"));
//		System.out.println(getDocumentID("/test/"));
//		
//		setDocumentID("/testing/1234567890");
//		
//		System.out.println("MAX ID: " + getMaxDocumentID());
		
//		showDatabase();
//		dropDatabase();
	}
}
