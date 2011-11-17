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

/**
 *
 * @author Boardwalk
 */
public class MainWindow extends javax.swing.JFrame {
    DefaultTableModel dtm = new DefaultTableModel();
    String selectedFile = "";



    
    /** Creates new form MainWindow */
    public MainWindow() {
        initComponents();

        AuthWindow aw = new AuthWindow();
        aw.setVisible(true);
       

    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        btn_RemoveFile = new javax.swing.JButton();
        btn_EditFileKeywords = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtree = new javax.swing.JTree();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Add File");

        btn_RemoveFile.setText("Remove File");
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

        jLabel1.setText("File Password:");

        jLabel2.setText("SUPER AWESOME DROPBOX PROJECT");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(165, 165, 165)
                .addComponent(jLabel2)
                .addContainerGap(168, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                    .addComponent(btn_EditFileKeywords, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE))
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(71, 71, 71))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_RemoveFile, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                .addGap(595, 595, 595))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 446, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(242, 242, 242)
                        .addComponent(jLabel2)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_EditFileKeywords)
                    .addComponent(jLabel1)
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_RemoveFile)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jtreeValueChanged
        // TODO add your handling code here:
        //System.out.println(jtree.getSelectionPath().toString());


    }//GEN-LAST:event_jtreeValueChanged

    private void btn_RemoveFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_RemoveFileActionPerformed
        // TODO add your handling code here:
        System.out.println("DELETE ME: " + jtree.getSelectionPath().toString());

        String deleteMe = jtree.getSelectionPath().toString();
        deleteMe = deleteMe.substring(deleteMe.indexOf("\\")+1, deleteMe.length()-1);
        System.out.println(deleteMe);


        String userPath = jtree.getSelectionPath().toString();
        userPath = userPath.substring(1, userPath.indexOf(","));
        System.out.println("UPATH : " + userPath);
        File delMe = new File(userPath + "\\" + deleteMe);
        if (delMe.exists())
        {
            delMe.delete();
        }

        try{
        jtree.setVisible(false);
        jtree.setSelectionPath(null);
        DAPI.delete(deleteMe);
        AuthWindow.obtainFiles(new File(userPath));
        FileTreeModel model = new FileTreeModel(new File(userPath));
        jtree.setModel(model);
        jtree.setVisible(true);
        }catch(Exception e){e.printStackTrace();}
    }//GEN-LAST:event_btn_RemoveFileActionPerformed

    private void btn_EditFileKeywordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EditFileKeywordsActionPerformed
        // TODO add your handling code here:
        if (jtree.getSelectionPath().toString().equals(null))
        {
            System.out.println("Select a file first");
        }
        else
        {
            String selectedFile = jtree.getSelectionPath().toString();
            int lastComma = 0;
            while(selectedFile.indexOf(",") != -1)
            {
                lastComma = selectedFile.indexOf(",") + 1;
                selectedFile = selectedFile.substring(lastComma);
            }
            selectedFile = selectedFile.substring(selectedFile.indexOf("\\"), selectedFile.length()-1);
            String selectedFileLocation = AuthWindow.currentUserPath + selectedFile;
            //System.out.println("File Location: " + selectedFileLocation);

            File editFile = new File(selectedFileLocation);

            if (passwordField.getPassword().equals(null))
            {
                System.out.println("No password given");
                //show jmessagebox?
            }
            else
            {
                //send File and Given Password to crypto
                try
                {
                    //crypto.Crypto.keyAESdec(editFile, passwordField.getPassword());
                    new EditWindow().setVisible(true);
                }catch (Exception ae){System.out.println("Exception occured (possible bad pw");}
            }
        }

    }//GEN-LAST:event_btn_EditFileKeywordsActionPerformed

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
    private javax.swing.JButton btn_EditFileKeywords;
    private javax.swing.JButton btn_RemoveFile;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTree jtree;
    private javax.swing.JPasswordField passwordField;
    // End of variables declaration//GEN-END:variables

}
