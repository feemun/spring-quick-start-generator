package cloud.catfish.mbg.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class DomainModelPlugin extends PluginAdapter {

    // Configuration property keys
    private static final String ENABLE_DATE_TIME_ANNOTATIONS = "enableDateTimeAnnotations";
    private static final String DATE_TIME_PATTERN = "dateTimePattern";
    private static final String TIMEZONE = "timezone";
    
    // Default values
    private static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_TIMEZONE = "GMT+8";
    
    // Configuration fields
    private boolean enableDateTimeAnnotations = true;
    private String dateTimePattern = DEFAULT_DATE_TIME_PATTERN;
    private String timezone = DEFAULT_TIMEZONE;

    @Override
    public boolean validate(List<String> warnings) {
        // Parse configuration properties
        parseConfigurationProperties();
        
        // Validate configuration
        if (dateTimePattern == null || dateTimePattern.trim().isEmpty()) {
            warnings.add("DomainModelPlugin: dateTimePattern is empty, using default: " + DEFAULT_DATE_TIME_PATTERN);
            dateTimePattern = DEFAULT_DATE_TIME_PATTERN;
        }
        
        if (timezone == null || timezone.trim().isEmpty()) {
            warnings.add("DomainModelPlugin: timezone is empty, using default: " + DEFAULT_TIMEZONE);
            timezone = DEFAULT_TIMEZONE;
        }
        
        return true;
    }
    
    /**
     * Parses configuration properties from the plugin configuration.
     */
    private void parseConfigurationProperties() {
        if (properties != null) {
            enableDateTimeAnnotations = Boolean.parseBoolean(properties.getProperty(ENABLE_DATE_TIME_ANNOTATIONS, "true"));
            String oldJson = properties.getProperty("enableJsonFormat");
            String oldDateTime = properties.getProperty("enableDateTimeFormat");
            if (oldJson != null || oldDateTime != null) {
                boolean v1 = oldJson != null && Boolean.parseBoolean(oldJson);
                boolean v2 = oldDateTime != null && Boolean.parseBoolean(oldDateTime);
                enableDateTimeAnnotations = v1 || v2;
            }
            dateTimePattern = properties.getProperty(DATE_TIME_PATTERN, DEFAULT_DATE_TIME_PATTERN);
            timezone = properties.getProperty(TIMEZONE, DEFAULT_TIMEZONE);
        }
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, 
                                     IntrospectedColumn introspectedColumn, 
                                     IntrospectedTable introspectedTable, 
                                     ModelClassType modelClassType) {
        if (isDateTimeType(field.getType()) && enableDateTimeAnnotations) {
            topLevelClass.addImportedType(new FullyQualifiedJavaType("com.fasterxml.jackson.annotation.JsonFormat"));
            topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.format.annotation.DateTimeFormat"));
            field.addAnnotation("@JsonFormat(pattern = \"" + dateTimePattern + "\", timezone = \"" + timezone + "\")");
            field.addAnnotation("@DateTimeFormat(pattern = \"" + dateTimePattern + "\")");
        }
        return true;
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass,
                                            IntrospectedColumn introspectedColumn,
                                            IntrospectedTable introspectedTable,
                                            ModelClassType modelClassType) {
        // Return false to disable getter generation if configured
        return false;
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, 
                                            IntrospectedColumn introspectedColumn, 
                                            IntrospectedTable introspectedTable, 
                                            ModelClassType modelClassType) {
        // Return false to disable setter generation if configured
        return false;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType lombokData = new FullyQualifiedJavaType("lombok.Data");
        if (!topLevelClass.getImportedTypes().contains(lombokData)) {
            topLevelClass.addImportedType(lombokData);
            topLevelClass.addAnnotation("@Data");
        }
        return true;
    }

    private boolean isDateTimeType(FullyQualifiedJavaType type) {
        String name = type.getFullyQualifiedName();
        return name.equals("java.time.LocalDateTime")
                || name.equals("java.time.LocalDate")
                || name.equals("java.time.LocalTime")
                || name.equals("java.util.Date")
                || name.equals("java.sql.Date")
                || name.equals("java.sql.Timestamp");
    }

}
