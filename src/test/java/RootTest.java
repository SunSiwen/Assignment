import org.junit.Test;

import javax.swing.filechooser.FileSystemView;
import java.util.Arrays;

/**
 * @author Siwen Sun
 * @date 2022/08/12/ 11:46
 */
public class RootTest {

    @Test
    public void printRoot(){
        Arrays.stream(FileSystemView.getFileSystemView().getRoots()).forEach(System.out::println);
    }
}
