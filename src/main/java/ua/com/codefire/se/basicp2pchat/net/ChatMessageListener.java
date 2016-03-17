/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.se.basicp2pchat.net;

/**
 *
 * @author 1
 */
public interface ChatMessageListener {
    
    public void incomingMessage(String address, String message);
}
