/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.projectlogger;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author amina
 */
public class SendData implements Runnable {
    private OutputStream outStream;
    private Application application;
    private volatile boolean exit = false; 

    
    public SendData(Application application, OutputStream outStream) {
        this.outStream = outStream;
        this.application = application;
    }
    
    @Override
    public void run() {
        while(!exit) {
            Application.State state = application.getThreadState();
            switch(state) {
                case IDENTIFY: 
                    sendIdentifyPacket();
                    break;
                case START:
                    sendStartPacket();
                    break;
                default:
                    break;
            }
        }
    }

    private void send(PacketBuffer buffer) throws IOException {
        outStream.write(buffer.array());
    }
    
    private void sendIdentifyPacket() {
        try {
            PacketBuffer sendBuffer = new PacketBuffer("coursework");
            send(sendBuffer);
            application.setThreadState(Application.State.LOGGING_IN);
        } catch (IOException ex) {
            Logger.getLogger(SendData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
        private void sendStartPacket() {
        try {
            PacketBuffer sendBuffer = new PacketBuffer("Start");
            send(sendBuffer);
            application.setThreadState(Application.State.RECEIVE_MEASUREMENTS);
        } catch (IOException ex) {
            Logger.getLogger(SendData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void stop() {
        exit = true;
    } 
    
}
