package app.gui;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import javax.swing.JOptionPane;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;

import app.user.AnynomousAppUser;
import app.user.PublishableAppUser;
import app.user.ReadOnlyAppUser;
import app.utils.ByteUtils;
import app.utils.Cryptography;

public class ForumJFrame extends javax.swing.JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates new form ForumJFrame
     */
    private ForumJFrame(final Contract contract) {
        this.contract = contract;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        userPublicKeyJTextField = new javax.swing.JTextField();
        userPrivateKeyJTextField = new javax.swing.JTextField();
        generateKeyPairJButton = new javax.swing.JButton();
        pointAmountJTextField = new javax.swing.JTextField();
        refreshPointAmountJButton = new javax.swing.JButton();
        pointAmountJLabel = new javax.swing.JLabel();
        MessageJTextField = new javax.swing.JTextField();
        searchJTextField = new javax.swing.JTextField();
        contentJTabbedPane = new javax.swing.JTabbedPane();
        viewPostJPanel = new javax.swing.JPanel();
        viewPostKeysJScrollPane = new javax.swing.JScrollPane();
        viewPostKeysJList = new javax.swing.JList<>();
        viewPostKeysQueryJComboBox = new javax.swing.JComboBox<>();
        viewPostJScrollPane = new javax.swing.JScrollPane();
        viewPostJTextArea = new javax.swing.JTextArea();
        publishPostJPanel = new javax.swing.JPanel();
        postEditorJScrollPane = new javax.swing.JScrollPane();
        postEditorJTextArea = new javax.swing.JTextArea();
        publishPostSubmitJButton = new javax.swing.JButton();
        publishPostResetJButton = new javax.swing.JButton();
        userSearchJSeparator = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1280, 720));

        userPublicKeyJTextField.setToolTipText("User Public Key");
        userPublicKeyJTextField.setActionCommand("<Not Set>");
        userPublicKeyJTextField.setName(""); // NOI18N

        userPrivateKeyJTextField.setToolTipText("User Private Key");
        userPrivateKeyJTextField.setName(""); // NOI18N

        generateKeyPairJButton.setText("Generate Key Pair");
        generateKeyPairJButton.setName(""); // NOI18N
        generateKeyPairJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateKeyPairJButtonActionPerformed(evt);
            }
        });

        pointAmountJTextField.setEditable(false);
        pointAmountJTextField.setToolTipText("Point Amount");
        pointAmountJTextField.setName(""); // NOI18N

        refreshPointAmountJButton.setText("Refresh");
        refreshPointAmountJButton.setName(""); // NOI18N
        refreshPointAmountJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshPointAmountJButtonActionPerformed(evt);
            }
        });

        pointAmountJLabel.setText("Point Amount");
        pointAmountJLabel.setToolTipText("");
        pointAmountJLabel.setName(""); // NOI18N

        MessageJTextField.setEditable(false);
        MessageJTextField.setToolTipText("Message");

        searchJTextField.setToolTipText("Search Bar");

        viewPostKeysJScrollPane.setPreferredSize(new java.awt.Dimension(284, 500));

        viewPostKeysJList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        viewPostKeysJList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                viewPostKeysJListValueChanged(evt);
            }
        });
        viewPostKeysJScrollPane.setViewportView(viewPostKeysJList);

        viewPostKeysQueryJComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Search By Author", "Search By Post Key" }));
        viewPostKeysQueryJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewPostKeysQueryJComboBoxActionPerformed(evt);
            }
        });

        viewPostJScrollPane.setPreferredSize(new java.awt.Dimension(985, 500));

        viewPostJTextArea.setEditable(false);
        viewPostJTextArea.setColumns(20);
        viewPostJTextArea.setLineWrap(true);
        viewPostJTextArea.setRows(5);
        viewPostJTextArea.setTabSize(4);
        viewPostJTextArea.setWrapStyleWord(true);
        viewPostJScrollPane.setViewportView(viewPostJTextArea);

        javax.swing.GroupLayout viewPostJPanelLayout = new javax.swing.GroupLayout(viewPostJPanel);
        viewPostJPanel.setLayout(viewPostJPanelLayout);
        viewPostJPanelLayout.setHorizontalGroup(
            viewPostJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(viewPostJPanelLayout.createSequentialGroup()
                .addGroup(viewPostJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(viewPostKeysQueryJComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(viewPostKeysJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(viewPostJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        viewPostJPanelLayout.setVerticalGroup(
            viewPostJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(viewPostJPanelLayout.createSequentialGroup()
                .addComponent(viewPostKeysQueryJComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(viewPostJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(viewPostJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(viewPostKeysJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 66, Short.MAX_VALUE))
        );

        contentJTabbedPane.addTab("Posts", viewPostJPanel);

        postEditorJScrollPane.setPreferredSize(new java.awt.Dimension(1275, 528));

        postEditorJTextArea.setColumns(20);
        postEditorJTextArea.setLineWrap(true);
        postEditorJTextArea.setRows(5);
        postEditorJTextArea.setTabSize(4);
        postEditorJTextArea.setWrapStyleWord(true);
        postEditorJScrollPane.setViewportView(postEditorJTextArea);

        publishPostSubmitJButton.setText("Publish");
        publishPostSubmitJButton.setToolTipText("");
        publishPostSubmitJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                publishPostSubmitJButtonActionPerformed(evt);
            }
        });

        publishPostResetJButton.setText("Reset");
        publishPostResetJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                publishPostResetJButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout publishPostJPanelLayout = new javax.swing.GroupLayout(publishPostJPanel);
        publishPostJPanel.setLayout(publishPostJPanelLayout);
        publishPostJPanelLayout.setHorizontalGroup(
            publishPostJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(publishPostJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(publishPostResetJButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(publishPostSubmitJButton)
                .addContainerGap())
            .addComponent(postEditorJScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        publishPostJPanelLayout.setVerticalGroup(
            publishPostJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(publishPostJPanelLayout.createSequentialGroup()
                .addComponent(postEditorJScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 528, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(publishPostJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(publishPostSubmitJButton)
                    .addComponent(publishPostResetJButton))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        contentJTabbedPane.addTab("Publish", publishPostJPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(searchJTextField)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(userPublicKeyJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(userPrivateKeyJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(generateKeyPairJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pointAmountJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pointAmountJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(refreshPointAmountJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addComponent(MessageJTextField, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(userSearchJSeparator)
            .addComponent(contentJTabbedPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userPublicKeyJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(userPrivateKeyJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(generateKeyPairJButton)
                    .addComponent(refreshPointAmountJButton)
                    .addComponent(pointAmountJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pointAmountJLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(userSearchJSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(searchJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(contentJTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 623, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(MessageJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void viewPostKeysJListValueChanged(final javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_viewPostKeysJListValueChanged
        if (this.viewPostKeysJList.getSelectedIndex() == -1) {
            return;
        }

        final Function<String, String> getPrettyJson = (rawString) -> {
            final var objectMapper = new ObjectMapper();
            try {
                final Object json = objectMapper.readValue(rawString, Object.class);
                final String prettified = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
                return prettified;
            } catch (final JsonParseException e) {
                return rawString;
            } catch (final Exception e) {
                e.printStackTrace();
            }
            return null;
        };

        final String selectedPostKey = this.viewPostKeysJList.getSelectedValue();
        try {
            // TODO: use standard api here
            final String postString = new String(this.contract.evaluateTransaction("getPostByKey", selectedPostKey));
            final String beautifulPostString = getPrettyJson.apply(postString);
            this.viewPostJTextArea.setText(beautifulPostString);
        } catch (final ContractException e1) {
            e1.printStackTrace();
        }
    }// GEN-LAST:event_viewPostKeysJListValueChanged

    private void viewPostKeysQueryJComboBoxActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_viewPostKeysQueryJComboBoxActionPerformed
        if (this.viewPostKeysQueryJComboBox.getSelectedItem().equals("All")) {
            final var userApp = new AnynomousAppUser(this.contract);
            final String[] postKeys = userApp.fetchAllPostKeys();
            this.viewPostKeysJList.setListData(postKeys);
        }
    }// GEN-LAST:event_viewPostKeysQueryJComboBoxActionPerformed

    private void generateKeyPairJButtonActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_generateKeyPairJButtonActionPerformed
        if (!this.userPublicKeyJTextField.getText().isEmpty() || !this.userPrivateKeyJTextField.getText().isEmpty()) {
            final int choice = JOptionPane.showConfirmDialog(null,
                    "Do you want to overwrite the existing Public and/or Private Keys?", "Warning",
                    JOptionPane.OK_CANCEL_OPTION);
            if (choice == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        try {
            final KeyPair keyPair = Cryptography.generateRandomKeyPair();
            final String publicKeyString = ByteUtils.toHexString(keyPair.getPublic().getEncoded());
            final String privateKeyString = ByteUtils.toHexString(keyPair.getPrivate().getEncoded());
            this.userPublicKeyJTextField.setText(publicKeyString);
            this.userPrivateKeyJTextField.setText(privateKeyString);
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }// GEN-LAST:event_generateKeyPairJButtonActionPerformed

    private void publishPostResetJButtonActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_publishPostResetJButtonActionPerformed
        final int choice = JOptionPane.showConfirmDialog(null, "Do you want to discard the content?", "Warning",
                JOptionPane.OK_CANCEL_OPTION);
        if (choice == JOptionPane.CANCEL_OPTION) {
            return;
        }
        this.postEditorJTextArea.setText(new String());
    }// GEN-LAST:event_publishPostResetJButtonActionPerformed

    private void publishPostSubmitJButtonActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_publishPostSubmitJButtonActionPerformed
        final String publicKeyString = this.userPublicKeyJTextField.getText();
        final String privateKeyString = this.userPrivateKeyJTextField.getText();

        if (publicKeyString.isEmpty() || privateKeyString.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please provide a Public/Private Key Pair!");
            return;
        }

        final int choice = JOptionPane.showConfirmDialog(null,
                "Do you want to publish the post using the provided Public and/or Private Keys?", "Confirm",
                JOptionPane.OK_CANCEL_OPTION);
        if (choice == JOptionPane.CANCEL_OPTION) {
            return;
        }

        try {
            final var appUser = new PublishableAppUser(this.contract,
                    Cryptography.parsePublicKey(ByteUtils.toByteArray(publicKeyString)),
                    Cryptography.parsePrivateKey(ByteUtils.toByteArray(privateKeyString)));

            appUser.publishNewPost(this.postEditorJTextArea.getText());

            JOptionPane.showMessageDialog(null, "The post was published successfully!");

            this.postEditorJTextArea.setText(new String());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | ContractException
                | SignatureException | TimeoutException | InterruptedException e1) {
            JOptionPane.showMessageDialog(null, "The post failed to be published!");
            e1.printStackTrace();
        }

    }// GEN-LAST:event_publishPostSubmitJButtonActionPerformed

    private void refreshPointAmountJButtonActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_refreshPointAmountJButtonActionPerformed
        final String userPublicKey = this.userPublicKeyJTextField.getText();
        String pointAmount = new String();
        try {
            final var appUser = new ReadOnlyAppUser(this.contract,
                    Cryptography.parsePublicKey(ByteUtils.toByteArray(userPublicKey)));
            pointAmount = appUser.getPointAmount();
        } catch (ContractException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        this.pointAmountJTextField.setText(pointAmount);
    }// GEN-LAST:event_refreshPointAmountJButtonActionPerformed

    public static void run(final Contract contract) {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default
         * look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (final javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (final ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ForumJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (final InstantiationException ex) {
            java.util.logging.Logger.getLogger(ForumJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (final IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ForumJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (final javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ForumJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        }
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ForumJFrame(contract).setVisible(true);
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        run(null);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField MessageJTextField;
    private javax.swing.JTabbedPane contentJTabbedPane;
    private javax.swing.JButton generateKeyPairJButton;
    private javax.swing.JLabel pointAmountJLabel;
    private javax.swing.JTextField pointAmountJTextField;
    private javax.swing.JScrollPane postEditorJScrollPane;
    private javax.swing.JTextArea postEditorJTextArea;
    private javax.swing.JPanel publishPostJPanel;
    private javax.swing.JButton publishPostResetJButton;
    private javax.swing.JButton publishPostSubmitJButton;
    private javax.swing.JButton refreshPointAmountJButton;
    private javax.swing.JTextField searchJTextField;
    private javax.swing.JTextField userPrivateKeyJTextField;
    private javax.swing.JTextField userPublicKeyJTextField;
    private javax.swing.JSeparator userSearchJSeparator;
    private javax.swing.JPanel viewPostJPanel;
    private javax.swing.JScrollPane viewPostJScrollPane;
    private javax.swing.JTextArea viewPostJTextArea;
    private javax.swing.JList<String> viewPostKeysJList;
    private javax.swing.JScrollPane viewPostKeysJScrollPane;
    private javax.swing.JComboBox<String> viewPostKeysQueryJComboBox;
    // End of variables declaration//GEN-END:variables

    private final Contract contract;
}
