/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package xyz.kraftwork.chatbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import xyz.kraftwork.chatbot.messagebridges.IrcMessageBridge;
import xyz.kraftwork.chatbot.messagebridges.MessageBridge;
import xyz.kraftwork.chatbot.messagebridges.MessageBridge.Bridge;
import xyz.kraftwork.chatbot.messagebridges.XmppMessageBridge;

/**
 *
 * @author mnunez
 */
public class Chatbot implements MessageListener, CommandListener, RegistrationListener {

    private final ArrayList<MessageListener> MessageListeners;
    private final ArrayList<CommandListener> CommandListeners;
    private final ArrayList<RegistrationListener> RegistrationListeners;
    private final HashMap<Bridge, MessageBridge> bridges;
    private final HashMap<String, Command> commands;
    private final CommandLineParser parser;

    public Chatbot() {
        this.MessageListeners = new ArrayList();
        this.CommandListeners = new ArrayList();
        this.RegistrationListeners = new ArrayList();
        this.commands = new HashMap();
        this.bridges = new HashMap();
        this.parser = new DefaultParser();
        setBridges();
        setDefaultCommands();
    }

    public void sendMessageAll(String text) {
        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setMessage(text);
        for (MessageBridge mb : bridges.values()) {
            mb.sendMessageAll(chatInfo);
        }
    }

    public Object sendMessage(ChatInfo chatInfo) {
        // TODO: Filter bridges from 
        Bridge b = chatInfo.getBridge();
        if (b == Bridge.ALL) {
            return null;
        }
        bridges.get(b).sendMessage(chatInfo);
        return null;
    }

    @Override
    public Object onRegistration() {
        for (RegistrationListener l : RegistrationListeners){
            l.onRegistration();
        }
        return null;
    }
    
    @Override
    public Object onMessage(ChatInfo info) {
        String text = info.getMessage().trim();
        for (MessageListener l : MessageListeners) {
            l.onMessage(info);
        }
        String[] split = text.split(" ");
        if (commands.get(split[0]) != null) {
            Command command = commands.get(split[0]);
            try {
                CommandLine cmd = parser.parse(command, split);
                System.out.println("Found command " + command);
                info.setCommand(command);
                this.onCommand(info, cmd);
                for (CommandListener l : this.CommandListeners) {
                    l.onCommand(info, cmd);
                }

            } catch (ParseException ex) {
                info.setMessage(ex.getMessage());
                sendMessage(info);
                return null;
            }
        } else {
            System.out.println("Command not found");
            System.out.println(commands);
        }

        return null;
    }

    @Override
    public Object onCommand(ChatInfo info, CommandLine cmd) {
        System.out.println("================================");
        Command command = info.getCommand();
        switch (command.getName()) {
            case "test" -> {
                info.setMessage("Message received.");
                if (cmd.hasOption("v")) {
                    info.setMessage("Verbose message!");
                }
                this.sendMessage(info);
            }
            case "req" -> {
                info.setMessage(cmd.getOptionValue("name"));
                this.sendMessage(info);

            }
        }
        return null;
    }

    public void addMessageListener(MessageListener listener) {
        this.MessageListeners.add(listener);
    }

    public void addCommandListener(CommandListener listener) {
        this.CommandListeners.add(listener);
    }
    
    public void addRegistrationListener(RegistrationListener listener){
        this.RegistrationListeners.add(listener);
    }

    public void setCommands(Iterable<Command> specifiedCommands) {
        for (Command i : specifiedCommands) {
            this.commands.put(i.getName(), i);
        }
    }

    public void addCommand(Command command) {
        System.out.println("Adding command" + command);
        this.commands.put(command.getName(), command);
    }
    
    public void addCommandOptions(String name, boolean required, String shortOpt, String longOpt, boolean hasArg, String description){
        Command c = this.commands.get(name);
        if(c == null){
            c = new Command(name);
            addCommand(c);
        } else {
            c.addOption(shortOpt, longOpt, hasArg, description);
        }
        if(required){
            c.addRequiredOption(longOpt, longOpt, hasArg, description);
        } else {
            c.addOption(longOpt, longOpt, hasArg, description);
        }
    }

    public static void main(String[] args) {
        Chatbot bot = new Chatbot();
    }

    private void setDefaultCommands() {
        Command testCommand = new Command("test");
        testCommand.addOption("v", "verbose", false, "This is a verbose command");
        addCommand(testCommand);

        Command requiredCommand = new Command("req");
        requiredCommand.addRequiredOption("n", "name", true, "Required option name");
        addCommand(requiredCommand);
    }

    private void setBridges() {
        setBridge(IrcMessageBridge.class);
        setBridge(XmppMessageBridge.class);
    }

    private void setBridge(Class<? extends MessageBridge> b) {
        try {
            System.out.println("Loading " + b.getSimpleName());
            MessageBridge bridge = b.getConstructor(Chatbot.class).newInstance(this);
            boolean result = bridge.init();
            if (result) {
                this.bridges.put(bridge.bridgeType(), bridge);
            } else {
                System.out.println("Couldnt set");
            }
        } catch (Exception ex) {
            Logger.getLogger(Chatbot.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
