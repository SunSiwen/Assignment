package entities;

import application.Main;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import util.FileUtil;

import java.io.File;
import java.util.function.Function;


/**
 * @author Siwen Sun
 * @date 2022/07/31/ 12:31
 */
public class FileTreeItem extends TreeItem<FileItem> {

    private boolean notInitialized = true;

    private final File file;
    private final Function<File, File[]> supplier;

    public FileTreeItem(File file) {
        super(new FileItem(file), FileUtil.getFileIconToNode(file));
        this.file = file;
        supplier = (File f) -> {
            if (((FileTreeItem) this.getParent()).getFile().equals(new File(Main.getPath()))) {
                String name = FileUtil.getFileName(f);
                if (name.equals("网络") || name.equals("家庭组")) {
                    return new File[0];
                }
            }
            return f.listFiles();
        };
    }

    /**
     * @param file
     * @param supplier
     * @return null
     * @author Siwen Sun
     * @date 2022/8/13 13:06
     */
    public FileTreeItem(File file, Function<File, File[]> supplier) {
        super(new FileItem(file), FileUtil.getFileIconToNode(file));
        this.file = file;
        this.supplier = supplier;
    }


    /**
     * @return javafx.collections.ObservableList<javafx.scene.control.TreeItem < entities.FileItem>>
     * @author Siwen Sun
     * @date 2022/8/13 13:06
     */
    @Override
    public ObservableList<TreeItem<FileItem>> getChildren() {
        ObservableList<TreeItem<FileItem>> children = super.getChildren();
        if (this.notInitialized && this.isExpanded()) {
            this.notInitialized = false;
            if (this.getFile().isDirectory() && null != supplier.apply(this.getFile())) {
                for (File f : supplier.apply(this.getFile())) {
                    if (f.isDirectory() || ".txt".equals(f.getName().substring(Math.max(0, f.getName().indexOf('.'))))) {
                        children.add(new FileTreeItem(f));
                    }
                }

            }
        }
        return children;
    }

    /**
     * @return boolean
     * @author Siwen Sun
     * @date 2022/8/13 13:07
     */
    @Override
    public boolean isLeaf() {
        return !file.isDirectory();
    }

    /**
     * @return java.io.File
     * @author Siwen Sun
     * @date 2022/8/13 13:07
     */
    public File getFile() {
        return file;
    }

}
