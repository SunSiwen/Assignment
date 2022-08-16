package entities;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Siwen Sun
 * @date 2022/08/15/ 23:09
 */
public class TestFileItem {

    FileItem fileItem = new FileItem(new File(String.valueOf(getClass().getResource("../application.css"))));

    @Test
    public void testFileItem() {
        assertEquals("application.css", fileItem.getFileName());
        assertEquals(-1, fileItem.getType());
        assertEquals("css", fileItem.getFileSuffix());
        assertTrue(fileItem.contains(new String[]{"css"}, fileItem.getFileSuffix()));
    }

}
