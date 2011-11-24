package crypto;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Class: Crypto.java - Cryptography for files and keywords
 * Author: Donald Talkington - dst071000@utdallas.edu
 * Project: Dropbox API with Symmetric Searchable Encryption
 * Date: November 11th, 2011
 * Version: 1.0
 * @author dtalk
 */
public class Crypto
{
	private final static int BIT_SIZE = 8;
	private final static int SALT_SIZE = 8;
	private final static int IV_SIZE = 16;
	private final static int NUM_ROUNDS = 1024;
	private final static String SHA256_MODE = "SHA-256";
	private final static int HMAC_SIZE = 32;
	private final static int HMAC_KEY_SIZE = 256;
	private final static String HMAC_MODE = "HmacSHA256";
	private final static int AES_KEY_SIZE = 128;
	private final static String AES_CIPHER_MODE = "AES/CTR/NoPadding";
	public final static String EXT = ".SSE2";
	private final static String TEMP = ".TEMP";
	private final static String regex = "^([A-Za-z]+)?(,[A-Za-z]+)*$";
	
	/**
	 * Crypto fileAESenc method - encrypts file
	 * If destination file exists HMAC verification occurs.
	 * If successful the file is truncated, and AES password based encryption is then applied to the source file.
	 * After encryption the HMAC is calculated and appended to the file by calling the appendHMAC method.
	 * @param src File object to be encrypted
	 * @param dest File object to write encrypted contents to
	 * @param pass Character array that contains password used for password based encryption
	 * @param delete Boolean flag for indicating of the src File should be deleted on completion
	 * @throws AlertException Thrown for HMAC verification failure, IOExceptions, Cryptography library exceptions
	 */
	public static void fileAESenc(File src, File dest, final char[] pass, final boolean delete) throws AlertException
	{
		RandomAccessFile file;
		FileChannel channel;
		ByteBuffer buf;
		if(dest.exists())
		{
			if(!verifyHMAC(dest, pass.clone()))
				throw new AlertException("fileAESenc: hmac verification failed");
			
			try
			{
				file = new RandomAccessFile(dest, "rw");
				channel = file.getChannel();
				channel.truncate(0);
				channel.close();
				file.close();
			}
			catch(Exception e)
			{throw new AlertException("fileAESenc: unable to clear file");}
		}
		
		final byte[] fKey = generateBytes(SALT_SIZE);
		final byte[] fIV = generateBytes(IV_SIZE);
		final byte[] secret = keygen(pass.clone(), fKey, AES_KEY_SIZE);
		SecretKeySpec keySpec = new SecretKeySpec(secret, "AES");
		Arrays.fill(secret, (byte) 0x00);
		
		try
		{
			final Cipher cipher = Cipher.getInstance(AES_CIPHER_MODE);
			IvParameterSpec ivSpec = new IvParameterSpec(fIV);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
			
			FileInputStream is = new FileInputStream(src);
			CipherOutputStream os = new CipherOutputStream(new FileOutputStream(dest,true), cipher);
			
			byte[] buffer = new byte[NUM_ROUNDS];
			int numRead = 0;
			while((numRead = is.read(buffer)) >= 0)
			{os.write(buffer,0,numRead);}
			is.close();
			os.close();
		}
                catch(Exception e)
		{throw new AlertException("fileAESenc: unable to encrypt source to destination");}
		
		try
		{
			file = new RandomAccessFile(dest, "rw");
			channel = file.getChannel();
			channel.position(channel.size());
			buf = ByteBuffer.wrap(fKey);
			channel.write(buf);
			buf = ByteBuffer.wrap(fIV);
			channel.write(buf);
			channel.close();
			file.close();
			
			appendHMAC(dest, pass);
		}
		catch(Exception e)
		{throw new AlertException("fileAESenc: unable to append structure");}
		
		Arrays.fill(pass, (char) 0);
		
		if(delete == true)
		{
			if(!src.delete())
				throw new AlertException("fileAESenc: unable to delete source file");
		}
	}
	
	/**
	 * Crypto fileAESdec method - decrypts file
	 * Firstly HMAC verification occurs.
	 * If successful the source file is parsed for necessary information.
	 * Then if the destination file exists it is deleted.
	 * Finally AES password based decryption is applied to the source document.
	 * @param src File object to be decrypted
	 * @param dest File object to write decrypted contents to
	 * @param pass Character array that contains password used for password based encryption
	 * @throws AlertException Thrown for HMAC verification failure, IOExceptions, Cryptography library exceptions
	 */
	public static void fileAESdec(File src, File dest, final char[] pass) throws AlertException
	{
		if(!verifyHMAC(src, pass.clone()))
			throw new AlertException("fileAESdec: hmac verification failed");
		
		final byte[] fKey = new byte[SALT_SIZE];
		final byte[] fIV = new byte[IV_SIZE];
		long len = 0;
		
		RandomAccessFile file;
		FileChannel channel;
		ByteBuffer buf;
		try
		{
			file = new RandomAccessFile(src, "r");
			channel = file.getChannel();
			len = channel.size() - (SALT_SIZE + IV_SIZE + SALT_SIZE + HMAC_SIZE);
			System.out.println(len);
                        channel.position((long)len);
			buf = ByteBuffer.wrap(fKey);
                        
			channel.read(buf);
			buf = ByteBuffer.wrap(fIV);
			channel.read(buf);
			channel.close();
			file.close();
		}
		catch(Exception e)
		{e.printStackTrace();throw new AlertException("fileAESdec: unable to parse structure");}
		
		if(dest.exists())
		{
			if(!dest.delete())
				throw new AlertException("fileAESdec: unable to delete destination");
		}
		
		final byte[] secret = keygen(pass.clone(), fKey, AES_KEY_SIZE);
		Arrays.fill(pass, (char) 0);
		
		SecretKeySpec keySpec = new SecretKeySpec(secret, "AES");
		Arrays.fill(secret, (byte) 0x00);
		
		try
		{
			final Cipher cipher = Cipher.getInstance(AES_CIPHER_MODE);
			IvParameterSpec ivSpec = new IvParameterSpec(fIV);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
			
			CipherInputStream is = new CipherInputStream(new FileInputStream(src), cipher);
			FileOutputStream os = new FileOutputStream(dest);
			
			byte[] buffer;
			int size;
			while(len > 0)
			{
				size = (int)((len >= NUM_ROUNDS) ? NUM_ROUNDS : len);
				buffer = new byte[size];
				is.read(buffer);
				os.write(buffer);
				len -= size;
			}
			is.close();
			os.close();
		}
		catch(Exception e)
		{throw new AlertException("fileAESdec: unable to decrypt source to destination");}
	}
	
	/**
	 * Crypto keygen method - generates secret key based on password
	 * @param pass Character array that contains password used for password based encryption
	 * @param salt Byte array that contains salt used for password based encryption
	 * @param size Integer that indications the size of the key that should be generated (max 256 bit)
	 * @return Byte array that contains secret key based on password
	 * @throws AlertException Thrown for Cryptography library exceptions
	 */
	public static final byte[] keygen(final char[] pass, final byte[] salt, final int size) throws AlertException
	{
		final int len = Math.abs(size / BIT_SIZE);
		final byte[] result = new byte[len];
		
		final MessageDigest md;
		try
		{
			md = MessageDigest.getInstance(SHA256_MODE);
			md.reset();
			md.update(salt);
			
			final byte[] bytes = convertChars(pass);
			Arrays.fill(pass, (char) 0);
			byte[] secret = md.digest(bytes);
			Arrays.fill(bytes, (byte) 0x00);
			
			for(int i = 0; i < NUM_ROUNDS; i++)
			{secret = md.digest(secret);}
			
			System.arraycopy(secret, 0, result, 0, result.length);
			Arrays.fill(secret, (byte) 0x00);
		}
		catch(Exception e)
		{throw new AlertException("keygen: unable to generate secret key");}
		
		return result;
	}
	
	/**
	 * Crypto appendHMAC method - calculates and appends HMAC to encrypted file
	 * Generates secret key using provided password.
	 * Then the HMAC is calculated using the encrypted file contents.
	 * Finally the HMAC is appended to the encrypted file.
	 * @param src File object that contains encrypted contents
	 * @param pass Character array that contains password used for password based encryption
	 * @throws AlertException Thrown for IOExceptions, Cryptography library exceptions
	 */
	private static void appendHMAC(File src, final char[] pass) throws AlertException
	{
		final byte[] salt = generateBytes(SALT_SIZE);
		
		byte[] secret = keygen(pass, salt, HMAC_KEY_SIZE);
		Arrays.fill(pass, (char) 0);
		
		SecretKeySpec key = new SecretKeySpec(secret, HMAC_MODE);
		Arrays.fill(secret, (byte) 0x00);
		
		byte[] value = new byte[HMAC_SIZE];
		try
		{
			InputStream is = new FileInputStream(src);
			Mac mac = Mac.getInstance(HMAC_MODE);
			mac.init(key);
			
			byte[] buffer;
			int size;
			int len = (int)(src.length() - (SALT_SIZE + IV_SIZE));
			while(len > 0)
			{
				size = (len >= NUM_ROUNDS) ? NUM_ROUNDS : len;
				buffer = new byte[size];
				is.read(buffer);
				mac.update(buffer);
				len -= size;
			}
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
			file = new RandomAccessFile(src, "rw");
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
	 * Crypto verifyHMAC method - verifies the HMAC for file
	 * Parses the source file for encrypted contents and HMAC.
	 * Then calculates HMAC using encrypted file contents.
	 * Returns boolean result that indicates if the HMAC values match.
	 * @param src File object that contains encrypted contents and HMAC
	 * @param pass Character array that contains password used for password based encryption
	 * @return Boolean value that indicates if the calculated HMAC and file HMAC match
	 * @throws AlertException Thrown for IOExceptions, Cryptography library exceptions
	 */
	public static final boolean verifyHMAC(File src, final char[] pass) throws AlertException
	{
		final byte[] salt = new byte[SALT_SIZE];
		final byte[] hmac = new byte[HMAC_SIZE];
		int len = 0;
		RandomAccessFile file;
		FileChannel channel;
		ByteBuffer buf;
		try
		{
			file = new RandomAccessFile(src, "rw");
			channel = file.getChannel();
                        System.out.println(channel);
			len = (int) channel.size();
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
		
		final byte[] secret = keygen(pass, salt, HMAC_KEY_SIZE);
		Arrays.fill(pass, (char) 0);
		
		SecretKeySpec key = new SecretKeySpec(secret, HMAC_MODE);
		Arrays.fill(secret, (byte) 0x00);
		
		byte[] value = new byte[HMAC_SIZE];
		try
		{
			InputStream is = new FileInputStream(src);
			Mac mac = Mac.getInstance(HMAC_MODE);
			mac.init(key);
			
			byte[] buffer;
			int size;
			len = len - (SALT_SIZE + IV_SIZE + SALT_SIZE + HMAC_SIZE);
			while(len > 0)
			{
				size = (len >= NUM_ROUNDS) ? NUM_ROUNDS : len;
				buffer = new byte[size];
				is.read(buffer);
				mac.update(buffer);
				len -= size;
			}
			value = mac.doFinal();
			is.close();
		}
		catch(Exception e)
		{throw new AlertException("verifyHMAC: unable to calcuate hmac");}
		
		return Arrays.equals(value, hmac);
	}
	
	/**
	 * Crypto keyAESenc - encrypts keyword file
	 * First a regular expression is used to test the StringBuilder structure for a comma separated list.
	 * If successful the contents are written temporary file.
	 * Afterwards the temporary file is encrypted using the fileAESenc method.
	 * Upon completion the temporary file will be deleted.
	 * @param src File object that points to the associated encrypted file
	 * @param pass Character array that contains password used for password based encryption
	 * @param str StringBuilder object that contains comma separated list of keywords
	 * @throws AlertException Thrown for regular expression failure, IOExceptions
	 */
	public static void keyAESenc(File src, final char[] pass, final StringBuilder str) throws AlertException
	{
		File dest = new File(src.getAbsolutePath() + EXT);
		File temp = new File(src.getAbsolutePath() + TEMP);
		
		if(!Pattern.matches(regex, str))
			throw new AlertException("keyAESenc: regex failed");
		
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
			for(int i = 0; i < str.length(); i++)
				writer.write(str.charAt(i));
			writer.flush();
			writer.close();
                        
		}
		catch(Exception e)
		{throw new AlertException("keyAESenc: unable to create temp file");}
		
		fileAESenc(temp,dest,pass.clone(),true);
		
		Arrays.fill(pass, (char) 0);
		str.delete(0, str.length());
	}
	
	/**
	 * Crypto keyAESdec - decrypts keyword file
	 * First fileAESdec is called to decrypt the contents to a temporary file.
	 * Then the temporary file is parsed for contents and appended to the StringBuilder.
	 * Afterwards the temporary file is deleted.
	 * Finally a regular expression is used to test the StringBuilder structure for a comma separated list.
	 * @param src File object that points to the associated encrypted file
	 * @param pass Character array that contains password used for password based encryption
	 * @return StringBuilder object that contains comma separated list of keywords
	 * @throws AlertException Thrown for IOException, regular expression failure
	 */
	public static final StringBuilder keyAESdec(File src, final char[] pass) throws AlertException
	{
		File source = new File(src.getAbsolutePath() + EXT);
		File temp = new File(src.getAbsolutePath() + TEMP);
		
		fileAESdec(source,temp,pass.clone());
		Arrays.fill(pass, (char) 0);
		
		StringBuilder str = new StringBuilder();
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(temp));
			int ch;
			while((ch = reader.read()) != -1)
				str.append((char)ch);
			reader.close();
			
			if(!temp.delete())
				throw new AlertException("keyAESdec: unable to delete temp file");
		}
		catch(Exception e)
		{throw new AlertException("keyAESdec: unable to create list");}
		System.out.println("keyword list: " + str);
		if(!Pattern.matches(regex, str))
			throw new AlertException("keyAESenc: regex failed");
		
		return str;
	}
	
	/**
	 * Crypto delete method - deletes the encrypted data and keyword files
	 * First HMAC verification occurs.
	 * If successful an attempt is made to delete the encrypted data and keyword files.
	 * Finally a boolean result is returned that indicates if both files were deleted properly.
	 * @param src File object that points to the associated encrypted file
	 * @param pass Character array that contains the password used for password based encryption
	 * @return Boolean result that indicates if both the data and keyword files were deleted
	 * @throws AlertException Thrown for HMAC verification failure, IOExceptions
	 */
	public static final boolean delete(File src, final char[] pass) throws AlertException
	{
		if(!verifyHMAC(src,pass.clone()))
			throw new AlertException("delete: hmac verification failed");
		Arrays.fill(pass, (char) 0);
		
		File dest = new File(src.getAbsolutePath() + EXT);
		
		if(!src.delete())
			throw new AlertException("delete: unable to delete source file");
		if(!dest.delete())
			throw new AlertException("delete: unable to delete key file");
		
		return true;
	}
	
	/**
	 * Crypto move method - moves the encrypted data and keyword files
	 * First HMAC verification occurs.
	 * If successful an attempt is made to rename the encrypted data and keyword files.
	 * Finally a boolean result is returned that indicates if both files were moved properly.
	 * @param src File object that points to the associated encrypted file
	 * @param dest File object that points to the new destination path
	 * @param pass Character array that contains password used for password based encryption
	 * @return Boolean result that indicates if both the data and keyword files were moved
	 * @throws AlertException Thrown for HMAC verification failure, IOExceptions
	 */
	public static final boolean move(File src, File dest, final char[] pass) throws AlertException
	{
		if(!verifyHMAC(src,pass.clone()))
			throw new AlertException("move: hmac verification failed");
		Arrays.fill(pass, (char) 0);
		
		File in = new File(src.getAbsolutePath() + EXT);
		File out = new File(dest.getAbsolutePath() + EXT);
		
		if(!src.renameTo(dest))
			throw new AlertException("move: unable to move source file");
		if(!in.renameTo(out))
			throw new AlertException("move: unable to move key file");
		
		return true;
	}
	
	/**
	 * Crypto main method - used for example test cases
	 * @param args no arguments should be supplied or required
	 * @throws Exception - inappropriate catch all case
	 */
	public static void main(String[] args) throws Exception
	{
		File cleartext = new File("cleartext.txt");
		File ciphertext = new File("ciphertext.txt");
		
		String pw = "This is an extremely long generic key 0123456789 !@#$%^&*(){}|:\"<>?,./;'[]\'";
		char[] pass = pw.toCharArray();
		
		char[] password = pass.clone();
		fileAESenc(cleartext, ciphertext, password, true);
		
		password = pass.clone();
		boolean verify = verifyHMAC(ciphertext, password);
		System.out.println("verifyHMAC: " + verify);
		
		password = pass.clone();
		fileAESdec(ciphertext, cleartext, password);
		
		StringBuilder str = new StringBuilder("apple");
		str.append("");
		keyAESenc(ciphertext,pass.clone(),str);
		
		str.delete(0, str.length());
		str = keyAESdec(ciphertext,pass.clone());
		
		System.out.println(str.toString());
		
		File move = new File("move.txt");
		verify = move(ciphertext,move,pass.clone());
		System.out.println("MOVE: " + verify);
		
//		verify = delete(move, pass.clone());
//		System.out.println("DELETE: " + verify);
		
		byte[] bytes = new byte[pass.length];
		for(int i = 0; i < pass.length; i++)
			bytes[i] = (byte) pass[i];
		
		System.out.println(toHexString(bytes));
		System.out.println(toHexString(new String(pass).getBytes()));
		System.out.println(new String(bytes));
		System.out.println(new String(pass));
		System.out.println(pass.length + " " + pw.getBytes().length + " " + pw.length());
	}
	
	// BEGIN HELPER METHODS - BELOW THIS LINE
	/**
	 * Crypto convertChars method
	 * Converts ASCII character array to byte array
	 * @param chars Character array to be converted
	 * @return Byte array that contains converted contents
	 */
	public static byte[] convertChars(final char[] chars)
	{
		byte[] bytes = new byte[chars.length];
		for(int i = 0; i < chars.length; i++)
			bytes[i] = (byte) chars[i];
		return bytes;
	}
	/**
	 * Crypto convertBytes method
	 * Converts Byte array to ASCII character array
	 * @param bytes Byte array to be converted
	 * @return Character array that contains converted contents
	 */
	public static char[] convertBytes(final byte[] bytes)
	{
		char[] chars = new char[bytes.length];
		for(int i = 0; i < bytes.length; i++)
			chars[i] = (char) bytes[i];
		return chars;
	}
	/**
	 * Crypto generateBytes method
	 * Uses SecureRandom to generate indicated number of bytes
	 * @param num Integer that indicates the number of bytes to generate
	 * @return Byte array that contains random bytes
	 */
	public static final byte[] generateBytes(int num)
	{
		num = (num >= 0) ? num : 0;
		final byte[] bytes = new byte[num];
		SecureRandom sr = new SecureRandom();
		sr.nextBytes(bytes);
		return bytes;
	}
	/**
	 * Crypto toHexString method
	 * Converts Byte array to String with HEX representation
	 * @param bytes - Byte array to be converted
	 * @return String with HEX representation
	 */
	public static final String toHexString(final byte[] bytes)
	{
		StringBuffer buf = new StringBuffer(bytes.length * 2);
		int v = 0;
		for(int i = 0; i < bytes.length; i++)
		{
			v = bytes[i] & 0xff;
			if (v < 16)
				buf.append('0');
			buf.append(Integer.toHexString(v));
		}
		return buf.toString().toUpperCase();
	}
	/**
	 * Crypto toByteArray method
	 * Converts String with HEX representation to byte array
	 * @param str String with HEX representation
	 * @return Byte array with converted contents
	 */
	public static final byte[] toByteArray(final String str)
	{
		int len = str.length();
		byte[] bytes = new byte[len / 2];
		for(int i = 0; i < len; i += 2)
		{
			bytes[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) 
								+ Character.digit(str.charAt(i+1), 16));
		}
		return bytes;
	}
	// END HELPER METHODS - ABOVE THIS LINE

        
        /**
         * Overwrites a file's contents with null bytes 10 times before deleting
         * @param openFrom
         * @throws IOException 
         */
    public static void secureDelete(File openFrom) throws IOException {
        for(int t = 0; t < 10; t++){
            FileWriter fw = new FileWriter(openFrom);
            for(long i = 0; i < openFrom.length();i++){
                fw.write(0);
            }
            fw.close();
        }
        openFrom.delete();
    }
}
