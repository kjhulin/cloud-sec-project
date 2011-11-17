/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dropbox;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.RequestTokenPair;
import com.dropbox.client2.session.WebAuthSession;
import com.dropbox.client2.session.WebAuthSession.WebAuthInfo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Desktop;


/**
 *
 * @author Boardwalk
 */
public class Auth
{

    final static private String APP_KEY = "lzw0thue2gu9hco";
    final static private String APP_SECRET = "6p5trcwivd1zhsh";
    final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
    static private DropboxAPI<WebAuthSession> DAPI;

    static Desktop desktop = Desktop.getDesktop();
    static AppKeyPair AKPair = new AppKeyPair(APP_KEY, APP_SECRET);
    static WebAuthSession WAS;
    
    public static void main(String[] args)
    {
    	
    	boolean isAuthed = false;
    	
    	//CHECK IF WE ALREADY HAVE AN AUTH SESSION
        File tokensFile = new File("auth.dat");
        out:
        if(tokensFile.exists()){
        	try{
        		ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(tokensFile)));
        		TokenContainer tkc =  (TokenContainer)ois.readObject();
        		System.out.println("Logged in as " + tkc.username + ".  Change users? (Y/N)");
        		Scanner in = new Scanner(System.in);
        		while(true){
	        		String s = in.nextLine();
	        		if(s.startsWith("y")){
	        			tokensFile.delete();
	        			break out;
	        		}else if(s.startsWith("n")){
	        			break;
	        		}
        		}
        		AccessTokenPair atp = tkc.getAccessTokenPair();
        		WAS = new WebAuthSession(AKPair,ACCESS_TYPE,atp);
        		DAPI = new DropboxAPI<WebAuthSession>(WAS);
        		isAuthed = true;
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        }
        try{
	        if(!isAuthed){//GOGO AUTHENTICATE
	        	WebAuthSession WAS = new WebAuthSession(AKPair, ACCESS_TYPE);
	        	DAPI = new DropboxAPI<WebAuthSession>(WAS);
	            WebAuthInfo authInfo = DAPI.getSession().getAuthInfo();
	            RequestTokenPair rtp = authInfo.requestTokenPair;
	                      
	            //Launch Browser for user to click accept
	            try{
	            	URI authURL = new URI(authInfo.url);
	            	desktop.browse(authURL);
	            }catch(Exception e){
	            	System.err.println("Error opening webpage");
	            	e.printStackTrace();
	            	System.err.println("Navigate to: " + authInfo.url);
	            }
	            
	            //Wait for user!
	            System.out.println("Hit enter once you have logged into Drop Box and granted permission to this application");
	            Scanner in = new Scanner(System.in);
	            in.nextLine();
	            //Store key!
	            System.out.println("Successfully authenticated for user id:" + WAS.retrieveWebAccessToken(rtp));
	            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(tokensFile)));
	            AccessTokenPair atp = WAS.getAccessTokenPair();
	            TokenContainer tkc = new TokenContainer(atp);
	            tkc.username = DAPI.accountInfo().displayName;
	            oos.writeObject(tkc);
	            oos.close();
	        }
        }catch(Exception e){
        	e.printStackTrace();
        }
        	
        
        try{
        	
        	System.out.println("Welcome " + DAPI.accountInfo().displayName);
        	File f = createRandomFile();
        	
        	DAPI.putFile(f.getName(),new FileInputStream(f), f.length(),null, null);
        	for(Entry e : listDirectory("SecureSSE")){
        		System.out.println(e.fileName());
        		DropboxAPI.DropboxInputStream dis = DAPI.getFileStream(e.fileName(), null);
        		int tmp;
        		while((tmp = dis.read())!=-1){
        			System.out.print((char)tmp);
        		}
        		dis.close();
        		System.out.println();
        		
        	}
        }catch(Exception e){
        	e.printStackTrace();
        }

    } //end of main function
    public static File createRandomFile() throws Exception{    	
    	String fileName = randomString(8);
    	String fileContents = randomString(100);
    	File ret = new File(fileName);
    	FileOutputStream fos = new FileOutputStream(ret);
    	fos.write(fileContents.getBytes());
    	fos.close();
    	return ret;
    }
    static String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static String randomString(int len){
    	Random rand = new Random();
    	String ret = "";
    	for(int i = 0; i < len; i++){
    		ret += chars.charAt(rand.nextInt(chars.length()));
    	}
    	return ret;
    	}
    	
    
    public static  List<Entry> listDirectory(String path) throws Exception{
    	Entry entry = DAPI.metadata("", 0, null, true, null);
    	if (!entry.isDir) return null;
    		return entry.contents;
    	} 
}//end of class

class TokenContainer implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String secret;
	String key;
	String username;
	public TokenContainer(AccessTokenPair atp){
		secret = atp.secret;
		key = atp.key;
		
	}
	public AccessTokenPair getAccessTokenPair(){
		return new AccessTokenPair(key,secret);
	}
}
