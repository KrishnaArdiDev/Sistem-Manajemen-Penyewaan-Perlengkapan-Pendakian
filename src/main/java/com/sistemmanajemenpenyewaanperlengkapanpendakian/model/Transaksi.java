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
    private long denda;
    private String metodePembayaran;

    private Pelanggan pelanggan;
    private List<DetailTransaksi> daftarDetail;

    public Transaksi(Pelanggan pelanggan, List<DetailTransaksi> daftarDetail,
                     LocalDate tanggalTransaksi, LocalDate tanggalSewa,
                     int durasiSewa, String metodePembayaran) {
        this.pelanggan = pelanggan;
        this.daftarDetail = (daftarDetail != null) ? daftarDetail : new ArrayList<>();
        this.tanggalTransaksi = tanggalTransaksi;
        this.tanggalSewa = tanggalSewa;
        this.durasiSewa = durasiSewa;
        this.metodePembayaran = metodePembayaran;
    }

    public Transaksi(int idTransaksi, Pelanggan pelanggan, List<DetailTransaksi> daftarDetail,
                     LocalDate tanggalTransaksi, LocalDate tanggalSewa, int durasiSewa,
                     LocalDate tanggalKembali, long totalHarga, long denda,
                     String metodePembayaran) {
        this(pelanggan, daftarDetail, tanggalTransaksi, tanggalSewa, durasiSewa, metodePembayaran);
        this.idTransaksi = idTransaksi;
        this.tanggalKembali = tanggalKembali;
        this.totalHarga = totalHarga;
        this.denda = denda;
    }

    public int getIdTransaksi() { return idTransaksi; }

    public LocalDate getTanggalSewa() { return tanggalSewa; }
    public void setTanggalSewa(LocalDate tanggalSewa) { this.tanggalSewa = tanggalSewa; }

    public int getDurasiSewa() { return durasiSewa; }
    public void setDurasiSewa(int durasiSewa) { this.durasiSewa = durasiSewa; }

    public LocalDate getTanggalKembali() { return tanggalKembali; }

    public long getTotalHarga() { return totalHarga; }

    public long getDenda() { return denda; }

    public String getMetodePembayaran() { return metodePembayaran; }
    public void setMetodePembayaran(String metodePembayaran) { this.metodePembayaran = metodePembayaran; }

    public LocalDate getTanggalTransaksi() { return tanggalTransaksi; }
    public Pelanggan getPelanggan() { return pelanggan; }
    public List<DetailTransaksi> getDaftarDetail() { return daftarDetail; }

    public long hitungTotalHarga() {
        long total = 0;
        for (DetailTransaksi detail : daftarDetail) {
            total += detail.getPerlengkapanYangDisewa().hitungHargaSewa(durasiSewa)
                    * detail.getKuantitasPerAlat();
        }
        this.totalHarga = total;
        return total;
    }

    public LocalDate hitungTanggalKembali() {
        return tanggalSewa.plusDays(durasiSewa);
    }

    public long hitungDenda() {
        if (tanggalSewa == null || durasiSewa <= 0) {
            this.denda = 0;
            return 0;
        }
        LocalDate batas = (tanggalKembali != null) ? tanggalKembali : hitungTanggalKembali();

        LocalDate terakhir = null;
        for (DetailTransaksi detail : daftarDetail) {
            LocalDate tgl = detail.getTanggalPengembalianBarang();
            if (tgl != null && (terakhir == null || tgl.isAfter(terakhir))) {
                terakhir = tgl;
            }
        }
        if (terakhir == null) {
            this.denda = 0;
            return 0;
        }
        long hariTerlambat = Math.max(0, ChronoUnit.DAYS.between(batas, terakhir));
        long biayaSewaSehari = hitungTotalHarga() / durasiSewa;
        this.denda = hariTerlambat * biayaSewaSehari;
        return denda;
    }

    public void konfirmasiTransaksi() throws DataTidakLengkapException {
        if (pelanggan == null
                || isKosong(pelanggan.getNamaPelanggan())
                || isKosong(pelanggan.getNoIdentitasPelanggan())) {
            throw new DataTidakLengkapException("Data pelanggan wajib diisi lengkap.");
        }
        if (daftarDetail == null || daftarDetail.isEmpty()) {
            throw new DataTidakLengkapException("Perlengkapan yang disewa wajib dipilih.");
        }
        if (tanggalTransaksi == null) {
            throw new DataTidakLengkapException("Tanggal transaksi wajib diisi.");
        }
        if (tanggalSewa == null) {
            throw new DataTidakLengkapException("Tanggal sewa wajib diisi.");
        }
        if (durasiSewa <= 0) {
            throw new DataTidakLengkapException("Durasi sewa minimal 1 hari.");
        }
        if (isKosong(metodePembayaran)) {
            throw new DataTidakLengkapException("Metode pembayaran wajib diisi.");
        }
        this.tanggalKembali = hitungTanggalKembali();
        hitungTotalHarga();
    }

    @Override
    public void cetakNota() {
        System.out.println("========== NOTA PENYEWAAN ==========");
        System.out.println("No. Transaksi     : " + idTransaksi);
        System.out.println("Pelanggan         : " + pelanggan.getNamaPelanggan());
        System.out.println("No. Identitas     : " + pelanggan.getNoIdentitasPelanggan());
        System.out.println("Tanggal Transaksi : " + tanggalTransaksi);
        System.out.println("Tanggal Sewa      : " + tanggalSewa);
        System.out.println("Durasi            : " + durasiSewa + " hari");
        System.out.println("Tanggal Kembali   : " + tanggalKembali);
        System.out.println("Metode Pembayaran : " + metodePembayaran);
        System.out.println("------------------------------------");
        System.out.println("Total Harga       : " + rupiah(totalHarga));
        System.out.println("Denda             : " + rupiah(denda));
        System.out.println("TOTAL BAYAR       : " + rupiah(totalHarga + denda));
        System.out.println("====================================");
    }

    @Override
    public void ringkasanCetak() {
        System.out.println("Transaksi #" + idTransaksi
                + " | " + pelanggan.getNamaPelanggan()
                + " | " + tanggalSewa + " s/d " + tanggalKembali
                + " | Total: " + rupiah(totalHarga + denda));
    }

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