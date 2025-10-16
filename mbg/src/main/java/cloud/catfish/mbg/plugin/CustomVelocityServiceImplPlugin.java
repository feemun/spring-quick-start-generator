package cloud.catfish.mbg.plugin;

import cloud.catfish.mbg.comm.CommonConstant;
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

public class CustomVelocityServiceImplPlugin extends PluginAdapter {

    private static final String VELOCITY_INPUT_ENCODING = "UTF-8";
    private static final String VELOCITY_OUTPUT_ENCODING = "UTF-8";
    private static final String VELOCITY_RESOURCE_LOADER = "class";
    private static final String VELOCITY_CLASS_LOADER = "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader";

    // Instance Fields
    private VelocityEngine velocityEngine;

    @Override
    public boolean validate(List<String> warnings) {
        velocityEngine = new VelocityEngine();
        initializeVelocityEngine(velocityEngine);
        velocityEngine.getTemplate(CommonConstant.SERVICE_IMPL_TEMPLATE_PATH);

        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);

        VelocityEngine velocityEngine = new VelocityEngine();
        initializeVelocityEngine(velocityEngine);
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
        generateServiceImpl(topLevelClass, introspectedTable);
        return true;
    }

    /**
     * Generates the service implementation class for the given model.
     *
     * @param topLevelClass     the model class
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
        String serviceImplClassName = entityName + CommonConstant.SERVICE_IMPL_SUFFIX_FILE_NAME;
        writeServiceImplFile(serviceImplContent, serviceImplClassName);
    }

    /**
     * Creates and populates the Velocity context with all necessary variables.
     *
     * @param entityName        the entity name
     * @param packageName       the base package name
     * @param introspectedTable the table information
     * @return populated Velocity context
     */
    private VelocityContext createVelocityContext(String entityName, String packageName, IntrospectedTable introspectedTable) {
        VelocityContext context = new VelocityContext();

        // Package information
        context.put("ControllerPackage", CommonConstant.CONTROLLER_PACKAGE_NAME);
        context.put("ServicePackage", CommonConstant.SERVICE_PACKAGE_NAME);
        context.put("ServiceImplPackage", CommonConstant.SERVICE_IMPL_PACKAGE_NAME);
        context.put("VoPackage", CommonConstant.SERVICE_PACKAGE_NAME);
        context.put("ModelPackage", CommonConstant.MODEL_PACKAGE_NAME);
        context.put("RequestParamPackage", CommonConstant.REQUEST_PARAM_PACKAGE_NAME);
        context.put("MapstructConverterPackage", CommonConstant.SERVICE_PACKAGE_NAME);
        context.put("MapperPackage", CommonConstant.MAPPER_PACKAGE_NAME);
        context.put("VoPackage", CommonConstant.VO_PACKAGE_NAME);

        // Class names
        context.put("ControllerClassName", entityName + CommonConstant.CONTROLLER_SUFFIX_FILE_NAME);
        context.put("ServiceClassName", entityName + CommonConstant.SERVICE_SUFFIX_FILE_NAME);
        context.put("ServiceImplClassName", entityName + CommonConstant.SERVICE_IMPL_SUFFIX_FILE_NAME);
        context.put("ModelClassName", entityName);
        context.put("RequestParamClassName", entityName + CommonConstant.REQUEST_SUFFIX_PARAM_FILE_NAME);
        context.put("VoClassName", entityName + CommonConstant.VO_SUFFIX_FILE_NAME);
        context.put("MapstructConverterClassName", entityName + CommonConstant.CONVERTER_SUFFIX_FILE_NAME);
        context.put("MapperClassName", entityName + CommonConstant.MAPPER_SUFFIX_FILE_NAME);

        // Variable names
        context.put("ServiceVariableName", StringHelper.firstCharToLower(entityName) + CommonConstant.SERVICE_SUFFIX_FILE_NAME);
        context.put("ServiceImplVariableName", StringHelper.firstCharToLower(entityName) + CommonConstant.SERVICE_SUFFIX_FILE_NAME);
        context.put("VoVariableName", StringHelper.firstCharToLower(entityName) + CommonConstant.VO_SUFFIX_FILE_NAME);
        context.put("RequestParamVariableName", StringHelper.firstCharToLower(entityName) + CommonConstant.REQUEST_SUFFIX_PARAM_FILE_NAME);
        context.put("MapstructConverterVariableName", StringHelper.firstCharToLower(entityName) + CommonConstant.CONVERTER_SUFFIX_FILE_NAME);
        context.put("MapperVariableName", StringHelper.firstCharToLower(entityName) + CommonConstant.MAPPER_SUFFIX_FILE_NAME);

        // Primary key information
        addPrimaryKeyInformation(context, introspectedTable);
        System.out.println("Generated Velocity context for entity: " + entityName);
        return context;
    }

    /**
     * Generates the service implementation content using Velocity template.
     *
     * @param context the Velocity context
     * @return generated service implementation content
     */
    private String generateServiceImplContent(VelocityContext context) {
        StringWriter writer = new StringWriter();
        Template serviceTemplate = velocityEngine.getTemplate(CommonConstant.SERVICE_IMPL_TEMPLATE_PATH);
        serviceTemplate.merge(context, writer);
        return writer.toString();
    }

    /**
     * Writes the service implementation file to the specified location.
     *
     * @param content   the service implementation content
     * @param className the service implementation class name
     */
    private void writeServiceImplFile(String content, String className) {
        StringWriter writer = new StringWriter();
        writer.write(content);
        VelocityUtil.processTemplate(writer,
                CommonConstant.MBG_MODULE_ABSOLUTE_PATH + CommonConstant.RESOURCES_RELATIVE_PATH + CommonConstant.SERVICE_IMPL_PACKAGE_RELATIVE_NAME,
                className + ".java");

        System.out.println("Generated service implementation: " + className + ".java");
    }

    /**
     * Adds primary key information to the Velocity context.
     *
     * @param context           the Velocity context
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

            // Unified primary key support - treat single key as special case of multiple keys
            context.put("hasPrimaryKey", true);

            List<String> pkTypes = primaryKeyColumns.stream()
                    .map(col -> col.getFullyQualifiedJavaType().getShortName())
                    .collect(Collectors.toList());
            List<String> pkProperties = primaryKeyColumns.stream()
                    .map(IntrospectedColumn::getJavaProperty)
                    .collect(Collectors.toList());

            context.put("primaryKeyTypes", pkTypes);
            context.put("primaryKeyProperties", pkProperties);

            // Generate unified method parameters for all primary key scenarios
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
            context.put("hasPrimaryKey", false);
        }
    }
}