package com.sistemmanajemenpenyewaanperlengkapanpendakian.model;

public class Tas extends Perlengkapan {
    private String tipeTas;

    public Tas(String namaPerlengkapan, int stok, String namaVendor,
               long hargaPerlengkapan, long hargaSewaHarian, String tipeTas) {
        super(namaPerlengkapan, stok, namaVendor, hargaPerlengkapan, hargaSewaHarian);
        this.tipeTas = tipeTas;
    }

    public String getTipeTas() {
        return tipeTas;
    }

    public void setTipeTas(String tipeTas) {
        this.tipeTas = tipeTas;
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
