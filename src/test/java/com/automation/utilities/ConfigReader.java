package com.automation.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ConfigReader - Reads configuration from properties file
 */
public class ConfigReader {

    private static Properties properties;
    private static final String CONFIG_PATH = "src/test/resources/config.properties";

    static {
        loadProperties();
    }

    /**
     * Load properties from config file
     */
    private static void loadProperties() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            properties.load(fis);
            System.out.println("Configuration loaded from: " + CONFIG_PATH);
        } catch (IOException e) {
            System.out.println("Failed to load config.properties: " + e.getMessage());
            throw new RuntimeException("Could not load configuration file: " + CONFIG_PATH, e);
        }
    }

    /**
     * Get property value by key
     */
    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            System.out.println("Property not found: " + key);
        }
        return value;
    }

    /**
     * Get property with default value
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Get base URL
     */
    public static String getBaseUrl() {
        return getProperty("baseUrl");
    }

    /**
     * Get default timeout
     */
    public static int getDefaultTimeout() {
        return Integer.parseInt(getProperty("defaultTimeout", "10"));
    }

    /**
     * Get browser name
     */
    public static String getBrowser() {
        return getProperty("browser", "chrome");
    }
}



