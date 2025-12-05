package cloud.catfish.mbg.plugin;

import cloud.catfish.mbg.comm.CommonConstant;
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

    // Configuration fields
    private boolean enableDateTimeAnnotations = true;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass,
                                       IntrospectedColumn introspectedColumn,
                                       IntrospectedTable introspectedTable,
                                       ModelClassType modelClassType) {
        if (isDateTimeType(field.getType()) && enableDateTimeAnnotations) {
            topLevelClass.addImportedType(new FullyQualifiedJavaType("com.fasterxml.jackson.annotation.JsonFormat"));
            topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.format.annotation.DateTimeFormat"));
            field.addAnnotation(CommonConstant.DEFAULT_JSON_FORMAT);
            field.addAnnotation(CommonConstant.DEFAULT_DATE_TIME_FORMAT);
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
