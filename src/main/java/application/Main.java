package application;

import entities.FileItem;
import entities.FileTreeItem;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import util.FileUtil;
import util.TabUtil;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


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

    private Menu help;
    private MenuItem about;

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
        openDirectory = new Menu("Open Directory");
        enterPath = new MenuItem("Enter The Directory");
        separatorMenuItem = new SeparatorMenuItem();
        fileChooser = new MenuItem("File Chooser");
        save = new Menu("Save");
        saveFile = new MenuItem("Save File");
        help = new Menu("Help");
        about = new MenuItem("About");
        scene = new Scene(anchorPane);
    }

    private void registerEvent(Stage primaryStage) {

        primaryStage.setOnCloseRequest(event -> {
            ObservableList<Tab> tabs = tabPane.getTabs();
            tabs.forEach(TabUtil::close);
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
            dialog.setTitle("Path Input");
            dialog.setHeaderText("Please input Your directory path");
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
            Optional.ofNullable(selectedDirectory).ifPresent(file -> {
                setPath(file.getAbsolutePath());
                display(primaryStage);
            });

        });

        saveFile.setOnAction(event -> save());

        about.setOnAction(event -> openBrowser());
        treeView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                TreeItem<FileItem> selectedItem = treeView.getSelectionModel().getSelectedItem();
                if (selectedItem != null && !selectedItem.getValue().getFile().isDirectory()) {
                    TabUtil.openFile(tabPane, selectedItem.getValue());
                }
            }
        });
    }

    private void openBrowser() {
        try {
            Desktop.getDesktop().browse(new URI("www.google.com"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void assemble(Stage primaryStage) {
        menu.getItems().add(showInExplorer);
        treeView.setContextMenu(menu);
        treeView.setPrefWidth(300);
        treeView.setShowRoot(false);
        treeView.setMinWidth(250);

        directoryChooser.setInitialDirectory(new File(path));

        openDirectory.getItems().addAll(enterPath, separatorMenuItem, fileChooser);
        save.getItems().addAll(saveFile);
        help.getItems().addAll(about);

        menuBar.getMenus().addAll(openDirectory, save, help);
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

    private void save() {
        Tab selectedItem = tabPane.getSelectionModel().getSelectedItem();
        FileUtil.saveFileByTab(selectedItem);
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