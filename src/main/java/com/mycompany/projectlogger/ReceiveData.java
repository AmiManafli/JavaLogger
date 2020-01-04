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
    private volatile boolean exit = false; 
    
    
    public ReceiveData(Application application, InputStream inStream) {
        this.inStream = inStream;
        this.application = application;
    }

    private PacketBuffer readPacket() throws IOException {
        byte[] lengthBytes = new byte[4];
        inStream.read(lengthBytes);
        int packetLength = ByteBuffer.wrap(lengthBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
        byte[] packet = new byte[packetLength-4];
        inStream.read(packet);
        PacketBuffer packetBuffer = new PacketBuffer(packet);
        return packetBuffer;
    }    

    public void readIdentifyPacket() {
        try {
            PacketBuffer packetBuffer = readPacket();
            String messageRcv = packetBuffer.getUnicodeString();
            
            if(messageRcv.compareTo("Identify")==0) {
                application.setThreadState(Application.State.IDENTIFY);
            }
        } catch(IOException e) {
            application.appendToLog("ERROR: Failed to receive Identify!");
        } 
    }
    
    private void readAcceptedPacket() {
        try {
            PacketBuffer packetBuffer = readPacket();
            String messageRcv = packetBuffer.getUnicodeString();
            
            if(messageRcv.compareTo("Accepted")==0) {
                application.setThreadState(Application.State.CONNECTED);
                application.appendToLog("Connected to the server");
                application.connectButton.setEnabled(false);
                application.startButton.setEnabled(true);
                application.disconnectButton.setEnabled(true);

            } else {
                application.appendToLog("Failed to log in...");
                application.setThreadState(Application.State.INITIAL);
                application.connectButton.setEnabled(true);
                application.startButton.setEnabled(false);
                application.disconnectButton.setEnabled(false);
           
            }
        } catch(IOException e) {
            application.appendToLog("ERROR: Failed to receive Accepted!");
        } 
    }
    
    private boolean isValueInt(String pointName) {
        return (
            pointName.contains("quantity") || 
            pointName.contains("volume") ||
            pointName.contains("level") || 
            pointName.contains("concentration")
        ); 
    }
    
    private void readMeasurementPacket() {
        try {
            PacketBuffer buffer = readPacket();
            int numChannels = buffer.getInt();
            application.appendToLog("Number of channels: " + numChannels);
            
            for(int channel = 0; channel < numChannels; channel++) {
                int numPoints = buffer.getInt();
                application.appendToLog("Number of points: " + numPoints); 
                String channelName = buffer.getString();
                application.appendToLog("Name of the channel: " + channelName); 
                
                for(int point = 0; point < numPoints; point++) {
                    String pointName = buffer.getString();
                    application.appendToLog("Name of the point: " + pointName); 
                    if(isValueInt(pointName)) {
                        int value = buffer.getInt();
                        application.appendToLog("Int Measurement: " + value);
                    } else {
                        double value = buffer.getDouble();
                        application.appendToLog("Double Measurement: " + value); 
                    } 
                }
            }
            
            
        } catch(IOException e) {
            application.appendToLog("ERROR: Failed to receive Identify!");
        } 
    }


    @Override
    public void run() {
        while(!exit){
            Application.State state = application.getThreadState();
            switch(state) {
                case RECV_IDENTIFY:
                    readIdentifyPacket();
                    break;
                case LOGGING_IN: 
                    readAcceptedPacket();
                    break;
                case RECEIVE_MEASUREMENTS:
                    readMeasurementPacket();
                    break;
                default:
                    break;
            }
        }
        application.appendToLog("ReceiveThread stopping");
    }

    public void stop() {
        exit = true;
    }


    
    
    
}
