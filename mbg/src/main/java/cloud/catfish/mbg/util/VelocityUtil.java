package cloud.catfish.mbg.util;

import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

public class VelocityUtil {

    public static void processTemplate(StringWriter writer, String outputPath, String filename) {
        try {
            // 创建输出目录
            File outputDir = new File(outputPath);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            // 写入文件
            FileWriter fileWriter = new FileWriter(new File(outputDir, filename));
            fileWriter.write(writer.toString());
            fileWriter.close();

            System.out.println("Generated: " + outputPath + "/" + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
