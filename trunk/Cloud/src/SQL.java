import java.sql.*;
import java.util.Vector;

//TODO - Javadoc comments
// cryptoproject11@gmail.com // password

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
	
	// SELECT LOOKUP - COMPLETE
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
	
	// GET IDS FOR WORD - COMPLETE
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
	
	// GET IDS FOR WORD - COMPLETE
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
	
	// GET IDS FOR WORD - COMPLETE
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
	
	// GET DISTINCT WORDS - COMPLETE
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
	
	// INSERT WORD - COMPLETE
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
	
	// INSERT WORD - COMPLETE
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
	
	// INSERT DOCUMENT ID - COMPLETE
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
	
	// GET DOCUMENT ID - COMPLETE
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
	
	// GET DOCUMENT PATH - COMPLETE
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
	
	// GET MAX LOOKUP VALUE LENTH - COMPLETE
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
	
	// MAX DOCUMENT ID (USED BY TRAPDOOR) - COMPLETE
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

	// DROP WORD - COMPLETE
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
	
	// DROP DATABASE - COMPLETE
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
	
	// CREATE DATABASE - COMPLETE
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
