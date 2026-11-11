package com.sistemmanajemenpenyewaanperlengkapanpendakian.model;

public class Sepatu extends Perlengkapan{
    private int ukuranSepatu;

    public int getUkuranSepatu(){
        return this.ukuranSepatu;
    }

    public void setUkuranSepatu(int ukuranSepatu){
        this.ukuranSepatu = ukuranSepatu;
    }

    @Override 
    public long hitungHargaSewa(int durasiSewa){
        if (durasiSewa <= 0){
            throw new IllegalArgumentException("Durasi sewa harus lebih dari 0");
        }

        return Math.multiplyExact(getHargaSewaHarian(), durasiSewa);
    }
}

