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
 * @author kases1, kustl1
 * 
 */
public class Profile {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCategory.SETUP);

    private enum Key {
        /**
         * Default allocation of resources for gathering food. Value in percent of available resources.
         */
        DEFAULT_ALLOCATION_GATHER_FOOD("defaultAllocation.gatherFood"),
        /**
         * Default allocation of resources for exploration. Value in percent of available resources.
         */
        DEFAULT_ALLOCATION_EXPLORE("defaultAllocation.explore"),
        /**
         * Default allocation of resources for attacking hills. Value in percent of available resources.
         */
        DEFAULT_ALLOCATION_ATTACK_HILLS("defaultAllocation.attackHills"),
        /**
         * Default allocation of resources for defending hills. Value in percent of available resources.
         */
        DEFAULT_ALLOCATION_DEFEND_HILLS("defaultAllocation.defendHills"),
        /**
         * Turn at which we start defending hills even if no enemy is nearby.
         */
        DEFEND_HILLS_START_TURN("defendHills.startTurn"),
        /**
         * Minimum control radius for hill defense (radius squared).
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
        EXPLORE_FORCE_GAIN("explore.forceGain"),
        /**
         * Determines how much exploration is boosted when we have a dominant position. Value in percent of available
         * resources.
         */
        EXPLORE_DOMINANT_POSITION_BOOST("explore.dominantPositionBoost"),
        /**
         * Determines how much attacking hills is boosted when we have a dominant position. Value in percent of
         * available resources.
         */
        ATTACK_HILLS_DOMINANT_POSITION_BOOST("attackHills.dominantPositionBoost"),
        /**
         * After half-time, attacking hills is emphasized. This determines how much. Value in percent of available
         * resources.
         */
        ATTACK_HILLS_HALFTIME_BOOST("attackHills.halfTimeBoost");
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
        defaultProperties.setProperty(Key.EXPLORE_DOMINANT_POSITION_BOOST.key, "5");
        defaultProperties.setProperty(Key.ATTACK_HILLS_DOMINANT_POSITION_BOOST.key, "2");
        defaultProperties.setProperty(Key.ATTACK_HILLS_HALFTIME_BOOST.key, "20");
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

    public int getExplore_DominantPositionBoost() {
        return getInteger(Key.EXPLORE_DOMINANT_POSITION_BOOST);
    }

    public int getAttackHills_HalfTimeBoost() {
        return getInteger(Key.ATTACK_HILLS_HALFTIME_BOOST);
    }

    public int getAttackHills_DominantPositionBoost() {
        return getInteger(Key.ATTACK_HILLS_DOMINANT_POSITION_BOOST);
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
        LOGGER.info("Configured profile (%s):\n%s ", profile == null ? "DEFAULT" : profile.toUpperCase(),
                getPropertiesAsString());
        validate();
    }

    private void validate() {
        // make sure there are default values set for every parameter
        for (Key key : Key.values()) {
            if (defaultProperties.get(key.key) == null)
                throw new InvalidProfileConfigurationException("No default property set for key " + key);
        }
        // make sure the default resource allocation values add up to 100
        int totalDefaultAllocation = getDefaultAllocation_AttackHills() + getDefaultAllocation_DefendHills()
                + getDefaultAllocation_Explore() + getDefaultAllocation_GatherFood();
        if (totalDefaultAllocation != 100)
            throw new InvalidProfileConfigurationException("Default resource allocations do not add up to 100");
        // values that need to be between 0 and 100
        checkValidPercentRange(Key.EXPLORE_FORCE_THRESHOLD_PERCENT, getExplore_ForceThresholdPercent());
        checkValidPercentRange(Key.ATTACK_HILLS_HALFTIME_BOOST, getAttackHills_HalfTimeBoost());
        checkValidPercentRange(Key.EXPLORE_DOMINANT_POSITION_BOOST, getExplore_DominantPositionBoost());
        checkValidPercentRange(Key.ATTACK_HILLS_DOMINANT_POSITION_BOOST, getAttackHills_DominantPositionBoost());
        // values that need to be between 0 and 1
        checkValidFraction(Key.EXPLORE_FORCE_GAIN, getExplore_ForceGain());
    }

    private int getInteger(Key key) {
        return Integer.valueOf(properties.getProperty(key.key));
    }

    private float getFloat(Key key) {
        return Float.valueOf(properties.getProperty(key.key));
    }

    private void checkValidPercentRange(Key key, int value) {
        if (value > 100 || value < 0)
            throw new InvalidProfileConfigurationException(key.key + " must be between 0 and 100");
    }

    private void checkValidFraction(Key key, float value) {
        if (value > 1.0f || value < 0.0f)
            throw new InvalidProfileConfigurationException(key.key + " must be between 0.0 and 1.0");
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
