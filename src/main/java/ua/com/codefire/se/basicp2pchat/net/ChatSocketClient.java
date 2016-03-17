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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 1
 */
public class ChatSocketClient {

    private final String address;
    private final int port;

    public ChatSocketClient(String address, int port) {
        this.address = address;
        this.port = port;
    }

    /**
     * 
     * @param from
     * @param message 
     */
    public void sendMessage(String from, String message) {
        new Thread(() -> {
            try (Socket socket = new Socket(address, port)) {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                dos.writeUTF(from);
                dos.writeUTF(message);
                dos.flush();

                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(ChatSocketClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();
    }

}
