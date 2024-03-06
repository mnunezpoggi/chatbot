/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package xyz.kraftwork.chatbot;

import org.apache.commons.cli.CommandLine;

/**
 *
 * @author mnunez
 */
public interface CommandListener {
    public Object onCommand(ChatInfo info, CommandLine cmd);
}
