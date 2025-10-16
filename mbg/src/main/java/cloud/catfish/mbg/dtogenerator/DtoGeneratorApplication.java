package cloud.catfish.mbg.dtogenerator;

import cloud.catfish.mbg.dtogenerator.config.ConfigurationManager;
import cloud.catfish.mbg.dtogenerator.database.DatabaseConnectionManager;
import cloud.catfish.mbg.dtogenerator.metadata.SqlMetadataExtractor;
import cloud.catfish.mbg.dtogenerator.codegen.DtoCodeGenerator;
import cloud.catfish.mbg.dtogenerator.model.FieldInfo;

import java.sql.Connection;
import java.util.List;

public class DtoGeneratorApplication {

    private final ConfigurationManager configManager;
    private final DatabaseConnectionManager dbManager;
    private final SqlMetadataExtractor metadataExtractor;
    private final DtoCodeGenerator codeGenerator;

    /**
     * 构造函数，初始化所有模块
     */
    public DtoGeneratorApplication() {
        this.configManager = new ConfigurationManager();
        this.dbManager = new DatabaseConnectionManager(configManager);
        this.metadataExtractor = new SqlMetadataExtractor();
        this.codeGenerator = new DtoCodeGenerator();
    }

    /**
     * 主方法 - 从配置文件读取参数并生成DTO
     */
    public static void main(String[] args) {
        try {
            DtoGeneratorApplication app = new DtoGeneratorApplication();
            app.generateDtoFromConfig();
        } catch (Exception e) {
            System.err.println("DTO生成失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 从配置文件生成DTO
     */
    public void generateDtoFromConfig() {
        try {
            System.out.println("🚀 开始生成DTO...");

            // 加载配置
            configManager.loadConfiguration();

            // 获取配置信息
            String sql = configManager.getSql();
            String packageName = configManager.getPackageName();
            String className = configManager.getClassName();
            String targetPath = configManager.getTargetPath();

            System.out.println("📋 配置信息:");
            System.out.println("  SQL: " + sql);
            System.out.println("  包名: " + packageName);
            System.out.println("  类名: " + className);
            System.out.println("  目标路径: " + (targetPath != null ? targetPath : "默认路径"));

            // 生成DTO
            generateDtoFromSql(sql, className, packageName, targetPath);

            System.out.println("✅ DTO生成完成!");

        } catch (Exception e) {
            System.err.println("❌ 从配置文件生成DTO失败: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 从SQL查询生成DTO
     *
     * @param sql         SQL查询语句
     * @param className   DTO类名
     * @param packageName 包名
     * @param targetPath  目标路径（可选）
     */
    public void generateDtoFromSql(String sql, String className, String packageName, String targetPath) {
        Connection connection = null;
        try {
            System.out.println("🔗 连接数据库...");

            // 创建数据库连接
            connection = dbManager.createConnection();

            // 测试连接
            if (dbManager.testConnection()) {
                System.out.println("✅ 数据库连接成功");
            } else {
                throw new RuntimeException("数据库连接测试失败");
            }

            System.out.println("🔍 分析SQL查询...");

            // 提取字段信息
            List<FieldInfo> fields = metadataExtractor.extractFieldsFromSql(connection, sql);

            if (fields.isEmpty()) {
                throw new RuntimeException("未能从SQL查询中提取到字段信息");
            }

            System.out.println("📊 提取到 " + fields.size() + " 个字段:");
            for (FieldInfo field : fields) {
                System.out.println("  - " + field.fieldName + " (" + field.javaType + ") - " +
                        (field.comment != null && !field.comment.trim().isEmpty() ? field.comment : "无注释"));
            }

            System.out.println("🏗️ 生成DTO代码...");

            // 生成DTO类
            codeGenerator.generateDtoClass(fields, className, packageName, targetPath);

        } catch (Exception e) {
            System.err.println("❌ 生成DTO失败: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            // 关闭数据库连接
            if (connection != null) {
                dbManager.closeConnection(connection);
                System.out.println("🔒 数据库连接已关闭");
            }
        }
    }
}