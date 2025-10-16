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
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Custom Velocity Controller Plugin for MyBatis Generator.
 * <p>
 * This plugin generates Spring Boot REST controllers using Apache Velocity templates.
 * It automatically creates controller classes with standard CRUD operations based on
 * the generated model classes.
 * <p>
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

    // Instance fields
    private VelocityEngine velocityEngine;

    /**
     * Validates the plugin configuration and dependencies.
     *
     * @param warnings list to collect validation warnings
     * @return true if validation passes, false otherwise
     */
    @Override
    public boolean validate(List<String> warnings) {
        boolean valid = true;

        // Validate Velocity template availability
        if (velocityEngine != null) {
            velocityEngine.getTemplate("templates/controller.vm");
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

        // Set encoding properties - Velocity expects String values, not Charset objects
        velocityEngine.setProperty(Velocity.INPUT_ENCODING, StandardCharsets.UTF_8.name());
        velocityEngine.setProperty(Velocity.OUTPUT_ENCODING, StandardCharsets.UTF_8.name());

        // Configure classpath resource loader to find templates in resources
        velocityEngine.setProperty(Velocity.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

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
        generateController(topLevelClass, introspectedTable);
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

        // Create Velocity context with all necessary variables
        VelocityContext context = createVelocityContext(entityName, introspectedTable);

        // Generate controller content using template
        String controllerContent = generateControllerContent(context);

        System.out.println("Generated controller for " + entityName + ":");
        // Write controller file to disk
        writeControllerFile(controllerContent, entityName);
    }

    /**
     * Creates a Velocity context with all necessary variables for template processing.
     *
     * @param entityName        the entity class name
     * @param introspectedTable the table information
     * @return configured VelocityContext
     */
    private VelocityContext createVelocityContext(String entityName, IntrospectedTable introspectedTable) {
        VelocityContext context = new VelocityContext();

        // Package information
        context.put("ControllerPackage", CommonConstant.CONTROLLER_PACKAGE_NAME);
        context.put("ServicePackage", CommonConstant.SERVICE_PACKAGE_NAME);
        context.put("ServiceImplPackage", CommonConstant.SERVICE_PACKAGE_NAME);
        context.put("VoPackage", CommonConstant.SERVICE_PACKAGE_NAME);
        context.put("ModelPackage", CommonConstant.MODEL_PACKAGE_NAME);
        context.put("RequestParamPackage", CommonConstant.REQUEST_PARAM_PACKAGE_NAME);
        context.put("MapstructConverterPackage", CommonConstant.SERVICE_PACKAGE_NAME);
        context.put("MapperPackage", CommonConstant.SERVICE_PACKAGE_NAME);
        context.put("VoPackage", CommonConstant.VO_PACKAGE_NAME);

        // Class names
        context.put("ControllerClassName", entityName + CommonConstant.CONTROLLER_SUFFIX_FILE_NAME);
        context.put("ServiceClassName", entityName + CommonConstant.SERVICE_SUFFIX_FILE_NAME);
        context.put("ServiceImplClassName", entityName + CommonConstant.SERVICE_IMPL_SUFFIX_FILE_NAME);
        context.put("ModelClassName", entityName);
        context.put("RequestParamClassName", entityName + CommonConstant.REQUEST_SUFFIX_PARAM_FILE_NAME);
        context.put("VoClassName", entityName + CommonConstant.VO_SUFFIX_FILE_NAME);
        context.put("MapstructConverterClassName", entityName + CommonConstant.REQUEST_SUFFIX_PARAM_FILE_NAME);
        context.put("MapperClassName", entityName + CommonConstant.MAPPER_SUFFIX_FILE_NAME);

        // Variable names
        context.put("ServiceVariableName", StringHelper.firstCharToLower(entityName) + CommonConstant.SERVICE_SUFFIX_FILE_NAME);
        context.put("ServiceImplVariableName", StringHelper.firstCharToLower(entityName) + CommonConstant.SERVICE_SUFFIX_FILE_NAME);
        context.put("VoVariableName", StringHelper.firstCharToLower(entityName) + CommonConstant.VO_SUFFIX_FILE_NAME);
        context.put("RequestParamVariableName", StringHelper.firstCharToLower(entityName) + CommonConstant.REQUEST_SUFFIX_PARAM_FILE_NAME);
        context.put("MapstructConverterVariableName", StringHelper.firstCharToLower(entityName) + CommonConstant.CONVERTER_SUFFIX_FILE_NAME);
        context.put("MapperVariableName", StringHelper.firstCharToLower(entityName) + CommonConstant.MAPPER_SUFFIX_FILE_NAME);

        // API configuration
        context.put("apiBaseUrl", "/" + entityName);

        // Primary key information
        addPrimaryKeyInformation(context, introspectedTable);

        return context;
    }

    /**
     * 为 Velocity 模板上下文添加主键相关信息，用于动态生成 Controller 方法。
     *
     * <p>该方法会分析数据库表的主键结构，并生成以下信息：</p>
     * <ul>
     *   <li>主键基础信息：类型、属性名、列名等</li>
     *   <li>主键集合信息：支持复合主键的属性和类型列表</li>
     *   <li>动态代码片段：方法参数、服务调用参数、URL路径变量</li>
     * </ul>
     *
     * <p>生成的信息将被 Velocity 模板使用，自动适配单主键和复合主键场景。</p>
     *
     * @param context           Velocity 模板上下文，用于存储主键信息
     * @param introspectedTable 数据库表的元数据信息，包含主键详情
     */
    private void addPrimaryKeyInformation(VelocityContext context, IntrospectedTable introspectedTable) {
        // 获取表的主键列信息
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();

        // 检查是否存在主键
        if (primaryKeyColumns == null || primaryKeyColumns.isEmpty()) {
            // 无主键情况：设置标志位为 false
            context.put("hasPrimaryKey", false);
            return;
        }

        // === 设置主键存在标志 ===
        context.put("hasPrimaryKey", true);

        // === 设置第一个主键的基础信息（兼容单主键场景） ===
        IntrospectedColumn firstPrimaryKey = primaryKeyColumns.get(0);
        context.put("primaryKeyType", firstPrimaryKey.getFullyQualifiedJavaType().getShortName());
        context.put("primaryKeyProperty", firstPrimaryKey.getJavaProperty());
        context.put("primaryKeyColumn", firstPrimaryKey.getActualColumnName());

        // === 提取所有主键的属性和类型列表（支持复合主键） ===
        List<String> primaryKeyProperties = primaryKeyColumns.stream()
                .map(IntrospectedColumn::getJavaProperty)
                .collect(Collectors.toList());

        List<String> primaryKeyTypes = primaryKeyColumns.stream()
                .map(column -> column.getFullyQualifiedJavaType().getShortName())
                .collect(Collectors.toList());

        context.put("primaryKeyTypes", primaryKeyTypes);
        context.put("primaryKeyProperties", primaryKeyProperties);

        // === 动态生成 Controller 方法所需的代码片段 ===
        StringBuilder methodParametersBuilder = new StringBuilder();      // 方法参数：@PathVariable Long id
        StringBuilder serviceCallParametersBuilder = new StringBuilder(); // 服务调用参数：id
        StringBuilder pathVariablesBuilder = new StringBuilder();         // URL路径变量：{id}

        // 遍历所有主键列，构建代码片段
        for (int i = 0; i < primaryKeyColumns.size(); i++) {
            IntrospectedColumn primaryKeyColumn = primaryKeyColumns.get(i);
            String parameterType = primaryKeyColumn.getFullyQualifiedJavaType().getShortName();
            String parameterName = primaryKeyColumn.getJavaProperty();

            // 非第一个参数时添加分隔符
            if (i > 0) {
                methodParametersBuilder.append(", ");
                serviceCallParametersBuilder.append(", ");
                pathVariablesBuilder.append("/");
            }

            // 构建方法参数：@PathVariable Long id
            methodParametersBuilder.append("@PathVariable ")
                    .append(parameterType)
                    .append(" ")
                    .append(parameterName);

            // 构建服务调用参数：id
            serviceCallParametersBuilder.append(parameterName);

            // 构建URL路径变量：{id}
            pathVariablesBuilder.append("{")
                    .append(parameterName)
                    .append("}");
        }

        // === 将生成的代码片段添加到模板上下文 ===
        context.put("primaryKeyMethodParams", methodParametersBuilder.toString());
        context.put("primaryKeyServiceCallParams", serviceCallParametersBuilder.toString());
        context.put("primaryKeyPathVariables", pathVariablesBuilder.toString());
    }

    /**
     * Generates controller content using the Velocity template.
     *
     * @param context the Velocity context with template variables
     * @return generated controller content as string
     */
    private String generateControllerContent(VelocityContext context) {
        StringWriter writer = new StringWriter();
        Template controllerTemplate = velocityEngine.getTemplate(CommonConstant.CONTROLLER_TEMPLATE_PATH);
        controllerTemplate.merge(context, writer);
        return writer.toString();
    }

    /**
     * Writes the generated controller content to a file.
     *
     * @param controllerContent the generated controller content
     * @param entityName        the entity name for file naming
     */
    private void writeControllerFile(String controllerContent, String entityName) {
        StringWriter writer = new StringWriter();
        writer.write(controllerContent);

        String fileName = entityName + CommonConstant.CONTROLLER_SUFFIX_FILE_NAME + ".java";
        VelocityUtil.processTemplate(writer,
                CommonConstant.MBG_MODULE_ABSOLUTE_PATH + CommonConstant.RESOURCES_RELATIVE_PATH + CommonConstant.CONTROLLER_PACKAGE_RELATIVE_NAME,
                fileName);
    }
}