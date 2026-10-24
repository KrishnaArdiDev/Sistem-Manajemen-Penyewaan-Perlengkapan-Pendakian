
package com.sistemmanajemenpenyewaanperlengkapanpendakian.model;

public class Sepatu extends Perlengkapan {

    private int ukuranSepatu;

    public Sepatu() {
        super();
    }

    public Sepatu(
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

    public int getUkuranSepatu() {
        return ukuranSepatu;
    }

    public void setUkuranSepatu(int ukuranSepatu) {
        this.ukuranSepatu = ukuranSepatu;
    }

    @Override
    public long hitungHargaSewa(int durasiSewa) {
        if (durasiSewa <= 0) {
            return 0;
        }
        return hargaSewaHarian * durasiSewa;
    }
}
