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

import crypto.Crypto;
import crypto.SSE2;
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
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.table.*;
import javax.swing.JTree;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainWindow.java
 *
 * Created on Nov 7, 2011, 10:12:05 PM
 */

public class MainWindow extends javax.swing.JFrame {
    public static SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss ZZZZZ");
    public static String rootPath = new File(".").getAbsolutePath(); //does this always point to Current User's Root Folder? -CQ

    DefaultTableModel dtm = new DefaultTableModel();
    String selectedFile = "";
    public static String userName; //userName is defined in AuthWindow.java
    public static HashMap<String,Date> meta;

    public static DefaultListModel searchingForModel;
    public static DefaultListModel resultsModel;


    /** Creates new form MainWindow */
    public MainWindow() {
        if(rootPath.endsWith("\\.")){
            rootPath = rootPath.substring(0,rootPath.length()-1);
        }
        initComponents();
        searchingForModel = new DefaultListModel();
        resultsModel = new DefaultListModel();
        list_SearchingFor.setModel(searchingForModel);
        list_Results.setModel(resultsModel);

        AuthWindow aw = new AuthWindow();
        aw.setVisible(true);


    }
    //Map dropbox string path + dateMod
    public static void updateMeta(String filePath, Date mod){
        if(mod == null){
            if(meta.containsKey(filePath)){
                meta.remove(filePath);
            }
        }else{
            meta.put(filePath, mod);
        }
        try{ //Update meta file
            System.out.println(filePath + " -- " + mod);
            System.out.println("Updating meta file " + userName + File.separator+".meta");
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(userName + File.separator+".meta")));
            oos.writeObject(meta);
            oos.close();
        }catch(Exception e){
            System.err.println("Error writing to meta file");
            e.printStackTrace();
        }
    }

    public static Entry pushFile(File f) throws Exception{
        String dbPath = f.getAbsolutePath().replaceFirst(rootPath.replace("\\","\\\\")+userName, "").replace("\\","/");

        //String dbPath = getDBPathFromTree();
        System.out.println("Pusing file " + f.getAbsolutePath() + " to dbpath: " + dbPath);
        if(f.isDirectory()){
            return DAPI.createFolder(dbPath);
        }else{
            FileInputStream fis = new FileInputStream(f);
            Entry e = DAPI.putFileOverwrite(dbPath,fis,f.length(),null);
            fis.close();
            updateMeta(dbPath,df.parse(e.modified));
            return e;
        }

    }

    public static FilenameFilter ffilter = new FilenameFilter() {
                public boolean accept(File file, String string) {
                    System.out.println(string);
                    return !string.endsWith(Crypto.EXT);
                }
            };
    public static void deleteFile(File f, boolean force) throws Exception{
        
        System.out.println("file path: " + f.getAbsolutePath());
        System.out.println("rootPath: " + rootPath.replace("\\","\\\\")+userName);
        if(f.isDirectory()){
            if(!force){
                Object[] options = { "OK", "CANCEL" };
                int t = JOptionPane.showOptionDialog(null, "Warning! " + f.getName() 
                                + " is a directory.  Delete directory and all contents?", "Warning",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                                null, options, options[1]);
                if( t!= 0)
                    return;
            }
            
            for(File ff : f.listFiles(ffilter)){
                System.out.println("folder contents: " + ff.getName());
                deleteFile(ff,true);    
            }
            System.out.println("Deleting file: " + f.getName());
            f.delete();
            if(f.exists()){
                refreshTree();
                JOptionPane.showMessageDialog(null,"Could not delete all of directory contents.  Are some files open by other programs?");
            }
            String fname = f.getAbsolutePath().replaceFirst(rootPath.replace("\\","\\\\")+userName, "").replace("\\", "/");
            try{
                DAPI.delete(fname);
            }catch(Exception e){System.out.println("Could not delete DB: " + fname);}
            refreshTree();
            
        }else{
            String dbPath = f.getAbsolutePath().replaceFirst(rootPath.replace("\\","\\\\")+userName, "").replace("\\", "/");
            Crypto.secureDelete(f);
            Crypto.secureDelete(new File(f.getAbsolutePath() + Crypto.EXT));
            System.out.println("Deleting " + dbPath);
            try{
                DAPI.delete(dbPath);   
            }catch(Exception e){System.out.println("Could not delete from DB: " + dbPath);}
            try{
                DAPI.delete(dbPath + Crypto.EXT);
            }catch(Exception e){System.out.println("Could not delete EXT from DB" + dbPath + Crypto.EXT);}
            updateMeta(dbPath,null);
            refreshTree();
        }

    }


    public static String getUserPathFromTree(){

        String treePath = jtree.getSelectionPath().getLastPathComponent().toString();
        return treePath;
    }
    public static String getDBPathFromTree(){
        String s = getUserPathFromTree();
        if(new File(s).isDirectory() ){
            s = s + "\\";
        }
        if(s.contains(File.separator)){
            s = s.substring(s.indexOf(File.separator)).replace(File.separator,"/");
        }

        return s;
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_AddFile = new javax.swing.JButton();
        btn_RemoveFile = new javax.swing.JButton();
        btn_EditFileKeywords = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtree = new javax.swing.JTree();
        jLabel1 = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        searchPasswordField = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();
        txtField_SearchForKey = new javax.swing.JTextField();
        btn_AddKeyToSearchFor = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        list_SearchingFor = new javax.swing.JList();
        jLabel4 = new javax.swing.JLabel();
        btn_Search = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        list_Results = new javax.swing.JList();
        jSeparator2 = new javax.swing.JSeparator();
        btn_RemoveSearchKey = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        btn_clearResults = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btn_AddFile.setText("Add File In Currently Selected Directory");
        btn_AddFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AddFileActionPerformed(evt);
            }
        });

        btn_RemoveFile.setText("Remove File / Directory");
        btn_RemoveFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_RemoveFileActionPerformed(evt);
            }
        });

        btn_EditFileKeywords.setText("Edit File Content / Keywords");
        btn_EditFileKeywords.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EditFileKeywordsActionPerformed(evt);
            }
        });

        jtree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jtreeValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jtree);

        jLabel1.setText("Add/Edit File PW:");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel2.setText("Global Search Password:");

        jLabel3.setText("Keyword To Search For:");

        btn_AddKeyToSearchFor.setText("Add Keyword To Search List");
        btn_AddKeyToSearchFor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AddKeyToSearchForActionPerformed(evt);
            }
        });

        list_SearchingFor.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(list_SearchingFor);

        jLabel4.setText("Searching For...");

        btn_Search.setText("Search");
        btn_Search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SearchActionPerformed(evt);
            }
        });

        jLabel5.setText("Results:");

        list_Results.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(list_Results);

        btn_RemoveSearchKey.setText("Remove Selected Key From List");
        btn_RemoveSearchKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_RemoveSearchKeyActionPerformed(evt);
            }
        });

        jButton1.setText("Add Subdirectory In Currently Selected Directory");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btn_clearResults.setText("Clear Results");
        btn_clearResults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_clearResultsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                    .addComponent(btn_AddFile, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                    .addComponent(btn_RemoveFile, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                    .addComponent(btn_EditFileKeywords, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 702, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btn_AddKeyToSearchFor, javax.swing.GroupLayout.DEFAULT_SIZE, 677, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel2))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(searchPasswordField, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
                                            .addComponent(txtField_SearchForKey, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)))
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 677, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel5)
                                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(btn_RemoveSearchKey, javax.swing.GroupLayout.PREFERRED_SIZE, 156, Short.MAX_VALUE)
                                            .addComponent(btn_clearResults, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(btn_Search, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(147, 147, 147)
                                .addComponent(jLabel4))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(searchPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtField_SearchForKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_AddKeyToSearchFor)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btn_Search)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_RemoveSearchKey)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_clearResults)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_AddFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_EditFileKeywords)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_RemoveFile)))
                .addGap(26, 26, 26))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_RemoveFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_RemoveFileActionPerformed
        if(jtree.getSelectionPath() == null){
            JOptionPane.showMessageDialog(this,"Select a file to be deleted.");
            return;                    
        }
        try{
            deleteFile(new File(getUserPathFromTree()),false);
        }catch(Exception e){
            e.printStackTrace();
            System.err.println("Error deleting file... Does it exist?");
        }
    }//GEN-LAST:event_btn_RemoveFileActionPerformed
    public static void refreshTree(){
        jtree.setVisible(false);
        //AuthWindow.obtainFiles(new File(userName));
        FileTreeModel model = new FileTreeModel(new File(userName));
        jtree.setModel(model);
        jtree.setVisible(true);
    }
    private void btn_EditFileKeywordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EditFileKeywordsActionPerformed
        // TODO add your handling code here:
        if (jtree.getSelectionPath() == null)
        {
            JOptionPane.showMessageDialog(this,"Select a file to edit!");
            return;
            //System.out.println("Select a file first");
        }
        else
        {

            String selectedFileLocation = getUserPathFromTree();
            File editFile = new File(selectedFileLocation);
            if(editFile.isDirectory()){
                JOptionPane.showMessageDialog(this,"Directory selected.  Please select a file.");
                return;
            }
            if (passwordField.getPassword().length == 0)
            {

                JOptionPane.showMessageDialog(null,"No password given");
                return;
            }
            else
            {
                //send File and Given Search Password to crypto
                try
                {
                    File SSEfile = new File(editFile.getAbsolutePath()+Crypto.EXT);

                    if(!SSEfile.exists()){

                        //String searchKey = JOptionPane.showInputDialog(this,"SSE File not found! Enter the SSE Search password to create it:","Search Password",0);
                        //TODO: Verify searchKey against Database?
                        JOptionPane.showMessageDialog(this,"SSE File not found! Generating search file from provided password\nTODO:Verifying Search Key (against DB?) here");
                        Crypto.keyAESenc(editFile, passwordField.getPassword(), new StringBuilder(""));
                        //Push encrypted file to dropbox
                        pushFile(SSEfile);
                    }

                    if(!Crypto.verifyHMAC(SSEfile, passwordField.getPassword())){
                        JOptionPane.showMessageDialog(this,"Invalid search password");
                        return;
                    }
                    //Create SSEFile if it dosent exist

                    //crypto.Crypto.keyAESdec(editFile, passwordField.getPassword());
                    EditWindow ew = new EditWindow(editFile,passwordField.getPassword());
                    ew.setVisible(true);
                    ew.setTitle(editFile.getAbsolutePath());

                }catch (Exception ae){ae.printStackTrace();}
            }
        }

    }//GEN-LAST:event_btn_EditFileKeywordsActionPerformed

    private void btn_AddKeyToSearchForActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AddKeyToSearchForActionPerformed
        if(!SSE2.isValidKeyword(txtField_SearchForKey.getText())){
            JOptionPane.showMessageDialog(this,"Invalid keyword:  Must follow the pattern: " + SSE2.keywordRegex);
            return;
        }
        String keyToAdd = txtField_SearchForKey.getText();
        txtField_SearchForKey.setText("");
        searchingForModel.addElement(keyToAdd);
    }//GEN-LAST:event_btn_AddKeyToSearchForActionPerformed

    private void btn_SearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SearchActionPerformed
        char[] searchKey = searchPasswordField.getPassword();
        SSE2.setDB_Path(rootPath + userName );
        
        if (searchKey.length == 0) //is null
        {
            JOptionPane.showMessageDialog(this, "No global search password entered");
        }
        
        //TODO: Verify hmac here!  (Maybe make a new file just to hold the hash of the password)
        else
        {
            resultsModel.clear();
            try
            {
                //String userPath = rootPath+ File.separator + userName;
                String userPath = rootPath + userName + File.separator;
                System.out.println("USER PATH: " + userPath);

                crypto.SSE2.createDatabase(searchKey.clone());
                File userRootPath = new File(userPath);
                crypto.SSE2.buildIndex(userRootPath, searchKey.clone());
                Vector<String> results = new Vector<String>();
                Vector<String> traps = new Vector<String>();

                for(int i = 0; i < searchingForModel.getSize(); i++)
                {
                    traps =
                            crypto.SSE2.trapdoor(searchingForModel.getElementAt(i).toString(), searchKey.clone());

                    results.addAll(crypto.SSE2.search(traps, searchKey.clone()));
                }
                //Return results here
                for (int i = 0; i < results.size(); i++)
                {
                    resultsModel.addElement("\"\" found in: " + results.get(i));
                }

                crypto.SSE2.deleteDatabase(searchKey,userPath);
            }
            catch(Exception e){e.printStackTrace();}
        }
    }//GEN-LAST:event_btn_SearchActionPerformed

    private void btn_AddFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AddFileActionPerformed
        if(passwordField.getPassword().length==0){
            JOptionPane.showMessageDialog(this,"Enter a password for the file");
            return;
        }
        if(jtree.isSelectionEmpty()){//If nothing is selected, select root
            jtree.setSelectionInterval(0,0);
        }
        if(!new File(getUserPathFromTree()).isDirectory()){//If file is selected, select file parent
            jtree.setSelectionPath(jtree.getSelectionPath().getParentPath());
        }

        String userLocation = getUserPathFromTree();
        String DBLocation = getDBPathFromTree();
        System.out.println("file's local location = " + userLocation);
        System.out.println("dropboxAddPath = "+ DBLocation);


        JFileChooser jfc = new JFileChooser("");
        jfc.showOpenDialog(this);
        File f = jfc.getSelectedFile();

        if(f == null){
            System.err.println("Did not choose a file");
            return;
        }
        System.out.println(f.getAbsolutePath());

        try{
            //Create encrypted version of file in user SecDB directory
            File destination = new File(userLocation + File.separator+f.getName());
            Crypto.fileAESenc(f,destination, passwordField.getPassword().clone(),false);
            
            String searchKey = showPasswordDialog("Enter the SSE Search password:");
            System.out.println(Arrays.toString(searchKey.toCharArray()));
            if(searchKey == null) return;
            
            Crypto.keyAESenc(destination, searchKey.toCharArray(), new StringBuilder(""));
            //Push encrypted file to dropbox
            pushFile(destination);
            File searchFile = new File(userLocation + File.separator + f.getName() + Crypto.EXT);
            System.out.println(searchFile.getAbsolutePath());
            pushFile(searchFile);
            refreshTree();

        }catch(Exception e){System.err.println("Error encrypting and pushing file"); e.printStackTrace();}
    }//GEN-LAST:event_btn_AddFileActionPerformed

    private void btn_RemoveSearchKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_RemoveSearchKeyActionPerformed
        int selectedIndex = list_SearchingFor.getSelectedIndex();
        if (selectedIndex != -1)
        {
            searchingForModel.remove(selectedIndex);
        }
        else
        {
            JOptionPane.showMessageDialog(this,"Please select keyword to delete");
            
        }
    }//GEN-LAST:event_btn_RemoveSearchKeyActionPerformed

    private void jtreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jtreeValueChanged
        // TODO add your handling code here:
        if(jtree != null && jtree.getSelectionPath()!= null){
            System.out.println("User Path: " + getUserPathFromTree());
            System.out.println("DB Path: " + getDBPathFromTree());
        }
    }//GEN-LAST:event_jtreeValueChanged
    /**
     * MKDIR Button
     */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if(jtree.isSelectionEmpty()){//If nothing is selected, select root
            jtree.setSelectionInterval(0,0);
        }
        if(!new File(getUserPathFromTree()).isDirectory()){//If file is selected, select file parent
            jtree.setSelectionPath(jtree.getSelectionPath().getParentPath());
        }
        String dirName = JOptionPane.showInputDialog(this,"Name of new directory?");
        if(dirName == null){
            return;
        }
        String userLocation = getUserPathFromTree();
        String DBLocation = getDBPathFromTree();
        try{
            File dir = new File(userLocation + File.separator + dirName);
            if(dir.exists()) throw new Exception();
            dir.mkdir();
            if(!dir.isDirectory()) throw new Exception();
            pushFile(dir);
            refreshTree();
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, "Error creating directory. Be sure that the directory name is not already being used and that it contains only valid characters.");
            return;
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void btn_clearResultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_clearResultsActionPerformed

        resultsModel.clear();
    }//GEN-LAST:event_btn_clearResultsActionPerformed

    /**
    * @param args the command line arguments
    */


   final static public String APP_KEY = "lzw0thue2gu9hco";
   final static public String APP_SECRET = "6p5trcwivd1zhsh";
   final static public AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
   static public DropboxAPI<WebAuthSession> DAPI;

   static public Desktop desktop = Desktop.getDesktop();
   static public AppKeyPair AKPair = new AppKeyPair(APP_KEY, APP_SECRET);
   //static public WebAuthSession WAS;

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
                jtree.setVisible(false);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_AddFile;
    private javax.swing.JButton btn_AddKeyToSearchFor;
    private javax.swing.JButton btn_EditFileKeywords;
    private javax.swing.JButton btn_RemoveFile;
    private javax.swing.JButton btn_RemoveSearchKey;
    private javax.swing.JButton btn_Search;
    private javax.swing.JButton btn_clearResults;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    public static javax.swing.JTree jtree;
    private javax.swing.JList list_Results;
    private javax.swing.JList list_SearchingFor;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JPasswordField searchPasswordField;
    private javax.swing.JTextField txtField_SearchForKey;
    // End of variables declaration//GEN-END:variables

    private String showPasswordDialog(String string) {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setEchoChar('*');
        Object[] obj = {string, passwordField};
        Object stringArray[] = {"OK","Cancel"};
        if (JOptionPane.showOptionDialog(null, obj, "Need password",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, stringArray, obj) == JOptionPane.YES_OPTION)
            return  new String(passwordField.getPassword());
        return null;
    }

}
