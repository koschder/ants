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
        /**
         * Default allocation of resources for gathering food.
         */
        DEFAULT_ALLOCATION_GATHER_FOOD("defaultAllocation.gatherFood"),
        /**
         * Default allocation of resources for exploration.
         */
        DEFAULT_ALLOCATION_EXPLORE("defaultAllocation.explore"),
        /**
         * Default allocation of resources for attacking hills.
         */
        DEFAULT_ALLOCATION_ATTACK_HILLS("defaultAllocation.attackHills"),
        /**
         * Default allocation of resources for defending hills.
         */
        DEFAULT_ALLOCATION_DEFEND_HILLS("defaultAllocation.defendHills"),
        /**
         * Turn at which we start defending hills even if no enemy is nearby.
         */
        DEFEND_HILLS_START_TURN("defendHills.startTurn"),
        /**
         * Minimum control radius for hill defense (measured in tiles).
         */
        DEFEND_HILLS_MIN_CONTROL_RADIUS("defendHills.minControlRadius"),
        /**
         * Threshold at which exploration is granted additional resources, expressed in percent of invisible tiles.
         */
        EXPLORE_FORCE_THRESHOLD_PERCENT("explore.forceThresholdPercent"),
        /**
         * Determines how much exploration is boosted when the threshold is met. Expressed as a fraction of 1, default
         * is 0.25.
         */
        EXPLORE_FORCE_GAIN("explore.forceGain");
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
        defaultProperties.setProperty(Key.DEFEND_HILLS_START_TURN.key, "30");
        defaultProperties.setProperty(Key.DEFEND_HILLS_MIN_CONTROL_RADIUS.key, "8");
        defaultProperties.setProperty(Key.EXPLORE_FORCE_THRESHOLD_PERCENT.key, "80");
        defaultProperties.setProperty(Key.EXPLORE_FORCE_GAIN.key, "0.25");
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

    public int getDefendHills_StartTurn() {
        return getInteger(Key.DEFEND_HILLS_START_TURN);
    }

    public int getDefendHills_MinControlRadius() {
        return getInteger(Key.DEFEND_HILLS_MIN_CONTROL_RADIUS);
    }

    public int getExplore_ForceThresholdPercent() {
        return getInteger(Key.EXPLORE_FORCE_THRESHOLD_PERCENT);
    }

    public float getExplore_ForceGain() {
        return getFloat(Key.EXPLORE_FORCE_GAIN);
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
        LOGGER.info("Configured profile (%s): %s \n", profile == null ? "DEFAULT" : profile.toUpperCase(),
                getPropertiesAsString());
        validate();
    }

    private void validate() {
        int totalDefaultAllocation = getDefaultAllocation_AttackHills() + getDefaultAllocation_DefendHills()
                + getDefaultAllocation_Explore() + getDefaultAllocation_GatherFood();
        if (totalDefaultAllocation != 100)
            throw new InvalidProfileConfigurationException("Default resource allocations do not add up to 100");
        if (getExplore_ForceThresholdPercent() > 100 || getExplore_ForceThresholdPercent() < 0)
            throw new InvalidProfileConfigurationException("explore.forceThresholdPercent must be between 0 and 100");
        if (getExplore_ForceGain() > 1.0f || getExplore_ForceGain() < 0.0f)
            throw new InvalidProfileConfigurationException("explore.forceGain must be between 0.0 and 1.0");
    }

    private int getInteger(Key key) {
        return Integer.valueOf(properties.getProperty(key.key));
    }

    private float getFloat(Key key) {
        return Float.valueOf(properties.getProperty(key.key));
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
