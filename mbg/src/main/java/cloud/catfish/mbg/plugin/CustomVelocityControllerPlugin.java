package cloud.catfish.mbg.plugin;

import cloud.catfish.mbg.util.StringHelper;
import cloud.catfish.mbg.util.VelocityUtil;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

/**
 * Custom Velocity Controller Plugin for MyBatis Generator.
 * 
 * This plugin generates Spring Boot REST controllers using Apache Velocity templates.
 * It automatically creates controller classes with standard CRUD operations based on
 * the generated model classes.
 * 
 * Features:
 * - Generates REST controllers with proper annotations
 * - Configurable package structure
 * - Template-based code generation using Velocity
 * - Automatic service layer integration
 * - Customizable API base URLs and response models
 * 
 * @author MyBatis Generator Plugin
 * @version 1.0
 */
public class CustomVelocityControllerPlugin extends PluginAdapter {

    // Constants for Velocity configuration
    private static final String INPUT_ENCODING = "UTF-8";
    private static final String OUTPUT_ENCODING = "UTF-8";
    private static final String RESOURCE_LOADER = "resource.loader";
    private static final String CLASS_LOADER = "class";
    private static final String CLASS_RESOURCE_LOADER_CLASS = "class.resource.loader.class";
    private static final String CLASSPATH_RESOURCE_LOADER = "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader";
    
    // Constants for template and package configuration
    private static final String CONTROLLER_TEMPLATE_PATH = "templates/controller.vm";
    private static final String DEFAULT_BASE_PACKAGE_PATH = "mbg/src/main/java/cloud/catfish/mbg/controller";
    private static final String MODEL_PACKAGE_SUFFIX = ".model";
    private static final String SERVICE_PACKAGE_SUFFIX = ".service";
    private static final String CONTROLLER_PACKAGE_SUFFIX = ".controller";
    private static final String PARAM_PACKAGE_SUFFIX = ".model.param";
    private static final String CONTROLLER_CLASS_SUFFIX = "Controller";
    private static final String SERVICE_INTERFACE_PREFIX = "I";
    private static final String SERVICE_CLASS_SUFFIX = "Service";
    private static final String REQUEST_PARAM_CLASS_SUFFIX = "RequestParam";
    private static final String DEFAULT_RESPONSE_MODEL = "CommonResult";
    
    // Configuration properties
    private static final String BASE_PACKAGE_PATH_PROPERTY = "basePackagePath";
    private static final String RESPONSE_MODEL_PROPERTY = "responseModel";
    private static final String ENABLE_DEBUG_OUTPUT_PROPERTY = "enableDebugOutput";
    
    // Instance fields
    private VelocityEngine velocityEngine;
    private String basePackagePath = DEFAULT_BASE_PACKAGE_PATH;
    private String responseModel = DEFAULT_RESPONSE_MODEL;
    private boolean enableDebugOutput = false;

    /**
     * Validates the plugin configuration and dependencies.
     * 
     * @param warnings list to collect validation warnings
     * @return true if validation passes, false otherwise
     */
    @Override
    public boolean validate(List<String> warnings) {
        boolean valid = true;
        
        // Validate that required utility classes are available
        try {
            Class.forName("cloud.catfish.mbg.util.StringHelper");
            Class.forName("cloud.catfish.mbg.util.VelocityUtil");
        } catch (ClassNotFoundException e) {
            warnings.add("Required utility classes not found: " + e.getMessage());
            valid = false;
        }
        
        // Validate Velocity template availability
        if (velocityEngine != null) {
            try {
                velocityEngine.getTemplate(CONTROLLER_TEMPLATE_PATH);
            } catch (Exception e) {
                warnings.add("Controller template not found: " + CONTROLLER_TEMPLATE_PATH);
                valid = false;
            }
        }
        
        return valid;
    }

    /**
     * Sets plugin properties from configuration.
     * 
     * @param properties configuration properties
     */
    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        
        // Parse configuration properties
        parseConfigurationProperties(properties);
        
        // Initialize Velocity engine with configuration
        initializeVelocityEngine();
    }
    
    /**
     * Parses configuration properties and sets instance variables.
     * 
     * @param properties configuration properties
     */
    private void parseConfigurationProperties(Properties properties) {
        // Parse base package path
        String customBasePackagePath = properties.getProperty(BASE_PACKAGE_PATH_PROPERTY);
        if (customBasePackagePath != null && !customBasePackagePath.trim().isEmpty()) {
            this.basePackagePath = customBasePackagePath.trim();
        }
        
        // Parse response model
        String customResponseModel = properties.getProperty(RESPONSE_MODEL_PROPERTY);
        if (customResponseModel != null && !customResponseModel.trim().isEmpty()) {
            this.responseModel = customResponseModel.trim();
        }
        
        // Parse debug output flag
        this.enableDebugOutput = Boolean.parseBoolean(
            properties.getProperty(ENABLE_DEBUG_OUTPUT_PROPERTY, "false")
        );
    }
    
    /**
     * Initializes the Velocity engine with proper configuration.
     */
    private void initializeVelocityEngine() {
        velocityEngine = new VelocityEngine();
        
        // Set encoding properties
        velocityEngine.setProperty(Velocity.INPUT_ENCODING, INPUT_ENCODING);
        velocityEngine.setProperty(Velocity.OUTPUT_ENCODING, OUTPUT_ENCODING);
        
        // Set resource loader properties
        velocityEngine.setProperty(RESOURCE_LOADER, CLASS_LOADER);
        velocityEngine.setProperty(CLASS_RESOURCE_LOADER_CLASS, CLASSPATH_RESOURCE_LOADER);
        
        // Initialize the engine
        velocityEngine.init();
    }

    /**
     * Called when a model base record class is generated.
     * Triggers the controller generation process.
     * 
     * @param topLevelClass the generated model class
     * @param introspectedTable table information
     * @return true to continue with other plugins
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        try {
            generateController(topLevelClass, introspectedTable);
        } catch (Exception e) {
            // Log error but don't fail the generation process
            System.err.println("Error generating controller for " + topLevelClass.getType().getShortName() + ": " + e.getMessage());
            if (enableDebugOutput) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * Generates a Spring Boot REST controller for the given model class.
     * 
     * @param topLevelClass the model class to generate controller for
     * @param introspectedTable table information
     */
    private void generateController(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // Extract entity information
        String entityName = topLevelClass.getType().getShortName();
        String packageName = topLevelClass.getType().getPackageName();
        
        // Create Velocity context with all necessary variables
        VelocityContext context = createVelocityContext(entityName, packageName);
        
        // Generate controller content using template
        String controllerContent = generateControllerContent(context);
        
        // Output debug information if enabled
        if (enableDebugOutput) {
            System.out.println("Generated controller for " + entityName + ":");
            System.out.println(controllerContent);
        }
        
        // Write controller file to disk
        writeControllerFile(controllerContent, entityName);
    }
    
    /**
     * Creates a Velocity context with all necessary variables for template processing.
     * 
     * @param entityName the entity class name
     * @param packageName the base package name
     * @return configured VelocityContext
     */
    private VelocityContext createVelocityContext(String entityName, String packageName) {
        VelocityContext context = new VelocityContext();
        
        // Package information
        context.put("packageName", packageName);
        context.put("servicePackage", packageName.replace(MODEL_PACKAGE_SUFFIX, SERVICE_PACKAGE_SUFFIX));
        context.put("ControllerPackage", packageName.replace(MODEL_PACKAGE_SUFFIX, CONTROLLER_PACKAGE_SUFFIX));
        
        // Class names
        context.put("ControllerSimpleName", entityName + CONTROLLER_CLASS_SUFFIX);
        context.put("ServiceClassName", SERVICE_INTERFACE_PREFIX + entityName + SERVICE_CLASS_SUFFIX);
        context.put("ModelSimpleName", entityName);
        context.put("RequestParamClassName", entityName + REQUEST_PARAM_CLASS_SUFFIX);
        
        // Variable names
        context.put("ServiceVariableName", StringHelper.firstCharToLower(entityName));
        
        // API configuration
        context.put("apiBaseUrl", "/" + entityName);
        context.put("SimplResponseModel", responseModel);
        
        return context;
    }
    
    /**
     * Generates controller content using the Velocity template.
     * 
     * @param context the Velocity context with template variables
     * @return generated controller content as string
     */
    private String generateControllerContent(VelocityContext context) {
        StringWriter writer = new StringWriter();
        
        try {
            Template controllerTemplate = velocityEngine.getTemplate(CONTROLLER_TEMPLATE_PATH);
            controllerTemplate.merge(context, writer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate controller content: " + e.getMessage(), e);
        }
        
        return writer.toString();
    }
    
    /**
     * Writes the generated controller content to a file.
     * 
     * @param controllerContent the generated controller content
     * @param entityName the entity name for file naming
     */
    private void writeControllerFile(String controllerContent, String entityName) {
        try {
            StringWriter writer = new StringWriter();
            writer.write(controllerContent);
            
            String fileName = entityName + CONTROLLER_CLASS_SUFFIX + ".java";
            VelocityUtil.processTemplate(writer, basePackagePath, fileName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write controller file: " + e.getMessage(), e);
        }
    }
}