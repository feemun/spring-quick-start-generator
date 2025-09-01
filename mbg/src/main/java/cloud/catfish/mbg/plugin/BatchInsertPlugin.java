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
 * 批量插入插件
 * 为MyBatis Generator生成批量插入方法
 */
public class BatchInsertPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
 * 在Mapper接口中添加批量插入方法
     */
    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        addBatchInsertMethod(interfaze, introspectedTable);
        return true;
    }

    /**
     * 在XML映射文件中添加批量插入的SQL语句
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        addBatchInsertXmlElement(document, introspectedTable);
        return true;
    }

    /**
     * 添加批量插入方法到Mapper接口
     */
    private void addBatchInsertMethod(Interface interfaze, IntrospectedTable introspectedTable) {
        // 获取实体类的完全限定名
        FullyQualifiedJavaType recordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        
        // 创建List<Entity>类型
        FullyQualifiedJavaType listType = new FullyQualifiedJavaType("java.util.List");
        listType.addTypeArgument(recordType);
        
        // 创建方法
        Method method = new Method("batchInsert");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setAbstract(true);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        
        // 添加参数
        Parameter parameter = new Parameter(listType, "records");
        method.addParameter(parameter);
        
        // 添加注释
        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * 批量插入记录");
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
     * 添加批量插入的XML元素
     */
    private void addBatchInsertXmlElement(Document document, IntrospectedTable introspectedTable) {
        XmlElement rootElement = document.getRootElement();
        
        // 创建insert元素
        XmlElement insertElement = new XmlElement("insert");
        insertElement.addAttribute(new Attribute("id", "batchInsert"));
        insertElement.addAttribute(new Attribute("parameterType", "java.util.List"));
        
        // 构建SQL语句
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        sb.append(" (");
        
        // 添加列名
        List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
        boolean first = true;
        for (IntrospectedColumn column : columns) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(column.getActualColumnName());
            first = false;
        }
        sb.append(") VALUES");
        
        insertElement.addElement(new TextElement(sb.toString()));
        
        // 添加foreach元素
        XmlElement foreachElement = new XmlElement("foreach");
        foreachElement.addAttribute(new Attribute("collection", "list"));
        foreachElement.addAttribute(new Attribute("item", "item"));
        foreachElement.addAttribute(new Attribute("separator", ","));
        
        // 构建VALUES部分
        StringBuilder valuesSb = new StringBuilder();
        valuesSb.append("(");
        first = true;
        for (IntrospectedColumn column : columns) {
            if (!first) {
                valuesSb.append(", ");
            }
            valuesSb.append("#{item.");
            valuesSb.append(column.getJavaProperty());
            if (column.getTypeHandler() != null) {
                valuesSb.append(",typeHandler=");
                valuesSb.append(column.getTypeHandler());
            }
            valuesSb.append("}");
            first = false;
        }
        valuesSb.append(")");
        
        foreachElement.addElement(new TextElement(valuesSb.toString()));
        insertElement.addElement(foreachElement);
        
        // 添加到根元素
        rootElement.addElement(insertElement);
    }
}