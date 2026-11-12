package com.sistemmanajemenpenyewaanperlengkapanpendakian.model;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.DataTidakLengkapException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Transaksi implements DapatDicetak {

    private int idTransaksi;
    private LocalDate tanggalSewa;
    private int durasiSewa;
    private LocalDate tanggalKembali;
    private LocalDate tanggalTransaksi;
    private long totalHarga;
    private String fotoIdentitas;

    private boolean isKosong(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String rupiah(long nilai) {
        return String.format(Locale.forLanguageTag("id-ID"), "Rp%,d", nilai);
    }

    private String namaBarangTemp;
    private String namaPenyewaTemp;

    public Transaksi(String namaPenyewa, String namaBarang, int kuantitas, LocalDate tglSewa, LocalDate tglKembali, long totalHarga) {
        this.pelanggan = new Pelanggan();
        this.pelanggan.setNamaPelanggan(namaPenyewa);
        this.pelanggan.setNoIdentitasPelanggan("-");

        this.tanggalTransaksi = LocalDate.now();
        this.tanggalSewa = tglSewa;
        this.tanggalKembali = tglKembali;
        this.durasiSewa = (int) Math.max(1, java.time.temporal.ChronoUnit.DAYS.between(tglSewa, tglKembali));
        this.metodePembayaran = "Tunai";
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
        this.denda = 0;

        this.namaBarangTemp = namaBarang;
        this.namaPenyewaTemp = namaPenyewa;
        this.stokSudahDikurangi = false;
    }

    public String getNamaBarang() {
        if (daftarDetail != null && !daftarDetail.isEmpty() && daftarDetail.get(0).getPerlengkapanYangDisewa() != null) {
            return daftarDetail.get(0).getPerlengkapanYangDisewa().getNamaPerlengkapan();
        }
        return namaBarangTemp != null ? namaBarangTemp : "-";
    }

    public String getNamaPenyewa() {
        if (pelanggan != null && pelanggan.getNamaPelanggan() != null) {
            return pelanggan.getNamaPelanggan();
        }
        return namaPenyewaTemp != null ? namaPenyewaTemp : "-";
    }

    public LocalDate getTglSewa() {
        return tanggalSewa;
    }

    public LocalDate getTglKembali() {
        return tanggalKembali;
    }

    public String getStatus() {
        return "Aktif";
    }
}