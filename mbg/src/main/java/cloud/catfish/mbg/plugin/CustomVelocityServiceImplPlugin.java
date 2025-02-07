package cloud.catfish.mbg.plugin;

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

public class CustomVelocityServiceImplPlugin extends PluginAdapter {

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
        String basePackagePath = "mbg";

        String entityName = topLevelClass.getType().getShortName();
        String packageName = topLevelClass.getType().getPackageName();

        VelocityContext context = new VelocityContext();
        context.put("ModelSimpleName", entityName);
        context.put("serviceImplPackage", packageName.replace(".model", ".service.impl"));

        StringWriter writer = new StringWriter();
        Template serviceImplTemplate = velocityEngine.getTemplate("templates/serviceImpl.vm");
        serviceImplTemplate.merge(context, writer);
        System.out.println(writer);

        // 生成 Service 实现文件
        VelocityUtil.processTemplate(writer, basePackagePath, "test.java");
    }
}