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

        jSeparator1 = new javax.swing.JSeparator();
        jButton1 = new javax.swing.JButton();
        btn_RemoveFile = new javax.swing.JButton();
        btn_EditFile = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtree = new javax.swing.JTree();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Add File");

        btn_RemoveFile.setText("Remove File");
        btn_RemoveFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_RemoveFileActionPerformed(evt);
            }
        });

        btn_EditFile.setText("Edit File");

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jtree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jtreeValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jtree);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 895, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_EditFile, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                            .addComponent(btn_RemoveFile, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(520, 520, 520))))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(590, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 446, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_EditFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_RemoveFile))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE))
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
    private javax.swing.JButton btn_EditFile;
    private javax.swing.JButton btn_RemoveFile;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    public static javax.swing.JTree jtree;
    // End of variables declaration//GEN-END:variables

}
