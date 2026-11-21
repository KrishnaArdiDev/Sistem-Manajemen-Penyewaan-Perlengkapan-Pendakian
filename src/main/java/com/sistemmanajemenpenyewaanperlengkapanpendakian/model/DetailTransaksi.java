package com.sistemmanajemenpenyewaanperlengkapanpendakian.model;
import java.time.LocalDate;


public class DetailTransaksi {
    private int idDetailTransaksi;
    private int kuantitasPerAlat;
    private LocalDate tanggalPengembalianBarang;
    private Perlengkapan perlengkapanYangDisewa;
    private String statusPengembalian;
    private long biayaGantiRugi;
    // penambahan
    private Long hargaSewaHarianSaatTransaksi;

    // penambahan
    private boolean pengembalianSudahDiproses;

    public String getStatusPengembalianBarang(){
        return statusPengembalian;
    }

    public void setStatusPengembalianBarang(String statusPengembalian){
        this.statusPengembalian = statusPengembalian;
    }
    public void setPengembalianSudahDiproses(boolean status) {
    this.pengembalianSudahDiproses = status;
}

    public long getBiayaGantiRugi(){
        return biayaGantiRugi;
    }

    public Long getHargaSewaHarianSaatTransaksi(){
    return hargaSewaHarianSaatTransaksi;
    }

    public void setHargaSewaHarianSaatTransaksi(Long hargaSewaHarianSaatTransaksi){
        if (hargaSewaHarianSaatTransaksi != null && hargaSewaHarianSaatTransaksi < 0){
            throw new IllegalArgumentException("Harga sewa harian saat transaksi tidak boleh negatif");
        }
        this.hargaSewaHarianSaatTransaksi = hargaSewaHarianSaatTransaksi;
    }

    private long ambilHargaSewaHarianUntukPerhitungan(){
        return hargaSewaHarianSaatTransaksi != null ? hargaSewaHarianSaatTransaksi : perlengkapanYangDisewa.getHargaSewaHarian();
    }

    public long getSubTotal(){
        if (perlengkapanYangDisewa == null){
            throw new IllegalStateException("Perlengkapan yang disewa belum ditemukan");
        }

        if (kuantitasPerAlat <= 0){
            throw new IllegalStateException("Kuantitas perlengkapan belum ditentukan");
        }
        return Math.multiplyExact(ambilHargaSewaHarianUntukPerhitungan(), kuantitasPerAlat);
    }

    public LocalDate getTanggalPengembalianBarang(){
        return tanggalPengembalianBarang;
    }

    public int getIdDetailTransaksi(){
        return idDetailTransaksi;
    }

    public void setIdDetailTransaksi(int idDetailTransaksi){
        this.idDetailTransaksi = idDetailTransaksi;
    }

    // Penambahan setter getter kuantitas
    public int getKuantitasPerAlat(){
        return kuantitasPerAlat;
    }

    public void setKuantitasPerAlat(int kuantitasPerAlat){
        if (kuantitasPerAlat <= 0){
            throw new IllegalArgumentException("Kuantitas harus lebih dari 0");
        }
        this.kuantitasPerAlat = kuantitasPerAlat;
    }

    // penambahan getter setter tanggal pengembalian
    public void setTanggalPengembalianBarang(LocalDate tanggalPengembalianBarang){
        this.tanggalPengembalianBarang = tanggalPengembalianBarang;
    }

    // penambahan getter dan setter obj perlengkapan
    public Perlengkapan getPerlengkapanYangDisewa(){
        return perlengkapanYangDisewa;
    }

    public void setPerlengkapanYangDisewa(Perlengkapan perlengkapanYangDisewa){
        this.perlengkapanYangDisewa = perlengkapanYangDisewa;
    }

    // penambahan setter getter biaya ganti rugi
    public void setBiayaGantiRugi(long biayaGantiRugi){
        if (biayaGantiRugi <0){
            throw new IllegalArgumentException("Biaya gantirugi tidak boleh negatif");
        }
        this.biayaGantiRugi = biayaGantiRugi;
    }

    // Penambahan subtotal didasarkan durasi penyewaan
    public long getSubTotal(int durasiSewa){
        if (perlengkapanYangDisewa == null){
            throw new IllegalStateException("Perlengkapan yang disewa belum ditemukan");
        }

        if (kuantitasPerAlat <= 0){
            throw new IllegalStateException("Kuantitas perlengkapan belum ditentukan");
        }

        if (durasiSewa <= 0){
            throw new IllegalArgumentException("Durasi sewa harus lebih dari 0");
        }

        long totalPerUnit = Math.multiplyExact(ambilHargaSewaHarianUntukPerhitungan(), durasiSewa);
        return Math.multiplyExact(totalPerUnit, kuantitasPerAlat);
    }

    // penambahan
    public long prosesPengembalianBarang(LocalDate tanggalPengembalian, String kondisi){
        if (pengembalianSudahDiproses || tanggalPengembalianBarang != null){
            throw new IllegalStateException("Pengembalian barang sudah diproses");
        }
        if (perlengkapanYangDisewa == null){
            throw new IllegalStateException("Perlengkapan yang disewa belum ditentukan");
        }
        if (kuantitasPerAlat <= 0){
            throw new IllegalStateException("Kuantitas perlengkapan belum ditentukan");
        }

        // argum except
        if (tanggalPengembalian == null){
            throw new IllegalArgumentException("Tanggal pengembalian wajib diisi");
        }
        if (kondisi == null || kondisi.trim().isEmpty()){
            throw new IllegalArgumentException("Kondisi perlengkapan wajib diisi");
        }

        String kondisiNormal = kondisi.trim();
        long nilaiGantiRugi = 0;

        if (kondisiNormal.equalsIgnoreCase("utuh")){
            perlengkapanYangDisewa.tambahStok(kuantitasPerAlat);
        }else if (kondisiNormal.equalsIgnoreCase("rusak") || kondisiNormal.equalsIgnoreCase("hilang")){
            nilaiGantiRugi = Math.multiplyExact(perlengkapanYangDisewa.hitungGantiRugi(), kuantitasPerAlat);
        } else {
            throw new IllegalArgumentException("Kondisi perlengkapan harus utuh, rusak, atau hilang");
        }
        this.tanggalPengembalianBarang = tanggalPengembalian;
        this.statusPengembalian = kondisiNormal;
        this.biayaGantiRugi = nilaiGantiRugi;
        this.pengembalianSudahDiproses = true;

        return biayaGantiRugi;
    }
}
