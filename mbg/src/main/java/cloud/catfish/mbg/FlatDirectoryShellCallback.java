package cloud.catfish.mbg;

import cloud.catfish.mbg.comm.CommonConstant;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;

public class FlatDirectoryShellCallback extends DefaultShellCallback {
    public FlatDirectoryShellCallback(boolean overwrite) {
        super(overwrite);
    }

    @Override
    public File getDirectory(String targetProject, String targetPackage) {
        File dir = new File(targetProject);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
}
