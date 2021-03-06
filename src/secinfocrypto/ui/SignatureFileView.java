/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package secinfocrypto.ui;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import secinfocrypto.signature.SignatureFile;
import secinfocrypto.signature.SignatureListener;
import secinfocrypto.user.DataBase;

/**
 *
 * @author bruno
 */
public class SignatureFileView extends JPanel {

    private JProgressBar progressBar;
    private SignatureFile file;
    private JLabel fileName, algorithm;

    public class GeneratorListener implements SignatureListener<byte[]> {

        JPanel parent;

        GeneratorListener(JPanel parent) {
            this.parent = parent;
        }

        @Override
        public void setProgress(int i) {
            progressBar.setValue(i);
        }

        @Override
        public void setProcessInformation(String string) {
            this.parent.setToolTipText(string);
        }

        @Override
        public void setResult(byte[] result) {
        }

        @Override
        public void processStart() {
        }

        @Override
        public void processEnd() {
            try {
                DataBase.getInstance().save();
            } catch (Exception ex) {
                Logger.getLogger(UserInterface.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void exception(Exception e) {
            this.parent.setToolTipText("Error: " + e.getMessage());
            JLabel error = new JLabel("Error: " + e.getMessage());
            error.setForeground(Color.RED);
            this.parent.add(error);
            this.parent.updateUI();
        }
    }

    public class CheckerListener implements SignatureListener<Boolean> {

        JPanel parent;

        CheckerListener(JPanel parent) {
            this.parent = parent;
        }

        @Override
        public void setProgress(int i) {
            progressBar.setValue(i);
        }

        @Override
        public void setProcessInformation(String string) {
            this.parent.setToolTipText(string);
        }

        @Override
        public void setResult(Boolean result) {
            if (!result) {
                JOptionPane.showMessageDialog(this.parent, "Wrong signature : " + file.getFile().getName());
            }
        }

        @Override
        public void processStart() {
        }

        @Override
        public void processEnd() {
            try {
                DataBase.getInstance().save();
            } catch (Exception ex) {
                Logger.getLogger(UserInterface.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        @Override
        public void exception(Exception e) {
            this.parent.setToolTipText("Error: " + e.getMessage());
            JLabel error = new JLabel("Error: " + e.getMessage());
            error.setForeground(Color.RED);
            this.parent.add(error);
            this.parent.updateUI();
        }
    }

    SignatureFileView(final SignatureFile file) {
        super.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        super.setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        this.file = file;

        this.fileName = new JLabel();
        this.fileName.setText("File name: " + this.file.getFile().getName());
        super.add(this.fileName);

        this.algorithm = new JLabel();
        this.algorithm.setText("Algorithm: " + this.file.getAlgorithm());
        super.add(this.algorithm);

        this.progressBar = new JProgressBar();
        this.progressBar.setStringPainted(true);
        super.add(this.progressBar);
    }

    public SignatureFile getSignatureFile() {
        return this.file;
    }
}
