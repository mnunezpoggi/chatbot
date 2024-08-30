/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.kraftwork.chatbot;

import xyz.kraftwork.chatbot.messagebridges.MessageBridge.Bridge;

/**
 *
 * @author mnunez
 */
public class ChatInfo {
    private Bridge bridge;
    private String message;
    private String from;
    private String user; 

    private Command command;
        
    public ChatInfo(){        
    }

    public ChatInfo(Bridge bridge, String message, String from, String user) {
        this.bridge = bridge;
        this.message = message;
        this.from = from;
        this.user = user;
    }

    public Bridge getBridge() {
        return bridge;
    }

    public String getMessage() {
        return message;
    }

    public String getFrom() {
        return from;
    }
    
    public void setFrom(String from){
        this.from = from;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
    @Override
    public String toString() {
        return "ChatInfo{" + "bridge=" + bridge + ", message=" + message + ", from=" + from + ", command=" + command + '}';
    }
    
    
}
