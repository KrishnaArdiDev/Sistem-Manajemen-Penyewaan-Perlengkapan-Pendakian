package com.sistemmanajemenpenyewaanperlengkapanpendakian.model;

public abstract class Perlengkapan {

    private String namaPerlengkapan;
    private int stok;
    private String namaVendor;
    private long hargaPerlengkapan;
    private long hargaSewaHarian;

    public Perlengkapan(String namaPerlengkapan, int stok, String namaVendor,
                        long hargaPerlengkapan, long hargaSewaHarian) {
        this.namaPerlengkapan = namaPerlengkapan;
        this.stok = stok;
        this.namaVendor = namaVendor;
        this.hargaPerlengkapan = hargaPerlengkapan;
        this.hargaSewaHarian = hargaSewaHarian;
    }

    public String getNamaPerlengkapan() {
        return namaPerlengkapan;
    }

    public void setNamaPerlengkapan(String namaPerlengkapan) {
        this.namaPerlengkapan = namaPerlengkapan;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public long getHargaSewaHarian() {
        return hargaSewaHarian;
    }

    public void setHargaSewaHarian(long hargaSewaHarian) {
        this.hargaSewaHarian = hargaSewaHarian;
    }

    public int hitungJumlahStok() {
        return stok;
    }

    public void tambahStok(int jumlah) {
        if (jumlah > 0) {
            this.stok += jumlah;
        }
    }

    public void kurangiStok(int jumlah) {
        if (jumlah > 0 && jumlah <= this.stok) {
            this.stok -= jumlah;
        }
    }

    public String getNamaVendor() {
    return namaVendor;
    }

    public long getHargaPerlengkapan() {
    return hargaPerlengkapan;
    }

    public abstract long hitungHargaSewa(int durasiSewa);

    public abstract long hitungGantiRugi();
}
