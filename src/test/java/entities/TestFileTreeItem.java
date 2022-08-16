package entities;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * @author Siwen Sun
 * @date 2022/08/12/ 11:46
 */
public class TestFileTreeItem {



    FileTreeItem fileTreeItem = new FileTreeItem(new File("D:\\Assignment"));


    @Test
    public void testFileTreeItem() {
        assertEquals("Assignment", fileTreeItem.getFile().getName());
        FileItem value = fileTreeItem.getValue();
        assertEquals("Assignment", value.getFileName());
        assertEquals(-1, value.getType());
        assertEquals("", value.getFileSuffix());
        assertFalse(value.contains(new String[]{"css"}, value.getFileSuffix()));
        assertNotNull(fileTreeItem.getChildren());
    }
}

