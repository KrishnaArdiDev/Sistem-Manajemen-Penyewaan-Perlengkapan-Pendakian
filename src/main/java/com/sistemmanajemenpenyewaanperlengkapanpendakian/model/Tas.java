package com.sistemmanajemenpenyewaanperlengkapanpendakian.model;

public class Tas extends Perlengkapan{
    private String tipeTas;

    public String getTipeTas(){
        return tipeTas;
    }

    public void setTipeTas(String tipeTas){
        this.tipeTas = tipeTas;
    }

    @Override
    public long hitungHargaSewa(int durasiSewa){
        if (durasiSewa <= 0){
            throw new IllegalArgumentException( "Durasi sewa harus lebih dari 0");
        }
        return Math.multiplyExact(getHargaSewaHarian(), durasiSewa);
    }
}
