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
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

//TODO - Javadoc comments

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
	
	private final static String EXT = ".SSE2";
	private final static String TEMP = ".TEMP";
	private final static String regex = "^([A-Za-z]+)?(,[A-Za-z]+)*$";
	
	// Encrypt AES FILE - COMPLETE
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
			catch(IOException ioe)
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
		catch(IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException innii)
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
		catch(IOException ioe)
		{throw new AlertException("fileAESenc: unable to append structure");}
		
		Arrays.fill(pass, (char) 0);
		
		if(delete == true)
		{
			if(!src.delete())
				throw new AlertException("fileAESenc: unable to delete source file");
		}
	}
	
	// DECRYPT AES FILE - COMPLETE
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
			channel.position((long)len);
			buf = ByteBuffer.wrap(fKey);
			channel.read(buf);
			buf = ByteBuffer.wrap(fIV);
			channel.read(buf);
			channel.close();
			file.close();
		}
		catch(IOException ioe)
		{throw new AlertException("fileAESdec: unable to parse structure");}
		
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
		catch(IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException innii)
		{throw new AlertException("fileAESdec: unable to decrypt source to destination");}
	}
	
	// GENERATE PASSWORD BASED SECRET KEY - COMPLETE
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
		catch(NoSuchAlgorithmException e)
		{throw new AlertException("keygen: unable to generate secret key");}
		
		return result;
	}
	
	// APPEND HMAC TO FILE - COMPLETE
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
		catch(IOException | NoSuchAlgorithmException | InvalidKeyException ini)
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
		catch(IOException ioe)
		{throw new AlertException("appendHMAC: unable to append structure");}
	}
	
	// VERIFY HMAC FOR FILE - COMPLETE
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
		catch(IOException | NoSuchAlgorithmException | InvalidKeyException ini)
		{throw new AlertException("verifyHMAC: unable to calcuate hmac");}
		
		return Arrays.equals(value, hmac);
	}
	
	// ENCRYPT AES Keywords - COMPLETE
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
		catch(IOException ioe)
		{throw new AlertException("keyAESenc: unable to create temp file");}
		
		fileAESenc(temp,dest,pass.clone(),true);
		
		Arrays.fill(pass, (char) 0);
		str.delete(0, str.length());
	}
	
	// DECRYPT AES Keywords - COMPLETE
	public static final StringBuilder keyAESdec(File src, final char[] pass) throws AlertException
	{
		File dest = new File(src.getAbsolutePath() + EXT);
		File temp = new File(src.getAbsolutePath() + TEMP);
		
		fileAESdec(dest,temp,pass.clone());
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
		catch(IOException ioe)
		{throw new AlertException("keyAESdec: unable to create list");}
		
		if(!Pattern.matches(regex, str))
			throw new AlertException("keyAESenc: regex failed");
		
		return str;
	}
	
	// DELETE DATA + KEYWORD FILES - COMPLETE
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
	
	// MOVE DATA + KEYWORD FILES - COMPLETE
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
	
	// Test Main
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
	// Convert Char to Byte Array (ASCII Password)
	public static byte[] convertChars(final char[] chars)
	{
		byte[] bytes = new byte[chars.length];
		for(int i = 0; i < chars.length; i++)
			bytes[i] = (byte) chars[i];
		return bytes;
	}
	// Convert Byte to Char Array (ASCII Password)
	public static char[] convertBytes(final byte[] bytes)
	{
		char[] chars = new char[bytes.length];
		for(int i = 0; i < bytes.length; i++)
			chars[i] = (char) bytes[i];
		return chars;
	}
	// Generate Number of Bytes using SecureRandom
	public static final byte[] generateBytes(int num)
	{
		num = (num >= 0) ? num : 0;
		final byte[] bytes = new byte[num];
		SecureRandom sr = new SecureRandom();
		sr.nextBytes(bytes);
		return bytes;
	}
	// Convert Byte Array to HEX String
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
	// Convert HEX String to Byte Array
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
}
