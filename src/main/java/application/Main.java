package application;

import entities.FileItem;
import entities.FileTreeItem;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
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

    //My GitHub url
    private static final String MY_GITHUB = "https://github.com/SunSiwen/Assignment";
    //directory path
    private static String path;
    //directory chooser
    private DirectoryChooser directoryChooser;

    private BorderPane root;
    private TreeView<FileItem> treeView;
    private TabPane tabPane;

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

    /**
     * Initialize all variables
     *
     * @author Siwen Sun
     * @date 2022/8/13 12:58
     */
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

    /**
     * @param primaryStage: stage
     * @author Siwen Sun
     * @date 2022/8/13 13:05
     */
    private void registerEvent(Stage primaryStage) {

        //check all tabs before close
        primaryStage.setOnCloseRequest(event -> tabPane.getTabs().forEach(TabUtil::close));

        //open the directory in explorer
        showInExplorer.setOnAction(event-> Optional.ofNullable(treeView.getSelectionModel().getSelectedItem()).ifPresent(selectedItem -> {
            try {
                Desktop.getDesktop().open(new File(selectedItem.getValue().getFile().getParent()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        //enter the directory manually
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

        //select it via a file chooser button
        fileChooser.setOnAction(event -> {
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            Optional.ofNullable(selectedDirectory).ifPresent(file -> {
                setPath(file.getAbsolutePath());
                display(primaryStage);
            });
        });

        //save current tab
        saveFile.setOnAction(event -> save());

        //open browser
        about.setOnAction(event -> openBrowser());

        //display the content of files within a tab window
        treeView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                Optional.ofNullable(treeView.getSelectionModel().getSelectedItem()).ifPresent(selectedItem -> {
                    if (!selectedItem.getValue().getFile().isDirectory()) {
                        TabUtil.openFile(tabPane, selectedItem.getValue());
                    }
                });
            }
        });

        anchorPane.widthProperty().addListener((observable, oldValue, newValue) -> menuBar.setPrefWidth(newValue.doubleValue()));
    }


    /**
     * @param primaryStage: stage
     * @author Siwen Sun
     * @date 2022/8/13 13:05
     */
    private void assemble(Stage primaryStage) {
        //set up tree view menu
        menu.getItems().add(showInExplorer);
        treeView.setContextMenu(menu);
        treeView.setPrefWidth(300);
        treeView.setShowRoot(false);
        treeView.setMinWidth(250);

        // set initial directory for directory chooser
        directoryChooser.setInitialDirectory(new File(path));

        //add menu-items to menus
        openDirectory.getItems().addAll(enterPath, separatorMenuItem, fileChooser);
        save.getItems().addAll(saveFile);
        help.getItems().addAll(about);

        //add menus to menu bar
        menuBar.getMenus().addAll(openDirectory, save, help);
        menuBar.setPrefWidth(primaryStage.getWidth());

        //set root
        root.setPrefWidth(1300);
        root.setPrefHeight(650);
        root.setLeft(treeView);
        root.setCenter(tabPane);
        AnchorPane.setTopAnchor(root, 25.0);

        //add menu bar and root to anchor pane
        anchorPane.getChildren().add(menuBar);
        anchorPane.getChildren().add(root);

        //add scene to stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Demo");
        primaryStage.setWidth(1300);
        primaryStage.setHeight(800);
        display(primaryStage);
    }

    /**
     * @param primaryStage: stage
     * @author Siwen Sun
     * @date 2022/8/13 13:05
     */
    @Override
    public void start(Stage primaryStage) {
        initialize();
        registerEvent(primaryStage);
        assemble(primaryStage);
    }


    /**
     * @param primaryStage: stage
     * @author Siwen Sun
     * @date 2022/8/13 13:05
     */
    public void display(Stage primaryStage) {
        loadFileTree();
        primaryStage.show();
    }

    /**
     * generate the tree view
     *
     * @author Siwen Sun
     * @date 2022/8/13 13:05
     */
    public void loadFileTree() {
        FileTreeItem fileTreeItem = new FileTreeItem(new File(path), f -> {
            File[] allFiles = f.listFiles();
            assert allFiles != null;
            List<File> list = new ArrayList<>(Arrays.asList(allFiles));
            return list.toArray(new File[0]);
        });
        treeView.setRoot(fileTreeItem);
    }

    /**
     * save current tab
     *
     * @author Siwen Sun
     * @date 2022/8/13 13:05
     */
    private void save() {
        Tab selectedItem = tabPane.getSelectionModel().getSelectedItem();
        FileUtil.saveFileByTab(selectedItem);
    }

    /**
     * open browser and jump to my GitHub
     *
     * @author Siwen Sun
     * @date 2022/8/13 13:05
     */
    private void openBrowser() {
        try {
            Desktop.getDesktop().browse(new URI(MY_GITHUB));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getPath() {
        return path;
    }

    public static void setPath(String newPath) {
        path = newPath;
    }

    /**
     * @param args : args
     * @author Siwen Sun
     * @date 2022/8/13 13:05
     */
    public static void main(String[] args) {
        Application.launch(args);
    }
}