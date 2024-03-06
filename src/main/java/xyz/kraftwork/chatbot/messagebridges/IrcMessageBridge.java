/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.kraftwork.chatbot.messagebridges;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.MotdEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.managers.BackgroundListenerManager;
import xyz.kraftwork.chatbot.ChatInfo;
import xyz.kraftwork.chatbot.Chatbot;
import xyz.kraftwork.chatbot.MessageListener;
import static xyz.kraftwork.chatbot.messagebridges.MessageBridge.Bridge.IRC;

/**
 *
 * @author mnunez
 */
public class IrcMessageBridge extends MessageBridge {

    private static final String[] REQUIRED_CONFIGS = new String[]{"IRC_HOST", "IRC_NAME", "IRC_CHANNEL"};

    private PircBotX ircbot;

    public IrcMessageBridge(Chatbot bot) {
        super(bot);
    }

    @Override
    public Object sendMessage(ChatInfo info) {
        this.ircbot.sendIRC().message(info.getFrom(), info.getMessage());
        return null;
    }
    
    @Override
    public Object sendMessageAll(ChatInfo chatInfo) {
        chatInfo.setFrom(config.get("IRC_CHANNEL"));
        return sendMessage(chatInfo);
    }

    @Override
    public String[] requiredConfigs() {
        return REQUIRED_CONFIGS;
    }
    
    @Override
    public Bridge bridgeType() {
        return Bridge.IRC;
    }

    @Override
    public boolean initBridge() {
        ListenerAdapter adapter = new ListenerAdapter() {
            
            private void setChatInfo(String from, String message){
                 ChatInfo info = new ChatInfo(IRC, message, from);
                 bot.onMessage(info);
            }
            
            @Override
            public void onMessage(MessageEvent event) {
                setChatInfo(event.getChannelSource(), event.getMessage());
            }
            
            @Override
            public void onPrivateMessage(PrivateMessageEvent event){
                setChatInfo(event.getUser().getNick(), event.getMessage());
            }
            
            @Override
            public void onMotd(MotdEvent event){
                System.out.println("++++++++++++++++++++ ONMOTD");
                afterRegistration();
            }
        };
        BackgroundListenerManager myListenerManager = new BackgroundListenerManager();
        myListenerManager.addListener(adapter, true);
        Configuration config = new Configuration.Builder()
                .setName(this.config.get("IRC_NAME")) //Nick of the bot. CHANGE IN YOUR CODE
                //.setLogin("javabot") //Login part of hostmask, eg name:login@host
                .setAutoNickChange(true) //Automatically change nick when the current one is in use
                .addAutoJoinChannel(this.config.get("IRC_CHANNEL")) //Join #pircbotx channel on connect
                .setListenerManager(myListenerManager)
                .addServer(this.config.get("IRC_HOST"))
                //.addListener(adapter)
                .buildConfiguration(); //Create an immutable configuration from this builder

        this.ircbot = new PircBotX(config);
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    ircbot.startBot();
                } catch (Exception ex) {
                    logger.warning(ex.getMessage());
                    ircbot = null;
                }

            }
        };
        t.start();
        try {
            Thread.sleep(1000);} catch (InterruptedException ex) {}
        if(ircbot == null){
            System.out.println("COULDN'T connect");
            return false;
        }
        System.out.println("configured irc");
        return true;
    }

    @Override
    public Object afterRegistration() {
        return this.bot.onRegistration();
    }



}
