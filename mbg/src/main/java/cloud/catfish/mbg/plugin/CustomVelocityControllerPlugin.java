package cloud.catfish.mbg.plugin;

import cloud.catfish.mbg.comm.CommonConfig;
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

public class CustomVelocityControllerPlugin extends PluginAdapter {

    // Constants for Velocity configuration
    private static final String INPUT_ENCODING = "UTF-8";
    private static final String OUTPUT_ENCODING = "UTF-8";
    private static final String RESOURCE_LOADER = "resource.loader";
    private static final String CLASS_LOADER = "class";
    private static final String CLASS_RESOURCE_LOADER_CLASS = "class.resource.loader.class";
    private static final String CLASSPATH_RESOURCE_LOADER = "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader";


    // Configuration properties
    private static final String BASE_PACKAGE_PATH_PROPERTY = "basePackagePath";
    private static final String RESPONSE_MODEL_PROPERTY = "responseModel";
    private static final String ENABLE_DEBUG_OUTPUT_PROPERTY = "enableDebugOutput";

    // Instance fields
    private VelocityEngine velocityEngine;
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
                velocityEngine.getTemplate(CommonConfig.CONTROLLER_TEMPLATE_PATH);
            } catch (Exception e) {
                warnings.add("Controller template not found: " + CommonConfig.CONTROLLER_TEMPLATE_PATH);
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

        // Initialize Velocity engine with configuration
        initializeVelocityEngine();
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
     * @param topLevelClass     the generated model class
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
     * @param topLevelClass     the model class to generate controller for
     * @param introspectedTable table information
     */
    private void generateController(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // Extract entity information
        String entityName = topLevelClass.getType().getShortName();
        String packageName = topLevelClass.getType().getPackageName();

        // Create Velocity context with all necessary variables
        VelocityContext context = createVelocityContext(entityName, packageName, introspectedTable);

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
     * @param entityName        the entity class name
     * @param packageName       the base package name
     * @param introspectedTable the table information
     * @return configured VelocityContext
     */
    private VelocityContext createVelocityContext(String entityName, String packageName, IntrospectedTable introspectedTable) {
        VelocityContext context = new VelocityContext();

        // Package information
        context.put("packageName", packageName);
        context.put("servicePackage", CommonConfig.SERVICE_PACKAGE_NAME);
        context.put("ControllerPackage", CommonConfig.CONTROLLER_PACKAGE_NAME);
        context.put("voPackage", CommonConfig.VO_PACKAGE_NAME);

        // Class names
        context.put("ControllerSimpleName", entityName + CommonConfig.CONTROLLER_SUFFIX_FILE_NAME);
        context.put("ServiceClassName", CommonConfig.SERVICE_PACKAGE_NAME + entityName + CommonConfig.SERVICE_SUFFIX_FILE_NAME);
        context.put("ModelSimpleName", entityName);
        context.put("RequestParamClassName", entityName + CommonConfig.REQUEST_SUFFIX_PARAM_FILE_NAME);
        context.put("VoClassName", entityName + CommonConfig.VO_SUFFIX_FILE_NAME);
        context.put("VoMapperClassName", entityName + CommonConfig.MAPSTRUCT_SUFFIX_FILE_NAME);

        // Variable names
        context.put("ServiceVariableName", StringHelper.firstCharToLower(entityName));

        // API configuration
        context.put("apiBaseUrl", "/" + entityName);
        context.put("SimplResponseModel", null);

        // Primary key information
        addPrimaryKeyInformation(context, introspectedTable);

        return context;
    }

    /**
     * Adds primary key information to the Velocity context for dynamic handling.
     *
     * @param context           the Velocity context to add primary key information to
     * @param introspectedTable the table information containing primary key details
     */
    private void addPrimaryKeyInformation(VelocityContext context, IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();

        if (primaryKeyColumns != null && !primaryKeyColumns.isEmpty()) {
            context.put("hasPrimaryKey", true);
            context.put("primaryKeyType", primaryKeyColumns.get(0).getFullyQualifiedJavaType().getShortName());
            context.put("primaryKeyProperty", primaryKeyColumns.get(0).getJavaProperty());
            context.put("primaryKeyColumn", primaryKeyColumns.get(0).getActualColumnName());

            // Extract primary key properties for template iteration
            List<String> pkProperties = primaryKeyColumns.stream()
                    .map(IntrospectedColumn::getJavaProperty)
                    .collect(Collectors.toList());

            List<String> pkTypes = primaryKeyColumns.stream()
                    .map(col -> col.getFullyQualifiedJavaType().getShortName())
                    .collect(Collectors.toList());

            context.put("primaryKeyTypes", pkTypes);
            context.put("primaryKeyProperties", pkProperties);

            // Generate unified method parameters for all primary key scenarios
            StringBuilder methodParams = new StringBuilder();
            StringBuilder serviceCallParams = new StringBuilder();
            StringBuilder pathVariables = new StringBuilder();

            for (int i = 0; i < primaryKeyColumns.size(); i++) {
                IntrospectedColumn col = primaryKeyColumns.get(i);
                String paramType = col.getFullyQualifiedJavaType().getShortName();
                String paramName = col.getJavaProperty();

                if (i > 0) {
                    methodParams.append(", ");
                    serviceCallParams.append(", ");
                    pathVariables.append("/");
                }

                methodParams.append("@PathVariable ").append(paramType).append(" ").append(paramName);
                serviceCallParams.append(paramName);
                pathVariables.append("{").append(paramName).append("}");
            }

            context.put("primaryKeyMethodParams", methodParams.toString());
            context.put("primaryKeyServiceCallParams", serviceCallParams.toString());
            context.put("primaryKeyPathVariables", pathVariables.toString());
        } else {
            context.put("hasPrimaryKey", false);
        }
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
            Template controllerTemplate = velocityEngine.getTemplate(CommonConfig.CONTROLLER_TEMPLATE_PATH);
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
     * @param entityName        the entity name for file naming
     */
    private void writeControllerFile(String controllerContent, String entityName) {
        try {
            StringWriter writer = new StringWriter();
            writer.write(controllerContent);

            String fileName = entityName + CommonConfig.CONTROLLER_SUFFIX_FILE_NAME + ".java";
            VelocityUtil.processTemplate(writer, CommonConfig.PROJECT_ABSOLUTE_PATH, fileName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write controller file: " + e.getMessage(), e);
        }
    }
}