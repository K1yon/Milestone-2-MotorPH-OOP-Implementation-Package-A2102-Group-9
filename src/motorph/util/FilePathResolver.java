package motorph.util;

import java.io.File;

public final class FilePathResolver {

    private FilePathResolver() {}

    public static String resolve(String filename) {
        String[] candidates = {
            filename,
            "data"   + File.separator + filename,
            "src"    + File.separator + filename,
            ".."     + File.separator + filename,
            ".."     + File.separator + "data" + File.separator + filename,
            System.getProperty("user.dir") + File.separator + filename,
            System.getProperty("user.dir") + File.separator + "data" + File.separator + filename,
        };
        for (String path : candidates) {
            if (new File(path).exists()) return path;
        }
        return filename;
    }
}