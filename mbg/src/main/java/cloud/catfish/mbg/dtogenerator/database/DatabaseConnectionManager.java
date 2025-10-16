package cloud.catfish.mbg.dtogenerator.database;

import cloud.catfish.mbg.dtogenerator.config.ConfigurationManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 数据库连接管理类
 * 负责管理数据库连接的创建和释放
 * 
 * @author Generated
 * @since 2.0
 */
public class DatabaseConnectionManager {
    
    private final ConfigurationManager configManager;
    
    /**
     * 构造函数
     * 
     * @param configManager 配置管理器
     */
    public DatabaseConnectionManager(ConfigurationManager configManager) {
        this.configManager = configManager;
    }
    
    /**
     * 创建数据库连接
     * 
     * @return 数据库连接
     * @throws SQLException 连接异常
     */
    public Connection createConnection() throws SQLException {
        try {
            // 加载数据库驱动
            Class.forName(configManager.getDriverClass());
            
            // 创建连接
            Connection connection = DriverManager.getConnection(
                configManager.getConnectionUrl(),
                configManager.getUsername(),
                configManager.getPassword()
            );
            
            System.out.println("🔗 数据库连接创建成功");
            return connection;
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("找不到数据库驱动: " + configManager.getDriverClass(), e);
        }
    }
    
    /**
     * 安全关闭数据库连接
     * 
     * @param connection 要关闭的连接
     */
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("🔒 数据库连接已关闭");
                }
            } catch (SQLException e) {
                System.err.println("关闭数据库连接时发生错误: " + e.getMessage());
            }
        }
    }
    
    /**
     * 测试数据库连接
     * 
     * @return 连接是否成功
     */
    public boolean testConnection() {
        Connection connection = null;
        try {
            connection = createConnection();
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            System.err.println("数据库连接测试失败: " + e.getMessage());
            return false;
        } finally {
            closeConnection(connection);
        }
    }
}