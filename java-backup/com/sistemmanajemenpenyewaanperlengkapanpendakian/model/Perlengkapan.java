
package com.sistemmanajemenpenyewaanperlengkapanpendakian.model;

public abstract class Perlengkapan {

    protected int idPerlengkapan;
    protected int idVendor;
    protected String namaPerlengkapan;
    protected String jenisPerlengkapan;
    protected int stok;
    protected String namaVendor;
    protected long hargaPerlengkapan;
    protected long hargaSewaHarian;

    public Perlengkapan() {
    }

    public Perlengkapan(
            int idPerlengkapan,
            int idVendor,
            String namaPerlengkapan,
            String jenisPerlengkapan,
            int stok,
            long hargaPerlengkapan,
            long hargaSewaHarian) {

        this.idPerlengkapan = idPerlengkapan;
        this.idVendor = idVendor;
        this.namaPerlengkapan = namaPerlengkapan;
        this.jenisPerlengkapan = jenisPerlengkapan;
        this.stok = stok;
        this.hargaPerlengkapan = hargaPerlengkapan;
        this.hargaSewaHarian = hargaSewaHarian;
    }

    public abstract long hitungHargaSewa(int durasiSewa);

    public long hitungGantiRugi() {
        return hargaPerlengkapan;
    }

    public void tambahStok(int jumlah) {
        if (jumlah > 0) {
            stok += jumlah;
        }
    }

    public void kurangiStok(int jumlah) {
        if (jumlah > 0 && jumlah <= stok) {
            stok -= jumlah;
        }
    }

    public void hitungJumlahStok(int jumlah) {
        if (jumlah >= 0) {
            stok = jumlah;
        }
    }

    public int getIdPerlengkapan() {
        return idPerlengkapan;
    }

    public int getIdVendor() {
        return idVendor;
    }

    public String getNamaPerlengkapan() {
        return namaPerlengkapan;
    }

    public void setNamaPerlengkapan(String namaPerlengkapan) {
        this.namaPerlengkapan = namaPerlengkapan;
    }

    public String getJenisPerlengkapan() {
        return jenisPerlengkapan;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        if (stok >= 0) {
            this.stok = stok;
        }
    }

    public String getNamaVendor() {
        return namaVendor;
    }

    public void setNamaVendor(String namaVendor) {
        this.namaVendor = namaVendor;
    }

    public long getHargaPerlengkapan() {
        return hargaPerlengkapan;
    }

    public void setHargaPerlengkapan(long hargaPerlengkapan) {
        this.hargaPerlengkapan = hargaPerlengkapan;
    }

    public long getHargaSewaHarian() {
        return hargaSewaHarian;
    }

    public void setHargaSewaHarian(long hargaSewaHarian) {
        this.hargaSewaHarian = hargaSewaHarian;
    }
}
