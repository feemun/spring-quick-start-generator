package cloud.catfish.common.util;

/**
 * Snowflake ID Utility
 * A wrapper around the Twitter Snowflake algorithm implementation that provides
 * a simplified interface for generating Snowflake IDs.
 */
public class SnowflakeUtil {
    
    private static final int DEFAULT_NODE_ID = 1;
    private static volatile Snowflake defaultGenerator = null;
    
    private SnowflakeUtil() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Get a Snowflake ID generator with the default node ID (1)
     * 
     * @return A Snowflake ID generator
     */
    public static Snowflake getGenerator() {
        return getGenerator(DEFAULT_NODE_ID);
    }
    
    /**
     * Get a Snowflake ID generator with the specified node ID
     * 
     * @param nodeId The node ID (0-1023)
     * @return A Snowflake ID generator
     */
    public static Snowflake getGenerator(int nodeId) {
        if (nodeId == DEFAULT_NODE_ID && defaultGenerator != null) {
            return defaultGenerator;
        }
        
        Snowflake generator = new Snowflake(nodeId);
        
        if (nodeId == DEFAULT_NODE_ID) {
            defaultGenerator = generator;
        }
        
        return generator;
    }
    
    /**
     * Generate a new Snowflake ID using the default node ID (1)
     * 
     * @return A new Snowflake ID
     */
    public static long nextId() {
        return nextId(DEFAULT_NODE_ID);
    }
    
    /**
     * Generate a new Snowflake ID using the specified node ID
     * 
     * @param nodeId The node ID (0-1023)
     * @return A new Snowflake ID
     */
    public static long nextId(int nodeId) {
        return getGenerator(nodeId).nextId();
    }
}