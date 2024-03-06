/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.kraftwork.chatbot;

import org.apache.commons.cli.Options;

/**
 *
 * @author mnunez
 */
public class Command extends Options{
        
    private final String name;
    
    public Command(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Command{ " + "name: " + name + " args: " + getOptions() + " }";
    }

}
