package crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Vector;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Class: SSE2.java - Implementation for SSE2 Symmetric Searchable Encryption
 * Author: Donald Talkington - dst071000@utdallas.edu
 * Project: Dropbox API with Symmetric Searchable Encryption
 * Date: November 11th, 2011
 * Version: 1.0
 * @author dtalk
 */
public class SSE2
{
	private final static int SALT_SIZE = 8;
	private final static int IV_SIZE = 16;
	private final static int NUM_ROUNDS = 1024;
	private final static int HMAC_SIZE = 32;
	private final static int HMAC_KEY_SIZE = 256;
	private final static String HMAC_MODE = "HmacSHA256";
	private final static int AES_KEY_SIZE = 128;
	private final static String AES_CIPHER_MODE = "AES/CBC/PKCS5Padding";
        public final static String keywordRegex = "[A-Za-z]+";
	public final static String regex = "^("+keywordRegex+")?(,"+keywordRegex+")*$";
	private static String DB_FILE = "SSE2.DB";
	private final static String DB_EXT = ".EXT";

	/**
	 * SSE2 createDatabase method
	 * Performs HMAC verification. If successful, append structure to file.
	 * Finally makes method call to appendHMAC to calcualte and append HMAC.
	 * @param pass Character array that contains password used for password based encryption
	 * @return Boolean result that indicates if creation of structure file was successful
	 * @throws AlertException Thrown for HMAC verification failures, IOException exceptions
	 */
        public static void setDB_Path(String s){
            DB_FILE = s + File.separator + "SSE2.DB";
            SQL.SQL_FILE = "jdbc:sqlite:" + DB_FILE;
        }
        public static boolean isValidKeyword(String s){
            return Pattern.matches(keywordRegex, s);
        }
	public static final boolean createDatabase(final char[] pass) throws AlertException
	{
		File db = new File(DB_FILE);
		File ext = new File(db.getAbsolutePath() + DB_EXT);

                System.out.println(db);
                System.out.println(ext);

		if(ext.exists())
		{
			if(!verifyHMAC(pass.clone()))
				throw new AlertException("createDatabase: hmac verification failed");
		}

		if(!db.exists())
		{
                    if(!SQL.createDatabase())
                        throw new AlertException("createDatabase: unable to create database");
                }

		final byte[] salt = Crypto.generateBytes(SALT_SIZE);
		final byte[] iv = Crypto.generateBytes(IV_SIZE);

		RandomAccessFile file;
		FileChannel channel;
		ByteBuffer buf;
		try
		{
			file = new RandomAccessFile(ext, "rw");
			channel = file.getChannel();
			buf = ByteBuffer.wrap(salt);
			channel.write(buf);
			buf = ByteBuffer.wrap(iv);
			channel.write(buf);
			channel.close();
			file.close();
		}
		catch(Exception e)
		{throw new AlertException("createDatabase: unable to append structure");}

		appendHMAC(pass.clone());
		Arrays.fill(pass, (char) 0);

		return true;
	}

	 /**
	 * SSE2 deleteDatabase method
	 * Performs HMAC verification. If successful, an attempt is made to delete both the database and structure files.
	 * @param pass Character array that contains password used for password based encryption
	 * @return Boolean result that indicates if database and structure files were deleted successfully
	 * @throws AlertException Thrown for HMAC verification failure, IOExceptions
	 */
	public static final boolean deleteDatabase(final char[] pass,String path) throws AlertException
	{
		File db = new File(DB_FILE);
		if(!db.exists())
		if(!verifyHMAC(pass.clone()))
			throw new AlertException("deleteDatabase: hmac verification failed");
		Arrays.fill(pass, (char) 0);

		File ext = new File(db.getAbsolutePath() + DB_EXT);

		if(!db.delete())
			throw new AlertException("deleteDatabase: unable to delete source file");
		if(!ext.delete())
			throw new AlertException("deleteDatabase: unable to delete key file");

		return true;
	}

	/**
	 * SSE2 appendHMAC method
	 * Calculates HMAC for database file and appends the result to structure file
	 * @param pass Character array that contains password used for password based encryption
	 * @throws AlertException Thrown for Cryptography library, IOExceptions
	 */
	private static void appendHMAC(final char[] pass) throws AlertException
	{
		File db = new File(DB_FILE);
                System.out.println(DB_FILE);
		final byte[] salt = Crypto.generateBytes(SALT_SIZE);

		byte[] secret = Crypto.keygen(pass, salt, HMAC_KEY_SIZE);
		Arrays.fill(pass, (char) 0);

		SecretKeySpec key = new SecretKeySpec(secret, HMAC_MODE);
		Arrays.fill(secret, (byte) 0x00);

		byte[] value = new byte[HMAC_SIZE];
		try
		{
			InputStream is = new FileInputStream(db);
			Mac mac = Mac.getInstance(HMAC_MODE);
			mac.init(key);

			byte[] buffer = new byte[NUM_ROUNDS];
			int numRead = 0;
			while((numRead = is.read(buffer)) > 0)
				mac.update(buffer);
			value = mac.doFinal();
			is.close();
		}
		catch(Exception e)
		{throw new AlertException("appendHMAC: unable to calculate hmac");}

		RandomAccessFile file;
		FileChannel channel;
		ByteBuffer buf;
		try
		{
			file = new RandomAccessFile(new File(db.getAbsolutePath() + DB_EXT), "rw");
			channel = file.getChannel();
			channel.position(channel.size());
			buf = ByteBuffer.wrap(salt);
			channel.write(buf);
			buf = ByteBuffer.wrap(value);
			channel.write(buf);
			channel.close();
			file.close();
		}
		catch(Exception e)
		{throw new AlertException("appendHMAC: unable to append structure");}
	}

	/**
	 * SSE2 verifyHMAC method
	 * Parses structure document for HMAC and contents.
	 * Calculates HMAC for database file and returns the comparsion of the results.
	 * @param pass Character array that contains password used for password based encryption
	 * @return Boolean result that indicates if the HMAC values match
	 * @throws AlertException Thrown of IOException, Cryptography library exceptions
	 */
	public static final boolean verifyHMAC(final char[] pass) throws AlertException
	{
		File db = new File(DB_FILE);
                System.out.println(DB_FILE);
                System.out.println(Arrays.toString(pass));
		final byte[] salt = new byte[SALT_SIZE];
		final byte[] hmac = new byte[HMAC_SIZE];
		long len = 0;
		RandomAccessFile file;
		FileChannel channel;
		ByteBuffer buf;
		try
		{
			file = new RandomAccessFile(new File(db.getAbsolutePath() + DB_EXT), "r");
			channel = file.getChannel();
			len = channel.size();
                        System.out.println((len - (SALT_SIZE + HMAC_SIZE)));
			channel.position((long)(len - (SALT_SIZE + HMAC_SIZE)));
			buf = ByteBuffer.wrap(salt);
			channel.read(buf);
			buf = ByteBuffer.wrap(hmac);
			channel.read(buf);
			channel.close();
			file.close();
		}
		catch(Exception e)
		{e.printStackTrace();throw new AlertException("verifyHMAC: unable to parse structure");}

		final byte[] secret = Crypto.keygen(pass, salt, HMAC_KEY_SIZE);
		Arrays.fill(pass, (char) 0);

		SecretKeySpec key = new SecretKeySpec(secret, HMAC_MODE);
		Arrays.fill(secret, (byte) 0x00);

		byte[] value = new byte[HMAC_SIZE];
		try
		{
			InputStream is = new FileInputStream(db);
			Mac mac = Mac.getInstance(HMAC_MODE);
			mac.init(key);

			byte[] buffer = new byte[NUM_ROUNDS];
			int numRead = 0;
			while((numRead = is.read(buffer)) > 0)
				mac.update(buffer);
			value = mac.doFinal();
			is.close();
		}
		catch(Exception e)
		{throw new AlertException("verifyHMAC: unable to calcuate hmac");}

		return Arrays.equals(value, hmac);
	}

	/**
	 * SSE2 buildIndex method - used to rebuild document and lookup tables
	 * Delete database and structure files
	 * Create empty SQL database file
	 * Create database structure file and append HMAC
	 * Traverse top level directory for all keyword files
	 * Obtain maximum document id
	 * For each document id (1 to max) - obtain absolute path, decrypt keyword list, parse keyword list, and insert words into word table
	 * Parse structure document for contents, and generate secret key
	 * Obtain distinct set of keywords for all documents
	 * For each distinct word - obtain document ids that contain that keyword, encrypt keyword, and insert value into lookup table
	 * Pad lookup table with random entries
	 * Drop word table
	 * Append new HMAC to structure file
	 * @param dir File object that points to the top level directory of all keyword files
	 * @param pass Character array that contains the password used for password based encryption
	 * @throws AlertException Thrown for SQLException, IOException, Cryptography library exceptions
	 */
	public static void buildIndex(File dir, final char[] pass) throws AlertException
	{
		if(!deleteDatabase(pass.clone(),dir.getAbsolutePath()))
			throw new AlertException("buildIndex: unable to delete database");

		if(!SQL.createDatabase())
			throw new AlertException("buildIndex: unable to create database");

		if(!createDatabase(pass.clone()))
			throw new AlertException("buildIndex: unable to create structure");

		Traverse folder = new Traverse(dir);
		folder.start();

		int max = 0;
		try
		{max = SQL.getMaxDocumentID();}
		catch(AlertException e)
		{throw new AlertException("buildIndex: unable to get max document id");}

		String path;
		File src;
		for(int i = 1; i <= max; i++)
		{
			try
			{path = SQL.getDocumentPath(i);}
			catch(AlertException e)
			{throw new AlertException("buildIndex: unable to get document path");}

			src = new File(path);
                        StringBuilder sb = null;
                        try{
                             sb = Crypto.keyAESdec(src, pass.clone());
                        }catch(Exception e){
                            System.err.println("Could not decrypt file: " + src);
                            continue;

                        }
			Vector<String> keys = parseKeys(sb);
			sb.delete(0, sb.length());

			for(String key : keys)
			{
				if(!SQL.setWord(key, i))
					throw new AlertException("buildIndex: unable to insert keyword pair");
				key = null;
			}
			keys.clear();
		}

		final byte[] salt = new byte[SALT_SIZE];
		final byte[] iv = new byte[IV_SIZE];
		RandomAccessFile file;
		FileChannel channel;
		ByteBuffer buf;
		try
		{
			file = new RandomAccessFile(new File(DB_FILE + DB_EXT), "r");
			channel = file.getChannel();
			buf = ByteBuffer.wrap(salt);
			channel.read(buf);
			buf = ByteBuffer.wrap(iv);
			channel.read(buf);
			channel.close();
			file.close();
		}
		catch(Exception e)
		{throw new AlertException("buildIndex: unable to parse structure");}

		final byte[] secret = Crypto.keygen(pass.clone(), salt, AES_KEY_SIZE);

		Vector<String> words = new Vector<String>();
		try
		{words = SQL.getDistinctWords();}
		catch(AlertException e)
		{throw new AlertException("buildIndex: unable to select distinct words");}

		Vector<Integer> ids;
		for(String w : words)
		{
			ids = new Vector<Integer>();
			try
			{ids = SQL.getDocumentsWord(w);}
			catch(AlertException e)
			{throw new AlertException("buildIndex: unable to select document ids");}

			int id = 0;
			SecretKeySpec key;
			IvParameterSpec ivSpec;
	        Cipher cipher;
	        byte[] cleartext;
	        byte[] ciphertext;
	        int length;
	        String clear;
			for(int i = 0; i < ids.size(); i++)
			{
				id = ids.get(i);
				try
	        	{
	        		key = new SecretKeySpec(secret, "AES");
	            	ivSpec = new IvParameterSpec(iv);
	    			cipher = Cipher.getInstance(AES_CIPHER_MODE);

	            	cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
	            	clear = w + (i+1);
	            	cleartext = clear.getBytes();
	            	clear = null;
	                ciphertext = new byte[cipher.getOutputSize(cleartext.length)];
	                length = cipher.update(cleartext, 0, cleartext.length, ciphertext, 0);
	                length += cipher.doFinal(ciphertext, length);
	                clear = Crypto.toHexString(ciphertext);
	        	}
	        	catch(Exception e)

	        	{throw new AlertException("buildIndex: unable to generate lookup value");}

				if(!SQL.setLookupValue(clear, id))
					throw new AlertException("buildIndex: unable to insert lookup value");
				clear = null;
				ids.set(i, 0);
			}
			ids.clear();
		}
		Arrays.fill(secret, (byte) 0x00);

		Vector<String> counts = new Vector<String>();
		try
		{counts = SQL.getLookupCount();}
		catch(AlertException e)
		{throw new AlertException("buildIndex: unable to count values");}

		max = 0;
		int v = 0;
		for(String s : counts)
		{
			String[] tokens = s.split(",");
			v = Integer.parseInt(tokens[1]);
			if(v > max)
				max = v;
		}

		int len = 0;
		try
		{len = SQL.getMaxLookupValue();}
		catch(AlertException e)
		{throw new AlertException("buildIndex: unable to select lookup length");}

		byte[] rand;
		String value;
		int id, c, r;
		for(String s : counts)
		{
			String[] tokens = s.split(",");
			id = Integer.parseInt(tokens[0]);
			c = Integer.parseInt(tokens[1]);

			for(int i = c; i < max; i++)
			{
				do
				{
					rand = Crypto.generateBytes(len);
					value = Crypto.toHexString(rand);

					try
					{r = SQL.checkLookupValue(value);}
					catch(AlertException e)
					{throw new AlertException("buildIndex: unable to check lookup value");}
				}
				while(r != 0);

				if(!SQL.setLookupValue(value, id))
					throw new AlertException("buildIndex: unable to insert lookup value");
				value = null;
				id = 0;
			}
		}

		if(!SQL.dropWord())
			throw new AlertException("buildIndex: unable to drop words");

		appendHMAC(pass.clone());
		Arrays.fill(pass, (char) 0);
	}

	/**
	 * SSE2 trapdoor method - used to generate possbile trapdoor values for specific word
	 * @param w String representation of word
	 * @param pass Character array that contains the password used for password based encryption
	 * @return Vector of Strings with HEX representations of encrypted word
	 * @throws AlertException Thrown for HMAC verification failure, IOExceptions, SQLException, Cryptography library exceptions
	 */
	public static Vector<String> trapdoor(String w, final char[] pass) throws AlertException
	{
		if(!verifyHMAC(pass.clone()))
			throw new AlertException("trapdoor: hmac verification failed");

		final byte[] salt = new byte[SALT_SIZE];
		final byte[] iv = new byte[IV_SIZE];
		RandomAccessFile file;
		FileChannel channel;
		ByteBuffer buf;
		try
		{
			file = new RandomAccessFile(new File(DB_FILE + DB_EXT), "r");
			channel = file.getChannel();
			buf = ByteBuffer.wrap(salt);
			channel.read(buf);
			buf = ByteBuffer.wrap(iv);
			channel.read(buf);
			channel.close();
			file.close();
		}
		catch(Exception e)
		{throw new AlertException("trapdoor: unable to parse structure");}

		final byte[] secret = Crypto.keygen(pass.clone(), salt, AES_KEY_SIZE);
		Arrays.fill(pass, (char) 0);

		int max = 0;
		try
		{max = SQL.getMaxDocumentID();}
		catch(Exception e)
		{throw new AlertException("trapdoor: unable to calculate max id");}

		Vector<String> traps = new Vector<String>();
		SecretKeySpec key;
		IvParameterSpec ivSpec;
        Cipher cipher;
        byte[] cleartext;
        byte[] ciphertext;
        int length;
        String clear;
        for(int i = 1; i <= max; i++)
        {
        	try
        	{
        		key = new SecretKeySpec(secret, "AES");
            	ivSpec = new IvParameterSpec(iv);
    			cipher = Cipher.getInstance(AES_CIPHER_MODE);

            	cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            	clear = w + i;
            	cleartext = clear.getBytes();
            	clear = null;
                ciphertext = new byte[cipher.getOutputSize(cleartext.length)];
                length = cipher.update(cleartext, 0, cleartext.length, ciphertext, 0);
                length += cipher.doFinal(ciphertext, length);
                clear = Crypto.toHexString(ciphertext);
                traps.add(clear);
                clear = null;
        	}
        	catch(Exception e)

        	{throw new AlertException("trapdoor: unable to generate traps");}
        }

		Arrays.fill(secret, (byte) 0x00);
		return traps;
	}

	/**
	 * SSE2 search method - performs query of provided trapdoors
	 * @param traps Vector of Strings with representation of keywords in HEX form
	 * @param pass Character array that contains password used for password based encryption
	 * @return Vector of Strings which represent the absolute document paths which contain keywords
	 * @throws AlertException Thrown for HMAC verification failure, SQLException
	 */
	public static Vector<String> search(final Vector<String> traps, final char[] pass) throws AlertException
	{
		if(!verifyHMAC(pass.clone()))
			throw new AlertException("search: hmac verification failed");
		Arrays.fill(pass, (char) 0);

		Vector<String> docs = new Vector<String>();
		try
		{docs = SQL.selectLookup(traps);}
		catch(AlertException e)
		{throw new AlertException("search: unable to lookup values");}

		return docs;
	}

	/**
	 * SSE2 parseKeys method - parses comma separated list into array
	 * @param str StringBuilder which contains comma separated keyword list
	 * @return Vector of Strings which contains all the keywords
	 * @throws AlertException Thrown for regular expression exception
	 */
	public static Vector<String> parseKeys(StringBuilder str) throws AlertException
	{
		if(!Pattern.matches(regex, str))
			throw new AlertException("keyAESenc: regex failed");

		Vector<String> words = new Vector<String>();
		int len = str.length();
		int pos = 0;
		int com = 0;
		String word;
		while(len > 0)
		{
			com = str.indexOf(",", pos);
			if(com != -1)
			{
				word = str.substring(pos, com);
				pos = com + 1;
				len = len - ((com - pos) + 1);
			}
			else
			{
				word = str.substring(pos, str.length());
				pos = str.length();
				len = 0;
			}

			words.add(word);
			word = null;
		}

		return words;
	}

	/**
	 * SSE2 main method - test cases
	 * @param args no arguments should be provided or required
	 * @throws Exception inappropriate use of catch-all case
	 */
	public static void main(String[] args) throws Exception
	{
		String pw = "This is an extremely long generic key 0123456789 !@#$%^&*(){}|:\"<>?,./;'[]\'";
		char [] pass = pw.toCharArray();

		createDatabase(pass.clone());

		boolean verify = verifyHMAC(pass.clone());
		System.out.println("verify: " + verify);

//		verify = deleteDatabase(pass.clone());
//		System.out.println("delete: " + verify);

		StringBuilder str = new StringBuilder();
		str.append("Hello");

		Vector<String> words = parseKeys(str);
		for(String w : words)
			System.out.println("word: " + w + " " + w.length());

		str.append(",World");
		words = parseKeys(str);
		for(String w : words)
			System.out.println("word: " + w + " " + w.length());

		str.append(",Testing");
		words = parseKeys(str);
		for(String w : words)
			System.out.println("word: " + w + " " + w.length());
		File test = new File("TEST");
		buildIndex(test, pass.clone());

		words = trapdoor("hello", pass.clone());
		for(String w : words)
			System.out.println("eword: " + w + " " + w.length());

		words = search(words,pass.clone());
		for(String doc : words)
			System.out.println("found in: " + doc);

		words = trapdoor("test", pass.clone());
		for(String w : words)
			System.out.println("eword: " + w + " " + w.length());

		words = search(words,pass.clone());
		for(String doc : words)
			System.out.println("found in: " + doc);

		words = trapdoor("cake", pass.clone());
		for(String w : words)
			System.out.println("eword: " + w + " " + w.length());

		words = search(words,pass.clone());
		for(String doc : words)
			System.out.println("found in: " + doc);
	}
}
