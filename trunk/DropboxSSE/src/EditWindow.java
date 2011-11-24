
import java.io.File;
import java.util.Scanner;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;

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
 * @author Boardwalk
 */
public class EditWindow extends javax.swing.JFrame {

    public static EditWindow ew;
    public File currentFile; //file currently being edited
    public static DefaultListModel keywordsModel;

    /** Creates new form EditWindow */
    public EditWindow() {
        //EDITWINDOW SHOULD ONLY BE CALLED WITH A FILE PASSED TO IT
    }

    public EditWindow(File passedFile) {
        initComponents();
        currentFile = passedFile;
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
        searchPasswordField = new javax.swing.JPasswordField();
        btn_RevealKeywords = new javax.swing.JButton();
        btn_SaveKeywords = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        btn_Browse1 = new javax.swing.JButton();
        txtField_DecryptBrowse = new javax.swing.JTextField();

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

        searchPasswordField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchPasswordFieldActionPerformed(evt);
            }
        });

        btn_RevealKeywords.setText("Reveal File Keywords");
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(102, 102, 102))
                    .addComponent(btn_RevealKeywords, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(144, 144, 144))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btn_SaveKeywords, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                            .addComponent(btn_DeleteKeyword, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                            .addComponent(btn_AddKeyword, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                            .addComponent(txtField_newKeyword, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE))
                        .addGap(10, 10, 10)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btn_ReplaceFile, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(btn_Browse)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtField_Browse, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addComponent(btn_Decrypt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_Browse1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtField_DecryptBrowse, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(234, 234, 234)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(searchPasswordField, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                .addGap(171, 171, 171))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_Browse1)
                            .addComponent(txtField_DecryptBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_Decrypt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_Browse)
                            .addComponent(txtField_Browse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_ReplaceFile))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(btn_RevealKeywords)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtField_newKeyword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_AddKeyword)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_DeleteKeyword)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_SaveKeywords))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_DecryptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_DecryptActionPerformed
        try
        {
            File saveAs = new File(txtField_Browse.getText().toString());
            crypto.Crypto.fileAESdec(currentFile, saveAs, searchPasswordField.getPassword());
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

    private void btn_RevealKeywordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_RevealKeywordsActionPerformed
        try
        {
            StringBuilder sb = crypto.Crypto.keyAESdec(currentFile, searchPasswordField.getPassword());
            Vector<String> fileKeys = crypto.SSE2.parseKeys(sb);

            for(int i = 0; i < fileKeys.size(); i++)
            {
                keywordsModel.addElement(fileKeys.elementAt(i));
            }
        }
        catch(Exception e){e.printStackTrace();}
    }//GEN-LAST:event_btn_RevealKeywordsActionPerformed

    private void btn_SaveKeywordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SaveKeywordsActionPerformed
        try
        {
        
        }
        catch(Exception e){e.printStackTrace();}
    }//GEN-LAST:event_btn_SaveKeywordsActionPerformed

    private void searchPasswordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchPasswordFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchPasswordFieldActionPerformed

    private void btn_ReplaceFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ReplaceFileActionPerformed
        try
        {
            //delete original file from dropbox
            MainWindow.deleteFile(currentFile);

            //push new file  to dropbox
            File uploadFile = new File(txtField_Browse.getText());
            File decryptedUploadFile = new File("path_to_file");

            crypto.Crypto.fileAESenc(uploadFile, decryptedUploadFile, searchPasswordField.getPassword(), true); //true means we always delete original

            //push new file with new keywords to dropbox
            MainWindow.pushFile(currentFile);    
           // MainWindow.pushFile(currentFile);
        }catch (Exception e){e.printStackTrace();}
    }//GEN-LAST:event_btn_ReplaceFileActionPerformed

    private void btn_Browse1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_Browse1ActionPerformed
        // TODO add your handling code here:
        JFileChooser fc = new JFileChooser();
        fc.showSaveDialog(this);
        try
        {
            txtField_DecryptBrowse.setText(fc.getSelectedFile().getAbsolutePath().toString());
            //File saveFile = new File(fc.getSelectedFile().getAbsolutePath().toString());
        }
        catch(Exception e){e.printStackTrace();}
    }//GEN-LAST:event_btn_Browse1ActionPerformed

    private void btn_AddKeywordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AddKeywordActionPerformed
        String newKeyword = txtField_newKeyword.getText();
        if(newKeyword.length() == 0)
        {
            System.out.println("no keyword entered");
        }
        else
        {
            txtField_newKeyword.setText("");
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
            System.out.println("Please select keyword to delete");
        }

    }//GEN-LAST:event_btn_DeleteKeywordActionPerformed

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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JList list_Keywords;
    private javax.swing.JPasswordField searchPasswordField;
    private javax.swing.JTextField txtField_Browse;
    private javax.swing.JTextField txtField_DecryptBrowse;
    private javax.swing.JTextField txtField_newKeyword;
    // End of variables declaration//GEN-END:variables

}
