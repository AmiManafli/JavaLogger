/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.projectlogger;

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

    
    public SendData(Application application, OutputStream outStream) {
        this.outStream = outStream;
        this.application = application;
    }
    
    @Override
    public void run() {
        while(true) {
            Application.State state = application.getThreadState();
            switch(state) {
                case IDENTIFY: 
                    sendIdentifyPacket();
                    break;
                default:
                    break;
            }
        }
    }

    private void sendIdentifyPacket() {
        System.out.println("Sending a password packet...");
        application.setThreadState(Application.State.LOGGING_IN);
    }
        
    
    
}
