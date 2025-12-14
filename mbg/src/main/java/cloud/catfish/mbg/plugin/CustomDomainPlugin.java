package cloud.catfish.mbg.plugin;

import cloud.catfish.comm.CommonConstant;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.java.Method;

import java.util.List;

 
public class CustomDomainPlugin extends PluginAdapter {

    private static final String SERIALIZABLE_TYPE = "java.io.Serializable";
    private static final String SERIAL_ANNOTATION_TYPE = "java.io.Serial";
    private static final String LONG_TYPE = "long";
    private static final String SERIAL_VERSION_UID_FIELD = "serialVersionUID";

    private final FullyQualifiedJavaType serializable;
    private final FullyQualifiedJavaType serialAnnotation;

    public CustomDomainPlugin() {
        super();
        this.serializable = new FullyQualifiedJavaType(SERIALIZABLE_TYPE);
        this.serialAnnotation = new FullyQualifiedJavaType(SERIAL_ANNOTATION_TYPE);
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void setProperties(java.util.Properties properties) {
        // no-op: minimal plugin has no configurable properties
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        makeSerializable(topLevelClass);
        FullyQualifiedJavaType lombokData = new FullyQualifiedJavaType("lombok.Data");
        if (!topLevelClass.getImportedTypes().contains(lombokData)) {
            topLevelClass.addImportedType(lombokData);
            topLevelClass.addAnnotation("@Data");
        }
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        makeSerializable(topLevelClass);
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        makeSerializable(topLevelClass);
        return true;
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass,
                                       IntrospectedColumn introspectedColumn,
                                       IntrospectedTable introspectedTable,
                                       ModelClassType modelClassType) {
        if (isDateTimeType(field.getType())) {
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
        return false;
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass,
                                              IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              ModelClassType modelClassType) {
        return false;
    }

    protected void makeSerializable(TopLevelClass topLevelClass) {
        addJavaSerializableInterface(topLevelClass);
        addSerialVersionUIDField(topLevelClass);
    }
    
    private void addJavaSerializableInterface(TopLevelClass topLevelClass) {
        topLevelClass.addImportedType(serializable);
        topLevelClass.addSuperInterface(serializable);
    }
    
    private void addSerialVersionUIDField(TopLevelClass topLevelClass) {
        Field field = createSerialVersionUIDField();
        topLevelClass.addImportedType(serialAnnotation);
        field.addAnnotation("@Serial");
        topLevelClass.addField(field);
    }
    
    private Field createSerialVersionUIDField() {
        Field field = new Field(SERIAL_VERSION_UID_FIELD, new FullyQualifiedJavaType(LONG_TYPE));
        field.setFinal(true);
        field.setStatic(true);
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setInitializationString("1L");
        return field;
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
