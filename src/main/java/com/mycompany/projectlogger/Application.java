/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.projectlogger;

import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author amina
 */
public class Application extends javax.swing.JFrame {
    private BufferedWriter fileWriter = null;
    private ClientConnection connection;
    private Thread receiveThread;
    private Thread sendThread;
    private ReceiveData receiveData;
    private SendData sendData;
    
    private State state = State.INITIAL;
    public enum State {
        INITIAL,
        RECV_IDENTIFY,
        IDENTIFY,
        LOGGING_IN,
        CONNECTED,
        START,
        RECEIVE_MEASUREMENTS,
        READY,
        BREAK
    }
    
    /**
     * Creates new form Application
     */
    public Application() {
        initComponents();
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                serverDisconnect();
            }
        });
        connection = new ClientConnection("127.0.0.1", 1234);
        DefaultCaret caret = (DefaultCaret)logTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }
    
    public synchronized State getThreadState() {
        return state;
    }
    
    public synchronized void setThreadState(State state) {
        this.state = state;
        System.out.println("Changing to state: " + state);
    }    
    
    public void serverDisconnect() {
        try {
            if(receiveData != null) {
                receiveData.stop();
            }
            if(sendData != null) {
                sendData.stop();
            }
            connection.disconnect();
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
            startButton.setEnabled(false);
            breakButton.setEnabled(false);
            closeButton.setEnabled(true);            
            
        } catch(IOException e) {
            appendToLog("Disconnect failed", false);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        logScrollPanel = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();
        logFilePanel = new javax.swing.JPanel();
        openButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        connectionPanel = new javax.swing.JPanel();
        connectButton = new javax.swing.JButton();
        disconnectButton = new javax.swing.JButton();
        measurementPanel = new javax.swing.JPanel();
        startButton = new javax.swing.JButton();
        breakButton = new javax.swing.JButton();
        exitPanel = new javax.swing.JPanel();
        exitButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(350, 211));
        setName("mainFrame"); // NOI18N
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.PAGE_AXIS));

        logScrollPanel.setName(""); // NOI18N

        logTextArea.setColumns(20);
        logTextArea.setRows(10);
        logTextArea.setMinimumSize(new java.awt.Dimension(25, 22));
        logScrollPanel.setViewportView(logTextArea);

        getContentPane().add(logScrollPanel);

        logFilePanel.setMaximumSize(new java.awt.Dimension(32767, 55));

        openButton.setText("Open");
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });
        logFilePanel.add(openButton);

        closeButton.setText("Close");
        closeButton.setEnabled(false);
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        logFilePanel.add(closeButton);

        getContentPane().add(logFilePanel);

        connectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Connection\n"));
        connectionPanel.setMaximumSize(new java.awt.Dimension(32767, 55));

        connectButton.setText("Connect");
        connectButton.setEnabled(false);
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });
        connectionPanel.add(connectButton);

        disconnectButton.setText("Disconnect");
        disconnectButton.setEnabled(false);
        disconnectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disconnectButtonActionPerformed(evt);
            }
        });
        connectionPanel.add(disconnectButton);

        getContentPane().add(connectionPanel);

        measurementPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Measurement"));
        measurementPanel.setMaximumSize(new java.awt.Dimension(32767, 55));

        startButton.setText("Start");
        startButton.setEnabled(false);
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });
        measurementPanel.add(startButton);

        breakButton.setText("Break");
        breakButton.setEnabled(false);
        breakButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                breakButtonActionPerformed(evt);
            }
        });
        measurementPanel.add(breakButton);

        getContentPane().add(measurementPanel);

        exitPanel.setLayout(new javax.swing.BoxLayout(exitPanel, javax.swing.BoxLayout.LINE_AXIS));

        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });
        exitPanel.add(exitButton);

        getContentPane().add(exitPanel);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void appendToLog(String text, boolean writeToFile) {
        String oldText = logTextArea.getText();
        String newText = text + System.lineSeparator();
        
        if(!writeToFile) {
            newText = "DEBUG: " + newText;
        }
        logTextArea.setText(oldText + newText);
        
        if(writeToFile && fileWriter != null) {
            try{
                fileWriter.append(newText);
                fileWriter.flush();
            } catch(IOException ex) {
                System.out.println("ERROR: Failed to write to the log file");
            }
        }
    }
    
    public void appendToLog(String text) {
        appendToLog(text, true);           
    }
  
    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        int choice = chooser.showOpenDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION) {
            try {
                fileWriter = Files.newBufferedWriter(chooser.getSelectedFile().toPath());
                appendToLog("Opened file: " + chooser.getSelectedFile().getName(), false);
                closeButton.setEnabled(true);
                connectButton.setEnabled(true);
                openButton.setEnabled(false);

            } catch (FileNotFoundException e) {
                appendToLog("File not found", false);
            } catch (IOException ex) {
                appendToLog("Error opening the file", false);
            }        
        }
    }//GEN-LAST:event_openButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
       if (fileWriter != null) {
           try {
               fileWriter.close();
               fileWriter = null;
               closeButton.setEnabled(false);
               openButton.setEnabled(true);
               connectButton.setEnabled(false);
               appendToLog("File closed.", false);
               
           } catch (IOException ex) {
               Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
    }//GEN-LAST:event_closeButtonActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        serverDisconnect();
        System.exit(0);
    }//GEN-LAST:event_exitButtonActionPerformed
    
    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
        try {
            connection.connect();
            receiveData = new ReceiveData(this, connection.getInputStream());
            receiveThread = new Thread(receiveData);
            sendData = new SendData(this, connection.getOutputStream());
            sendThread = new Thread(sendData);
            receiveThread.start();
            sendThread.start();
            setThreadState(State.RECV_IDENTIFY);
            
        } catch(IOException e) {
            appendToLog("Connection refused", false);
        }
    }//GEN-LAST:event_connectButtonActionPerformed

    private void disconnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disconnectButtonActionPerformed
        serverDisconnect();        
    }//GEN-LAST:event_disconnectButtonActionPerformed

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        setThreadState(State.START);
    }//GEN-LAST:event_startButtonActionPerformed

    private void breakButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_breakButtonActionPerformed
        setThreadState(State.BREAK);
    }//GEN-LAST:event_breakButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Application().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton breakButton;
    javax.swing.JButton closeButton;
    javax.swing.JButton connectButton;
    private javax.swing.JPanel connectionPanel;
    javax.swing.JButton disconnectButton;
    private javax.swing.JButton exitButton;
    private javax.swing.JPanel exitPanel;
    private javax.swing.JPanel logFilePanel;
    private javax.swing.JScrollPane logScrollPanel;
    private javax.swing.JTextArea logTextArea;
    private javax.swing.JPanel measurementPanel;
    private javax.swing.JButton openButton;
    javax.swing.JButton startButton;
    // End of variables declaration//GEN-END:variables
}
