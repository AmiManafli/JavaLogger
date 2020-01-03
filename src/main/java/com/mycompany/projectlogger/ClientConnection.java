/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.projectlogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author amina
 */
public class ClientConnection {
    private Socket socket;
    private String hostname;
    private int port;
    public ClientConnection(String hostname, int port) {
  //      socket = new Socket();
        this.hostname = hostname;
        this.port = port;
    }
    
    InputStream getInputStream() throws IOException{
        return socket.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }
    
    public void connect() throws IOException {
        if(socket != null && socket.isConnected()){
            disconnect();
        }
        socket = new Socket(hostname, port);
    }
    
    public void disconnect() throws IOException {
        socket.close();
    }
}
