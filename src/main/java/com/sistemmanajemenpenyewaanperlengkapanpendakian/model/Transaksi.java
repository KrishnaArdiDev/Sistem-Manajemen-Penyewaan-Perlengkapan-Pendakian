package com.sistemmanajemenpenyewaanperlengkapanpendakian.model;
import java.time.LocalDate;
public class Transaksi {
    private String namaPenyewa, namaBarang, status;
    private int jumlah;
    private LocalDate tglSewa, tglKembali;
    private long totalHarga;
    private String fotoIdentitas;

        public Transaksi(String namaPenyewa, String namaBarang, int jumlah,LocalDate tglSewa, LocalDate tglKembali, long totalHarga) {
        this.namaPenyewa = namaPenyewa;
        this.namaBarang = namaBarang;
        this.jumlah = jumlah;
        this.tglSewa = tglSewa;
        this.tglKembali = tglKembali;
        this.totalHarga = totalHarga;
        this.status = "Berhasil";
        }

        public Transaksi(String namaPenyewa, String namaBarang, int jumlah,
                         LocalDate tglSewa, LocalDate tglKembali,
                         long totalHarga, String fotoIdentitas) {
            this(namaPenyewa, namaBarang, jumlah, tglSewa, tglKembali, totalHarga);
            this.fotoIdentitas = fotoIdentitas;
        }

    public String getNamaPenyewa(){ 
    return namaPenyewa; }
    public String getNamaBarang(){
         return namaBarang; }
    public int getJumlah(){
         return jumlah; }
    public LocalDate getTglSewa(){
         return tglSewa; }
    public LocalDate getTglKembali(){ 
        return tglKembali; }
    public long getTotalHarga(){
        return totalHarga; }
    public String getStatus(){ 
        return status; }
    public String getFotoIdentitas(){
        return fotoIdentitas; }
 }

