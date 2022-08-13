package application;

import entities.FileItem;
import entities.FileTreeItem;
import excepetions.CannotOpenException;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.*;


/**
 * @author Siwen Sun
 * @Date 2022/07/30/ 16:16
 */
public class Main extends Application {

    private static String path;
    private DirectoryChooser directoryChooser = new DirectoryChooser();
    private BorderPane root;
    private TabPane tabPane;
    private TreeView<FileItem> treeView;
    private ContextMenu menu;
    private MenuItem showInExplorer;
    private AnchorPane anchorPane;

    private MenuBar menuBar;

    private Menu openDirectory;
    private MenuItem enterPath;
    private SeparatorMenuItem separatorMenuItem;
    private MenuItem fileChooser;

    private Menu save;
    private MenuItem saveFile;

    private Scene scene;

    private void initialize() {
        setPath("C:\\Users\\" + System.getenv("USERNAME") + "\\Documents");
        directoryChooser = new DirectoryChooser();
        root = new BorderPane();
        tabPane = new TabPane();
        treeView = new TreeView<>();
        menu = new ContextMenu();
        showInExplorer = new MenuItem("Open In Explorer");
        anchorPane = new AnchorPane();
        menuBar = new MenuBar();
        openDirectory = new Menu("open directory");
        save = new Menu("save");
        enterPath = new MenuItem("enter the directory");
        separatorMenuItem = new SeparatorMenuItem();
        fileChooser = new MenuItem("file chooser");
        saveFile = new MenuItem("save file");
        scene = new Scene(anchorPane);
    }

    private void registerEvent(Stage primaryStage) {

        primaryStage.setOnCloseRequest(event -> {
            ObservableList<Tab> tabs = tabPane.getTabs();
            tabs.forEach(this::close);
        });

        showInExplorer.setOnAction((ActionEvent t) -> {
            try {
                TreeItem<FileItem> selectedItem = treeView.getSelectionModel().getSelectedItem();
                Desktop.getDesktop().open(new File(selectedItem.getValue().getFile().getParent()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        enterPath.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog(path);
            dialog.setTitle("Text Input Dialog");
            dialog.setHeaderText("Please Input Your directory path");
            dialog.setContentText("Absolute Path:");
            dialog.getDialogPane().setMinWidth(500);
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(s -> {
                File folder = new File(s);
                if (folder.exists() && folder.isDirectory()) {
                    setPath(s);
                    display(primaryStage);
                } else {
                    Alert warning = new Alert(Alert.AlertType.WARNING);
                    warning.setHeaderText("Fail");
                    warning.setContentText("Your path does not exist or is not a directory");
                    warning.showAndWait();
                }
            });
        });

        fileChooser.setOnAction(event -> {
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            setPath(selectedDirectory.getAbsolutePath());
            display(primaryStage);
        });

        saveFile.setOnAction(event -> save());

        treeView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                TreeItem<FileItem> selectedItem = treeView.getSelectionModel().getSelectedItem();
                if (selectedItem != null && !selectedItem.getValue().getFile().isDirectory()) {
                    openFile(selectedItem.getValue());
                }
            }
        });
    }


    private void assemble(Stage primaryStage) {
        menu.getItems().add(showInExplorer);
        treeView.setContextMenu(menu);
        treeView.setPrefWidth(200);
        treeView.setShowRoot(false);
        treeView.setMinWidth(250);

        directoryChooser.setInitialDirectory(new File(path));

        save.getItems().addAll(saveFile);

        openDirectory.getItems().addAll(enterPath, separatorMenuItem, fileChooser);
        menuBar.getMenus().addAll(openDirectory, save);
        menuBar.setPrefWidth(primaryStage.getWidth());

        root.setPrefWidth(1300);
        root.setPrefHeight(650);
        AnchorPane.setTopAnchor(root, 25.0);

        anchorPane.getChildren().add(menuBar);
        anchorPane.getChildren().add(root);
        anchorPane.widthProperty().addListener((observable, oldValue, newValue) -> menuBar.setPrefWidth(newValue.doubleValue()));


        display(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Demo");
        primaryStage.setWidth(1300);
        primaryStage.setHeight(700);
        primaryStage.show();
    }

    @Override
    public void start(Stage primaryStage) {
        initialize();
        registerEvent(primaryStage);
        assemble(primaryStage);
    }


    public void display(Stage primaryStage) {
        loadFileTree();
        root.setLeft(treeView);
        root.setCenter(tabPane);
        primaryStage.show();
    }

    public void loadFileTree() {
        FileTreeItem fileTreeItem = new FileTreeItem(new File(path), f -> {
            File[] allFiles = f.listFiles();
            assert allFiles != null;
            List<File> list = new ArrayList<>(Arrays.asList(allFiles));
            return list.toArray(new File[0]);
        });
        treeView.setRoot(fileTreeItem);
    }


    // 打开左侧文件
    public void openFile(FileItem fileItem) {
        // 查看选项卡是否打开
        Tab tab = findTab(fileItem);
        if (tab != null) {
            tabPane.getSelectionModel().select(tab);
            return;
        }
        try {
            openNewTab(fileItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openNewTab(FileItem fileItem) throws CannotOpenException {
        Node currentView;
        if (fileItem.getType() == FileItem.TEXT) {
            TextArea textArea = new TextArea();
            File file = fileItem.getFile();
            if (file != null) {
                try (Scanner scanner = new Scanner(file)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    while (scanner.hasNext()) {
                        //每次读一行，并在其后添加回车符和换行符
                        stringBuilder.append(scanner.nextLine()).append("\r\n");
                    }
                    //将读取到的数据放入文本区中显示
                    textArea.setText(stringBuilder.toString());
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            } else {
                textArea.setText("没有选择文件");
            }
            currentView = textArea;
            // 创建新的选项卡并选中
            Tab tab = new Tab();
            tab.setId(fileItem.getFile().getAbsolutePath());
            tab.setText(fileItem.getFileName());
            tab.setContent(currentView);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
            Tab finalTab = tab;
            textArea.setOnKeyPressed(event -> {
                String text = finalTab.getText();
                if (!text.endsWith("*")) {
                    finalTab.setText(text + "*");
                }
            });

            finalTab.setOnCloseRequest(event -> close(finalTab));
        } else throw new CannotOpenException("Cannot open this file");
    }

    private void close(Tab finalTab) {
        if (finalTab.getText().endsWith("*")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Look, a Confirmation Dialog");
            alert.setContentText("Do you want to save this?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                save(finalTab);
            }
        }
    }

    private void save(Tab finalTab) {
        File file = new File(finalTab.getId());
        //将文本区中的内容转化为字节存入数组中并写入对应的文件

        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            byte[] b = (((TextArea) (finalTab.getContent())).getText()).getBytes();
            out.write(b, 0, b.length);
            String text = finalTab.getText();
            if (text.endsWith("*")) {
                finalTab.setText(text.substring(0, text.length() - 1));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void save() {
        Tab selectedItem = tabPane.getSelectionModel().getSelectedItem();
        save(selectedItem);
    }


    // 查看在右侧选项卡是否打开
    public Tab findTab(FileItem fileItem) {
        ObservableList<Tab> tabs = tabPane.getTabs();
        for (Tab tab : tabs) {
            if (tab.getId().equals(fileItem.getFile().getAbsolutePath())) {
                return tab;
            }
        }
        return null;
    }


    public static String getPath() {
        return path;
    }

    public static void setPath(String newPath) {
        path = newPath;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}