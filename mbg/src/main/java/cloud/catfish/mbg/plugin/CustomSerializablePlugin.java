package cloud.catfish.mbg.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.IntrospectedTable.TargetRuntime;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.kotlin.KotlinFile;
import org.mybatis.generator.api.dom.kotlin.KotlinType;

import java.util.List;
import java.util.Properties;

/**
 * Custom MyBatis Generator plugin that adds Serializable interface and serialVersionUID field
 * to generated model classes. Supports both Java and Kotlin classes, with optional GWT support.
 * 
 * <p>Configuration properties:
 * <ul>
 *   <li>addGWTInterface - Add GWT IsSerializable interface (default: false)</li>
 *   <li>suppressJavaInterface - Suppress Java Serializable interface (default: false)</li>
 *   <li>serialVersionUID - Custom serialVersionUID value (default: 1L)</li>
 *   <li>addSerialAnnotation - Add @Serial annotation to serialVersionUID (default: true)</li>
 * </ul>
 * 
 * @author MyBatis Generator Custom Plugin
 * @version 2.0
 */
public class CustomSerializablePlugin extends PluginAdapter {

    // Constants for type names and property names
    private static final String SERIALIZABLE_TYPE = "java.io.Serializable";
    private static final String GWT_SERIALIZABLE_TYPE = "com.google.gwt.user.client.rpc.IsSerializable";
    private static final String SERIAL_ANNOTATION_TYPE = "java.io.Serial";
    private static final String LONG_TYPE = "long";
    private static final String SERIAL_VERSION_UID_FIELD = "serialVersionUID";
    private static final String ADD_GWT_INTERFACE_PROPERTY = "addGWTInterface";
    private static final String SUPPRESS_JAVA_INTERFACE_PROPERTY = "suppressJavaInterface";
    private static final String SERIAL_VERSION_UID_PROPERTY = "serialVersionUID";
    private static final String ADD_SERIAL_ANNOTATION_PROPERTY = "addSerialAnnotation";
    private static final String DEFAULT_SERIAL_VERSION_UID = "1L";
    
    // Type references
    private final FullyQualifiedJavaType serializable;
    private final FullyQualifiedJavaType gwtSerializable;
    private final FullyQualifiedJavaType serialAnnotation;
    
    // Configuration properties
    private boolean addGWTInterface = false;
    private boolean suppressJavaInterface = false;
    private String serialVersionUID = DEFAULT_SERIAL_VERSION_UID;
    private boolean addSerialAnnotation = true;

    public CustomSerializablePlugin() {
        super();
        this.serializable = new FullyQualifiedJavaType(SERIALIZABLE_TYPE);
        this.gwtSerializable = new FullyQualifiedJavaType(GWT_SERIALIZABLE_TYPE);
        this.serialAnnotation = new FullyQualifiedJavaType(SERIAL_ANNOTATION_TYPE);
    }

    @Override
    public boolean validate(List<String> warnings) {
        // Validate serialVersionUID format if provided
        if (serialVersionUID != null && !serialVersionUID.matches("\\d+L?")) {
            warnings.add("CustomSerializablePlugin: Invalid serialVersionUID format. Expected format: number followed by optional 'L' (e.g., '1L', '123L')");
            return false;
        }
        
        // Validate conflicting configurations
        if (suppressJavaInterface && addGWTInterface) {
            warnings.add("CustomSerializablePlugin: Cannot suppress Java Serializable interface while adding GWT interface. GWT interface requires Java Serializable.");
            return false;
        }
        
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        
        // Parse boolean properties with default values
        this.addGWTInterface = Boolean.parseBoolean(properties.getProperty(ADD_GWT_INTERFACE_PROPERTY, "false"));
        this.suppressJavaInterface = Boolean.parseBoolean(properties.getProperty(SUPPRESS_JAVA_INTERFACE_PROPERTY, "false"));
        this.addSerialAnnotation = Boolean.parseBoolean(properties.getProperty(ADD_SERIAL_ANNOTATION_PROPERTY, "true"));
        
        // Parse serialVersionUID with validation
        String customSerialVersionUID = properties.getProperty(SERIAL_VERSION_UID_PROPERTY);
        if (customSerialVersionUID != null && !customSerialVersionUID.trim().isEmpty()) {
            this.serialVersionUID = customSerialVersionUID.trim();
            // Ensure it ends with 'L' if it's just a number
            if (this.serialVersionUID.matches("\\d+") && !this.serialVersionUID.endsWith("L")) {
                this.serialVersionUID += "L";
            }
        }
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        makeSerializable(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        makeSerializable(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        makeSerializable(topLevelClass, introspectedTable);
        return true;
    }

    /**
     * Makes the given TopLevelClass serializable by adding appropriate interfaces and fields.
     * 
     * @param topLevelClass the class to make serializable
     * @param introspectedTable the table information
     */
    protected void makeSerializable(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // Add GWT interface if configured
        if (addGWTInterface) {
            addGWTSerializableInterface(topLevelClass);
        }

        // Add Java Serializable interface and serialVersionUID field if not suppressed
        if (!suppressJavaInterface) {
            addJavaSerializableInterface(topLevelClass);
            addSerialVersionUIDField(topLevelClass, introspectedTable);
        }
    }
    
    /**
     * Adds GWT IsSerializable interface to the class.
     * 
     * @param topLevelClass the class to modify
     */
    private void addGWTSerializableInterface(TopLevelClass topLevelClass) {
        topLevelClass.addImportedType(gwtSerializable);
        topLevelClass.addSuperInterface(gwtSerializable);
    }
    
    /**
     * Adds Java Serializable interface to the class.
     * 
     * @param topLevelClass the class to modify
     */
    private void addJavaSerializableInterface(TopLevelClass topLevelClass) {
        topLevelClass.addImportedType(serializable);
        topLevelClass.addSuperInterface(serializable);
    }
    
    /**
     * Adds serialVersionUID field to the class with proper annotations and comments.
     * 
     * @param topLevelClass the class to modify
     * @param introspectedTable the table information for comment generation
     */
    private void addSerialVersionUIDField(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // Create the serialVersionUID field
        Field field = createSerialVersionUIDField();
        
        // Add @Serial annotation if configured
        if (addSerialAnnotation) {
            topLevelClass.addImportedType(serialAnnotation);
            field.addAnnotation("@Serial");
        }
        
        // Add appropriate comments based on target runtime
        addFieldComments(field, introspectedTable, topLevelClass);
        
        // Add the field to the class
        topLevelClass.addField(field);
    }
    
    /**
     * Creates the serialVersionUID field with proper configuration.
     * 
     * @return the configured Field object
     */
    private Field createSerialVersionUIDField() {
        Field field = new Field(SERIAL_VERSION_UID_FIELD, new FullyQualifiedJavaType(LONG_TYPE));
        field.setFinal(true);
        field.setStatic(true);
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setInitializationString(serialVersionUID);
        return field;
    }
    
    /**
     * Adds appropriate comments to the serialVersionUID field based on the target runtime.
     * 
     * @param field the field to add comments to
     * @param introspectedTable the table information
     * @param topLevelClass the class containing the field
     */
    private void addFieldComments(Field field, IntrospectedTable introspectedTable, TopLevelClass topLevelClass) {
        if (introspectedTable.getTargetRuntime() == TargetRuntime.MYBATIS3_DSQL) {
            context.getCommentGenerator().addFieldAnnotation(field, introspectedTable, topLevelClass.getImportedTypes());
        } else {
            context.getCommentGenerator().addFieldComment(field, introspectedTable);
        }
    }

    @Override
    public boolean kotlinDataClassGenerated(KotlinFile kotlinFile, KotlinType dataClass,
                                            IntrospectedTable introspectedTable) {
        kotlinFile.addImport("java.io.Serializable"); //$NON-NLS-1$
        dataClass.addSuperType("Serializable"); //$NON-NLS-1$
        return true;
    }
}
