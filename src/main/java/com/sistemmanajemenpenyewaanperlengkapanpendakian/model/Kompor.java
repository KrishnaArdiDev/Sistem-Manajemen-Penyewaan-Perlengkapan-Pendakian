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
        return getHargaSewaHarian() *durasiSewa;
    }
}
