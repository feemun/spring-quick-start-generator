# 多表联查DTO生成器使用指南

## 📋 概述

`DtoGeneratorApplication` 是专门为多表联查场景优化的DTO生成工具。它能够从复杂的SQL查询中提取元数据，并生成对应的Java DTO类。

## 🚀 主要特性

- ✅ **多表联查支持**：完美处理JOIN查询的字段映射
- ✅ **智能别名处理**：自动识别和处理字段别名
- ✅ **表前缀处理**：智能移除表前缀（如 `u.name` → `name`）
- ✅ **类型映射**：精确的SQL到Java类型映射
- ✅ **注释生成**：自动生成详细的字段注释
- ✅ **驼峰命名**：自动转换为Java驼峰命名规范

## 📖 使用方式

### 1. 命令行使用

#### 单表模式
```bash
java cloud.catfish.mbg.DtoGeneratorApplication user_info UserInfoDto com.example.dto
```

#### SQL模式（推荐用于多表联查）
```bash
java cloud.catfish.mbg.DtoGeneratorApplication --sql "SELECT u.id, u.name, p.title FROM users u JOIN profiles p ON u.id=p.user_id" UserProfileDto com.example.dto
```

### 2. 编程方式使用

```java
DtoGeneratorApplication app = new DtoGeneratorApplication();

// 多表联查示例
String joinSql = "SELECT u.id as user_id, u.name as user_name, u.email, " +
                 "p.title as profile_title, p.bio as profile_bio, " +
                 "r.name as role_name " +
                 "FROM users u " +
                 "LEFT JOIN profiles p ON u.id = p.user_id " +
                 "LEFT JOIN roles r ON u.role_id = r.id";

app.generateDtoFromSql(joinSql, "UserProfileDto", "com.example.dto");
```

## 🎯 实际应用场景

### 场景1：用户信息聚合查询

**SQL查询：**
```sql
SELECT 
    u.id as user_id,
    u.username,
    u.email,
    u.created_at as user_created_at,
    p.first_name,
    p.last_name,
    p.phone,
    p.avatar_url,
    r.name as role_name,
    r.permissions
FROM users u
LEFT JOIN profiles p ON u.id = p.user_id
LEFT JOIN roles r ON u.role_id = r.id
WHERE u.status = 'ACTIVE'
```

**生成命令：**
```bash
java DtoGeneratorApplication --sql "SELECT u.id as user_id, u.username, u.email, u.created_at as user_created_at, p.first_name, p.last_name, p.phone, p.avatar_url, r.name as role_name, r.permissions FROM users u LEFT JOIN profiles p ON u.id = p.user_id LEFT JOIN roles r ON u.role_id = r.id WHERE u.status = 'ACTIVE'" UserDetailDto com.example.dto
```

**生成的DTO类：**
```java
package com.example.dto;

import java.time.LocalDateTime;

/**
 * UserDetailDto - 数据传输对象
 * 
 * <p>该DTO类包含以下字段：</p>
 * <ul>
 *   <li><strong>userId</strong> (Long): 来自表 users 的字段 id</li>
 *   <li><strong>username</strong> (String): 来自表 users 的字段 username</li>
 *   <li><strong>email</strong> (String): 来自表 users 的字段 email</li>
 *   <li><strong>userCreatedAt</strong> (LocalDateTime): 来自表 users 的字段 created_at</li>
 *   <li><strong>firstName</strong> (String): 来自表 profiles 的字段 first_name</li>
 *   <li><strong>lastName</strong> (String): 来自表 profiles 的字段 last_name</li>
 *   <li><strong>phone</strong> (String): 来自表 profiles 的字段 phone</li>
 *   <li><strong>avatarUrl</strong> (String): 来自表 profiles 的字段 avatar_url</li>
 *   <li><strong>roleName</strong> (String): 来自表 roles 的字段 name</li>
 *   <li><strong>permissions</strong> (String): 来自表 roles 的字段 permissions</li>
 * </ul>
 * 
 * @author DTO Generator
 * @version 2.0
 */
public class UserDetailDto {
    
    private Long userId;
    private String username;
    private String email;
    private LocalDateTime userCreatedAt;
    private String firstName;
    private String lastName;
    private String phone;
    private String avatarUrl;
    private String roleName;
    private String permissions;
    
    // 构造函数、getter、setter方法...
}
```

### 场景2：订单统计聚合查询

**SQL查询：**
```sql
SELECT 
    u.id as user_id,
    u.name as user_name,
    COUNT(o.id) as order_count,
    SUM(o.total_amount) as total_amount,
    AVG(o.total_amount) as avg_order_amount,
    MAX(o.created_at) as last_order_date,
    MIN(o.created_at) as first_order_date
FROM users u 
LEFT JOIN orders o ON u.id = o.user_id 
WHERE u.status = 'ACTIVE'
GROUP BY u.id, u.name
```

**生成命令：**
```bash
java DtoGeneratorApplication --sql "SELECT u.id as user_id, u.name as user_name, COUNT(o.id) as order_count, SUM(o.total_amount) as total_amount, AVG(o.total_amount) as avg_order_amount, MAX(o.created_at) as last_order_date, MIN(o.created_at) as first_order_date FROM users u LEFT JOIN orders o ON u.id = o.user_id WHERE u.status = 'ACTIVE' GROUP BY u.id, u.name" UserOrderSummaryDto com.example.dto
```

### 场景3：复杂的多表关联查询

**SQL查询：**
```sql
SELECT 
    p.id as product_id,
    p.name as product_name,
    p.price,
    c.name as category_name,
    b.name as brand_name,
    s.name as supplier_name,
    s.contact_email as supplier_email,
    inv.quantity as stock_quantity,
    inv.last_updated as stock_updated_at
FROM products p
INNER JOIN categories c ON p.category_id = c.id
INNER JOIN brands b ON p.brand_id = b.id
INNER JOIN suppliers s ON p.supplier_id = s.id
LEFT JOIN inventory inv ON p.id = inv.product_id
WHERE p.status = 'ACTIVE'
```

## 🔧 配置说明

### 数据库配置文件 (generator.properties)

```properties
jdbc.driverClass=com.mysql.cj.jdbc.Driver
jdbc.connectionURL=jdbc:mysql://localhost:3306/your_database?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
jdbc.userId=your_username
jdbc.password=your_password
```

### 输出目录配置

```java
DtoGeneratorApplication app = new DtoGeneratorApplication();
app.setOutputDirectory("src/main/java");  // 自定义输出目录
```

## 📝 字段映射规则

### 命名转换规则

| 数据库字段名 | Java字段名 | 说明 |
|-------------|-----------|------|
| `user_id` | `userId` | 下划线转驼峰 |
| `first_name` | `firstName` | 下划线转驼峰 |
| `u.name` | `name` | 移除表前缀 |
| `COUNT(*)` | `count` | 函数名转换 |
| `MAX(created_at)` | `maxCreatedAt` | 函数+字段名组合 |

### 类型映射规则

| SQL类型 | Java类型 | 说明 |
|---------|----------|------|
| `INT`, `INTEGER` | `Integer` | 整数类型 |
| `BIGINT` | `Long` | 长整数类型 |
| `VARCHAR`, `TEXT` | `String` | 字符串类型 |
| `DECIMAL`, `NUMERIC` | `BigDecimal` | 精确数值类型 |
| `DOUBLE`, `FLOAT` | `Double` | 浮点数类型 |
| `DATE` | `LocalDate` | 日期类型 |
| `DATETIME`, `TIMESTAMP` | `LocalDateTime` | 日期时间类型 |
| `TIME` | `LocalTime` | 时间类型 |
| `BOOLEAN`, `BIT` | `Boolean` | 布尔类型 |

## ⚠️ 注意事项

1. **字段别名**：建议在多表联查中使用有意义的字段别名，避免字段名冲突
2. **表前缀**：工具会自动移除表前缀，如 `u.name` 会变成 `name`
3. **聚合函数**：聚合函数会被转换为有意义的字段名，如 `COUNT(*)` → `count`
4. **数据库连接**：确保 `generator.properties` 中的数据库配置正确
5. **权限要求**：确保数据库用户有查询相关表的权限

## 🎉 优势特点

- **零配置**：只需要一个properties文件即可开始使用
- **智能识别**：自动识别字段类型、别名、表关系
- **完整文档**：生成的DTO包含完整的JavaDoc注释
- **标准规范**：遵循Java命名规范和最佳实践
- **灵活扩展**：支持自定义输出目录和包名

通过这个工具，您可以快速从复杂的多表联查中生成标准的Java DTO类，大大提高开发效率！