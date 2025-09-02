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
 * Custom Velocity Service Plugin for MyBatis Generator.
 * 
 * This plugin generates Spring Boot service interface classes using Apache Velocity templates.
 * It automatically creates service interfaces that define the contract for business logic
 * operations and can be implemented by corresponding service implementation classes.
 * 
 * Features:
 * - Generates service interface classes with proper annotations
 * - Configurable package structure for services and models
 * - Template-based code generation using Velocity
 * - Customizable response models and naming conventions
 * - Automatic API base URL generation for REST endpoints
 * 
 * Generated service interfaces include:
 * - Standard CRUD operation definitions
 * - Proper method signatures with return types
 * - Generic response model support
 * - Configurable naming patterns
 * 
 * @author MyBatis Generator Plugin
 * @version 1.0
 */
public class CustomVelocityServicePlugin extends PluginAdapter {

    // Velocity Configuration Constants
    private static final String VELOCITY_INPUT_ENCODING = "UTF-8";
    private static final String VELOCITY_OUTPUT_ENCODING = "UTF-8";
    private static final String VELOCITY_RESOURCE_LOADER = "class";
    private static final String VELOCITY_CLASS_LOADER = "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader";
    
    // Template and Package Configuration
    private static final String SERVICE_TEMPLATE = "templates/service.vm";
    private static final String DEFAULT_BASE_PACKAGE_PATH = "mbg/src/main/java/cloud/catfish/mbg/service";
    private static final String MODEL_PACKAGE_SUFFIX = ".model";
    private static final String SERVICE_PACKAGE_SUFFIX = ".service";
    
    // Naming Convention Constants
    private static final String SERVICE_CLASS_PREFIX = "I";
    private static final String SERVICE_CLASS_SUFFIX = "Service";
    private static final String REQUEST_PARAM_CLASS_SUFFIX = "RequestParam";
    private static final String DEFAULT_RESPONSE_MODEL = "CommonResult";
    private static final String API_BASE_URL_PREFIX = "/";
    
    // Configuration Properties
    private static final String BASE_PACKAGE_PATH_PROPERTY = "basePackagePath";
    private static final String RESPONSE_MODEL_PROPERTY = "responseModel";
    private static final String ENABLE_DEBUG_OUTPUT_PROPERTY = "enableDebugOutput";
    private static final String API_BASE_URL_PROPERTY = "apiBaseUrl";
    
    // Instance Fields
    private VelocityEngine velocityEngine;
    private String basePackagePath;
    private String responseModel;
    private boolean enableDebugOutput;
    private String apiBaseUrlPattern;

    @Override
    public boolean validate(List<String> warnings) {
        // Validate required utility classes
        try {
            Class.forName("cloud.catfish.mbg.util.StringHelper");
            Class.forName("cloud.catfish.mbg.util.VelocityUtil");
        } catch (ClassNotFoundException e) {
            warnings.add("Required utility classes not found: " + e.getMessage());
            return false;
        }
        
        // Validate Velocity template availability
        try {
            VelocityEngine testEngine = new VelocityEngine();
            initializeVelocityEngine(testEngine);
            testEngine.getTemplate(SERVICE_TEMPLATE);
        } catch (Exception e) {
            warnings.add("Velocity template validation failed: " + e.getMessage());
            return false;
        }
        
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        
        // Parse configuration properties
        parseConfigurationProperties(properties);
        
        // Initialize Velocity engine
        velocityEngine = new VelocityEngine();
        initializeVelocityEngine(velocityEngine);
    }
    
    /**
     * Parses configuration properties and sets default values.
     * 
     * @param properties the configuration properties
     */
    private void parseConfigurationProperties(Properties properties) {
        basePackagePath = properties.getProperty(BASE_PACKAGE_PATH_PROPERTY, DEFAULT_BASE_PACKAGE_PATH);
        responseModel = properties.getProperty(RESPONSE_MODEL_PROPERTY, DEFAULT_RESPONSE_MODEL);
        enableDebugOutput = Boolean.parseBoolean(properties.getProperty(ENABLE_DEBUG_OUTPUT_PROPERTY, "false"));
        apiBaseUrlPattern = properties.getProperty(API_BASE_URL_PROPERTY, API_BASE_URL_PREFIX + "{entityName}");
        
        if (enableDebugOutput) {
            System.out.println("CustomVelocityServicePlugin Configuration:");
            System.out.println("  Base Package Path: " + basePackagePath);
            System.out.println("  Response Model: " + responseModel);
            System.out.println("  API Base URL Pattern: " + apiBaseUrlPattern);
        }
    }
    
    /**
     * Initializes the Velocity engine with standard configuration.
     * 
     * @param engine the Velocity engine to initialize
     */
    private void initializeVelocityEngine(VelocityEngine engine) {
        engine.setProperty(Velocity.INPUT_ENCODING, VELOCITY_INPUT_ENCODING);
        engine.setProperty(Velocity.OUTPUT_ENCODING, VELOCITY_OUTPUT_ENCODING);
        engine.setProperty("resource.loader", VELOCITY_RESOURCE_LOADER);
        engine.setProperty("class.resource.loader.class", VELOCITY_CLASS_LOADER);
        engine.init();
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        try {
            generateService(topLevelClass, introspectedTable);
            return true;
        } catch (Exception e) {
            if (enableDebugOutput) {
                System.err.println("Error generating service interface: " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Generates the service interface class for the given model.
     * 
     * @param topLevelClass the model class
     * @param introspectedTable the table information
     */
    private void generateService(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String entityName = topLevelClass.getType().getShortName();
        String packageName = topLevelClass.getType().getPackageName();
        
        // Create Velocity context with all necessary variables
        VelocityContext context = createVelocityContext(entityName, packageName);
        
        // Generate service interface content
        String serviceContent = generateServiceContent(context);
        
        // Write service interface file
        String serviceClassName = SERVICE_CLASS_PREFIX + entityName + SERVICE_CLASS_SUFFIX;
        writeServiceFile(serviceContent, serviceClassName);
    }
    
    /**
     * Creates and populates the Velocity context with all necessary variables.
     * 
     * @param entityName the entity name
     * @param packageName the base package name
     * @return populated Velocity context
     */
    private VelocityContext createVelocityContext(String entityName, String packageName) {
        VelocityContext context = new VelocityContext();
        
        // Package imports
        context.put("ServicePackage", packageName.replace(MODEL_PACKAGE_SUFFIX, SERVICE_PACKAGE_SUFFIX));
        context.put("modelPackage", packageName);
        
        // Class names
        String serviceClassName = SERVICE_CLASS_PREFIX + entityName + SERVICE_CLASS_SUFFIX;
        context.put("ServiceClassName", serviceClassName);
        
        // Variable names
        context.put("ServiceVariableName", StringHelper.firstCharToLower(entityName));
        
        // Model and API configuration
        context.put("ModelSimpleName", entityName);
        context.put("RequestParamClassName", entityName + REQUEST_PARAM_CLASS_SUFFIX);
        context.put("apiBaseUrl", generateApiBaseUrl(entityName));
        context.put("SimplResponseModel", responseModel);
        
        if (enableDebugOutput) {
            System.out.println("Generated Velocity context for entity: " + entityName);
        }
        
        return context;
    }
    
    /**
     * Generates the API base URL for the given entity.
     * 
     * @param entityName the entity name
     * @return the API base URL
     */
    private String generateApiBaseUrl(String entityName) {
        return apiBaseUrlPattern.replace("{entityName}", entityName);
    }
    
    /**
     * Generates the service interface content using Velocity template.
     * 
     * @param context the Velocity context
     * @return generated service interface content
     */
    private String generateServiceContent(VelocityContext context) {
        try {
            StringWriter writer = new StringWriter();
            Template serviceTemplate = velocityEngine.getTemplate(SERVICE_TEMPLATE);
            serviceTemplate.merge(context, writer);
            
            String content = writer.toString();
            if (enableDebugOutput) {
                System.out.println("Generated service content:");
                System.out.println(content);
            }
            
            return content;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate service interface content", e);
        }
    }
    
    /**
     * Writes the service interface file to the specified location.
     * 
     * @param content the service interface content
     * @param className the service interface class name
     */
    private void writeServiceFile(String content, String className) {
        try {
            StringWriter writer = new StringWriter();
            writer.write(content);
            VelocityUtil.processTemplate(writer, basePackagePath, className + ".java");
            
            if (enableDebugOutput) {
                System.out.println("Generated service interface: " + className + ".java");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to write service interface file: " + className, e);
        }
    }
}