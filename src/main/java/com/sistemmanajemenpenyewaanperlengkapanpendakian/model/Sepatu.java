package com.sistemmanajemenpenyewaanperlengkapanpendakian.model;

public class Sepatu extends Perlengkapan {
    private int ukuranSepatu;

    public Sepatu(String namaPerlengkapan, int stok, String namaVendor,
                  long hargaPerlengkapan, long hargaSewaHarian, int ukuranSepatu) {
        super(namaPerlengkapan, stok, namaVendor, hargaPerlengkapan, hargaSewaHarian);
        this.ukuranSepatu = ukuranSepatu;
    }

    public int getUkuranSepatu() {
        return ukuranSepatu;
    }

    public void setUkuranSepatu(int ukuranSepatu) {
        this.ukuranSepatu = ukuranSepatu;
    }

    @Override
    public long hitungHargaSewa(int durasiSewa) {
        return getHargaSewaHarian() * durasiSewa;
    }

    @Override
    public long hitungGantiRugi() {
        return getHargaPerlengkapan();
    }
}

