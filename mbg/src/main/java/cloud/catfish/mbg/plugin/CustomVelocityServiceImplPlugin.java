package cloud.catfish.mbg.plugin;

import cloud.catfish.mbg.util.StringHelper;
import cloud.catfish.mbg.util.VelocityUtil;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.io.StringWriter;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Custom Velocity Service Implementation Plugin for MyBatis Generator.
 * 
 * This plugin generates Spring Boot service implementation classes using Apache Velocity templates.
 * It automatically creates service implementation classes that implement the corresponding service
 * interfaces and integrate with MyBatis mappers for data access operations.
 * 
 * Features:
 * - Generates service implementation classes with proper annotations
 * - Configurable package structure for services, models, and mappers
 * - Template-based code generation using Velocity
 * - Automatic mapper integration and dependency injection
 * - Customizable response models and naming conventions
 * 
 * Generated service implementations include:
 * - Standard CRUD operations
 * - Proper exception handling
 * - Transaction management annotations
 * - Mapper dependency injection
 * 
 * @author MyBatis Generator Plugin
 * @version 1.0
 */
public class CustomVelocityServiceImplPlugin extends PluginAdapter {

    // Velocity Configuration Constants
    private static final String VELOCITY_INPUT_ENCODING = "UTF-8";
    private static final String VELOCITY_OUTPUT_ENCODING = "UTF-8";
    private static final String VELOCITY_RESOURCE_LOADER = "class";
    private static final String VELOCITY_CLASS_LOADER = "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader";
    
    // Template and Package Configuration
    private static final String SERVICE_IMPL_TEMPLATE = "templates/serviceImpl.vm";
    private static final String DEFAULT_BASE_PACKAGE_PATH = "mbg/src/main/java/cloud/catfish/mbg/service/impl";
    private static final String MODEL_PACKAGE_SUFFIX = ".model";
    private static final String SERVICE_PACKAGE_SUFFIX = ".service";
    private static final String SERVICE_IMPL_PACKAGE_SUFFIX = ".service.impl";
    private static final String MAPPER_PACKAGE_SUFFIX = ".mapper";
    
    // Naming Convention Constants
    private static final String SERVICE_CLASS_PREFIX = "I";
    private static final String SERVICE_CLASS_SUFFIX = "Service";
    private static final String SERVICE_IMPL_CLASS_SUFFIX = "ServiceImpl";
    private static final String MAPPER_CLASS_SUFFIX = "Mapper";
    private static final String REQUEST_PARAM_CLASS_SUFFIX = "RequestParam";
    private static final String VO_CLASS_SUFFIX = "Vo";
    private static final String VO_MAPPER_CLASS_SUFFIX = "VoMapper";
    private static final String VO_PACKAGE_SUFFIX = ".vo";
    private static final String VO_MAPPER_PACKAGE_SUFFIX = ".mapper.vo";
    private static final String DEFAULT_RESPONSE_MODEL = "CommonResult";
    
    // Configuration Properties
    private static final String BASE_PACKAGE_PATH_PROPERTY = "basePackagePath";
    private static final String RESPONSE_MODEL_PROPERTY = "responseModel";
    private static final String ENABLE_DEBUG_OUTPUT_PROPERTY = "enableDebugOutput";
    
    // Instance Fields
    private VelocityEngine velocityEngine;
    private String basePackagePath;
    private String responseModel;
    private boolean enableDebugOutput;

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
            testEngine.getTemplate(SERVICE_IMPL_TEMPLATE);
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
        
        if (enableDebugOutput) {
            System.out.println("CustomVelocityServiceImplPlugin Configuration:");
            System.out.println("  Base Package Path: " + basePackagePath);
            System.out.println("  Response Model: " + responseModel);
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
            generateServiceImpl(topLevelClass, introspectedTable);
            return true;
        } catch (Exception e) {
            if (enableDebugOutput) {
                System.err.println("Error generating service implementation: " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Generates the service implementation class for the given model.
     * 
     * @param topLevelClass the model class
     * @param introspectedTable the table information
     */
    private void generateServiceImpl(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String entityName = topLevelClass.getType().getShortName();
        String packageName = topLevelClass.getType().getPackageName();
        
        // Create Velocity context with all necessary variables
        VelocityContext context = createVelocityContext(entityName, packageName, introspectedTable);
        
        // Generate service implementation content
        String serviceImplContent = generateServiceImplContent(context);
        
        // Write service implementation file
        String serviceImplClassName = SERVICE_CLASS_PREFIX + entityName + SERVICE_IMPL_CLASS_SUFFIX;
        writeServiceImplFile(serviceImplContent, serviceImplClassName);
    }
    
    /**
     * Creates and populates the Velocity context with all necessary variables.
     * 
     * @param entityName the entity name
     * @param packageName the base package name
     * @param introspectedTable the table information
     * @return populated Velocity context
     */
    private VelocityContext createVelocityContext(String entityName, String packageName, IntrospectedTable introspectedTable) {
        VelocityContext context = new VelocityContext();
        
        // Package imports
        context.put("ServicePackage", packageName.replace(MODEL_PACKAGE_SUFFIX, SERVICE_PACKAGE_SUFFIX));
        context.put("modelPackage", packageName);
        context.put("ServiceImplPackage", packageName.replace(MODEL_PACKAGE_SUFFIX, SERVICE_IMPL_PACKAGE_SUFFIX));
        context.put("MapperPackage", packageName.replace(MODEL_PACKAGE_SUFFIX, MAPPER_PACKAGE_SUFFIX));
        
        // Class names
        String serviceClassName = SERVICE_CLASS_PREFIX + entityName + SERVICE_CLASS_SUFFIX;
        String mapperClassName = entityName + MAPPER_CLASS_SUFFIX;
        String serviceImplClassName = SERVICE_CLASS_PREFIX + entityName + SERVICE_IMPL_CLASS_SUFFIX;
        
        context.put("ServiceSimpleName", serviceClassName);
        context.put("DaoSimpleName", mapperClassName);
        context.put("DaoVariableName", StringHelper.firstCharToLower(mapperClassName));
        context.put("ServiceImplSimpleName", serviceImplClassName);
        
        // Variable names
        context.put("ServiceVariableName", StringHelper.firstCharToLower(entityName));
        
        // Model and response configuration
        context.put("ModelSimpleName", entityName);
        context.put("RequestParamClassName", entityName + REQUEST_PARAM_CLASS_SUFFIX);
        context.put("VoClassName", entityName + VO_CLASS_SUFFIX);
        context.put("VoMapperClassName", entityName + VO_MAPPER_CLASS_SUFFIX);
        context.put("voPackage", packageName + VO_PACKAGE_SUFFIX);
        context.put("voMapperPackage", packageName.replace(MODEL_PACKAGE_SUFFIX, "") + VO_MAPPER_PACKAGE_SUFFIX);
        context.put("SimplResponseModel", responseModel);
        
        // Primary key information
        addPrimaryKeyInformation(context, introspectedTable);
        
        if (enableDebugOutput) {
            System.out.println("Generated Velocity context for entity: " + entityName);
        }
        
        return context;
    }
    
    /**
     * Generates the service implementation content using Velocity template.
     * 
     * @param context the Velocity context
     * @return generated service implementation content
     */
    private String generateServiceImplContent(VelocityContext context) {
        try {
            StringWriter writer = new StringWriter();
            Template serviceTemplate = velocityEngine.getTemplate(SERVICE_IMPL_TEMPLATE);
            serviceTemplate.merge(context, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate service implementation content", e);
        }
    }
    
    /**
     * Writes the service implementation file to the specified location.
     * 
     * @param content the service implementation content
     * @param className the service implementation class name
     */
    private void writeServiceImplFile(String content, String className) {
        try {
            StringWriter writer = new StringWriter();
            writer.write(content);
            VelocityUtil.processTemplate(writer, basePackagePath, className + ".java");
            
            if (enableDebugOutput) {
                System.out.println("Generated service implementation: " + className + ".java");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to write service implementation file: " + className, e);
        }
    }
    
    /**
     * Adds primary key information to the Velocity context.
     * 
     * @param context the Velocity context
     * @param introspectedTable the table information
     */
    private void addPrimaryKeyInformation(VelocityContext context, IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        
        if (!primaryKeyColumns.isEmpty()) {
            IntrospectedColumn primaryKeyColumn = primaryKeyColumns.get(0);
            
            // Primary key field information
            context.put("primaryKeyType", primaryKeyColumn.getFullyQualifiedJavaType().getShortName());
            context.put("primaryKeyProperty", primaryKeyColumn.getJavaProperty());
            context.put("primaryKeyColumn", primaryKeyColumn.getActualColumnName());
            
            // Multiple primary keys support
            context.put("hasPrimaryKey", true);
            context.put("hasSinglePrimaryKey", primaryKeyColumns.size() == 1);
            context.put("hasCompositePrimaryKey", primaryKeyColumns.size() > 1);
            
            if (primaryKeyColumns.size() > 1) {
                List<String> pkTypes = primaryKeyColumns.stream()
                    .map(col -> col.getFullyQualifiedJavaType().getShortName())
                    .collect(Collectors.toList());
                List<String> pkProperties = primaryKeyColumns.stream()
                    .map(IntrospectedColumn::getJavaProperty)
                    .collect(Collectors.toList());
                    
                context.put("primaryKeyTypes", pkTypes);
                context.put("primaryKeyProperties", pkProperties);
                
                // Generate method parameters for composite primary keys
                StringBuilder methodParams = new StringBuilder();
                StringBuilder serviceCallParams = new StringBuilder();
                
                for (int i = 0; i < primaryKeyColumns.size(); i++) {
                    IntrospectedColumn col = primaryKeyColumns.get(i);
                    String paramType = col.getFullyQualifiedJavaType().getShortName();
                    String paramName = col.getJavaProperty();
                    
                    if (i > 0) {
                        methodParams.append(", ");
                        serviceCallParams.append(", ");
                    }
                    
                    methodParams.append(paramType).append(" ").append(paramName);
                    serviceCallParams.append(paramName);
                }
                
                context.put("primaryKeyMethodParams", methodParams.toString());
                context.put("primaryKeyServiceCallParams", serviceCallParams.toString());
            } else {
                // Single primary key
                String paramType = primaryKeyColumn.getFullyQualifiedJavaType().getShortName();
                String paramName = primaryKeyColumn.getJavaProperty();
                
                context.put("primaryKeyMethodParam", paramType + " " + paramName);
                context.put("primaryKeyServiceCall", paramName);
            }
        } else {
            context.put("hasPrimaryKey", false);
            context.put("hasSinglePrimaryKey", false);
            context.put("hasCompositePrimaryKey", false);
        }
    }
}