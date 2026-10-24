
package com.sistemmanajemenpenyewaanperlengkapanpendakian.model;

public class Tas extends Perlengkapan {

    private String tipeTas;

    public Tas() {
        super();
    }

    public Tas(
            int idPerlengkapan,
            int idVendor,
            String namaPerlengkapan,
            String jenisPerlengkapan,
            int stok,
            long hargaPerlengkapan,
            long hargaSewaHarian) {

        super(idPerlengkapan, idVendor, namaPerlengkapan,
                jenisPerlengkapan, stok, hargaPerlengkapan,
                hargaSewaHarian);
    }

    public String getTipeTas() {
        return tipeTas;
    }

    public void setTipeTas(String tipeTas) {
        this.tipeTas = tipeTas;
    }

    @Override
    public long hitungHargaSewa(int durasiSewa) {
        if (durasiSewa <= 0) {
            return 0;
        }
        return hargaSewaHarian * durasiSewa;
    }
}
