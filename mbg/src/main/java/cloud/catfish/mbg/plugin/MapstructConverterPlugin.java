package cloud.catfish.mbg.plugin;

import cloud.catfish.mbg.comm.CommonConstant;
import cloud.catfish.mbg.util.VelocityUtil;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

public class MapstructConverterPlugin extends PluginAdapter {

    // MapStruct annotation
    private static final String MAPSTRUCT_MAPPER_CLASS = "org.mapstruct.Mapper";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        try {
            // Generate MapStruct converter
            generateMapStructConverter(topLevelClass, introspectedTable);
        } catch (Exception e) {
            System.err.println("Warning: Failed to generate Converter for " + topLevelClass.getType().getShortName() + ": " + e.getMessage());
        }

        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    /**
     * Generates a MapStruct converter interface for domain-VO conversion.
     */
    private void generateMapStructConverter(TopLevelClass domainClass, IntrospectedTable introspectedTable) throws IOException {
        String entityName = domainClass.getType().getShortName();

        String modelClassName = entityName;
        String voClassName = entityName + CommonConstant.VO_SUFFIX_FILE_NAME;
        String converterClassName = entityName + CommonConstant.CONVERTER_SUFFIX_FILE_NAME;

        StringBuilder converterContent = new StringBuilder();

        // Package declaration
        converterContent.append("package ")
                .append(CommonConstant.CONVERTER_PACKAGE_NAME)
                .append(";\n\n");

        // Imports
        converterContent.append("import ")
                .append(MAPSTRUCT_MAPPER_CLASS)
                .append(";\n");
        converterContent.append("import ")
                .append(domainClass.getType().getFullyQualifiedName())
                .append(";\n");
        converterContent.append("import ")
                .append(CommonConstant.VO_PACKAGE_NAME)
                .append(".")
                .append(entityName + CommonConstant.VO_SUFFIX_FILE_NAME)
                .append(";\n");
        converterContent.append("import java.util.List;\n\n");

        // Converter annotation and interface declaration
        converterContent.append("@Mapper(componentModel = \"")
                .append("spring")
                .append("\")\n");
        converterContent.append("public interface ")
                .append(converterClassName)
                .append(" {\n\n");

        // Conversion methods
        converterContent.append("    /**\n");
        converterContent.append("     * Converts a domain model to a VO.\n");
        converterContent.append("     * \n");
        converterContent.append("     * @param ").append(modelClassName.toLowerCase()).append(" the domain model to convert\n");
        converterContent.append("     * @return the corresponding VO, or null if input is null\n");
        converterContent.append("     */\n");
        converterContent.append("    ").append(voClassName).append(" toVo(").append(modelClassName).append(" ").append(modelClassName.toLowerCase()).append(");\n\n");

        converterContent.append("    /**\n");
        converterContent.append("     * Converts a VO to a domain model.\n");
        converterContent.append("     * \n");
        converterContent.append("     * @param ").append(voClassName.toLowerCase()).append(" the VO to convert\n");
        converterContent.append("     * @return the corresponding domain model, or null if input is null\n");
        converterContent.append("     */\n");
        converterContent.append("    ").append(modelClassName).append(" toDomain(").append(voClassName).append(" ").append(voClassName.toLowerCase()).append(");\n\n");

        converterContent.append("    /**\n");
        converterContent.append("     * Converts a list of domain models to VOs.\n");
        converterContent.append("     * \n");
        converterContent.append("     * @param ").append(modelClassName.toLowerCase()).append("s the list of domain models to convert\n");
        converterContent.append("     * @return the list of corresponding VOs, or null if input is null\n");
        converterContent.append("     */\n");
        converterContent.append("    List<").append(voClassName).append("> toVoList(List<").append(modelClassName).append("> ").append(modelClassName.toLowerCase()).append("s);\n\n");

        converterContent.append("    /**\n");
        converterContent.append("     * Converts a list of VOs to domain models.\n");
        converterContent.append("     * \n");
        converterContent.append("     * @param ").append(voClassName.toLowerCase()).append("s the list of VOs to convert\n");
        converterContent.append("     * @return the list of corresponding domain models, or null if input is null\n");
        converterContent.append("     */\n");
        converterContent.append("    List<").append(modelClassName).append("> toDomainList(List<").append(voClassName).append("> ").append(voClassName.toLowerCase()).append("s);\n");

        converterContent.append("}\n");

        // Write converter file
        writeConverterFile(converterContent.toString(), converterClassName);
    }

    /**
     * Writes the MapStruct converter interface to a file.
     */
    private void writeConverterFile(String content, String className) throws IOException {
        StringWriter writer = new StringWriter();
        writer.write(content);
        VelocityUtil.processTemplate(writer,
                CommonConstant.OUTPUT_ABSOLUTE_PATH,
                className + ".java"
        );
        System.out.println("Generated Converter: " + className + ".java");
    }

}
