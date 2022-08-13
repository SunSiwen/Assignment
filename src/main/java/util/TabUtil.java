package util;

import entities.FileItem;
import excepetions.CannotOpenException;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.util.Optional;

/**
 * @author Siwen Sun
 * @date 2022/08/13/ 12:01
 */
public class TabUtil {

    private TabUtil() {
    }

    public static Tab findTab(TabPane tabPane, FileItem fileItem) {
        ObservableList<Tab> tabs = tabPane.getTabs();
        for (Tab tab : tabs) {
            if (tab.getId().equals(fileItem.getFile().getAbsolutePath())) {
                return tab;
            }
        }
        return null;
    }


    public static void openFile(TabPane tabPane, FileItem fileItem) {
        Tab tab = TabUtil.findTab(tabPane, fileItem);
        if (tab != null) {
            tabPane.getSelectionModel().select(tab);
            return;
        }
        try {
            openNewTab(tabPane, fileItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void openNewTab(TabPane tabPane, FileItem fileItem) throws CannotOpenException {
        TextArea textArea;
        if (fileItem.getType() == FileItem.TEXT) {
            textArea = new TextArea();
            textArea.setText(FileUtil.readFile(fileItem.getFile()));
            // 创建新的选项卡并选中
            Tab tab = new Tab();
            tab.setId(fileItem.getFile().getAbsolutePath());
            tab.setText(fileItem.getFileName());
            tab.setContent(textArea);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
            textArea.setOnKeyPressed(event -> {
                String text = tab.getText();
                if (!text.endsWith("*")) {
                    tab.setText(text + "*");
                }
            });

            tab.setOnCloseRequest(event -> close(tab));
        } else throw new CannotOpenException("Cannot open this file");
    }

    public static void close(Tab finalTab) {
        if (finalTab.getText().endsWith("*")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Wait! The application will close.");
            alert.setContentText("Do you want to save this?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                FileUtil.saveFileByTab(finalTab);
            }
        }
    }
}
