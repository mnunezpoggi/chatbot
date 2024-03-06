/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.kraftwork.chatbot.messagebridges;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import xyz.kraftwork.chatbot.ChatInfo;
import xyz.kraftwork.chatbot.Chatbot;
import xyz.kraftwork.chatbot.utils.LoggerFactory;
import xyz.kraftwork.chatbot.MessageListener;
import xyz.kraftwork.chatbot.RegistrationListener;
import xyz.kraftwork.chatbot.utils.ConfigurationHolder;

/**
 *
 * @author mnunez
 */
public abstract class MessageBridge {

    protected final Chatbot bot;
    protected final Logger logger;
    protected final ConfigurationHolder config;

    public enum Bridge {
        XMPP,
        IRC,
        ALL
    }

    public MessageBridge(Chatbot bot) {
        this.bot = bot;
        this.logger = LoggerFactory.buildLogger(this.getClass());
        this.config = ConfigurationHolder.getInstance();
    }

    public ArrayList verifyConfigs() {
        ArrayList<String> list = new ArrayList();
        for (String s : requiredConfigs()) {
            String value = config.get(s);
            if (value == null) {
                list.add(value);
            }
        }
        return list;
    }

    public boolean init() {
        List missing = verifyConfigs();
        if (!missing.isEmpty()) {
            logger.warning("missing! " + missing);
            return false;
        }
        return initBridge();
    }

    public abstract boolean initBridge();

    public abstract Object sendMessage(ChatInfo chatInfo);
    
    public abstract Object sendMessageAll(ChatInfo chatInfo);
    
    public abstract Object afterRegistration();

    public abstract String[] requiredConfigs();

    public abstract Bridge bridgeType();

}
