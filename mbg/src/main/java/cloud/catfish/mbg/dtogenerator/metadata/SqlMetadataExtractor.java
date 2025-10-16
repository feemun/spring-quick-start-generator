package cloud.catfish.mbg.dtogenerator.metadata;

import cloud.catfish.mbg.dtogenerator.database.DatabaseConnectionManager;
import cloud.catfish.mbg.dtogenerator.model.FieldInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL元数据提取类
 * 负责从SQL查询中提取字段元数据信息
 * 
 * @author Generated
 * @since 2.0
 */
public class SqlMetadataExtractor {
    
    private final DatabaseConnectionManager connectionManager;
    
    /**
     * 默认构造函数
     */
    public SqlMetadataExtractor() {
        this.connectionManager = null;
    }
    
    /**
     * 构造函数
     * 
     * @param connectionManager 数据库连接管理器
     */
    public SqlMetadataExtractor(DatabaseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }
    
    /**
     * 从SQL查询中提取字段信息
     * 
     * @param connection 数据库连接
     * @param sql SQL查询语句
     * @return 字段信息列表
     * @throws SQLException SQL异常
     */
    public List<FieldInfo> extractFieldsFromSql(Connection connection, String sql) throws SQLException {
        List<FieldInfo> fields = new ArrayList<>();
        
        try {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                ResultSetMetaData metaData = stmt.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                System.out.println("🔍 分析结果集元数据...");
                
                for (int i = 1; i <= columnCount; i++) {
                    FieldInfo field = extractFieldInfo(metaData, i, connection);
                    fields.add(field);
                    
                    System.out.println("  ✓ " + field.fieldName + " (" + field.javaType + ") - " + field.comment);
                }
            }
        } catch (SQLException e) {
            System.err.println("提取字段信息时发生错误: " + e.getMessage());
            throw e;
        }
        
        return fields;
    }
    
    /**
     * 提取单个字段信息
     */
    private FieldInfo extractFieldInfo(ResultSetMetaData metaData, int columnIndex, Connection connection) 
            throws SQLException {
        
        // 获取字段基本信息
        String columnName = metaData.getColumnName(columnIndex);
        String columnLabel = metaData.getColumnLabel(columnIndex);
        String columnTypeName = metaData.getColumnTypeName(columnIndex);
        int columnType = metaData.getColumnType(columnIndex);
        boolean nullable = metaData.isNullable(columnIndex) == ResultSetMetaData.columnNullable;
        
        // 获取表信息（可能为空，特别是在聚合查询中）
        String tableName = null;
        String schemaName = null;
        try {
            tableName = metaData.getTableName(columnIndex);
            schemaName = metaData.getSchemaName(columnIndex);
        } catch (SQLException e) {
            // 某些数据库或查询类型可能不支持获取表名
        }
        
        // 创建字段信息
        FieldInfo field = new FieldInfo(columnName, columnLabel, columnTypeName, columnType, nullable);
        field.tableName = tableName;
        field.schemaName = schemaName;
        
        // 智能生成Java字段名（优先使用别名）
        field.fieldName = generateJavaFieldName(columnLabel != null ? columnLabel : columnName);
        field.javaType = mapSqlTypeToJavaType(columnType, columnTypeName);
        
        // 尝试获取字段注释（仅对单表有效）
        if (tableName != null && !tableName.isEmpty()) {
            field.comment = getColumnComment(connection, tableName, columnName);
        }
        
        // 如果没有注释，生成默认描述
        if (field.comment == null || field.comment.trim().isEmpty()) {
            field.comment = generateFieldDescription(field);
        }
        
        return field;
    }
    
    /**
     * 智能生成Java字段名（处理别名和表前缀）
     */
    private String generateJavaFieldName(String columnLabel) {
        if (columnLabel == null || columnLabel.isEmpty()) {
            return "unknownField";
        }
        
        // 移除可能的表前缀（如 u.name -> name）
        String fieldName = columnLabel;
        if (fieldName.contains(".")) {
            fieldName = fieldName.substring(fieldName.lastIndexOf(".") + 1);
        }
        
        // 转换为驼峰命名
        return convertToCamelCase(fieldName);
    }
    
    /**
     * 转换为驼峰命名
     */
    private String convertToCamelCase(String columnName) {
        if (columnName == null || columnName.isEmpty()) {
            return columnName;
        }
        
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;
        
        for (char c : columnName.toLowerCase().toCharArray()) {
            if (c == '_' || c == '-' || c == ' ') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    result.append(c);
                }
            }
        }
        
        return result.toString();
    }
    
    /**
     * 将SQL类型映射为Java类型
     */
    private String mapSqlTypeToJavaType(int sqlType, String typeName) {
        switch (sqlType) {
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
                return "Integer";
            case Types.BIGINT:
                return "Long";
            case Types.FLOAT:
            case Types.REAL:
                return "Float";
            case Types.DOUBLE:
            case Types.NUMERIC:
            case Types.DECIMAL:
                return "Double";
            case Types.BOOLEAN:
            case Types.BIT:
                return "Boolean";
            case Types.DATE:
                return "java.time.LocalDate";
            case Types.TIME:
                return "java.time.LocalTime";
            case Types.TIMESTAMP:
                return "java.time.LocalDateTime";
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
            case Types.BLOB:
                return "byte[]";
            default:
                return "String"; // 默认使用String
        }
    }
    
    /**
     * 获取字段注释
     */
    private String getColumnComment(Connection conn, String tableName, String columnName) {
        try {
            DatabaseMetaData dbMetaData = conn.getMetaData();
            
            // 首先尝试使用连接的catalog
            String catalog = conn.getCatalog();
            if (catalog != null && !catalog.isEmpty()) {
                try (ResultSet rs = dbMetaData.getColumns(catalog, null, tableName, columnName)) {
                    if (rs.next()) {
                        String remarks = rs.getString("REMARKS");
                        if (remarks != null && !remarks.trim().isEmpty()) {
                            return remarks.trim();
                        }
                    }
                }
            }
            
            // 如果上面失败，尝试使用null作为catalog
            try (ResultSet rs = dbMetaData.getColumns(null, null, tableName, columnName)) {
                if (rs.next()) {
                    String remarks = rs.getString("REMARKS");
                    if (remarks != null && !remarks.trim().isEmpty()) {
                        return remarks.trim();
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("获取字段注释失败: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * 生成字段描述
     */
    private String generateFieldDescription(FieldInfo field) {
        if (field.tableName != null && !field.tableName.isEmpty()) {
            return String.format("%s表的%s字段", field.tableName, field.columnName);
        } else {
            return field.columnName + "字段";
        }
    }
}