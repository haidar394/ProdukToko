package kelas;

import kelas.model.Produk;

public class DemoTerminal {
    public static void main(String[] args) {
        System.out.println("=== Pengujian Model Produk ===");
        Produk p1 = new Produk("PR001", "Roti Cokelat", "Makanan", 15000);
        System.out.println("Kode     : " + p1.getKode());
        System.out.println("Nama     : " + p1.getNama());
        System.out.println("Kategori : " + p1.getKategori());
        System.out.println("Harga    : " + p1.getHarga());

        System.out.println("\n=== Pengujian Validasi Nama Kosong ===");
        try {
            new Produk("PR002", "", "Minuman", 10000);
        } catch (IllegalArgumentException e) {
            System.out.println("Berhasil ditolak: " + e.getMessage());
        }

        System.out.println("\n=== Pengujian Validasi Harga <= 0 ===");
        try {
            new Produk("PR003", "Kopi Susu", "Minuman", 0);
        } catch (IllegalArgumentException e) {
            System.out.println("Berhasil ditolak: " + e.getMessage());
        }
    }
}

