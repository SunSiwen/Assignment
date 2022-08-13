package entities;

import java.io.File;

/**
 * @author Siwen Sun
 * @date 2022/07/31/ 1:04
 */
public class FileItem {
    private final String fileName;
    private final File file;
    private int type; // 1, 文本文件; -1, 不支持的文件类型
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


    // 文件类型常量
    public static final int TEXT = 1;
    public static final int BAD_FORMAT = -1;

    // 构造函数，传入一个file并取得file的名字和类型
    public FileItem(File file) {
        this.file = file;

        // 取得文件名
        fileName = file.getName();

        // 根据文件后缀来判断文件的类型
        String suffix = getFileSuffix(fileName);
        type = BAD_FORMAT;
        if (contains(txtTypes, suffix))
            type = TEXT;
    }

    // 判断是否图片
    public boolean contains(String[] types, String suffix) {
        suffix = suffix.toLowerCase(); // 统一转成小写
        for (String s : types) {
            if (s.equals(suffix))
                return true;
        }
        return false;
    }

    // 获取文件名的后缀
    public String getFileSuffix(String name) {
        int pos = name.lastIndexOf('.');
        if (pos > 0)
            return name.substring(pos + 1);

        return ""; // 无后缀文件
    }

    @Override
    public String toString() {
        return fileName;
    }
}