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
        while (!exit) {
            try {
                Application.State state = application.getThreadState();
                switch (state) {
                    case IDENTIFY:
                        sendIdentifyPacket();
                        break;
                    case START:
                        sendStartPacket();
                        break;
                    case READY:
                        sendReadyPacket();
                        break;
                    case BREAK:
                        sendBreakPacket();
                        break;
                    default:
                        break;
                }
            } catch (IOException ex) {
             //   Logger.getLogger(SendData.class.getName()).log(Level.SEVERE, null, ex);
                exit = true;
                application.serverDisconnect();
            }
        }
    }

    private void send(PacketBuffer buffer) throws IOException {
        outStream.write(buffer.array());
    }

    private void sendIdentifyPacket() throws IOException {
        PacketBuffer sendBuffer = new PacketBuffer("coursework");
        send(sendBuffer);
        application.setThreadState(Application.State.LOGGING_IN);

    }

    private void sendStartPacket() throws IOException {
        PacketBuffer sendBuffer = new PacketBuffer("Start");
        send(sendBuffer);
        application.setThreadState(Application.State.RECEIVE_MEASUREMENTS);
        application.startButton.setEnabled(false);
        application.breakButton.setEnabled(true);

    }

    public void stop() {
        exit = true;
    }

    private void sendReadyPacket() throws IOException {
        PacketBuffer sendBuffer = new PacketBuffer("Ready");
        send(sendBuffer);
        application.setThreadState(Application.State.RECEIVE_MEASUREMENTS);
    }

    private void sendBreakPacket() throws IOException {
        PacketBuffer sendBuffer = new PacketBuffer("Break");
        send(sendBuffer);
        application.setThreadState(Application.State.CONNECTED);
        application.startButton.setEnabled(true);
        application.breakButton.setEnabled(false);

    }
}
