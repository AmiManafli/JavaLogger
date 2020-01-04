/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.projectlogger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author amina
 */
public class PacketBuffer {
    private final ByteBuffer buffer;
    
    public PacketBuffer(String message) {
        int bufferLength = message.length() * 2 + 6;
        buffer = ByteBuffer.allocate(bufferLength).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(bufferLength);
        putString(message);
    }
    
    public PacketBuffer(byte[] bytes) {
        buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
    }   

    public int getInt() {
        return buffer.getInt();
    }
    
    public double getDouble() {
        return buffer.getDouble();
    }
    
    public String getUnicodeString() {
        StringBuilder builder = new StringBuilder();
        short letter = buffer.getShort(); 
        while(letter != 0) {
            builder.append((char) letter);
            letter = buffer.getShort();
        }
        return builder.toString(); 
    }
    
    public String getString() {
        StringBuilder builder = new StringBuilder();
        char letter = (char) buffer.get(); 
        while(letter != 0) {
            builder.append(letter);
            letter = (char) buffer.get();
        }
        return builder.toString();
    }
    
    public void putString(String message) {
        for(char letter: message.toCharArray()) {
            buffer.putShort((short) letter);
        }
        buffer.putShort((short) 0);
    }
    
    public byte[] array() {
        return buffer.array();
    }
    
}
