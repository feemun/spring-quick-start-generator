# Batch Insert Plugin for MyBatis Generator

This document describes the custom batch insert plugins implemented for the MyBatis Generator (MBG) in this project.

## Overview

Two custom plugins have been implemented to enhance the MyBatis Generator with batch insert capabilities:

1. **BatchInsertPlugin** - Generates standard batch insert methods
2. **BatchInsertSelectivePlugin** - Generates selective batch insert methods (ignores null values)

## Features

### BatchInsertPlugin

- **Method Generated**: `batchInsert(List<Entity> records)`
- **Functionality**: Inserts all records in a single SQL statement using `INSERT INTO ... VALUES (...), (...), ...`
- **Performance**: High performance for bulk inserts
- **Requirement**: All fields must have values (including nulls)

### BatchInsertSelectivePlugin

- **Method Generated**: `batchInsertSelective(List<Entity> records)`
- **Functionality**: Inserts records with dynamic column selection, ignoring null values
- **Performance**: Good performance with flexibility for partial data
- **Requirement**: Only non-null fields are included in the INSERT statement

## Generated Code Structure

### Mapper Interface Methods

```java
public interface EntityMapper {
    /**
     * 批量插入记录
     * @param records 要插入的记录列表
     * @return 插入的记录数
     */
    int batchInsert(List<Entity> records);
    
    /**
     * 批量选择性插入记录（忽略null值）
     * @param records 要插入的记录列表
     * @return 插入的记录数
     */
    int batchInsertSelective(List<Entity> records);
}
```

### XML Mapper Statements

#### BatchInsert XML
```xml
<insert id="batchInsert" parameterType="java.util.List">
    INSERT INTO table_name (col1, col2, col3) VALUES
    <foreach collection="list" item="item" separator=",">
        (#{item.field1}, #{item.field2}, #{item.field3})
    </foreach>
</insert>
```

#### BatchInsertSelective XML
```xml
<insert id="batchInsertSelective" parameterType="java.util.List">
    INSERT INTO table_name
    <foreach collection="list" item="item" index="index" separator=";">
        (
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <if test="item.field1 != null">col1,</if>
            <if test="item.field2 != null">col2,</if>
            <if test="item.field3 != null">col3,</if>
        </trim>
        VALUES 
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <if test="item.field1 != null">#{item.field1},</if>
            <if test="item.field2 != null">#{item.field2},</if>
            <if test="item.field3 != null">#{item.field3},</if>
        </trim>
        )
    </foreach>
</insert>
```

### Service Layer Integration

The plugins automatically integrate with the generated service layer:

```java
@Service
public class EntityServiceImpl implements IEntityService {
    
    @Resource
    private EntityMapper entityMapper;
    
    @Override
    public Boolean batchInsert(List<Entity> records) {
        if (records == null || records.isEmpty()) {
            return false;
        }
        return entityMapper.batchInsert(records) > 0;
    }
    
    @Override
    public Boolean batchInsertSelective(List<Entity> records) {
        if (records == null || records.isEmpty()) {
            return false;
        }
        return entityMapper.batchInsertSelective(records) > 0;
    }
}
```

## Configuration

The plugins are configured in `generatorConfig.xml`:

```xml
<generatorConfiguration>
    <context id="MySqlContext" targetRuntime="MyBatis3" defaultModelType="flat">
        <!-- Other plugins... -->
        
        <!-- batch insert Plugin -->
        <plugin type="cloud.catfish.mbg.plugin.BatchInsertPlugin"/>
        
        <!-- batch insert selective Plugin -->
        <plugin type="cloud.catfish.mbg.plugin.BatchInsertSelectivePlugin"/>
        
        <!-- Other configurations... -->
    </context>
</generatorConfiguration>
```

## Usage Examples

### Standard Batch Insert

```java
// Prepare data
List<UmsAdmin> adminList = Arrays.asList(
    UmsAdmin.builder().username("user1").email("user1@example.com").build(),
    UmsAdmin.builder().username("user2").email("user2@example.com").build(),
    UmsAdmin.builder().username("user3").email("user3@example.com").build()
);

// Batch insert
Boolean success = adminService.batchInsert(adminList);
if (success) {
    System.out.println("Batch insert successful!");
}
```

### Selective Batch Insert

```java
// Prepare data with some null values
List<UmsAdmin> adminList = Arrays.asList(
    UmsAdmin.builder().username("user1").email("user1@example.com").build(), // phone is null
    UmsAdmin.builder().username("user2").phone("123456789").build(), // email is null
    UmsAdmin.builder().username("user3").email("user3@example.com").phone("987654321").build()
);

// Selective batch insert (null values will be ignored)
Boolean success = adminService.batchInsertSelective(adminList);
if (success) {
    System.out.println("Selective batch insert successful!");
}
```

## Performance Considerations

1. **BatchInsert**: Best performance for large datasets where all fields have values
2. **BatchInsertSelective**: Slightly slower due to dynamic SQL generation, but more flexible
3. **Batch Size**: Consider breaking large lists into smaller batches (e.g., 1000 records per batch) to avoid memory issues
4. **Transaction Management**: Use `@Transactional` annotation for proper transaction handling

## Database Compatibility

- **MySQL**: Fully supported with optimized batch insert syntax
- **PostgreSQL**: Supported with standard SQL syntax
- **Oracle**: Supported with appropriate syntax adjustments
- **SQL Server**: Supported with standard SQL syntax

## Error Handling

The plugins include proper error handling:

- Null or empty list validation
- Database constraint violation handling
- Transaction rollback support
- Detailed error messages for debugging

## Best Practices

1. **Validate Data**: Always validate input data before batch operations
2. **Use Transactions**: Wrap batch operations in transactions for data consistency
3. **Monitor Performance**: Profile batch operations to optimize batch sizes
4. **Handle Failures**: Implement proper error handling and rollback mechanisms
5. **Log Operations**: Add logging for batch operations for monitoring and debugging

## Troubleshooting

### Common Issues

1. **Plugin Not Found**: Ensure the plugin classes are in the correct package
2. **SQL Syntax Errors**: Check database-specific SQL syntax requirements
3. **Performance Issues**: Consider reducing batch size or optimizing database configuration
4. **Memory Issues**: Use pagination for very large datasets

### Debug Tips

1. Enable MyBatis SQL logging to see generated statements
2. Use database profiling tools to monitor performance
3. Test with small datasets first before scaling up
4. Verify database constraints and indexes

## Future Enhancements

- Support for batch update operations
- Configurable batch size limits
- Enhanced error reporting
- Support for custom SQL templates
- Integration with database-specific optimizations