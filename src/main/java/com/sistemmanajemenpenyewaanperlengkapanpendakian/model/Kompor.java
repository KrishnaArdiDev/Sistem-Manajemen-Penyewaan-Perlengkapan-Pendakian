package com.sistemmanajemenpenyewaanperlengkapanpendakian.model;

public class Kompor extends Perlengkapan {
    private String tipeKompor;

    public Kompor(String namaPerlengkapan, int stok, String namaVendor,
                  long hargaPerlengkapan, long hargaSewaHarian, String tipeKompor) {
        super(namaPerlengkapan, stok, namaVendor, hargaPerlengkapan, hargaSewaHarian);
        this.tipeKompor = tipeKompor;
    }

    public String getTipeKompor() {
        return tipeKompor;
    }

    public void setTipeKompor(String tipeKompor) {
        this.tipeKompor = tipeKompor;
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
