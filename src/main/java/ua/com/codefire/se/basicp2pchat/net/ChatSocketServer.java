/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.se.basicp2pchat.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 1
 */
public class ChatSocketServer extends SocketServer {
    
    private List<ChatMessageListener> listeners;

    public ChatSocketServer(int port) {
        super(port);
        this.listeners = new ArrayList<>();
    }
    
    public ChatSocketServer addListener(ChatMessageListener listener) {
        listeners.add(listener);
        return this;
    }

    @Override
    protected void incomingSocket(Socket socket) throws IOException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        
        String address = dis.readUTF();
        String message = dis.readUTF();
        
        for (ChatMessageListener listener : listeners) {
            listener.incomingMessage(address, message);
        }
        
        dos.writeUTF("OK");
        dos.flush();
        
        socket.close();
    }
    
}
