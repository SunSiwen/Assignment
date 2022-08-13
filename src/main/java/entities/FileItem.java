package entities;

import java.io.File;

/**
 * @author Siwen Sun
 * @date 2022/07/31/ 1:04
 */
public class FileItem {

    private final String fileName;
    private final File file;
    private int type;
    private static final String[] txtTypes = {"txt"};

    public String getFileName() {
        return fileName;
    }

    public File getFile() {
        return file;
    }

    public int getType() {
        return type;
    }

    public static final int TEXT = 1;
    public static final int BAD_FORMAT = -1;

    public FileItem(File file) {
        this.file = file;
        fileName = file.getName();
        String suffix = getFileSuffix(fileName);
        type = BAD_FORMAT;
        if (contains(txtTypes, suffix))
            type = TEXT;
    }


    /**
     *
     * @author Siwen Sun
     * @date 2022/8/13 13:06
     * @param types
     * @param suffix
     * @return boolean
     */
    public boolean contains(String[] types, String suffix) {
        suffix = suffix.toLowerCase();
        for (String s : types) {
            if (s.equals(suffix))
                return true;
        }
        return false;
    }

    /**
     *
     * @author Siwen Sun
     * @date 2022/8/13 13:06
     * @param name: file name
     * @return java.lang.String
     */
    public String getFileSuffix(String name) {
        int pos = name.lastIndexOf('.');
        if (pos > 0)
            return name.substring(pos + 1);
        return "";
    }

    @Override
    public String toString() {
        return fileName;
    }
}