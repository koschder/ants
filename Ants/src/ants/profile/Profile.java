package ants.profile;

import java.io.InputStream;
import java.util.Properties;

import logging.Logger;
import logging.LoggerFactory;
import ants.LogCategory;

/**
 * This class represents a profile for the bot. It contains all parameters that can be configured to tweak the bot's
 * behavior.
 * 
 * @author S. Käser, L. Kuster
 * 
 */
public class Profile {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.SETUP);

    private enum Key {
        DEFAULT_ALLOCATION_GATHER_FOOD("defaultAllocation.gatherFood"),
        DEFAULT_ALLOCATION_EXPLORE("defaultAllocation.explore"),
        DEFAULT_ALLOCATION_ATTACK_HILLS("defaultAllocation.attackHills"),
        DEFAULT_ALLOCATION_DEFEND_HILLS("defaultAllocation.defendHills");
        private String key;

        private Key(String key) {
            this.key = key;
        }
    }

    private static Properties defaultProperties = new Properties();
    private Properties properties;

    static {
        defaultProperties.setProperty(Key.DEFAULT_ALLOCATION_GATHER_FOOD.key, "25");
        defaultProperties.setProperty(Key.DEFAULT_ALLOCATION_EXPLORE.key, "25");
        defaultProperties.setProperty(Key.DEFAULT_ALLOCATION_ATTACK_HILLS.key, "25");
        defaultProperties.setProperty(Key.DEFAULT_ALLOCATION_DEFEND_HILLS.key, "25");
    }

    /*
     * public getters for the profile parameters
     */

    public int getDefaultAllocation_GatherFood() {
        return getInteger(Key.DEFAULT_ALLOCATION_GATHER_FOOD);
    }

    public int getDefaultAllocation_Explore() {
        return getInteger(Key.DEFAULT_ALLOCATION_EXPLORE);
    }

    public int getDefaultAllocation_AttackHills() {
        return getInteger(Key.DEFAULT_ALLOCATION_ATTACK_HILLS);
    }

    public int getDefaultAllocation_DefendHills() {
        return getInteger(Key.DEFAULT_ALLOCATION_DEFEND_HILLS);
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
        validate();
    }

    private void validate() {
        int totalDefaultAllocation = getDefaultAllocation_AttackHills() + getDefaultAllocation_DefendHills()
                + getDefaultAllocation_Explore() + getDefaultAllocation_GatherFood();
        if (totalDefaultAllocation != 100)
            throw new InvalidProfileConfigurationException("Default resource allocations do not add up to 100");
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

    private class InvalidProfileConfigurationException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public InvalidProfileConfigurationException(String message) {
            super(message);
        }
    }
}
