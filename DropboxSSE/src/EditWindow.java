
import crypto.Crypto;
import crypto.SSE2;
import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EditWindow.java
 *
 * Created on Nov 16, 2011, 8:29:07 PM
 */

/**
 *
 * @author Boardwalk, Maligare
 */
public class EditWindow extends javax.swing.JFrame {

    public static EditWindow ew;
    private File currentFile; //file currently being edited
    private char[] password; //file's search password
    public static DefaultListModel keywordsModel;
    public boolean gaveCorrectPassword = false;

    /** Creates new form EditWindow */
    public EditWindow() {
        //EDITWINDOW SHOULD ONLY BE CALLED WITH A FILE PASSED TO IT
    }

    public EditWindow(File passedFile) {
        initComponents();
        currentFile = passedFile;
        //sseFile = new File(currentFile.getAbsoluteFile()+Crypto.EXT);

        keywordsModel = new DefaultListModel();
        list_Keywords.setModel(keywordsModel);

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
        jScrollPane1 = new javax.swing.JScrollPane();
        list_Keywords = new javax.swing.JList();
        btn_DeleteKeyword = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtField_newKeyword = new javax.swing.JTextField();
        btn_AddKeyword = new javax.swing.JButton();
        btn_Decrypt = new javax.swing.JButton();
        btn_ReplaceFile = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btn_Browse = new javax.swing.JButton();
        txtField_Browse = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        filePasswordField = new javax.swing.JPasswordField();
        btn_RevealKeywords = new javax.swing.JButton();
        btn_SaveKeywords = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        btn_Browse1 = new javax.swing.JButton();
        txtField_DecryptBrowse = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();

        jLabel1.setText("File Keywords:");

        list_Keywords.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(list_Keywords);

        btn_DeleteKeyword.setText("Delete Selected Keyword");
        btn_DeleteKeyword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_DeleteKeywordActionPerformed(evt);
            }
        });

        jLabel3.setText("New Keyword:");

        btn_AddKeyword.setText("Add New Keyword");
        btn_AddKeyword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AddKeywordActionPerformed(evt);
            }
        });

        btn_Decrypt.setText("Decrypt File To Location");
        btn_Decrypt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_DecryptActionPerformed(evt);
            }
        });

        btn_ReplaceFile.setText("Replace With File At Location");
        btn_ReplaceFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ReplaceFileActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        btn_Browse.setText("Browse");
        btn_Browse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BrowseActionPerformed(evt);
            }
        });

        jLabel2.setText("File Password:");

        filePasswordField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filePasswordFieldActionPerformed(evt);
            }
        });

        btn_RevealKeywords.setText("Show File's Current Keywords");
        btn_RevealKeywords.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_RevealKeywordsActionPerformed(evt);
            }
        });

        btn_SaveKeywords.setText("Save Keywords");
        btn_SaveKeywords.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SaveKeywordsActionPerformed(evt);
            }
        });

        btn_Browse1.setText("Browse");
        btn_Browse1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_Browse1ActionPerformed(evt);
            }
        });

        jCheckBox1.setSelected(true);
        jCheckBox1.setText("Open file for editing after save");

        jCheckBox2.setText("Securely delete plaintext file after uploading");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(102, 102, 102))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btn_RevealKeywords, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                                    .addComponent(btn_SaveKeywords, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                                    .addComponent(btn_DeleteKeyword, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                                    .addComponent(btn_AddKeyword, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                                    .addComponent(txtField_newKeyword, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE))
                                .addGap(10, 10, 10))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(104, 104, 104)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_Browse)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtField_Browse, javax.swing.GroupLayout.PREFERRED_SIZE, 381, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_Browse1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtField_DecryptBrowse, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE))
                    .addComponent(btn_Decrypt, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                    .addComponent(jCheckBox1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn_ReplaceFile, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                    .addComponent(jCheckBox2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filePasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(filePasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(btn_RevealKeywords)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btn_Browse1)
                                    .addComponent(txtField_DecryptBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_Decrypt)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btn_Browse)
                                    .addComponent(txtField_Browse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(1, 1, 1)
                                .addComponent(jCheckBox2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_ReplaceFile))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtField_newKeyword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_AddKeyword)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_DeleteKeyword)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_SaveKeywords))))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE))
                .addGap(25, 25, 25))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_DecryptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_DecryptActionPerformed
        try
        {
            if(filePasswordField.getPassword().length==0){
                JOptionPane.showMessageDialog(this, "No password entered!");
                return;
            }
            if(!Crypto.verifyHMAC(currentFile, filePasswordField.getPassword())){
                JOptionPane.showMessageDialog(this,"Cannot verify this file's HMAC." +
                        " Either the file has been altered or the given password is incorrect.");
                return;
            }
            if(txtField_DecryptBrowse.getText().length()==0){
                JOptionPane.showMessageDialog(this,"Select a location to save the decrypted file!");
                btn_Browse1ActionPerformed(evt);
                return;
            }
            File saveAs = new File(txtField_DecryptBrowse.getText().toString());
            if(saveAs == null){
                return;
            }
            //System.out.println("Save location: " + saveAs.getAbsolutePath());
            if(saveAs.exists()){
                Object[] options = { "OK", "CANCEL" };
                int t = JOptionPane.showOptionDialog(null, "Warning! " + saveAs.getName()
                            + " already exists!  This action will overwrite it.  Continue?", "Warning",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                            null, options, options[1]);
                if(t != 0)
                    return;
            }
            crypto.Crypto.fileAESdec(currentFile, saveAs, filePasswordField.getPassword());
            txtField_Browse.setText(saveAs.getAbsolutePath());
            if(jCheckBox1.isSelected()){
                try{
                    MainWindow.desktop.open(saveAs);
                }catch(Exception e){
                    JOptionPane.showMessageDialog(this, "Unable to open specified file.");
                }
            }
        }catch(Exception e){e.printStackTrace();}

    }//GEN-LAST:event_btn_DecryptActionPerformed

    private void btn_BrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BrowseActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.showOpenDialog(this);
        try
        {
            txtField_Browse.setText(fc.getSelectedFile().getAbsolutePath().toString());
        } catch (Exception e){}

    }//GEN-LAST:event_btn_BrowseActionPerformed


    /**
     * Reverts to keyword list stored in SSE file
     * @param evt
     */
    private void btn_RevealKeywordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_RevealKeywordsActionPerformed

        if (gaveCorrectPassword == false)
        {
            JPasswordField passwordField = new JPasswordField();
            passwordField.setEchoChar('*');
            Object[] obj = {"Enter file's search password", passwordField};
            Object stringArray[] = {"OK","Cancel"};
            if (JOptionPane.showOptionDialog(null, obj, "Need password",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, stringArray, obj) == JOptionPane.YES_OPTION)
            {
                password = passwordField.getPassword();
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Please enter a password");
                return;
            }
        }

        try
        {
            File SSEfile = new File(currentFile.getAbsolutePath()+Crypto.EXT);

            if(!SSEfile.exists()){

                //String searchKey = JOptionPane.showInputDialog(this,"SSE File not found! Enter the SSE Search password to create it:","Search Password",0);
                //TODO: Verify searchKey against Database?
                JOptionPane.showMessageDialog(this,"SSE File not found! Generating search file from provided password\nTODO:Verifying Search Key (against DB?) here");
                Crypto.keyAESenc(currentFile, password, new StringBuilder(""));

                //Push encrypted file to dropbox
                MainWindow.pushFile(SSEfile);
                gaveCorrectPassword = true;
                JOptionPane.showMessageDialog(this, "SSE File not found.  Creating a new one.");

            }
        }catch(Exception e){System.err.println("Error creating SSE file"); return;}

        try{
            StringBuilder sb = crypto.Crypto.keyAESdec(currentFile, password.clone());
            Vector<String> fileKeys = crypto.SSE2.parseKeys(sb);
            gaveCorrectPassword = true; //gets here if keyAESdec doesn't throw exception
            keywordsModel.clear();
            for(int i = 0; i < fileKeys.size(); i++)
            {
                keywordsModel.addElement(fileKeys.elementAt(i));
            }
        }
        catch(Exception e){e.printStackTrace();JOptionPane.showMessageDialog(null, "Cannot verify this file's keywords." +
                "Either the keyword file has been altered or the given password is incorrect."); return;}
    }//GEN-LAST:event_btn_RevealKeywordsActionPerformed


    /**
     * Saves keywords list to SSE file
     * @param evt
     */
    private void btn_SaveKeywordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SaveKeywordsActionPerformed
        if (gaveCorrectPassword == false)
        {
            JOptionPane.showMessageDialog(null, "Please reveal file's keywords first");
            return;
        }


        try
        {
// Uncomment to no longer allow empty keyword lists
//            if(keywordsModel.isEmpty()){
//                JOptionPane.showMessageDialog(this, "No keywords entered!");
//                return;
//            }

            StringBuilder sb = new StringBuilder("");
            String s;
            Enumeration<String> words = (Enumeration<String>) keywordsModel.elements();
            while(words.hasMoreElements()){
                sb.append(words.nextElement());
                if(words.hasMoreElements())sb.append(",");
            }
            //System.out.println(currentFile.getAbsolutePath());
            //System.out.println(sb.toString());
            //System.out.println(Arrays.toString(password));
            Crypto.keyAESenc(currentFile, password.clone(), sb);

            MainWindow.pushFile(new File(currentFile+Crypto.EXT));
            JOptionPane.showMessageDialog(null, "Keywords saved!");
        }
        catch(Exception e){e.printStackTrace();}
    }//GEN-LAST:event_btn_SaveKeywordsActionPerformed

    private void filePasswordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filePasswordFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filePasswordFieldActionPerformed

    private void btn_ReplaceFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ReplaceFileActionPerformed
        try
        {

            if(filePasswordField.getPassword().length==0){
                JOptionPane.showMessageDialog(this, "Enter the file password!");
                return;
            }
            if(!Crypto.verifyHMAC(currentFile, filePasswordField.getPassword().clone())){
               JOptionPane.showMessageDialog(this, "Cannot verify this file's HMAC." +
                        " Either the file has been altered or the given password is incorrect.");
               return;
            }

           if(txtField_Browse.getText().length()==0){
                JOptionPane.showMessageDialog(this,"Select a location from which load the plaintext file!");
                btn_BrowseActionPerformed(evt);
                return;
            }
            File openFrom = new File(txtField_Browse.getText().toString());
            if(openFrom == null){
                return;
            }
            //delete original file from dropbox
            //MainWindow.deleteFile(currentFile,false);

            //push new file  to dropbox

            crypto.Crypto.fileAESenc(openFrom, currentFile, filePasswordField.getPassword(), true); //true means we always delete original

            //push new file with new keywords to dropbox
            MainWindow.pushFile(currentFile);
            JOptionPane.showMessageDialog(this, "File saved!");
            if(jCheckBox2.isSelected()){
                Crypto.secureDelete(openFrom);
            }
           // MainWindow.pushFile(currentFile);
        }catch (Exception e){e.printStackTrace();}
    }//GEN-LAST:event_btn_ReplaceFileActionPerformed

    //Browse for location to save decrypted file to
    private void btn_Browse1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Browse1ActionPerformed
        // TODO add your handling code here:
        JFileChooser fc = new JFileChooser();
        fc.showSaveDialog(this);
        try
        {
            txtField_DecryptBrowse.setText(fc.getSelectedFile().getAbsolutePath().toString());
            //File saveFile = new File(fc.getSelectedFile().getAbsolutePath().toString());
        }
        catch(Exception e){}
    }//GEN-LAST:event_btn_Browse1ActionPerformed

    private void btn_AddKeywordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AddKeywordActionPerformed
        String newKeyword = txtField_newKeyword.getText();
        if(newKeyword.length() == 0)
        {
            //System.out.println("no keyword entered");
            JOptionPane.showMessageDialog(this,"Enter a keyword to add");
        }
        else
        {
            if(!SSE2.isValidKeyword(newKeyword)){
                JOptionPane.showMessageDialog(this,"Invalid keyword:  Must follow the pattern: " + SSE2.keywordRegex);
                return;
            }
            txtField_newKeyword.setText("");
            if(!keywordsModel.contains(newKeyword))
                keywordsModel.addElement(newKeyword);
        }
    }//GEN-LAST:event_btn_AddKeywordActionPerformed

    private void btn_DeleteKeywordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_DeleteKeywordActionPerformed
        int selectedIndex = list_Keywords.getSelectedIndex();
        if (selectedIndex != -1)
        {
            keywordsModel.remove(selectedIndex);
        }
        else
        {
            JOptionPane.showMessageDialog(null,"Please select keyword to delete");
        }

    }//GEN-LAST:event_btn_DeleteKeywordActionPerformed

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EditWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_AddKeyword;
    private javax.swing.JButton btn_Browse;
    private javax.swing.JButton btn_Browse1;
    private javax.swing.JButton btn_Decrypt;
    private javax.swing.JButton btn_DeleteKeyword;
    private javax.swing.JButton btn_ReplaceFile;
    private javax.swing.JButton btn_RevealKeywords;
    private javax.swing.JButton btn_SaveKeywords;
    private javax.swing.JPasswordField filePasswordField;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JList list_Keywords;
    private javax.swing.JTextField txtField_Browse;
    private javax.swing.JTextField txtField_DecryptBrowse;
    private javax.swing.JTextField txtField_newKeyword;
    // End of variables declaration//GEN-END:variables

}
