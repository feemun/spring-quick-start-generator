package cloud.catfish.mbg.plugin;

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
        String entityName = topLevelClass.getType().getShortName();
        String packageName = topLevelClass.getType().getPackageName();

        VelocityContext context = new VelocityContext();
        context.put("ModelSimpleName", entityName);
        context.put("controllerPackage", packageName.replace(".model", ".controller"));
        context.put("servicePackage", packageName.replace(".model", ".service"));
        context.put("serviceImplPackage", packageName.replace(".model", ".service.impl"));
        context.put("apiBaseUrl", "/" + entityName);

        context.put("SimplResponseModel", "CommonResult");

        StringWriter writer = new StringWriter();
        Template controllerTemplate = velocityEngine.getTemplate("templates/controller.vm");
        controllerTemplate.merge(context, writer);

        // 在这里你可以选择将生成的字符串写入文件或以其他方式处理
        System.out.println(writer.toString());

        // 对于服务层也执行类似的操作
        writer = new StringWriter();
        Template serviceTemplate = velocityEngine.getTemplate("templates/service.vm");
        serviceTemplate.merge(context, writer);
        System.out.println(writer.toString());

        writer = new StringWriter();
        Template serviceImplTemplate = velocityEngine.getTemplate("templates/serviceImpl.vm");
        serviceImplTemplate.merge(context, writer);
        System.out.println(writer.toString());
    }
}