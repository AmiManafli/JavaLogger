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
import java.text.SimpleDateFormat;
import java.util.Date;
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

    public void readIdentifyPacket() throws IOException {
        PacketBuffer packetBuffer = readPacket();
        String messageRcv = packetBuffer.getUnicodeString();

        if (messageRcv.compareTo("Identify") == 0) {
            application.setThreadState(Application.State.IDENTIFY);
        }
    }
    
    private void readAcceptedPacket() throws IOException {
        PacketBuffer packetBuffer = readPacket();
        String messageRcv = packetBuffer.getUnicodeString();

        if (messageRcv.compareTo("Accepted") == 0) {
            application.setThreadState(Application.State.CONNECTED);
            application.appendToLog("Connected to the server", false);
            application.connectButton.setEnabled(false);
            application.startButton.setEnabled(true);
            application.disconnectButton.setEnabled(true);
            application.breakButton.setEnabled(false);
            application.closeButton.setEnabled(false);

        } else {
            application.appendToLog("Failed to log in", false);
            application.setThreadState(Application.State.INITIAL);
            application.connectButton.setEnabled(true);
            application.startButton.setEnabled(false);
            application.disconnectButton.setEnabled(false);

        } 
    }
    
    private String getValue(String pointName, PacketBuffer buffer) {
        if (pointName.contains("quantity")) {
            return String.format("%d kg", buffer.getInt());            
        } else if (pointName.contains("volume")) {
            return String.format("%d L", buffer.getInt());      
        } else if (pointName.contains("level")) {
            return String.format("%d %%", buffer.getInt()); 
        } else if (pointName.contains("concentration")) {
            return String.format("%d %%", buffer.getInt()); 
        } else if (pointName.contains("temperature")) {
            return String.format("%.1f °C", buffer.getDouble()); 
        } else if (pointName.contains("pressure")) {
            return String.format("%.1f atm", buffer.getDouble()); 
        } else if (pointName.contains("pH")) {
            return String.format("%.1f", buffer.getDouble()); 
        } else if (pointName.contains("viscosity")) {
            return String.format("%.2f cSt", buffer.getDouble()); 
        } else if (pointName.contains("conductivity")) {
            return String.format("%.2f S/m", buffer.getDouble()); 
        } else if (pointName.contains("speed")) {
            return String.format("%.3f m³/s", buffer.getDouble()); 
        } else if (pointName.contains("turbidity")) {
            return String.format("%f NTU", buffer.getDouble()); 
        } else { 
            throw new RuntimeException("Couldn't parse the point name: " + pointName);
        }
    }
    
    
    private void readMeasurementPacket() throws IOException {
        PacketBuffer buffer = readPacket();
        SimpleDateFormat formatter = new SimpleDateFormat("'Measurement results at' yyyy-MM-dd HH:mm:ss");
        application.appendToLog(formatter.format(new Date(System.currentTimeMillis())));
        int numChannels = buffer.getInt();

        for (int channel = 0; channel < numChannels; channel++) {
            int numPoints = buffer.getInt();
            String channelName = buffer.getString();

            application.appendToLog(String.format("%s:", channelName));

            for (int point = 0; point < numPoints; point++) {
                String pointName = buffer.getString();
                String value = getValue(pointName, buffer);
                application.appendToLog(String.format("%s: %s", pointName, value));
            }
        }
        application.appendToLog("");
    }


    @Override
    public void run() {
        while(!exit){
            try {
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
                        application.setThreadState(Application.State.READY);
                        break;
                    default:
                        break;
                }
            } catch (IOException ex) {
            //    Logger.getLogger(ReceiveData.class.getName()).log(Level.SEVERE, null, ex);
                exit = true;
                application.serverDisconnect();
            }
        }
    }

    public void stop() {
        exit = true;
    }
}
