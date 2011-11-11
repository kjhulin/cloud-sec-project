import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Vector;
import java.util.regex.Pattern;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

//TODO - Javadoc comments

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

	private final static String regex = "^([A-Za-z]+)?(,[A-Za-z]+)*$";
	private final static String DB_FILE = "SSE2.DB";
	private final static String DB_EXT = ".EXT";
	
	// CREATE DATABASE + STRUCTURE FILES - COMPLETE
	public static final boolean createDatabase(final char[] pass) throws AlertException
	{
		File db = new File(DB_FILE);
		File ext = new File(db.getAbsolutePath() + DB_EXT);
		
		if(ext.exists())
		{
			if(!verifyHMAC(pass.clone()))
				throw new AlertException("createDatabase: hmac verification failed");
		}
		
		if(!db.exists())
		{throw new AlertException("createDatabase: unable to locate database file");}
		
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
		catch(IOException ioe)
		{throw new AlertException("createDatabase: unable to append structure");}
		
		appendHMAC(pass.clone());
		Arrays.fill(pass, (char) 0);
		
		return true;
	}
	
	// DELETE DATABASE + STRUCTURE FILES - COMPLETE
	public static final boolean deleteDatabase(final char[] pass) throws AlertException
	{
		File db = new File(DB_FILE);
		
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
	
	// APPEND HMAC TO FILE - COMPLETE
	private static void appendHMAC(final char[] pass) throws AlertException
	{
		File db = new File(DB_FILE);
		
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
		catch(IOException | NoSuchAlgorithmException | InvalidKeyException ini)
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
		catch(IOException ioe)
		{throw new AlertException("appendHMAC: unable to append structure");}
	}
	
	// VERIFY HMAC for DATABASE FILE - COMPLETE
	public static final boolean verifyHMAC(final char[] pass) throws AlertException
	{
		File db = new File(DB_FILE);
		
		final byte[] salt = new byte[SALT_SIZE];
		final byte[] hmac = new byte[HMAC_SIZE];
		int len = 0;
		RandomAccessFile file;
		FileChannel channel;
		ByteBuffer buf;
		try
		{
			file = new RandomAccessFile(new File(db.getAbsolutePath() + DB_EXT), "r");
			channel = file.getChannel();
			len = (int) channel.size();
			channel.position((long)(len - (SALT_SIZE + HMAC_SIZE)));
			buf = ByteBuffer.wrap(salt);
			channel.read(buf);
			buf = ByteBuffer.wrap(hmac);
			channel.read(buf);
			channel.close();
			file.close();
		}
		catch(IOException ioe)
		{throw new AlertException("verifyHMAC: unable to parse structure");}
		
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
		catch(IOException | NoSuchAlgorithmException | InvalidKeyException ini)
		{throw new AlertException("verifyHMAC: unable to calcuate hmac");}
		
		return Arrays.equals(value, hmac);
	}
	
	// SSE2 Build Index Function - COMPLETE
	public static void buildIndex(File dir, final char[] pass) throws AlertException
	{	
		if(!deleteDatabase(pass.clone()))
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
			StringBuilder sb = Crypto.keyAESdec(src, pass.clone());
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
		catch(IOException ioe)
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
	        	catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | 
	        			IllegalBlockSizeException | ShortBufferException | BadPaddingException e)
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
	
	// SSE2 Trapdoor Function - COMPLETE
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
		catch(IOException ioe)
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
        	catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | 
        			IllegalBlockSizeException | ShortBufferException | BadPaddingException e)
        	{throw new AlertException("trapdoor: unable to generate traps");}
        }
		
		Arrays.fill(secret, (byte) 0x00);
		return traps;
	}
	
	// SSE2 Search - COMPLETE
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
	
	// PARSE KEYWORDS - COMPLETE
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
	
	// TEST MAIN
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
		
		buildIndex(new File("TEST"), pass.clone());
		
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
