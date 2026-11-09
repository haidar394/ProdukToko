package kelas.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import kelas.Navigator;
import kelas.data.DataProduk;
import kelas.model.Produk;

public class FormController {

    @FXML
    private TextField namaField;
    @FXML
    private ComboBox<String> kategoriBox;
    @FXML
    private TextField hargaField;
    @FXML
    private Label pesanLabel;

    private Navigator navigator;
    private DataProduk data;

    @FXML
    private void initialize() {
        kategoriBox.getItems().addAll("Makanan", "Minuman");
        kategoriBox.setValue("Makanan");
    }

    public void setKonteks(Navigator navigator, DataProduk data) {
        this.navigator = navigator;
        this.data = data;
    }

    @FXML
    private void simpan() {
        String nama = namaField.getText();
        if (nama == null || nama.trim().isEmpty()) {
            pesanLabel.setText("Nama produk tidak boleh kosong.");
            return;
        }

        String kategori = kategoriBox.getValue();
        if (kategori == null || kategori.isBlank()) {
            pesanLabel.setText("Kategori produk wajib dipilih.");
            return;
        }

        String hargaText = (hargaField.getText() != null) ? hargaField.getText().trim() : "";
        if (hargaText.isEmpty()) {
            pesanLabel.setText("Harga produk tidak boleh kosong.");
            return;
        }

        long harga;
        try {
            harga = Long.parseLong(hargaText);
        } catch (NumberFormatException e) {
            pesanLabel.setText("Harga harus berupa bilangan bulat positif.");
            return;
        }

        if (harga <= 0) {
            pesanLabel.setText("Harga harus lebih dari nol.");
            return;
        }

        try {
            Produk produk = data.tambah(nama.trim(), kategori, harga);
            navigator.bukaDetail(produk);
        } catch (IllegalArgumentException e) {
            pesanLabel.setText(e.getMessage());
        } catch (IOException e) {
            pesanLabel.setText("Halaman detail gagal dibuka.");
            e.printStackTrace();
        }
    }

    @FXML
    private void bukaDaftar() {
        try {
            navigator.bukaDaftar();
        } catch (IOException e) {
            pesanLabel.setText("Daftar produk gagal dibuka.");
            e.printStackTrace();
        }
    }
}

