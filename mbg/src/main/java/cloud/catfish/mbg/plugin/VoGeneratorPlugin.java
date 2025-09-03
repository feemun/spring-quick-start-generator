package cloud.catfish.mbg.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * MyBatis Generator plugin for generating Value Object (VO) classes as Java records
 * with corresponding MapStruct mapper interfaces.
 * 
 * <p>This plugin automatically generates:</p>
 * <ul>
 *   <li><strong>VO Record Classes:</strong> Java 14+ record classes with the same structure as domain models</li>
 *   <li><strong>MapStruct Mappers:</strong> Interface mappers for converting between domain and VO objects</li>
 * </ul>
 * 
 * <p><strong>Features:</strong></p>
 * <ul>
 *   <li>Generates immutable VO record classes with proper naming (DomainNameVo)</li>
 *   <li>Creates MapStruct mapper interfaces for bidirectional conversion</li>
 *   <li>Configurable package structure for VOs and mappers</li>
 *   <li>Supports all field types from domain models</li>
 *   <li>Automatic import management for generated classes</li>
 *   <li>Proper Java record syntax with compact constructors</li>
 *   <li>Uses database column comments for Swagger3 field descriptions</li>
 *   <li>Automatic JSON and DateTime format annotations for LocalDateTime fields</li>
 * </ul>
 * 
 * <p><strong>Configuration Properties:</strong></p>
 * <ul>
 *   <li><code>voPackage</code> - Target package for VO classes (default: model.vo)</li>
 *   <li><code>mapperPackage</code> - Target package for MapStruct mappers (default: mapper.vo)</li>
 *   <li><code>voTargetProject</code> - Target project path for VO classes (default: src/main/java)</li>
 *   <li><code>mapperTargetProject</code> - Target project path for mappers (default: src/main/java)</li>
 *   <li><code>enableMappers</code> - Enable MapStruct mapper generation (default: true)</li>
 *   <li><code>mapperComponentModel</code> - MapStruct component model (default: spring)</li>
 *   <li><code>enableSwagger</code> - Enable Swagger3 annotations (default: true)</li>
 *   <li><code>swaggerDescriptionSuffix</code> - Suffix for class descriptions (default: " VO")</li>
 * </ul>
 * 
 * <p><strong>Usage Example:</strong></p>
 * <pre>
 * &lt;plugin type="cloud.catfish.mbg.plugin.VoGeneratorPlugin"&gt;
 *     &lt;property name="voPackage" value="cloud.catfish.mbg.model.vo"/&gt;
 *     &lt;property name="mapperPackage" value="cloud.catfish.mbg.mapper.vo"/&gt;
 *     &lt;property name="voTargetProject" value="src/main/java"/&gt;
 *     &lt;property name="mapperTargetProject" value="src/main/java"/&gt;
 *     &lt;property name="enableMappers" value="true"/&gt;
 *     &lt;property name="mapperComponentModel" value="spring"/&gt;
 *     &lt;property name="enableSwagger" value="true"/&gt;
 *     &lt;property name="swaggerDescriptionSuffix" value=" VO"/&gt;
 * &lt;/plugin&gt;
 * </pre>
 * 
 * <p><strong>Generated VO Example:</strong></p>
 * <pre>
 * &#64;Schema(description = "User VO")
 * public record UserVo(
 *     &#64;Schema(description = "Primary key identifier")
 *     Long id,
 *     &#64;Schema(description = "User login name")
 *     String username,
 *     &#64;Schema(description = "User email address")
 *     String email,
 *     &#64;JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
 *     &#64;DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
 *     &#64;Schema(description = "Record creation timestamp")
 *     LocalDateTime createTime
 * ) {}
 * </pre>
 * 
 * <p><strong>Note:</strong> Field descriptions are automatically extracted from database column comments.
 * If no column comment is available, a readable description is generated from the field name.
 * LocalDateTime fields automatically include JSON and DateTime format annotations.</p>
 * 
 * <p><strong>Generated Mapper Example:</strong></p>
 * <pre>
 * &#64;Mapper(componentModel = "spring")
 * public interface UserVoMapper {
 *     UserVo toVo(User user);
 *     User toDomain(UserVo userVo);
 *     List&lt;UserVo&gt; toVoList(List&lt;User&gt; users);
 *     List&lt;User&gt; toDomainList(List&lt;UserVo&gt; userVos);
 * }
 * </pre>
 * 
 * @author MyBatis Generator Plugin
 * @version 1.0
 * @since Java 14
 */
public class VoGeneratorPlugin extends PluginAdapter {

    // Configuration property keys
    private static final String VO_PACKAGE = "voPackage";
    private static final String MAPPER_PACKAGE = "mapperPackage";
    private static final String VO_TARGET_PROJECT = "voTargetProject";
    private static final String MAPPER_TARGET_PROJECT = "mapperTargetProject";
    private static final String ENABLE_MAPPERS = "enableMappers";
    private static final String MAPPER_COMPONENT_MODEL = "mapperComponentModel";
    private static final String ENABLE_SWAGGER = "enableSwagger";
    private static final String SWAGGER_DESCRIPTION_SUFFIX = "swaggerDescriptionSuffix";
    
    // Default values
    private static final String DEFAULT_VO_PACKAGE_SUFFIX = ".vo";
    private static final String DEFAULT_MAPPER_PACKAGE_SUFFIX = ".converter";
    private static final String DEFAULT_TARGET_PROJECT = "src/main/java";
    private static final String DEFAULT_COMPONENT_MODEL = "spring";
    private static final String VO_SUFFIX = "Vo";
    private static final String MAPPER_SUFFIX = "Converter";
    
    // MapStruct annotation
    private static final String MAPSTRUCT_MAPPER_CLASS = "org.mapstruct.Mapper";
    
    // Swagger3 annotations
    private static final String SWAGGER_SCHEMA_CLASS = "io.swagger.v3.oas.annotations.media.Schema";
    private static final String DEFAULT_SWAGGER_DESCRIPTION_SUFFIX = " VO";
    
    // Constants for JSON and DateTime format annotations
    private static final String JSON_FORMAT_CLASS = "com.fasterxml.jackson.annotation.JsonFormat";
    private static final String DATETIME_FORMAT_CLASS = "org.springframework.format.annotation.DateTimeFormat";
    private static final String LOCALDATETIME_TYPE = "LocalDateTime";
    
    // Configuration fields
    private String voPackage;
    private String mapperPackage;
    private String voTargetProject = DEFAULT_TARGET_PROJECT;
    private String mapperTargetProject = DEFAULT_TARGET_PROJECT;
    private boolean enableMappers = true;
    private String mapperComponentModel = DEFAULT_COMPONENT_MODEL;
    private boolean enableSwagger = true;
    private String swaggerDescriptionSuffix = DEFAULT_SWAGGER_DESCRIPTION_SUFFIX;

    @Override
    public boolean validate(List<String> warnings) {
        // Parse configuration properties
        parseConfigurationProperties();
        
        // Validate Java version for record support
        String javaVersion = System.getProperty("java.version");
        if (javaVersion != null && !isJava14OrLater(javaVersion)) {
            warnings.add("VoGeneratorPlugin: Java 14+ is required for record support. Current version: " + javaVersion);
        }
        
        // Validate target projects
        if (voTargetProject == null || voTargetProject.trim().isEmpty()) {
            warnings.add("VoGeneratorPlugin: voTargetProject is empty, using default: " + DEFAULT_TARGET_PROJECT);
            voTargetProject = DEFAULT_TARGET_PROJECT;
        }
        
        if (mapperTargetProject == null || mapperTargetProject.trim().isEmpty()) {
            warnings.add("VoGeneratorPlugin: mapperTargetProject is empty, using default: " + DEFAULT_TARGET_PROJECT);
            mapperTargetProject = DEFAULT_TARGET_PROJECT;
        }
        
        return true;
    }
    
    /**
     * Parses configuration properties from the plugin configuration.
     */
    private void parseConfigurationProperties() {
        if (properties != null) {
            // Determine base package from model generator configuration
            String basePackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
            if (basePackage.endsWith(".model")) {
                basePackage = basePackage.substring(0, basePackage.length() - 6);
            }
            
            voPackage = properties.getProperty(VO_PACKAGE, basePackage + DEFAULT_VO_PACKAGE_SUFFIX);
            mapperPackage = properties.getProperty(MAPPER_PACKAGE, basePackage + DEFAULT_MAPPER_PACKAGE_SUFFIX);
            voTargetProject = properties.getProperty(VO_TARGET_PROJECT, DEFAULT_TARGET_PROJECT);
            mapperTargetProject = properties.getProperty(MAPPER_TARGET_PROJECT, DEFAULT_TARGET_PROJECT);
            enableMappers = Boolean.parseBoolean(properties.getProperty(ENABLE_MAPPERS, "true"));
            mapperComponentModel = properties.getProperty(MAPPER_COMPONENT_MODEL, DEFAULT_COMPONENT_MODEL);
            enableSwagger = Boolean.parseBoolean(properties.getProperty(ENABLE_SWAGGER, "true"));
            swaggerDescriptionSuffix = properties.getProperty(SWAGGER_DESCRIPTION_SUFFIX, DEFAULT_SWAGGER_DESCRIPTION_SUFFIX);
        }
    }
    
    /**
     * Checks if the Java version is 14 or later.
     */
    private boolean isJava14OrLater(String javaVersion) {
        try {
            String[] parts = javaVersion.split("\\.");
            int majorVersion = Integer.parseInt(parts[0]);
            return majorVersion >= 14;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        try {
            // Generate VO record class
            generateVoRecord(topLevelClass, introspectedTable);
            
            // Generate MapStruct mapper if enabled
            if (enableMappers) {
                generateMapStructMapper(topLevelClass, introspectedTable);
            }
        } catch (Exception e) {
            System.err.println("Warning: Failed to generate VO for " + topLevelClass.getType().getShortName() + ": " + e.getMessage());
        }
        
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }
    
    /**
     * Generates a VO record class based on the domain model.
     */
    private void generateVoRecord(TopLevelClass domainClass, IntrospectedTable introspectedTable) throws IOException {
        String domainClassName = domainClass.getType().getShortName();
        String voClassName = domainClassName + VO_SUFFIX;
        
        StringBuilder voContent = new StringBuilder();
        
        // Package declaration
        voContent.append("package ").append(voPackage).append(";\n\n");
        
        // Imports
        addImports(voContent, domainClass);
        
        // Class documentation
        voContent.append("/**\n");
        voContent.append(" * Value Object (VO) record for ").append(domainClassName).append(".\n");
        voContent.append(" * \n");
        voContent.append(" * <p>This record provides an immutable data transfer object\n");
        voContent.append(" * representation of the ").append(domainClassName).append(" domain model.</p>\n");
        voContent.append(" * \n");
        voContent.append(" * @author Generated by MyBatis Generator\n");
        voContent.append(" */\n");
        
        // Swagger annotation for class
        if (enableSwagger) {
            voContent.append("@Schema(description = \"").append(domainClassName).append(swaggerDescriptionSuffix).append("\")\n");
        }
        
        // Record declaration
        voContent.append("public record ").append(voClassName).append("(\n");
        
        // Record components (fields)
        List<Field> fields = domainClass.getFields();
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            
            // Add JSON format and DateTime format annotations for LocalDateTime fields
            if (LOCALDATETIME_TYPE.equals(field.getType().getShortName())) {
                voContent.append("    @JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\", timezone = \"GMT+8\")\n");
                voContent.append("    @DateTimeFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")\n");
            }
            
            // Add Swagger annotation for field
            if (enableSwagger) {
                voContent.append("    @Schema(description = \"").append(getFieldDescription(field, introspectedTable)).append("\")\n");
            }
            
            voContent.append("    ").append(field.getType().getShortName()).append(" ").append(field.getName());
            if (i < fields.size() - 1) {
                voContent.append(",");
            }
            voContent.append("\n");
        }
        
        voContent.append(") {}\n");
        
        // Write VO file
        writeVoFile(voContent.toString(), voClassName);
    }
    
    /**
     * Generates a MapStruct mapper interface for domain-VO conversion.
     */
    private void generateMapStructMapper(TopLevelClass domainClass, IntrospectedTable introspectedTable) throws IOException {
        String domainClassName = domainClass.getType().getShortName();
        String voClassName = domainClassName + VO_SUFFIX;
        String mapperClassName = domainClassName + MAPPER_SUFFIX;
        
        StringBuilder mapperContent = new StringBuilder();
        
        // Package declaration
        mapperContent.append("package ").append(mapperPackage).append(";\n\n");
        
        // Imports
        mapperContent.append("import ").append(MAPSTRUCT_MAPPER_CLASS).append(";\n");
        mapperContent.append("import ").append(domainClass.getType().getFullyQualifiedName()).append(";\n");
        mapperContent.append("import ").append(voPackage).append(".").append(voClassName).append(";\n");
        mapperContent.append("import java.util.List;\n\n");
        
        // Interface documentation
        mapperContent.append("/**\n");
        mapperContent.append(" * MapStruct mapper for converting between ").append(domainClassName).append(" and ").append(voClassName).append(".\n");
        mapperContent.append(" * \n");
        mapperContent.append(" * <p>This mapper provides bidirectional conversion methods\n");
        mapperContent.append(" * between domain models and value objects.</p>\n");
        mapperContent.append(" * \n");
        mapperContent.append(" * @author Generated by MyBatis Generator\n");
        mapperContent.append(" */\n");
        
        // Mapper annotation and interface declaration
        mapperContent.append("@Mapper(componentModel = \"").append(mapperComponentModel).append("\")\n");
        mapperContent.append("public interface ").append(mapperClassName).append(" {\n\n");
        
        // Conversion methods
        mapperContent.append("    /**\n");
        mapperContent.append("     * Converts a domain model to a VO.\n");
        mapperContent.append("     * \n");
        mapperContent.append("     * @param ").append(domainClassName.toLowerCase()).append(" the domain model\n");
        mapperContent.append("     * @return the corresponding VO\n");
        mapperContent.append("     */\n");
        mapperContent.append("    ").append(voClassName).append(" toVo(").append(domainClassName).append(" ").append(domainClassName.toLowerCase()).append(");\n\n");
        
        mapperContent.append("    /**\n");
        mapperContent.append("     * Converts a VO to a domain model.\n");
        mapperContent.append("     * \n");
        mapperContent.append("     * @param ").append(voClassName.toLowerCase()).append(" the VO\n");
        mapperContent.append("     * @return the corresponding domain model\n");
        mapperContent.append("     */\n");
        mapperContent.append("    ").append(domainClassName).append(" toDomain(").append(voClassName).append(" ").append(voClassName.toLowerCase()).append(");\n\n");
        
        mapperContent.append("    /**\n");
        mapperContent.append("     * Converts a list of domain models to VOs.\n");
        mapperContent.append("     * \n");
        mapperContent.append("     * @param ").append(domainClassName.toLowerCase()).append("s the list of domain models\n");
        mapperContent.append("     * @return the list of corresponding VOs\n");
        mapperContent.append("     */\n");
        mapperContent.append("    List<").append(voClassName).append("> toVoList(List<").append(domainClassName).append("> ").append(domainClassName.toLowerCase()).append("s);\n\n");
        
        mapperContent.append("    /**\n");
        mapperContent.append("     * Converts a list of VOs to domain models.\n");
        mapperContent.append("     * \n");
        mapperContent.append("     * @param ").append(voClassName.toLowerCase()).append("s the list of VOs\n");
        mapperContent.append("     * @return the list of corresponding domain models\n");
        mapperContent.append("     */\n");
        mapperContent.append("    List<").append(domainClassName).append("> toDomainList(List<").append(voClassName).append("> ").append(voClassName.toLowerCase()).append("s);\n");
        
        mapperContent.append("}\n");
        
        // Write mapper file
        writeMapperFile(mapperContent.toString(), mapperClassName);
    }
    
    /**
     * Adds necessary imports to the VO class.
     */
    private void addImports(StringBuilder content, TopLevelClass domainClass) {
        // Check if LocalDateTime fields exist
        boolean hasLocalDateTime = domainClass.getFields().stream()
            .anyMatch(field -> LOCALDATETIME_TYPE.equals(field.getType().getShortName()));
        
        // Add JSON format and DateTime format imports if LocalDateTime fields exist
        if (hasLocalDateTime) {
            content.append("import ").append(JSON_FORMAT_CLASS).append(";\n");
            content.append("import ").append(DATETIME_FORMAT_CLASS).append(";\n");
        }
        
        // Add Swagger import if enabled
        if (enableSwagger) {
            content.append("import ").append(SWAGGER_SCHEMA_CLASS).append(";\n");
        }
        
        // Collect unique import types from fields
        domainClass.getFields().stream()
            .map(Field::getType)
            .filter(type -> !type.getPackageName().equals("java.lang") && type.getPackageName() != null)
            .map(FullyQualifiedJavaType::getFullyQualifiedName)
            .distinct()
            .sorted()
            .forEach(importName -> content.append("import ").append(importName).append(";\n"));
        
        if (!domainClass.getFields().isEmpty() || enableSwagger) {
            content.append("\n");
        }
    }
    
    /**
     * Generates a description for a field based on database column comments.
     */
    private String getFieldDescription(Field field, IntrospectedTable introspectedTable) {
        String fieldName = field.getName();
        
        // Try to find the corresponding column comment from the database
        for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
            if (column.getJavaProperty().equals(fieldName)) {
                String remarks = column.getRemarks();
                if (remarks != null && !remarks.trim().isEmpty()) {
                    return remarks.trim();
                }
                break;
            }
        }
        
        // Fallback to generated description if no column comment is available
        String readableName = fieldName.replaceAll("([a-z])([A-Z])", "$1 $2").toLowerCase();
        
        // Capitalize first letter
        if (!readableName.isEmpty()) {
            readableName = Character.toUpperCase(readableName.charAt(0)) + readableName.substring(1);
        }
        
        return readableName;
    }
    
    /**
     * Writes the VO record class to a file.
     */
    private void writeVoFile(String content, String className) throws IOException {
        String packagePath = voPackage.replace('.', File.separatorChar);
        String targetPath = voTargetProject + File.separator + packagePath;
        
        File targetDir = new File(targetPath);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        
        File voFile = new File(targetDir, className + ".java");
        try (FileWriter writer = new FileWriter(voFile)) {
            writer.write(content);
        }
        
        System.out.println("Generated VO: " + voFile.getAbsolutePath());
    }
    
    /**
     * Writes the MapStruct mapper interface to a file.
     */
    private void writeMapperFile(String content, String className) throws IOException {
        String packagePath = mapperPackage.replace('.', File.separatorChar);
        String targetPath = mapperTargetProject + File.separator + packagePath;
        
        File targetDir = new File(targetPath);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        
        File mapperFile = new File(targetDir, className + ".java");
        try (FileWriter writer = new FileWriter(mapperFile)) {
            writer.write(content);
        }
        
        System.out.println("Generated Mapper: " + mapperFile.getAbsolutePath());
    }
}