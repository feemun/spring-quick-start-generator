# RequestParamGeneratorPlugin 使用文档

## 概述

`RequestParamGeneratorPlugin` 是一个 MyBatis Generator 插件，用于自动生成 Request Parameter 类，这些类专门用于接收来自 Web 页面的查询条件参数。该插件能够大大简化 Web API 开发中参数接收和验证的工作。

## 主要功能

### 🚀 核心特性

1. **自动生成 RequestParam 类** - 基于数据库表结构自动生成对应的请求参数类
2. **Bean Validation 支持** - 自动添加参数验证注解，确保数据完整性
3. **Swagger3 文档支持** - 自动生成 API 文档注解，提升接口可读性
4. **日期范围查询** - 对 LocalDateTime 字段自动生成范围查询支持
5. **继承分页排序** - 自动继承 BaseRequestParam，获得分页和排序功能

### 📋 生成的类特性

- **数据验证**: 自动添加 `@Size`、`@Email` 等验证注解
- **API 文档**: 自动添加 `@Schema` 注解用于 Swagger 文档生成
- **日期范围**: LocalDateTime 字段自动生成 `fieldStart` 和 `fieldEnd` 两个字段
- **分页排序**: 继承 `BaseRequestParam` 获得分页和排序功能
- **Lombok 支持**: 使用 `@Data` 注解自动生成 getter/setter 方法

## 配置说明

### 必需配置参数

| 参数名 | 类型 | 说明 | 示例值 |
|--------|------|------|--------|
| `requestParamPackage` | String | 生成的 RequestParam 类的包路径 | `cloud.catfish.mbg.model.param` |
| `requestParamTargetProject` | String | 生成文件的目标项目路径 | `mbg/src/main/java` |

### 默认行为

插件 2.0 版本采用"约定优于配置"的设计理念，以下功能默认启用：

- ✅ **参数验证** - 自动添加验证注解
- ✅ **分页功能** - 继承分页参数
- ✅ **排序功能** - 继承排序参数  
- ✅ **Swagger 文档** - 自动生成 API 文档注解
- ✅ **日期范围查询** - LocalDateTime 字段自动支持范围查询
- ✅ **描述后缀** - 使用固定后缀 " Request Parameters"

## 使用方法

### 1. 在 generatorConfig.xml 中配置插件

```xml
<generatorConfiguration>
    <context id="context1">
        <!-- 其他配置... -->
        
        <!-- RequestParam generator Plugin -->
        <plugin type="cloud.catfish.mbg.plugin.RequestParamGeneratorPlugin">
            <property name="requestParamPackage" value="cloud.catfish.mbg.model.param"/>
            <property name="requestParamTargetProject" value="mbg/src/main/java"/>
        </plugin>
        
        <!-- 其他配置... -->
    </context>
</generatorConfiguration>
```

### 2. 运行 MyBatis Generator

```bash
mvn mybatis-generator:generate
```

## 生成示例

### 原始数据库表

假设有一个用户表 `ums_admin`：

```sql
CREATE TABLE ums_admin (
    id BIGINT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(64) COMMENT '用户名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(64) COMMENT '手机号',
    status INT COMMENT '帐号启用状态',
    create_time DATETIME COMMENT '创建时间',
    login_time DATETIME COMMENT '最后登录时间'
);
```

### 生成的 RequestParam 类

```java
package cloud.catfish.mbg.model.param;

import lombok.Data;
import cloud.catfish.common.param.BaseRequestParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import java.time.LocalDateTime;

@Data
@Schema(description = "UmsAdmin Request Parameters")
public class UmsAdminRequestParam extends BaseRequestParam {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    @Size(max = 255, message = "username cannot exceed 255 characters")
    private String username;

    @Schema(description = "邮箱")
    @Size(max = 255, message = "email cannot exceed 255 characters")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "手机号")
    @Size(max = 255, message = "phone cannot exceed 255 characters")
    private String phone;

    @Schema(description = "帐号启用状态")
    private Integer status;

    @Schema(description = "创建时间 start range")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间 end range")
    private LocalDateTime createTimeEnd;

    @Schema(description = "最后登录时间 start range")
    private LocalDateTime loginTimeStart;

    @Schema(description = "最后登录时间 end range")
    private LocalDateTime loginTimeEnd;
}
```

## 在 Controller 中使用

```java
@RestController
@RequestMapping("/admin")
public class UmsAdminController {

    @GetMapping("/list")
    @Operation(summary = "获取用户列表")
    public CommonResult<CommonPage<UmsAdmin>> list(
            @Valid UmsAdminRequestParam requestParam) {
        
        // 使用 requestParam 进行查询
        // requestParam 包含了分页、排序、过滤条件等所有参数
        
        return CommonResult.success(adminService.list(requestParam));
    }
}
```

## 高级特性

### 1. 自动字段验证

- **字符串字段**: 自动添加 `@Size(max = 255)` 验证
- **邮箱字段**: 字段名包含 "email" 时自动添加 `@Email` 验证
- **自定义消息**: 验证失败时提供友好的错误消息

### 2. 智能字段描述

- **数据库注释优先**: 优先使用数据库字段的 COMMENT 作为描述
- **智能生成**: 当没有数据库注释时，根据字段名自动生成可读描述
- **驼峰转换**: 将 camelCase 字段名转换为可读的描述文本

### 3. 日期范围查询

对于 `LocalDateTime` 类型的字段，插件会自动生成两个字段：
- `{fieldName}Start`: 开始时间
- `{fieldName}End`: 结束时间

这样可以方便地进行时间范围查询。

### 4. 继承分页排序

生成的类自动继承 `BaseRequestParam`，获得以下功能：
- 分页参数：`pageNum`、`pageSize`
- 排序参数：`orderBy`、`sort`

## 依赖要求

确保项目中包含以下依赖：

```xml
<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>

<!-- Swagger3 -->
<dependency>
    <groupId>io.swagger.core.v3</groupId>
    <artifactId>swagger-annotations</artifactId>
</dependency>

<!-- Bean Validation -->
<dependency>
    <groupId>jakarta.validation</groupId>
    <artifactId>jakarta.validation-api</artifactId>
</dependency>

<!-- 自定义 BaseRequestParam -->
<dependency>
    <groupId>cloud.catfish</groupId>
    <artifactId>common</artifactId>
</dependency>
```

## 版本历史

### v2.0 (当前版本)
- 🎯 简化配置，采用"约定优于配置"设计
- 🚀 默认启用所有功能，减少配置复杂度
- 📝 优化代码结构和文档

### v1.x (历史版本)
- 支持多种可选配置参数
- 需要显式配置各种功能开关

## 常见问题

### Q: 如何自定义验证规则？
A: 插件生成基础的验证注解，如需更复杂的验证规则，可以在生成后手动添加或通过继承的方式扩展。

### Q: 如何修改字段描述？
A: 优先在数据库表字段上添加 COMMENT，插件会自动使用这些注释作为字段描述。

### Q: 生成的类可以自定义吗？
A: 插件生成的是基础模板，可以在生成后根据具体需求进行修改和扩展。

### Q: 如何处理复杂的查询条件？
A: 对于复杂查询，建议在 Service 层根据 RequestParam 的参数构建相应的查询条件。

## 总结

`RequestParamGeneratorPlugin` 是一个强大而简洁的 MyBatis Generator 插件，它能够：

- 🎯 **提高开发效率** - 自动生成标准化的请求参数类
- 🛡️ **增强数据安全** - 自动添加参数验证
- 📚 **改善文档质量** - 自动生成 API 文档注解
- 🔧 **简化配置** - 采用约定优于配置的设计理念

通过使用这个插件，开发者可以专注于业务逻辑的实现，而不需要花费大量时间在重复的参数类编写上。