# SQL查询DTO生成器使用说明

## 📋 概述

`DtoGeneratorApplication` 是基于SQL查询结果集元数据的DTO生成工具。通过分析任意SQL查询的字段信息，自动生成对应的Java DTO类。

## 🎯 核心理念

**一个SQL查询 = 一个DTO类**

无论是单表查询、多表联查、聚合查询还是复杂的子查询，都通过SQL查询的方式来生成DTO，这种方式涵盖了所有可能的数据结构场景。

## 🚀 使用方式

### 命令行使用
```bash
java cloud.catfish.mbg.DtoGeneratorApplication "SQL查询语句" DTO类名 包名
```

### 编程方式使用
```java
DtoGeneratorApplication app = new DtoGeneratorApplication();
app.generateDtoFromSql("SQL查询语句", "DTO类名", "包名");
```

## 📖 实际应用场景

### 1. 单表查询
```sql
SELECT id, name, email, created_at FROM users WHERE status = 'ACTIVE'
```

### 2. 多表联查
```sql
SELECT u.id as user_id, u.name, u.email, 
       p.title as profile_title, p.bio,
       r.name as role_name
FROM users u
LEFT JOIN profiles p ON u.id = p.user_id
LEFT JOIN roles r ON u.role_id = r.id
```

### 3. 聚合查询
```sql
SELECT u.id, u.name, 
       COUNT(o.id) as order_count,
       SUM(o.amount) as total_amount,
       MAX(o.created_at) as last_order_date
FROM users u 
LEFT JOIN orders o ON u.id = o.user_id
GROUP BY u.id, u.name
```

### 4. 复杂子查询
```sql
SELECT u.id, u.name,
       (SELECT COUNT(*) FROM orders WHERE user_id = u.id) as order_count,
       (SELECT AVG(rating) FROM reviews WHERE user_id = u.id) as avg_rating
FROM users u
WHERE u.created_at > '2023-01-01'
```

## 🔧 配置要求

### 数据库配置文件 (generator.properties)
```properties
jdbc.driverClass=com.mysql.cj.jdbc.Driver
jdbc.connectionURL=jdbc:mysql://localhost:3306/your_database
jdbc.userId=your_username
jdbc.password=your_password
```

## 📝 智能特性

### 字段名处理
- **别名优先**：`u.name as user_name` → `userName`
- **表前缀移除**：`u.email` → `email`
- **驼峰转换**：`first_name` → `firstName`
- **聚合函数**：`COUNT(*)` → `count`

### 类型映射
- `INT/INTEGER` → `Integer`
- `BIGINT` → `Long`
- `VARCHAR/TEXT` → `String`
- `DECIMAL/NUMERIC` → `BigDecimal`
- `DATETIME/TIMESTAMP` → `LocalDateTime`
- `DATE` → `LocalDate`
- `TIME` → `LocalTime`

### 生成的DTO特点
- ✅ 完整的JavaDoc文档
- ✅ 字段来源信息（表名、别名）
- ✅ 标准的getter/setter方法
- ✅ 默认和全参构造函数
- ✅ toString()方法
- ✅ 自动导入必要的包

## 💡 使用建议

1. **使用有意义的别名**：避免字段名冲突，提高可读性
2. **SQL格式化**：保持SQL语句的可读性
3. **测试SQL**：确保SQL能正常执行
4. **权限检查**：确保数据库用户有相应的查询权限

## 🎉 优势

- **通用性强**：一种方式处理所有查询场景
- **零学习成本**：只需要会写SQL即可
- **智能识别**：自动处理各种命名和类型转换
- **标准输出**：生成符合Java规范的DTO类

通过SQL查询这一种统一的方式，您可以轻松应对任何数据结构的DTO生成需求！