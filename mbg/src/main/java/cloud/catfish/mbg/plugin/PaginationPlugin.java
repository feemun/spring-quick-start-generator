package cloud.catfish.mbg.plugin;

import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;

import java.util.List;

public class PaginationPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /***
     * 为 Example 模型添加 limit、offset 字段
     */
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
                                              IntrospectedTable introspectedTable) {

        // limit
        Field limit = new Field("limit", FullyQualifiedJavaType.getIntInstance());
        limit.setVisibility(JavaVisibility.PRIVATE);
        topLevelClass.addField(limit);

        Method setLimit = new Method("setLimit");
        setLimit.setVisibility(JavaVisibility.PUBLIC);
        setLimit.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), "limit"));
        setLimit.addBodyLine("this.limit = limit;");
        topLevelClass.addMethod(setLimit);

        Method getLimit = new Method("getLimit");
        getLimit.setVisibility(JavaVisibility.PUBLIC);
        getLimit.setReturnType(FullyQualifiedJavaType.getIntInstance());
        getLimit.addBodyLine("return limit;");
        topLevelClass.addMethod(getLimit);

        // offset
        Field offset = new Field("offset", FullyQualifiedJavaType.getIntInstance());
        offset.setVisibility(JavaVisibility.PRIVATE);
        topLevelClass.addField(offset);

        Method setOffset = new Method("setOffset");
        setOffset.setVisibility(JavaVisibility.PUBLIC);
        setOffset.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), "offset"));
        setOffset.addBodyLine("this.offset = offset;");
        topLevelClass.addMethod(setOffset);

        Method getOffset = new Method("getOffset");
        getOffset.setVisibility(JavaVisibility.PUBLIC);
        getOffset.setReturnType(FullyQualifiedJavaType.getIntInstance());
        getOffset.addBodyLine("return offset;");
        topLevelClass.addMethod(getOffset);

        return true;
    }

    /**
     * 在 selectByExample SQL 中增加 limit/offset
     */
    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
                                                                     IntrospectedTable introspectedTable) {

        // 添加 limit
        XmlElement ifLimitNotNull = new XmlElement("if");
        ifLimitNotNull.addAttribute(new Attribute("test", "limit != null"));
        ifLimitNotNull.addElement(new TextElement("limit #{limit}"));
        element.addElement(ifLimitNotNull);

        // 添加 offset
        XmlElement ifOffsetNotNull = new XmlElement("if");
        ifOffsetNotNull.addAttribute(new Attribute("test", "offset != null"));
        ifOffsetNotNull.addElement(new TextElement("offset #{offset}"));
        element.addElement(ifOffsetNotNull);

        return true;
    }
}