package cloud.catfish.common.util;

/**
 * Snowflake ID Generator
 * A distributed unique ID generator inspired by Twitter's Snowflake.
 * 
 * Structure:
 * - 1 bit: Always 0 (reserved for sign bit)
 * - 41 bits: Timestamp (milliseconds since epoch or custom epoch)
 * - 10 bits: Machine ID (configurable, identifies the data center and machine)
 * - 12 bits: Sequence number (incremented for IDs generated in the same millisecond)
 */
public class SnowflakeIdGenerator {
    // Constants
    private static final long EPOCH = 1609459200000L; // Custom epoch (2021-01-01)
    private static final long MACHINE_ID_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;
    
    private static final long MAX_MACHINE_ID = ~(-1L << MACHINE_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    
    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;
    
    // Instance variables
    private final long machineId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;
    
    // Singleton instance
    private static volatile SnowflakeIdGenerator instance;
    
    /**
     * Constructor with machine ID
     * 
     * @param machineId ID of the machine (0-1023)
     */
    private SnowflakeIdGenerator(long machineId) {
        if (machineId < 0 || machineId > MAX_MACHINE_ID) {
            throw new IllegalArgumentException("Machine ID must be between 0 and " + MAX_MACHINE_ID);
        }
        this.machineId = machineId;
    }
    
    /**
     * Get singleton instance with default machine ID (0)
     * 
     * @return SnowflakeIdGenerator instance
     */
    public static SnowflakeIdGenerator getInstance() {
        return getInstance(0);
    }
    
    /**
     * Get singleton instance with specified machine ID
     * 
     * @param machineId ID of the machine (0-1023)
     * @return SnowflakeIdGenerator instance
     */
    public static SnowflakeIdGenerator getInstance(long machineId) {
        if (instance == null) {
            synchronized (SnowflakeIdGenerator.class) {
                if (instance == null) {
                    instance = new SnowflakeIdGenerator(machineId);
                }
            }
        }
        return instance;
    }
    
    /**
     * Generate a unique ID
     * 
     * @return Unique ID as a long
     */
    public synchronized long nextId() {
        long currentTimestamp = getCurrentTimestamp();
        
        // Handle clock moving backwards
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate ID for " + 
                    (lastTimestamp - currentTimestamp) + " milliseconds.");
        }
        
        // Handle multiple requests within the same millisecond
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // Sequence overflow in the same millisecond
            if (sequence == 0) {
                // Wait until next millisecond
                currentTimestamp = waitForNextMillis(lastTimestamp);
            }
        } else {
            // Reset sequence for new millisecond
            sequence = 0L;
        }
        
        lastTimestamp = currentTimestamp;
        
        // Construct the ID from components
        return ((currentTimestamp - EPOCH) << TIMESTAMP_SHIFT) | 
               (machineId << MACHINE_ID_SHIFT) | 
               sequence;
    }
    
    /**
     * Wait until the next millisecond
     * 
     * @param lastTimestamp The last timestamp that was used
     * @return The current timestamp after waiting
     */
    private long waitForNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }
    
    /**
     * Get the current timestamp in milliseconds
     * 
     * @return Current timestamp
     */
    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
}