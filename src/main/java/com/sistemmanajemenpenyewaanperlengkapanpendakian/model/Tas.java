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
        return getHargaSewaHarian() * durasiSewa;
    }
}
