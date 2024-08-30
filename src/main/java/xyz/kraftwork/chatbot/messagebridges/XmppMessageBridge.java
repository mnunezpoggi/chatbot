/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.kraftwork.chatbot.messagebridges;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import xyz.kraftwork.chatbot.ChatInfo;
import xyz.kraftwork.chatbot.Chatbot;
import xyz.kraftwork.chatbot.MessageListener;
import static xyz.kraftwork.chatbot.messagebridges.MessageBridge.Bridge.*;

/**
 *
 * @author mnunez
 */
public class XmppMessageBridge extends MessageBridge implements IncomingChatMessageListener {

    private static final String[] REQUIRED_CONFIGS = new String[]{"XMPP_USER", "XMPP_PASSWORD", "XMPP_HOST", "XMPP_DOMAIN"};
    private static final String ADMIN_CONFIG = "XMPP_ADMINS";

    private ChatManager manager;
    private XMPPTCPConnection connection;

    public XmppMessageBridge(Chatbot bot) {
        super(bot);
    }

    @Override
    public Object sendMessageAll(ChatInfo chatInfo) {
        Collection<RosterEntry> entries = Roster.getInstanceFor(connection).getEntries();
        for (RosterEntry e : entries) {
            chatInfo.setFrom(e.getJid().asBareJid().toString());
            sendMessage(chatInfo);
        }
        return null;
    }

    @Override
    public Object afterRegistration() {
        return null;
    }

    @Override
    public Object sendMessage(ChatInfo text) {
        try {

            EntityBareJid jid = JidCreate.entityBareFrom(text.getFrom());
            Chat chat = manager.chatWith(jid);
            chat.send(text.getMessage());

        } catch (XmppStringprepException ex) {
            this.logger.log(Level.SEVERE, null, ex);
        } catch (SmackException.NotConnectedException ex) {
            Logger.getLogger(XmppMessageBridge.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(XmppMessageBridge.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
        System.out.println(message);
        System.out.println(message.getBody());
        ChatInfo info = new ChatInfo(XMPP, message.getBody(), from.asEntityBareJidString(), from.asEntityBareJidString());
        this.bot.onMessage(info);
    }

    @Override
    public String[] requiredConfigs() {
        return REQUIRED_CONFIGS;
    }
    
    @Override
    public String adminConfig(){
        return ADMIN_CONFIG;
    }

    @Override
    public boolean initBridge() {
        try {
            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword(this.config.get("XMPP_USER"), this.config.get("XMPP_PASSWORD"))
                    .setXmppDomain(this.config.get("XMPP_DOMAIN"))
                    .setHost(this.config.get("XMPP_HOST"))
                    .setHostnameVerifier((hostname, session) -> true)
                    .build();
            this.connection = new XMPPTCPConnection(config);
            this.connection.connect(); //Establishes a connection to the server
            this.connection.login(); //Logs in
            this.manager = ChatManager.getInstanceFor(connection);
            this.manager.addIncomingListener(this);
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    logger.info("Disconnecting");
                    connection.disconnect();
                }
            });
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public Bridge bridgeType() {
        return Bridge.XMPP;
    }

    @Override
    public boolean checkMessage(Object message) {
        return true;
    }

}
