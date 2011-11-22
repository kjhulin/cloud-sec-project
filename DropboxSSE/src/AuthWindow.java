import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
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
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Desktop;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;


public class AuthWindow extends javax.swing.JFrame {

    public File tokensFile = new File("auth.dat");
    public boolean isAuthed = false;
    static public WebAuthSession authWAS;
    public RequestTokenPair rtp;
    public static AuthWindow aw;
    public static String currentUserPath;
    

    /** Creates new form AuthWindow */
    public AuthWindow() {
        initComponents();
        
        if(tokensFile.exists())
        {
            try
            {
                ObjectInputStream ois = new ObjectInputStream(new
                    BufferedInputStream(new FileInputStream(tokensFile)));
                TokenContainer tkc =  (TokenContainer)ois.readObject();
                System.out.println("Logged in as " + tkc.username); //DEBUG CODE
                AccessTokenPair atp = tkc.getAccessTokenPair();
                authWAS = new WebAuthSession(MainWindow.AKPair,MainWindow.ACCESS_TYPE,atp);
                MainWindow.DAPI = new DropboxAPI<WebAuthSession>(authWAS);
                ois.close();

                //set lbl_currentUser == username in file
                lbl_currentUser.setText(tkc.username);
                MainWindow.userName = tkc.username;
                isAuthed = true;
            }catch(Exception e){e.printStackTrace();}
        }
        else
        {
           try
           {
               if(!isAuthed)
               {//GOGO AUTHENTICATE
                   authWAS = new WebAuthSession(MainWindow.AKPair, MainWindow.ACCESS_TYPE);
                   MainWindow.DAPI = new DropboxAPI<WebAuthSession>(authWAS);
                   WebAuthInfo authInfo = MainWindow.DAPI.getSession().getAuthInfo();
                   rtp = authInfo.requestTokenPair;

                   //Launch Browser for user to click accept
                   try{
                       URI authURL = new URI(authInfo.url);
                       MainWindow.desktop.browse(authURL);
                   }catch(Exception e){
                       System.err.println("Error opening webpage");
                       e.printStackTrace();
                       System.err.println("Navigate to: " + authInfo.url);
                   }
               } //end of if statement
       }catch(Exception e){e.printStackTrace();}
    }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        lbl_currentUser = new javax.swing.JLabel();
        btn_logOut = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btn_confirm = new javax.swing.JButton();
        btn_Continue = new javax.swing.JButton();

        setAlwaysOnTop(true);

        jLabel1.setText("Currently Logged In As:");

        lbl_currentUser.setText("NO USER DETECTED");

        btn_logOut.setText("Log Out");
        btn_logOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_logOutActionPerformed(evt);
            }
        });

        btn_confirm.setText("Confirm Log In");
        btn_confirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_confirmActionPerformed(evt);
            }
        });

        btn_Continue.setText("Continue");
        btn_Continue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ContinueActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btn_logOut, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btn_confirm, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_currentUser))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btn_Continue, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lbl_currentUser))
                .addGap(18, 18, 18)
                .addComponent(btn_Continue)
                .addGap(34, 34, 34)
                .addComponent(btn_logOut)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(btn_confirm)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_confirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_confirmActionPerformed
        // TODO add your handling code here:

        try{

        System.out.println("Confirm button hit");
        System.out.println("Successfully authenticated for userid:" + authWAS.retrieveWebAccessToken(rtp));
        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(tokensFile)));
        AccessTokenPair atp = authWAS.getAccessTokenPair();
        TokenContainer tkc = new TokenContainer(atp);
        tkc.username = MainWindow.DAPI.accountInfo().displayName;
        oos.writeObject(tkc);
        oos.close();

        MainWindow.userName = tkc.username;
        File userFolder = new File(tkc.username.toString());
        if(userFolder.exists())
        {
            //okay that's cool -- Grab the meta data
            if(new File(MainWindow.userName.toString()+File.separator+".meta").exists()){
                ObjectInputStream ois = new ObjectInputStream(new
                        BufferedInputStream(new FileInputStream(tkc.username.toString()+File.separator+".meta")));
                MainWindow.meta =  (HashMap<String,Date>)ois.readObject();
                ois.close();
                System.out.println(MainWindow.meta);
            }else{
                MainWindow.meta = new HashMap<String,Date>();
            }
        }
        else
        {
            //create that folder
            userFolder.mkdir();
            MainWindow.meta = new HashMap<String,Date>();
        }
        currentUserPath = userFolder.getAbsolutePath();
        obtainFiles(userFolder);
        //point Jtree at folder
        MainWindow.jtree.setVisible(false);
        FileTreeModel model = new FileTreeModel(userFolder);
        MainWindow.jtree.setModel(model);
        MainWindow.jtree.setVisible(true);

        this.setVisible(false);
        }catch(Exception e){e.printStackTrace();}

    }//GEN-LAST:event_btn_confirmActionPerformed

    public static void obtainFiles(File userPath){
            try{

            System.out.println("Welcome " + MainWindow.DAPI.accountInfo().displayName);
            getFolderContents("", userPath);
            }catch(Exception e){e.printStackTrace();}
            
    }
    
    public static SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss ZZZZZ");
    public static void getFolderContents(String s, File uPath){
        System.out.println("UPATH: "+ uPath);
        System.out.println("s: "+s);
        try{

            //upath == user path

            for(Entry e : listDirectory(s)){

                if(e.isDir){
                    System.out.println("e.fileName() == " + e.fileName());
                    System.out.println("eParentPath: "+e.parentPath());
                    File path = new File(uPath.toString() + File.separator + e.fileName());
                    System.out.println("PATH: "+path.getAbsolutePath().toString());
                    if(!path.exists()){
                        path.mkdir();
                    }
                    getFolderContents(e.parentPath() + e.fileName(), path);
                }else{
                    System.out.println("e.fileName() == " + e.fileName());
                    System.out.println("PARENT PATH: " + e.parentPath());

                    File downloadPath = new File(uPath.toString() + File.separator + e.fileName());
                    Date dateModified = df.parse(e.modified);
                    System.out.println("date modified: " + dateModified.toString());
                    System.out.println("DOWNLOAD PATH: " + downloadPath.getAbsolutePath().toString());
                    String dbPath = e.parentPath()+e.fileName();
                    if(!MainWindow.meta.containsKey(dbPath) ||
                            !new File(downloadPath.toString()).exists() ||
                        dateModified.after(MainWindow.meta.get(dbPath))){
                        System.out.println("Downloading new version of " + dbPath);
                        DropboxFileInfo dis =
                            MainWindow.DAPI.getFile(dbPath, null, new FileOutputStream(downloadPath.toString()), null);
                        MainWindow.updateMeta(dbPath, dateModified);
                    }
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }

    public static  List<Entry> listDirectory(String path) throws Exception{
        System.out.println("list path:" + path);
       Entry entry = MainWindow.DAPI.metadata(path, 0, null, true, null);
       if (!entry.isDir) return null;
               return entry.contents;
       }


    private void btn_logOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_logOutActionPerformed
        // TODO add your handling code here:

        tokensFile.delete();
        lbl_currentUser.setText("NO USER DETECTED (log out occurred)");
    }//GEN-LAST:event_btn_logOutActionPerformed

    private void btn_ContinueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ContinueActionPerformed
        // TODO add your handling code here:
        
        //AccessTokenPair atp = authWAS.getAccessTokenPair();
        MainWindow.userName = lbl_currentUser.getText();
        System.out.println("lbl_current_user == " + lbl_currentUser.getText());
        File userFolder = new File(lbl_currentUser.getText());
        if(userFolder.exists())
        {
            //okay that's cool -- Grab the meta data
            if(new File(MainWindow.userName.toString()+File.separator+".meta").exists()){
                try{
                ObjectInputStream ois = new ObjectInputStream(new
                        BufferedInputStream(new FileInputStream(MainWindow.userName.toString()+File.separator+".meta")));
                MainWindow.meta =  (HashMap<String,Date>)ois.readObject();
                ois.close();
                }catch(Exception e){
                    System.err.println("Error reading .meta -- recreating");
                    MainWindow.meta = new HashMap<String,Date>();
                }
            }else{
                MainWindow.meta = new HashMap<String,Date>();
            }
        }
        else
        {
            //create that folder
            userFolder.mkdir();
            MainWindow.meta = new HashMap<String,Date>();
        }

        currentUserPath = userFolder.getAbsolutePath();
        obtainFiles(userFolder);
        MainWindow.jtree.setVisible(false);
        FileTreeModel model = new FileTreeModel(userFolder);
        MainWindow.jtree.setModel(model);
        MainWindow.jtree.setVisible(true);

        this.setVisible(false);
    }//GEN-LAST:event_btn_ContinueActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                aw.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Continue;
    private javax.swing.JButton btn_confirm;
    private javax.swing.JButton btn_logOut;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lbl_currentUser;
    // End of variables declaration//GEN-END:variables

}



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



//Credit for below filetreemodel class:
//http://docstore.mik.ua/orelly/java-ent/jfc/ch03_19.htm

/**
 * The methods in this class allow the JTree component to traverse
 * the file system tree and display the files and directories.
 **/
class FileTreeModel implements TreeModel {
    FilenameFilter ff = new FilenameFilter() {

        public boolean accept(File file, String string) {
            return !string.startsWith(".");
        }
    };
  // We specify the root directory when we create the model.
  protected File root;
  public FileTreeModel(File root) { this.root = root; }

  // The model knows how to return the root object of the tree
  public Object getRoot() { return root; }

  // Tell JTree whether an object in the tree is a leaf
  public boolean isLeaf(Object node) {  return ((File)node).isFile(); }

  // Tell JTree how many children a node has
  public int getChildCount(Object parent) {
      
    String[] children = ((File)parent).list(ff);
    //System.out.println("printing children array: " + Arrays.toString(children));
    if (children == null) return 0;
    return children.length;
  }

  // Fetch any numbered child of a node for the JTree.
  // Our model returns File objects for all nodes in the tree.  The
  // JTree displays these by calling the File.toString() method.
  public Object getChild(Object parent, int index) {
    String[] children = ((File)parent).list(ff);
    if ((children == null) || (index >= children.length)) return null;
    return new File((File) parent, children[index]);
  }

  // Figure out a child's position in its parent node.
  public int getIndexOfChild(Object parent, Object child) {
    String[] children = ((File)parent).list(ff);
    if (children == null) return -1;
    String childname = ((File)child).getName();
    for(int i = 0; i < children.length; i++) {
      if (childname.equals(children[i])) return i;
    }
    return -1;
  }

  // This method is invoked by the JTree only for editable trees.
  // This TreeModel does not allow editing, so we do not implement
  // this method.  The JTree editable property is false by default.
  public void valueForPathChanged(TreePath path, Object newvalue) {}

  // Since this is not an editable tree model, we never fire any events,
  // so we don't actually have to keep track of interested listeners
  public void addTreeModelListener(TreeModelListener l) {}
  public void removeTreeModelListener(TreeModelListener l) {}
}