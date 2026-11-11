package com.sistemmanajemenpenyewaanperlengkapanpendakian.model;

public class Kompor extends Perlengkapan{
    private String tipeKompor;

    public String getTipeKompor(){
        return tipeKompor;
    }

    public void setTipeKompor(String tipeKompor){
        this.tipeKompor = tipeKompor;
    }

    @Override 
    public long hitungHargaSewa(int durasiSewa){
        if (durasiSewa <=0){
            throw new IllegalArgumentException("Durasi sewa harus lebih dari 0");
        }

        return Math.multiplyExact(getHargaSewaHarian(), durasiSewa);
    }
}
