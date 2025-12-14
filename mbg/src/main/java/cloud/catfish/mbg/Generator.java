package cloud.catfish.mbg;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import cloud.catfish.comm.CommonConstant;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * MBG代码生成工具
 * Created by macro on 2018/4/26.
 */
public class Generator {
    public static void main(String[] args) throws Exception {
        List<String> warnings = new ArrayList<>();
        boolean overwrite = true;
        InputStream is = Generator.class.getResourceAsStream("/generatorConfig.xml");
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(is);
        is.close();

        FlatDirectoryShellCallback callback = new FlatDirectoryShellCallback(overwrite);
        MyBatisGenerator gen = new MyBatisGenerator(config, callback, warnings);
        gen.generate(null);
        reorganizeOutputs(CommonConstant.OUTPUT_ABSOLUTE_PATH);
        for (String warning : warnings) {
            System.out.println(warning);
        }
    }

    private static void reorganizeOutputs(String root) {
        try {
            Files.createDirectories(Paths.get(root));
            Files.list(Paths.get(root))
                    .filter(p -> p.getFileName().toString().endsWith("Mapper.xml"))
                    .forEach(xmlPath -> {
                        try {
                            String xmlName = xmlPath.getFileName().toString();
                            String base = xmlName.substring(0, xmlName.length() - "Mapper.xml".length());
                            String content = Files.readString(xmlPath);
                            String table = extractTableName(content);
                            if (table == null || table.isEmpty()) {
                                // Fallback: use lowercase base as table name
                                table = base.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
                            }
                            Path targetDir = Paths.get(root, table);
                            Files.createDirectories(targetDir);

                            // Move xml
                            Files.move(xmlPath, targetDir.resolve(xmlName), StandardCopyOption.REPLACE_EXISTING);

                            // Move related Java files if exist
                            moveIfExists(root, targetDir.toString(), base + ".java");
                            moveIfExists(root, targetDir.toString(), base + "Example.java");
                            moveIfExists(root, targetDir.toString(), base + "Mapper.java");
                        } catch (Exception e) {
                            System.err.println("Reorganize failed for " + xmlPath + ": " + e.getMessage());
                        }
                    });
        } catch (Exception e) {
            System.err.println("Reorganize outputs error: " + e.getMessage());
        }
    }

    private static void moveIfExists(String root, String targetDir, String fileName) {
        try {
            Path src = Paths.get(root, fileName);
            if (Files.exists(src)) {
                Files.move(src, Paths.get(targetDir, fileName), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            System.err.println("Move failed for " + fileName + ": " + e.getMessage());
        }
    }

    private static String extractTableName(String xml) {
        String[] patterns = new String[]{
                "from\\s+([a-zA-Z0-9_]+)",
                "update\\s+([a-zA-Z0-9_]+)",
                "insert\\s+into\\s+([a-zA-Z0-9_]+)"
        };
        for (String pat : patterns) {
            Matcher m = Pattern.compile(pat).matcher(xml);
            if (m.find()) {
                return m.group(1);
            }
        }
        return null;
    }
}
