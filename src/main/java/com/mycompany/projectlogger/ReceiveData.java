/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.projectlogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author amina
 */
public class ReceiveData implements Runnable {
    private InputStream inStream;
    private Application application;
    
    public ReceiveData(Application application, InputStream inStream) {
        this.inStream = inStream;
        this.application = application;
    }
    
    public void readIdentifyPacket() {
        try {
            byte[] lengthBytes = new byte[4];
            inStream.read(lengthBytes);
            int packetLength = ByteBuffer.wrap(lengthBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
            System.out.println(packetLength);
            
            StringBuilder builder = new StringBuilder();
            byte[] packet = new byte[packetLength-4];
            inStream.read(packet);
            ByteBuffer buffer = ByteBuffer.wrap(packet).order(ByteOrder.LITTLE_ENDIAN);
            for(int i=0; i<packet.length-2; i+=2) {
                char letter = (char) buffer.getShort(i);                
                builder.append(letter);
            }
            String stringPacket = builder.toString();
            
            System.out.println(stringPacket); 
            System.out.println(stringPacket.compareTo("Identify") == 0); 
            if(stringPacket.compareTo("Identify")==0) {
                application.setThreadState(Application.State.IDENTIFY);
            }
        } catch(IOException e) {
        
        }
        
    }
    
    @Override
    public void run() {
        Application.State state = application.getThreadState();
        switch(state) {
            case RECV_IDENTIFY:
                readIdentifyPacket();
                break;
            default:
                break;
                
        }
    }
    
    
    
}
