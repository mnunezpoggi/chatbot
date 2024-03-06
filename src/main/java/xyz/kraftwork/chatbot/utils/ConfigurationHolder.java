/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xyz.kraftwork.chatbot.utils;

import java.io.File;
import java.util.Collection;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.EnvironmentConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.UnionCombiner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import java.util.logging.Logger;
import org.apache.commons.configuration2.JSONConfiguration;

/**
 *
 *
 */
public class ConfigurationHolder {

    private static final String DEFAULT_CONFIG_PATH = "config/";
    private static final String XML_EXTENSION = "xml";
    private static final String JSON_EXTENSION = "json";
    private static final String PROPERTIES_EXTENSION = "properties";
    private static final String[] CONFIG_EXTENSIONS = new String[]{PROPERTIES_EXTENSION, XML_EXTENSION};

    private static final Logger logger = LoggerFactory.buildLogger(ConfigurationHolder.class);

    private static ConfigurationHolder instance;

    public static final ConfigurationHolder getInstance() {
        return getInstance(DEFAULT_CONFIG_PATH);
    }

    public static final ConfigurationHolder getInstance(String path) {
        if (instance == null) {
            logger.info("Create new from " + path);
            instance = new ConfigurationHolder(path);
        } else {
            if (!new File(path).getAbsolutePath().equals(instance.ConfigPath.getAbsolutePath())) {
                logger.warning("Already instantiated, ignoring " + path);
            }
        }
        return instance;
    }

    private final CompositeConfiguration configuration;
    private final File ConfigPath;

    private ConfigurationHolder(String path) {
        configuration = new CompositeConfiguration();
        File f = new File(path);
        if (!f.isDirectory()) {
            logger.severe(path + " is not a directory, exiting");
            System.exit(0);
        }
        ConfigPath = f;
        addSystemConfig();
        addEnvironmentConfig();
        try {
            readConfigFiles();
        } catch (ConfigurationException ex) {
            logger.severe("Could not read config files");
            logger.severe("Message: " + ex.getLocalizedMessage());
            System.exit(0);
        }

    }

    private void readConfigFiles() throws ConfigurationException {
        CombinedConfiguration combined_configuration = new CombinedConfiguration(new UnionCombiner());
        Collection<File> files = FileUtils.listFiles(ConfigPath, CONFIG_EXTENSIONS, true);
        Configurations configs = new Configurations();
        for (File file : files) {
            logger.info("Read " + file.getName());
            String extension = FilenameUtils.getExtension(file.getAbsolutePath());
            Configuration config = null;
            switch (extension) {
                case XML_EXTENSION:
                    config = configs.xml(file);
                    break;
                case PROPERTIES_EXTENSION:
                    config = configs.properties(file);
                    break;
                case JSON_EXTENSION:
                    configuration.addConfiguration(new JSONConfiguration());
                    break;
            }
            combined_configuration.addConfiguration(config);
        }
        configuration.addConfiguration(combined_configuration);
    }

    private void addSystemConfig() {
        configuration.addConfiguration(new SystemConfiguration());
        logger.info("Added System configuration");
    }

    private void addEnvironmentConfig() {
        configuration.addConfiguration(new EnvironmentConfiguration());
        logger.info("Added Environment configuration");
    }

    public String get(String key) {
        logger.info("Querying " + key);
        return configuration.getString(key);
    }


}
