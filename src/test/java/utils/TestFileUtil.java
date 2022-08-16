package utils;

import entities.FileItem;
import org.junit.Test;
import util.FileUtil;

import java.io.File;

import static org.junit.Assert.*;

/**
 * @author Siwen Sun
 * @date 2022/08/15/ 23:11
 */
public class TestFileUtil {


    File file = new File("D:\\Assignment\\src\\main\\resources\\application.css");

    @Test
    public void testGetFileName() {
        assertEquals("application.css", FileUtil.getFileName(file));
    }

    @Test
    public void testReadFile() {
        assertNotNull(FileUtil.readFile(file));
    }

}
