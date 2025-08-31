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

public class CustomVelocityControllerPlugin extends PluginAdapter {

    private VelocityEngine velocityEngine;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);

        // 初始化 Velocity 引擎
        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        velocityEngine.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        velocityEngine.setProperty("resource.loader", "class");
        velocityEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.init();
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateControllerAndService(topLevelClass, introspectedTable);
        return true;
    }

    private void generateControllerAndService(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String basePackagePath = "mbg/src/main/java/cloud/catfish/mbg/controller";

        String entityName = topLevelClass.getType().getShortName();
        String packageName = topLevelClass.getType().getPackageName();

        VelocityContext context = new VelocityContext();

        // 导包
        context.put("packageName", packageName);
        context.put("servicePackage", packageName.replace(".model", ".service"));
        context.put("ControllerPackage", packageName.replace(".model", ".controller"));

        // 类名
        context.put("ControllerSimpleName", entityName + "Controller");

        // 成员变量
        context.put("ServiceClassName", "I" + entityName + "Service");
        context.put("ServiceVariableName", StringHelper.firstCharToLower(entityName));

        // 接口
        context.put("ModelSimpleName", entityName);
        context.put("apiBaseUrl", "/" + entityName);
        context.put("SimplResponseModel", "CommonResult");


        StringWriter writer = new StringWriter();
        Template controllerTemplate = velocityEngine.getTemplate("templates/controller.vm");
        controllerTemplate.merge(context, writer);

        // 在这里你可以选择将生成的字符串写入文件或以其他方式处理
        System.out.println(writer);

        // 生成 Controller 文件
        VelocityUtil.processTemplate(writer, basePackagePath, entityName + "Controller.java");
    }
}