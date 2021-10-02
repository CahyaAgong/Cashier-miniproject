package com.jeje.cashier.Model;

public class Barang {

    int id_barang, stok;
    String kode_barang, nama_barang;

    public Barang(int id_barang, int stok, String kode_barang, String nama_barang) {
        this.id_barang = id_barang;
        this.stok = stok;
        this.kode_barang = kode_barang;
        this.nama_barang = nama_barang;
    }

    public int getId_barang() {
        return id_barang;
    }

    public void setId_barang(int id_barang) {
        this.id_barang = id_barang;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public String getKode_barang() {
        return kode_barang;
    }

    public void setKode_barang(String kode_barang) {
        this.kode_barang = kode_barang;
    }

    public String getNama_barang() {
        return nama_barang;
    }

    public void setNama_barang(String nama_barang) {
        this.nama_barang = nama_barang;
    }
}
