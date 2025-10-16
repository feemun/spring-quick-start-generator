package cloud.catfish.mbg.dtogenerator.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置管理类
 * 负责加载和管理DTO生成器的配置信息
 * 
 * @author Generated
 * @since 2.0
 */
public class ConfigurationManager {
    
    private static final String DEFAULT_PROPERTIES_FILE = 
        "C:\\Users\\feemu\\Documents\\GitHub\\spring-quick-start-generator\\mbg\\src\\main\\resources\\complex-dto-generator.properties";
    
    private Properties properties;
    private String propertiesFile;
    
    /**
     * 使用默认配置文件路径构造
     */
    public ConfigurationManager() {
        this(DEFAULT_PROPERTIES_FILE);
    }
    
    /**
     * 使用指定配置文件路径构造
     * 
     * @param propertiesFile 配置文件路径
     */
    public ConfigurationManager(String propertiesFile) {
        this.propertiesFile = propertiesFile;
        loadConfiguration();
    }
    
    /**
     * 加载配置文件
     */
    public void loadConfiguration() {
        properties = new Properties();
        try (InputStream input = new FileInputStream(propertiesFile)) {
            properties.load(input);
            System.out.println("📋 配置文件加载成功: " + propertiesFile);
        } catch (IOException e) {
            throw new RuntimeException("无法加载配置文件: " + propertiesFile, e);
        }
    }
    
    /**
     * 获取数据库驱动类
     */
    public String getDriverClass() {
        return getRequiredProperty("jdbc.driverClass");
    }
    
    /**
     * 获取数据库连接URL
     */
    public String getConnectionUrl() {
        return getRequiredProperty("jdbc.connectionURL");
    }
    
    /**
     * 获取数据库用户名
     */
    public String getUsername() {
        return getRequiredProperty("jdbc.userId");
    }
    
    /**
     * 获取数据库密码
     */
    public String getPassword() {
        return getRequiredProperty("jdbc.password");
    }
    
    /**
     * 获取SQL查询语句
     */
    public String getSql() {
        return getRequiredProperty("dto.sql");
    }
    
    /**
     * 获取DTO包名
     */
    public String getPackageName() {
        return getRequiredProperty("dto.package.name");
    }
    
    /**
     * 获取DTO类名
     */
    public String getClassName() {
        return getRequiredProperty("dto.name");
    }
    
    /**
     * 获取目标路径（可选）
     */
    public String getTargetPath() {
        return properties.getProperty("dto.target.path");
    }
    
    /**
     * 获取必需的配置属性
     */
    private String getRequiredProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("配置文件中缺少必需的配置项: " + key);
        }
        return value.trim();
    }

}