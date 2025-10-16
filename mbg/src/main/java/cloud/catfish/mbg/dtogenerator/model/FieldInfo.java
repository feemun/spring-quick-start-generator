package cloud.catfish.mbg.dtogenerator.model;

/**
 * 字段信息模型类
 * 用于存储从数据库元数据中提取的字段信息
 * 
 * @author Generated
 * @since 2.0
 */
public class FieldInfo {
    
    /** 数据库字段名 */
    public String columnName;
    
    /** 字段标签（别名或原字段名） */
    public String columnLabel;
    
    /** 数据库字段类型名称 */
    public String columnTypeName;
    
    /** 数据库字段类型代码 */
    public int columnType;
    
    /** 是否可为空 */
    public boolean nullable;
    
    /** 表名 */
    public String tableName;
    
    /** 模式名 */
    public String schemaName;
    
    /** Java字段名 */
    public String fieldName;
    
    /** Java类型 */
    public String javaType;
    
    /** 字段注释 */
    public String comment;
    
    /**
     * 默认构造函数
     */
    public FieldInfo() {
    }
    
    /**
     * 构造函数
     * 
     * @param columnName 数据库字段名
     * @param columnLabel 字段标签
     * @param columnTypeName 数据库字段类型名称
     * @param columnType 数据库字段类型代码
     * @param nullable 是否可为空
     */
    public FieldInfo(String columnName, String columnLabel, String columnTypeName, 
                     int columnType, boolean nullable) {
        this.columnName = columnName;
        this.columnLabel = columnLabel;
        this.columnTypeName = columnTypeName;
        this.columnType = columnType;
        this.nullable = nullable;
    }
    
    @Override
    public String toString() {
        return String.format("FieldInfo{columnName='%s', fieldName='%s', javaType='%s', " +
                           "tableName='%s', comment='%s'}", 
                           columnName, fieldName, javaType, tableName, comment);
    }
}