package cloud.catfish.mbg.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * 批量选择性插入插件
 * 为MyBatis Generator生成批量选择性插入方法（忽略null值）
 */
public class BatchInsertSelectivePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * 在Mapper接口中添加批量选择性插入方法
     */
    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        addBatchInsertSelectiveMethod(interfaze, introspectedTable);
        return true;
    }

    /**
     * 在XML映射文件中添加批量选择性插入的SQL语句
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        addBatchInsertSelectiveXmlElement(document, introspectedTable);
        return true;
    }

    /**
     * 添加批量选择性插入方法到Mapper接口
     */
    private void addBatchInsertSelectiveMethod(Interface interfaze, IntrospectedTable introspectedTable) {
        // 获取实体类的完全限定名
        FullyQualifiedJavaType recordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        
        // 创建List<Entity>类型
        FullyQualifiedJavaType listType = new FullyQualifiedJavaType("java.util.List");
        listType.addTypeArgument(recordType);
        
        // 创建方法
        Method method = new Method("batchInsertSelective");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setAbstract(true);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        
        // 添加参数
        Parameter parameter = new Parameter(listType, "records");
        method.addParameter(parameter);
        
        // 添加注释
        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * 批量选择性插入记录（忽略null值）");
        method.addJavaDocLine(" * @param records 要插入的记录列表");
        method.addJavaDocLine(" * @return 插入的记录数");
        method.addJavaDocLine(" */");
        
        // 添加方法到接口
        interfaze.addMethod(method);
        
        // 添加必要的import
        interfaze.addImportedType(listType);
        interfaze.addImportedType(recordType);
    }

    /**
     * 添加批量选择性插入的XML元素
     */
    private void addBatchInsertSelectiveXmlElement(Document document, IntrospectedTable introspectedTable) {
        XmlElement rootElement = document.getRootElement();
        
        // 创建insert元素
        XmlElement insertElement = new XmlElement("insert");
        insertElement.addAttribute(new Attribute("id", "batchInsertSelective"));
        insertElement.addAttribute(new Attribute("parameterType", "java.util.List"));
        
        // 添加注释
        insertElement.addElement(new TextElement("<!-- 批量选择性插入，忽略null值 -->"));
        
        // 使用动态SQL来处理不同记录可能有不同的非null字段
        insertElement.addElement(new TextElement("INSERT INTO " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));
        
        // 创建foreach元素来遍历记录列表
        XmlElement foreachElement = new XmlElement("foreach");
        foreachElement.addAttribute(new Attribute("collection", "list"));
        foreachElement.addAttribute(new Attribute("item", "item"));
        foreachElement.addAttribute(new Attribute("index", "index"));
        foreachElement.addAttribute(new Attribute("separator", ";"));
        
        // 为每条记录生成单独的INSERT语句
        foreachElement.addElement(new TextElement("("));
        
        // 动态生成列名
        XmlElement trimColumns = new XmlElement("trim");
        trimColumns.addAttribute(new Attribute("prefix", "("));
        trimColumns.addAttribute(new Attribute("suffix", ")"));
        trimColumns.addAttribute(new Attribute("suffixOverrides", ","));
        
        List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
        for (IntrospectedColumn column : columns) {
            XmlElement ifElement = new XmlElement("if");
            ifElement.addAttribute(new Attribute("test", "item." + column.getJavaProperty() + " != null"));
            ifElement.addElement(new TextElement(column.getActualColumnName() + ","));
            trimColumns.addElement(ifElement);
        }
        
        foreachElement.addElement(trimColumns);
        foreachElement.addElement(new TextElement(" VALUES "));
        
        // 动态生成值
        XmlElement trimValues = new XmlElement("trim");
        trimValues.addAttribute(new Attribute("prefix", "("));
        trimValues.addAttribute(new Attribute("suffix", ")"));
        trimValues.addAttribute(new Attribute("suffixOverrides", ","));
        
        for (IntrospectedColumn column : columns) {
            XmlElement ifElement = new XmlElement("if");
            ifElement.addAttribute(new Attribute("test", "item." + column.getJavaProperty() + " != null"));
            
            StringBuilder valueBuilder = new StringBuilder();
            valueBuilder.append("#{item.");
            valueBuilder.append(column.getJavaProperty());
            if (column.getTypeHandler() != null) {
                valueBuilder.append(",typeHandler=");
                valueBuilder.append(column.getTypeHandler());
            }
            valueBuilder.append("},");
            
            ifElement.addElement(new TextElement(valueBuilder.toString()));
            trimValues.addElement(ifElement);
        }
        
        foreachElement.addElement(trimValues);
        foreachElement.addElement(new TextElement(")"));
        
        insertElement.addElement(foreachElement);
        
        // 添加到根元素
        rootElement.addElement(insertElement);
    }
}