
package com.sistemmanajemenpenyewaanperlengkapanpendakian.model;

public class Kompor extends Perlengkapan {

    private String tipeKompor;

    public Kompor() {
        super();
    }

    public Kompor(
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

    public String getTipeKompor() {
        return tipeKompor;
    }

    public void setTipeKompor(String tipeKompor) {
        this.tipeKompor = tipeKompor;
    }

    @Override
    public long hitungHargaSewa(int durasiSewa) {
        if (durasiSewa <= 0) {
            return 0;
        }
        return hargaSewaHarian * durasiSewa;
    }
}
