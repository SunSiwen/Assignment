package util;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.*;
import java.nio.IntBuffer;
import java.util.Scanner;


/**
 * @author Siwen Sun
 * @date 2022/07/31/ 12:28
 */
public class FileUtil {

    private FileUtil() {
    }

    public static Canvas getFileIconToNode(File file) {

        Image image = ((ImageIcon) FileSystemView.getFileSystemView()
                .getSystemIcon(file)).getImage();
        BufferedImage bi = new BufferedImage(image.getWidth(null),
                image.getHeight(null), Transparency.BITMASK);
        bi.getGraphics().drawImage(image, 0, 0, null);
        int[] data = ((DataBufferInt) bi.getData().getDataBuffer()).getData();
        WritablePixelFormat<IntBuffer> pixelFormat
                = PixelFormat.getIntArgbInstance();
        Canvas canvas = new Canvas(bi.getWidth() + 2.0, bi.getHeight() + 2.0);
        PixelWriter pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
        pixelWriter.setPixels(1, 1, bi.getWidth(), bi.getHeight(),
                pixelFormat, data, 0, bi.getWidth());
        return canvas;

    }

    public static String getFileName(File file) {
        return FileSystemView.getFileSystemView().getSystemDisplayName(file);
    }


    public static String readFile(File file) {
        if (file != null) {
            try (Scanner scanner = new Scanner(file)) {
                StringBuilder stringBuilder = new StringBuilder();
                while (scanner.hasNext()) {
                    stringBuilder.append(scanner.nextLine()).append("\r\n");
                }
                return stringBuilder.toString();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                return "Fail to find this File";
            }
        } else {
            return "Fail to open this file";
        }
    }


    public static void saveFileByTab(Tab finalTab) {
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(finalTab.getId()))) {
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
}