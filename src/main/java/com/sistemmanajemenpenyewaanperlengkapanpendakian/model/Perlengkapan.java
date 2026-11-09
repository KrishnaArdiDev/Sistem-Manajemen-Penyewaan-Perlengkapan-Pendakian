package com.sistemmanajemenpenyewaanperlengkapanpendakian.model;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.StokTidakCukupException;

public abstract class Perlengkapan {
    private String namaPerlengkapan;
    private String namaVendor;
    private int stok;
    private long hargaPerlengkapan;
    private long hargaSewaHarian;

    public String getNamaPerlengkapan(){
        return namaPerlengkapan;
    }

    public void setNamaPerlengkapan(String namaPerlengkapan){
        this.namaPerlengkapan = namaPerlengkapan;
    }

    public int getStok(){
        return stok;
    }

    public void setStok(int stok){
        hitungJumlahStok(stok);
    }

    public long getHargaSewaHarian(){
        return hargaSewaHarian;
    }

    public void setHargaSewaHarian(long hargaSewaHarian){
        this.hargaSewaHarian = hargaSewaHarian;
    }

    public long getHargaPerlengkapan(){
        return hargaPerlengkapan;
    }

    public void setHargaPerlengkapan(long hargaPerlengkapan){
        this.hargaPerlengkapan = hargaPerlengkapan;
    }

    public void tambahStok(int jumlah){
        // ini penambahan
        if (jumlah <=0){
            throw new IllegalArgumentException("Jumlah penambahan stok harus lebih dari 0");
        }

        hitungJumlahStok(this.stok + jumlah);
    }

    public void kurangiStok(int jumlah){
        // ini penambahan
        if (jumlah <= 0){
            throw new IllegalArgumentException("Jumlah pengurangan harus lebih dari 0");
        }
        if (jumlah > this.stok){
            throw new StokTidakCukupException("Stok perlengkapan tidak cukup");
        }
        hitungJumlahStok(this.stok - jumlah);
    }

    public void hitungJumlahStok(int jumlah){
        // ini penambahan
        if (jumlah < 0){
            throw new IllegalArgumentException("Jumlah stok tidak boleh negatif");
        }
        this.stok = jumlah;
    }

    public abstract long hitungHargaSewa(int durasiSewa);

    public long hitungGantiRugi(){
        return hargaPerlengkapan;
    }

    // ini penambahan
    // public String getNamaVendor(){
    //     return namaVendor;
    // }

    // ini penambahan
    public void setNamaVendor(String namaVendor){
        this.namaVendor = namaVendor;
    }
}
