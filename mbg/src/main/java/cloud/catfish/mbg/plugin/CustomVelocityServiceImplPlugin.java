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
        String basePackagePath = "mbg/src/main/java/cloud/catfish/mbg/service/impl";

        String entityName = topLevelClass.getType().getShortName();
        String packageName = topLevelClass.getType().getPackageName();

        VelocityContext context = new VelocityContext();

        // 导包
        context.put("ServicePackage", packageName.replace(".model", ".service"));
        context.put("modelPackage", packageName);
        context.put("ServiceImplPackage", packageName.replace(".model", ".service") + ".impl");
        context.put("MapperPackage", packageName.replace(".model", ".mapper"));

        // 类名
        String serviceClassName = "I" + entityName + "Service";
        context.put("ServiceSimpleName", serviceClassName);
        context.put("DaoSimpleName", entityName + "Mapper");
        context.put("DaoVariableName", StringHelper.firstCharToLower(entityName + "Mapper"));

        String serviceImplClassName = "I" + entityName + "ServiceImpl";
        context.put("ServiceImplSimpleName", serviceImplClassName);

        // 成员变量
        context.put("ServiceVariableName", StringHelper.firstCharToLower(entityName));

        // 接口
        context.put("ModelSimpleName", entityName);
        context.put("SimplResponseModel", "CommonResult");

        // 对于服务层也执行类似的操作
        StringWriter writer = new StringWriter();
        Template serviceTemplate = velocityEngine.getTemplate("templates/serviceImpl.vm");
        serviceTemplate.merge(context, writer);

        // 生成 Service 文件
        VelocityUtil.processTemplate(writer, basePackagePath, serviceImplClassName + ".java");
    }
}