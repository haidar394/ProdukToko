package kelas;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import kelas.controller.DaftarController;
import kelas.controller.DetailController;
import kelas.controller.FormController;
import kelas.data.DataProduk;
import kelas.model.Produk;

public class ScreenshotGenerator extends Application {
    private Stage stage;
    private Scene scene;
    private DataProduk data;
    private Navigator navigator;
    private File outputDir;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        this.outputDir = new File("screenshots");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        this.data = new DataProduk();
        this.navigator = new Navigator(stage, data);

        // Populate sample data
        data.tambah("Roti Tawar Gandum", "Makanan", 18000);
        Produk pDetail = data.tambah("Kopi Susu Gula Aren", "Minuman", 15000);
        data.tambah("Keripik Singkong Balado", "Makanan", 12500);
        data.tambah("Matcha Latte Ice", "Minuman", 20000);
        data.tambah("Croissant Butter", "Makanan", 22000);

        stage.setTitle("Pencatatan Produk Toko");
        stage.setWidth(920);
        stage.setHeight(700);

        // Step 1: Capture Form with input
        captureStep1Form(pDetail);
    }

    private void captureStep1Form(Produk pDetail) throws Exception {
        FXMLLoader l = new FXMLLoader(getClass().getResource("/kelas/form.fxml"));
        Parent root = l.load();
        FormController c = l.getController();
        c.setKonteks(navigator, data);

        // Pre-fill fields for a realistic screenshot
        TextField namaField = (TextField) root.lookup("#namaField");
        ComboBox<?> kategoriBox = (ComboBox<?>) root.lookup("#kategoriBox");
        TextField hargaField = (TextField) root.lookup("#hargaField");

        if (namaField != null) namaField.setText("Kopi Susu Gula Aren");
        if (kategoriBox != null) ((ComboBox<String>) kategoriBox).setValue("Minuman");
        if (hargaField != null) hargaField.setText("15000");

        showAndCapture(root, "01_form_tambah_produk.png", () -> {
            try {
                captureStep2Detail(pDetail);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void captureStep2Detail(Produk pDetail) throws Exception {
        FXMLLoader l = new FXMLLoader(getClass().getResource("/kelas/detail.fxml"));
        Parent root = l.load();
        DetailController c = l.getController();
        c.setData(navigator, pDetail);

        showAndCapture(root, "02_detail_produk.png", () -> {
            try {
                captureStep3Daftar();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void captureStep3Daftar() throws Exception {
        FXMLLoader l = new FXMLLoader(getClass().getResource("/kelas/daftar.fxml"));
        Parent root = l.load();
        DaftarController c = l.getController();
        c.setKonteks(navigator, data);

        showAndCapture(root, "03_daftar_produk.png", () -> {
            try {
                captureStep4Filter(root, c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void captureStep4Filter(Parent root, DaftarController c) {
        TextField cariField = (TextField) root.lookup("#cariField");
        if (cariField != null) {
            cariField.setText("Kopi");
        }
        ComboBox<String> filterBox = (ComboBox<String>) root.lookup("#filterKategoriBox");
        if (filterBox != null) {
            filterBox.setValue("Minuman");
        }

        // Trigger search by invoking reflection or private method cari or just calling action
        try {
            java.lang.reflect.Method cariMethod = DaftarController.class.getDeclaredMethod("cari");
            cariMethod.setAccessible(true);
            cariMethod.invoke(c);
        } catch (Exception e) {
            e.printStackTrace();
        }

        showAndCapture(root, "04_pencarian_dan_filter.png", () -> {
            System.out.println("All screenshots successfully captured!");
            Platform.exit();
        });
    }

    private void showAndCapture(Parent root, String filename, Runnable next) {
        if (scene == null) {
            scene = new Scene(root, 900, 680);
            scene.getStylesheets().add(getClass().getResource("/kelas/styles/app.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } else {
            scene.setRoot(root);
        }

        // Wait a short duration for layout and rendering to stabilize
        PauseTransition delay = new PauseTransition(Duration.millis(600));
        delay.setOnFinished(e -> {
            saveSnapshot(scene, filename);
            if (next != null) {
                next.run();
            }
        });
        delay.play();
    }

    private void saveSnapshot(Scene targetScene, String filename) {
        try {
            WritableImage image = targetScene.snapshot(null);
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            PixelReader reader = image.getPixelReader();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    bImage.setRGB(x, y, reader.getArgb(x, y));
                }
            }
            File file = new File(outputDir, filename);
            ImageIO.write(bImage, "png", file);
            System.out.println("Saved: " + file.getAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
