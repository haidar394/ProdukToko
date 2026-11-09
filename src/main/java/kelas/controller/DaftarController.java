package kelas.controller;

import java.io.IOException;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import kelas.Navigator;
import kelas.data.DataProduk;
import kelas.model.Produk;

public class DaftarController {

    @FXML
    private TableView<Produk> tabel;
    @FXML
    private TableColumn<Produk, String> kodeCol;
    @FXML
    private TableColumn<Produk, String> namaCol;
    @FXML
    private TableColumn<Produk, String> kategoriCol;
    @FXML
    private TableColumn<Produk, Long> hargaCol;
    @FXML
    private TextField cariField;
    @FXML
    private ComboBox<String> filterKategoriBox;
    @FXML
    private Label pesanLabel;

    private Navigator navigator;
    private DataProduk data;
    private final ObservableList<Produk> dataTampilan = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        kodeCol.setCellValueFactory(new PropertyValueFactory<>("kode"));
        namaCol.setCellValueFactory(new PropertyValueFactory<>("nama"));
        kategoriCol.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        hargaCol.setCellValueFactory(new PropertyValueFactory<>("harga"));

        filterKategoriBox.getItems().add("Semua");
        filterKategoriBox.getItems().add("Makanan");
        filterKategoriBox.getItems().add("Minuman");
        filterKategoriBox.setValue("Semua");

        tabel.setItems(dataTampilan);
        tabel.setPlaceholder(new Label("Belum ada data produk yang sesuai."));
    }

    public void setKonteks(Navigator navigator, DataProduk data) {
        this.navigator = navigator;
        this.data = data;
        cari();
    }

    @FXML
    private void cari() {
        if (data == null) {
            return;
        }
        dataTampilan.clear();
        String kata = (cariField.getText() != null) ? cariField.getText().trim().toLowerCase(Locale.ROOT) : "";
        String kategoriDipilih = filterKategoriBox.getValue();

        for (Produk p : data.getDaftar()) {
            String nama = p.getNama().toLowerCase(Locale.ROOT);
            boolean cocokNama = kata.isEmpty() || nama.contains(kata);
            boolean cocokKategori = (kategoriDipilih == null || "Semua".equals(kategoriDipilih))
                    || p.getKategori().equalsIgnoreCase(kategoriDipilih);

            if (cocokNama && cocokKategori) {
                dataTampilan.add(p);
            }
        }
        tabel.sort();
        pesanLabel.setText("Jumlah produk yang tampil: " + dataTampilan.size());
    }

    @FXML
    private void resetPencarian() {
        cariField.clear();
        filterKategoriBox.setValue("Semua");
        cari();
    }

    @FXML
    private void tambah() {
        try {
            navigator.bukaForm();
        } catch (IOException e) {
            pesanLabel.setText("Form produk gagal dibuka.");
            e.printStackTrace();
        }
    }

    @FXML
    private void detail() {
        Produk terpilih = tabel.getSelectionModel().getSelectedItem();
        if (terpilih == null) {
            pesanLabel.setText("Pilih satu produk pada tabel terlebih dahulu.");
            return;
        }
        try {
            navigator.bukaDetail(terpilih);
        } catch (IOException e) {
            pesanLabel.setText("Detail produk gagal dibuka.");
            e.printStackTrace();
        }
    }

    @FXML
    private void hapus() {
        Produk terpilih = tabel.getSelectionModel().getSelectedItem();
        if (terpilih == null) {
            pesanLabel.setText("Pilih satu produk pada tabel terlebih dahulu.");
            return;
        }
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION,
                "Hapus produk " + terpilih.getNama() + " (" + terpilih.getKode() + ")?",
                ButtonType.YES, ButtonType.NO);
        dialog.initOwner(tabel.getScene().getWindow());
        ButtonType jawaban = dialog.showAndWait().orElse(ButtonType.NO);
        if (jawaban == ButtonType.YES) {
            data.hapus(terpilih);
            cari();
        }
    }
}

