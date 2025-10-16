package cloud.catfish.mbg.dtogenerator.codegen;

import cloud.catfish.mbg.dtogenerator.model.FieldInfo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * DTO代码生成类
 * 负责生成DTO类的Java代码
 * 
 * @author Generated
 * @since 2.0
 */
public class DtoCodeGenerator {
    
    private static final String DEFAULT_OUTPUT_DIRECTORY = 
        "C:\\Users\\feemu\\Documents\\GitHub\\spring-quick-start-generator\\mbg\\src\\main\\java\\cloud\\catfish\\mbg\\dto";
    
    /**
     * 生成DTO类文件
     * 
     * @param fields 字段信息列表
     * @param className 类名
     * @param packageName 包名
     * @param targetPath 目标路径（可选）
     * @throws IOException IO异常
     */
    public void generateDtoClass(List<FieldInfo> fields, String className, String packageName, String targetPath)
            throws IOException {
        
        StringBuilder content = new StringBuilder();
        
        // 包声明
        content.append("package ").append(packageName).append(";\n\n");
        
        // 导入语句
        Set<String> imports = collectImports(fields);
        // 添加Lombok的@Data注解导入
        imports.add("lombok.Data");
        // 添加Swagger3注解导入
        imports.add("io.swagger.v3.oas.annotations.media.Schema");
        
        for (String importStr : imports) {
            content.append("import ").append(importStr).append(";\n");
        }
        if (!imports.isEmpty()) {
            content.append("\n");
        }
        
        // Lombok @Data 注解
        content.append("@Data\n");
        
        // 类声明
        content.append("public class ").append(className).append(" {\n\n");
        
        // 字段声明（添加Swagger3注解）
        for (FieldInfo field : fields) {
            // 添加Swagger3的@Schema注解，使用comment字段作为description
            String description = field.comment != null && !field.comment.trim().isEmpty() 
                ? field.comment.trim() 
                : field.fieldName;
            content.append("    @Schema(description = \"").append(description).append("\")\n");
            content.append("    private ").append(field.javaType).append(" ").append(field.fieldName).append(";\n\n");
        }
        
        content.append("}\n");
        
        // 写入文件
        writeToFile(content.toString(), className, packageName, targetPath);
    }
    
    /**
     * 生成DTO类文件（使用默认路径）
     * 
     * @param fields 字段信息列表
     * @param className 类名
     * @param packageName 包名
     * @throws IOException IO异常
     */
    public void generateDtoClass(List<FieldInfo> fields, String className, String packageName)
            throws IOException {
        generateDtoClass(fields, className, packageName, null);
    }
    
    /**
     * 收集需要的导入语句
     */
    private Set<String> collectImports(List<FieldInfo> fields) {
        Set<String> imports = new TreeSet<>();
        
        for (FieldInfo field : fields) {
            switch (field.javaType) {
                case "LocalDate":
                    imports.add("java.time.LocalDate");
                    break;
                case "LocalTime":
                    imports.add("java.time.LocalTime");
                    break;
                case "LocalDateTime":
                    imports.add("java.time.LocalDateTime");
                    break;
                case "BigDecimal":
                    imports.add("java.math.BigDecimal");
                    break;
                case "java.time.LocalDate":
                    imports.add("java.time.LocalDate");
                    break;
                case "java.time.LocalTime":
                    imports.add("java.time.LocalTime");
                    break;
                case "java.time.LocalDateTime":
                    imports.add("java.time.LocalDateTime");
                    break;
            }
        }
        
        return imports;
    }
    
    /**
     * 写入文件
     */
    private void writeToFile(String content, String className, String packageName, String targetPath) throws IOException {
        String finalTargetPath;
        
        if (targetPath != null && !targetPath.trim().isEmpty()) {
            // 使用配置文件中指定的路径，规范化路径分隔符
            finalTargetPath = targetPath.trim().replace('/', File.separatorChar).replace('\\', File.separatorChar);
        } else {
            // 使用默认路径
            finalTargetPath = DEFAULT_OUTPUT_DIRECTORY;
        }
        
        File targetDir = new File(finalTargetPath);
        if (!targetDir.exists()) {
            boolean created = targetDir.mkdirs();
            if (!created && !targetDir.exists()) {
                throw new IOException("无法创建目录: " + finalTargetPath);
            }
        }
        
        File javaFile = new File(targetDir, className + ".java");
        try (FileWriter writer = new FileWriter(javaFile)) {
            writer.write(content);
        }
        
        System.out.println("📁 生成DTO类文件: " + javaFile.getAbsolutePath());
    }
    
    /**
     * 首字母大写
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}