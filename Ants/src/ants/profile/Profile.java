package ants.profile;

import java.io.InputStream;
import java.util.Properties;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;

public class Profile {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.SETUP);

    private enum Key {
        DEFAULT_ALLOCATION_GATHER_FOOD("defaultAllocation.gatherFood");
        private String key;

        private Key(String key) {
            this.key = key;
        }
    }

    private static Properties defaultProperties = new Properties();
    private Properties properties;
    static {
        defaultProperties.setProperty(Key.DEFAULT_ALLOCATION_GATHER_FOOD.key, "25");
    }

    public Profile(String profile) {
        properties = new Properties(defaultProperties);
        if (profile != null) {
            try {
                final InputStream propFileStream = getClass().getResourceAsStream("/" + profile + ".properties");
                if (propFileStream != null) {
                    properties.load(propFileStream);
                    LOGGER.info("Loaded profile %s", profile.toUpperCase());
                } else {
                    LOGGER.error("Could not load properties for profile %s, running with default profile", profile);
                }

            } catch (Exception e) {
                LOGGER.error("Could not load properties for profile %s, running with default profile", profile);
            }
        }
        LOGGER.info("Configured profile (%s): %s", profile == null ? "DEFAULT" : profile.toUpperCase(),
                getPropertiesAsString());
    }

    public int getDefaultAllocation_GatherFood() {
        return getInteger(Key.DEFAULT_ALLOCATION_GATHER_FOOD);
    }

    private int getInteger(Key key) {
        return Integer.valueOf(properties.getProperty(key.key));
    }

    private String getPropertiesAsString() {
        StringBuilder sb = new StringBuilder();
        for (Key key : Key.values()) {
            sb.append(key.key).append("=").append(properties.getProperty(key.key)).append("\n");
        }
        return sb.toString();
    }
}
