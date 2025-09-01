package cloud.catfish.common.util;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Enumeration;

/**
 * Twitter Snowflake ID Generator Implementation
 * 
 * Generates unique IDs using Twitter's Snowflake algorithm.
 * 
 * The IDs are 64 bits, composed of:
 * - 41 bits for timestamp (milliseconds since custom epoch)
 * - 10 bits for machine/node ID (allows for 1024 nodes)
 * - 12 bits for sequence number (allows for 4096 IDs per millisecond per node)
 * 
 * This implementation uses January 1, 2015 as the custom epoch.
 */
public class Snowflake {

    // Custom epoch (January 1, 2015 Midnight UTC = 2015-01-01T00:00:00Z)
    private static final long CUSTOM_EPOCH = 1420070400000L;

    private final long nodeId;
    private volatile long lastTimestamp = -1L;
    private volatile long sequence = 0L;

    // Bit lengths for different parts of the ID
    private static final int NODE_ID_BITS = 10;
    private static final int SEQUENCE_BITS = 12;

    // Maximum values for node ID and sequence
    private static final long MAX_NODE_ID = (1L << NODE_ID_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    // Bit shifts for different parts of the ID
    private static final int TIMESTAMP_SHIFT = NODE_ID_BITS + SEQUENCE_BITS;
    private static final int NODE_ID_SHIFT = SEQUENCE_BITS;

    /**
     * Create a Snowflake ID generator with a randomly generated node ID
     */
    public Snowflake() {
        this.nodeId = createNodeId();
    }

    /**
     * Create a Snowflake ID generator with the specified node ID
     *
     * @param nodeId The node ID (0-1023)
     */
    public Snowflake(long nodeId) {
        if (nodeId < 0 || nodeId > MAX_NODE_ID) {
            throw new IllegalArgumentException(String.format("NodeId must be between 0 and %d", MAX_NODE_ID));
        }
        this.nodeId = nodeId;
    }

    /**
     * Generate a new Snowflake ID
     *
     * @return A new Snowflake ID
     */
    public synchronized long nextId() {
        long currentTimestamp = timestamp();

        // Handle clock moving backwards
        if (currentTimestamp < lastTimestamp) {
            throw new IllegalStateException("Clock moved backwards. Refusing to generate ID for " +
                    (lastTimestamp - currentTimestamp) + " milliseconds");
        }

        // If we're still in the same millisecond as the last ID generation,
        // increment the sequence number
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // If we've run out of sequences for this millisecond, wait until the next millisecond
            if (sequence == 0) {
                currentTimestamp = waitForNextMillis(lastTimestamp);
            }
        } else {
            // We're in a new millisecond, reset the sequence
            sequence = 0;
        }

        lastTimestamp = currentTimestamp;

        // Combine the different parts to form the final ID
        return ((currentTimestamp - CUSTOM_EPOCH) << TIMESTAMP_SHIFT) |
                (nodeId << NODE_ID_SHIFT) |
                sequence;
    }

    /**
     * Wait until the next millisecond
     *
     * @param lastTimestamp The last timestamp
     * @return The current timestamp
     */
    private long waitForNextMillis(long lastTimestamp) {
        long currentTimestamp = timestamp();
        while (currentTimestamp <= lastTimestamp) {
            currentTimestamp = timestamp();
        }
        return currentTimestamp;
    }

    /**
     * Get the current timestamp in milliseconds
     *
     * @return The current timestamp
     */
    private long timestamp() {
        return Instant.now().toEpochMilli();
    }

    /**
     * Create a node ID based on the MAC address or a random value if the MAC address is not available
     *
     * @return A node ID
     */
    private long createNodeId() {
        long nodeId;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    for (byte macByte : mac) {
                        sb.append(String.format("%02X", macByte));
                    }
                }
            }
            nodeId = sb.toString().hashCode();
        } catch (Exception ex) {
            nodeId = (new SecureRandom().nextInt());
        }
        nodeId = nodeId & MAX_NODE_ID;
        return Math.abs(nodeId);
    }

    /**
     * Extract the timestamp from a Snowflake ID
     *
     * @param id The Snowflake ID
     * @return The timestamp in milliseconds since the custom epoch
     */
    public long getTimestamp(long id) {
        return ((id >> TIMESTAMP_SHIFT) + CUSTOM_EPOCH);
    }

    /**
     * Extract the node ID from a Snowflake ID
     *
     * @param id The Snowflake ID
     * @return The node ID
     */
    public long getNodeId(long id) {
        return (id >> NODE_ID_SHIFT) & MAX_NODE_ID;
    }

    /**
     * Extract the sequence number from a Snowflake ID
     *
     * @param id The Snowflake ID
     * @return The sequence number
     */
    public long getSequence(long id) {
        return id & MAX_SEQUENCE;
    }
}