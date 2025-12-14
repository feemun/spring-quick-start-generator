package cloud.catfish.mbg.plugin;

import cloud.catfish.mbg.comm.CommonConstant;
import cloud.catfish.mbg.util.VelocityUtil;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.io.StringWriter;
import java.util.List;

public class RequestParamPlugin extends PluginAdapter {

    // Import constants
    private static final String SWAGGER_SCHEMA_CLASS = "io.swagger.v3.oas.annotations.media.Schema";
    private static final String DEFAULT_SWAGGER_DESCRIPTION_SUFFIX = " Request Parameters";

    // Validation annotation constants
    private static final String SIZE_CLASS = "jakarta.validation.constraints.Size";
    private static final String EMAIL_CLASS = "jakarta.validation.constraints.Email";

    // Date/Time format constants
    private static final String LOCALDATETIME_TYPE = "LocalDateTime";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateRequestParamClass(topLevelClass, introspectedTable);
        return true;
    }

    /**
     * Generates the RequestParam class for the given domain class.
     */
    private void generateRequestParamClass(TopLevelClass domainClass, IntrospectedTable introspectedTable) {
        String domainClassName = domainClass.getType().getShortName();
        String requestParamClassName = domainClassName + CommonConstant.REQUEST_SUFFIX_PARAM_FILE_NAME;

        StringBuilder content = new StringBuilder();

        // Package declaration
        content.append("package ")
                .append(CommonConstant.REQUEST_PARAM_PACKAGE_NAME)
                .append(";\n\n");

        // Add imports
        addImports(content, domainClass);

        // Class-level annotations
        content.append("@Data\n");
        content.append("@Schema(description = \"")
                .append(getClassDescription(domainClassName))
                .append("\")\n");

        // Class declaration extending BaseRequestParam
        content.append("public class ")
                .append(requestParamClassName)
                .append(" extends BaseRequestParam {\n\n");

        // Generate fields from domain class
        List<Field> fields = domainClass.getFields();
        for (Field field : fields) {
            generateRequestParamField(content, field, introspectedTable);
        }

        // Close class
        content.append("}\n");

        // Write to file
        writeRequestParamFile(content.toString(), requestParamClassName, introspectedTable);
    }

    /**
     * Generates a field for the RequestParam class.
     */
    private void generateRequestParamField(StringBuilder content, Field field, IntrospectedTable introspectedTable) {
        String fieldType = field.getType().getShortName();
        String fieldName = field.getName();

        // Handle LocalDateTime fields for date range queries
        if (LOCALDATETIME_TYPE.equals(fieldType)) {
            generateDateRangeFields(content, field, introspectedTable);
            return;
        }

        // Add field documentation
        String description = getFieldDescription(field, introspectedTable);
        content.append("    @Schema(description = \"").append(description).append("\")\n");

        // Add validation annotations
        addValidationAnnotations(content, field, fieldType);

        // Regular field declaration
        content.append("    private ").append(fieldType).append(" ").append(fieldName).append(";\n\n");
    }

    /**
     * Generates date range fields for LocalDateTime types.
     */
    private void generateDateRangeFields(StringBuilder content, Field field, IntrospectedTable introspectedTable) {
        String fieldName = field.getName();
        String fieldType = field.getType().getShortName();
        String description = getFieldDescription(field, introspectedTable);

        // Start range field
        content.append("    @Schema(description = \"").append(description).append(" start range\")\n");
        content.append("    private ").append(fieldType).append(" ").append(fieldName).append("Start;\n\n");

        // End range field
        content.append("    @Schema(description = \"").append(description).append(" end range\")\n");
        content.append("    private ").append(fieldType).append(" ").append(fieldName).append("End;\n\n");
    }

    /**
     * Adds validation annotations based on field type and database constraints.
     */
    private void addValidationAnnotations(StringBuilder content, Field field, String fieldType) {
        // Add @Size for String fields
        if ("String".equals(fieldType)) {
            content.append("    @Size(max = 255, message = \"").append(field.getName())
                    .append(" cannot exceed 255 characters\")\n");
        }

        // Add @Email for email-like fields
        if (field.getName().toLowerCase().contains("email")) {
            content.append("    @Email(message = \"Invalid email format\")\n");
        }
    }


    /**
     * Adds necessary imports to the generated class.
     */
    private void addImports(StringBuilder content, TopLevelClass domainClass) {
        // Always add basic imports
        content.append("import lombok.Data;\n");
        content.append("import cloud.catfish.common.param.BaseRequestParam;\n");
        content.append("import ").append(SWAGGER_SCHEMA_CLASS).append(";\n");

        // Add validation imports
        content.append("import ").append(SIZE_CLASS).append(";\n");
        content.append("import ").append(EMAIL_CLASS).append(";\n");

        // Check if we need LocalDateTime imports
        boolean hasLocalDateTime = domainClass.getFields().stream()
                .anyMatch(field -> LOCALDATETIME_TYPE.equals(field.getType().getShortName()));

        if (hasLocalDateTime) {
            content.append("import java.time.LocalDateTime;\n");
        }

        content.append("\n");
    }

    /**
     * Gets the class description for Swagger documentation.
     */
    private String getClassDescription(String domainClassName) {
        return domainClassName + DEFAULT_SWAGGER_DESCRIPTION_SUFFIX;
    }

    /**
     * Gets the field description from database column comments or generates one from field name.
     */
    private String getFieldDescription(Field field, IntrospectedTable introspectedTable) {
        String fieldName = field.getName();

        // Try to get description from database column comment
        for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
            if (column.getJavaProperty().equals(fieldName)) {
                String remarks = column.getRemarks();
                if (remarks != null && !remarks.trim().isEmpty()) {
                    return remarks.trim();
                }
                break;
            }
        }

        // Generate description from field name if no column comment
        return generateDescriptionFromFieldName(fieldName);
    }

    /**
     * Generates a readable description from a field name.
     */
    private String generateDescriptionFromFieldName(String fieldName) {
        // Convert camelCase to readable text
        StringBuilder description = new StringBuilder();
        for (int i = 0; i < fieldName.length(); i++) {
            char c = fieldName.charAt(i);
            if (i > 0 && Character.isUpperCase(c)) {
                description.append(" ");
            }
            if (i == 0) {
                description.append(Character.toUpperCase(c));
            } else {
                description.append(Character.toLowerCase(c));
            }
        }
        return description.toString();
    }

    /**
     * Writes the RequestParam class content to a file.
     */
    private void writeRequestParamFile(String content, String className, IntrospectedTable introspectedTable) {
        StringWriter writer = new StringWriter();
        writer.write(content);

        String fileName = className + ".java";
        String tableDir = introspectedTable.getFullyQualifiedTable().getIntrospectedTableName();
        VelocityUtil.processTemplate(writer,
                CommonConstant.OUTPUT_ABSOLUTE_PATH + "/" + tableDir,
                fileName);
    }

}
