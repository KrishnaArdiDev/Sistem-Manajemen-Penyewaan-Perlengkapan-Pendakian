package com.sistemmanajemenpenyewaanperlengkapanpendakian.model;

public class Pelanggan {
    private String namaPelanggan;
    private String noIdentitas;
    // penambahan, ini untuk id pelanggan
    private int idPelanggan;

    public int getIdPelanggan(){
        return idPelanggan;
    }

    public void setIdPelanggan(int idPelanggan){
        this.idPelanggan = idPelanggan;
    }

    public void setNamaPelanggan(String namaPelanggan){
        this.namaPelanggan = namaPelanggan;
    }
    public String getNamaPelanggan(){
        return namaPelanggan;
    }
    public void setNoIdentitasPelanggan(String noIdentitas){
        this.noIdentitas = noIdentitas;
    }
    public String getNoIdentitasPelanggan(){
        return noIdentitas;
    }

    
}
