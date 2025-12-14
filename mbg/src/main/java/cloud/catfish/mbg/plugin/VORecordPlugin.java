package cloud.catfish.mbg.plugin;

import cloud.catfish.mbg.comm.CommonConstant;
import cloud.catfish.mbg.util.VelocityUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.io.StringWriter;
import java.util.List;

@Slf4j
public class VORecordPlugin extends PluginAdapter {

    // Swagger3 annotations
    private static final String SWAGGER_SCHEMA_CLASS = "io.swagger.v3.oas.annotations.media.Schema";

    // Constants for JSON and DateTime format annotations
    private static final String JSON_FORMAT_CLASS = "com.fasterxml.jackson.annotation.JsonFormat";
    private static final String DATETIME_FORMAT_CLASS = "org.springframework.format.annotation.DateTimeFormat";
    private static final String LOCALDATETIME_TYPE = "LocalDateTime";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateVoRecord(topLevelClass, introspectedTable);
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    /**
     * Generates a VO record class based on the domain model.
     */
    private void generateVoRecord(TopLevelClass domainClass, IntrospectedTable introspectedTable) {
        String entityName = domainClass.getType().getShortName();

        String voClassName = entityName + CommonConstant.VO_SUFFIX_FILE_NAME;

        StringBuilder voContent = new StringBuilder();

        // Package declaration
        voContent.append("package ")
                .append(CommonConstant.VO_PACKAGE_NAME)
                .append(";\n\n");

        // Imports
        addImports(voContent, domainClass);

        // Swagger annotation for class
        voContent.append("@Schema(description = \"")
                .append(entityName)
                .append("\")\n");

        // Record declaration
        voContent.append("public record ").append(voClassName).append("(\n");

        // Record components (fields)
        List<Field> fields = domainClass.getFields();
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);

            // Add JSON format and DateTime format annotations for LocalDateTime fields
            if (LOCALDATETIME_TYPE.equals(field.getType().getShortName())) {
                voContent.append("    " + CommonConstant.DEFAULT_JSON_FORMAT + "\n");
                voContent.append("    " + CommonConstant.DEFAULT_DATE_TIME_FORMAT + "\n");
            }

            // Add Swagger annotation for field
            voContent.append("    @Schema(description = \"")
                    .append(getFieldDescription(field, introspectedTable))
                    .append("\")\n");

            voContent.append("    ").append(field.getType().getShortName()).append(" ").append(field.getName());
            if (i < fields.size() - 1) {
                voContent.append(",");
            }
            voContent.append("\n");
        }

        voContent.append(") {}\n");

        // Write VO file
        writeVoFile(voContent.toString(), voClassName, introspectedTable);
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

        // Always add Swagger import
        content.append("import ").append(SWAGGER_SCHEMA_CLASS).append(";\n");

        // Collect unique import types from fields
        domainClass.getFields().stream()
                .map(Field::getType)
                .filter(type -> !type.getPackageName().equals("java.lang") && type.getPackageName() != null)
                .map(FullyQualifiedJavaType::getFullyQualifiedName)
                .distinct()
                .sorted()
                .forEach(importName -> content.append("import ").append(importName).append(";\n"));

        if (!domainClass.getFields().isEmpty()) {
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
    private void writeVoFile(String content, String className, IntrospectedTable introspectedTable) {
        StringWriter writer = new StringWriter();
        writer.write(content);
        String tableDir = introspectedTable.getFullyQualifiedTable().getIntrospectedTableName();
        VelocityUtil.processTemplate(writer,
                CommonConstant.OUTPUT_ABSOLUTE_PATH + "/" + tableDir,
                className + ".java"
        );
        log.info("Generated VO: {}.java", className);
    }

}
