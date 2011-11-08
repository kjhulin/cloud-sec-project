/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dropboxtest;

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
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author Boardwalk
 */
public class Main
{

    final static private String APP_KEY = "9z070yjmiaq3whj";
    final static private String APP_SECRET = "kd8li5smj9669a9";
    final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
    static private DropboxAPI<WebAuthSession> DAPI;

    

    
    public static void main(String[] args)
    {
        AppKeyPair AKPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AccessTokenPair ATPair = new AccessTokenPair("KEY_HERE","SECRET_HERE");
        WebAuthSession WAS = new WebAuthSession(AKPair, ACCESS_TYPE);
        DAPI = new DropboxAPI<WebAuthSession>(WAS);
        
        try
        {
            /*
             * Use the below in a first time session in order to get the tokens
             * Save these tokens for use after allowing the application access
             * In a separate browser window. (Figure out an easy way to do this?)
             * 
             */
            WebAuthInfo authInfo = DAPI.getSession().getAuthInfo();
            AccessTokenPair tokens = DAPI.getSession().getAccessTokenPair();

            System.out.println("URL: " + authInfo.url);
            System.out.println("Auth Key: " + tokens.key + " || Auth Secret: " + tokens.secret);
           
/*
            //set access pair tokens here (already done manually)
            AccessTokenPair tokens = new AccessTokenPair("keyhere","secrethere");
            //DAPI.getSession().setAccessTokenPair(tokens);
            RequestTokenPair requestTokenPair = new RequestTokenPair(tokens.key, tokens.secret);
            String UserUID = DAPI.getSession().retrieveWebAccessToken(requestTokenPair);

            System.out.println("User UID: " + UserUID);*/
        }
        catch (DropboxException ex)
        {
            System.out.println("Exception thrown in trying to get authinfo");
        }
       

    } //end of main function
}//end of class
