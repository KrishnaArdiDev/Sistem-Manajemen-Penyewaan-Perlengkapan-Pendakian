package com.sistemmanajemenpenyewaanperlengkapanpendakian.model;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.DataTidakLengkapException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class Transaksi implements DapatDicetak {

    public static final String STATUS_BERLANGSUNG = "BERLANGSUNG";
    public static final String STATUS_SELESAI = "SELESAI";

    private int idTransaksi; // 0 berarti belum disimpan ke database
    private int idPelanggan;
    private int idVendor;
    private LocalDate tanggalSewa;
    private LocalDate tanggalKembaliRencana;
    private LocalDate tanggalKembaliAktual; // null selama belum dikembalikan
    private double totalBiaya;
    private double denda;
    private double gantiRugi;
    private String status;

    // untuk transaksi baru
    public Transaksi(int idPelanggan, int idVendor, LocalDate tanggalSewa,
                     LocalDate tanggalKembaliRencana) throws DataTidakLengkapException {
        this.idPelanggan = idPelanggan;
        this.idVendor = idVendor;
        this.tanggalSewa = tanggalSewa;
        this.tanggalKembaliRencana = tanggalKembaliRencana;
        this.status = STATUS_BERLANGSUNG;
        validasi();
    }

    // untuk data yang dibaca dari database
    public Transaksi(int idTransaksi, int idPelanggan, int idVendor,
                     LocalDate tanggalSewa, LocalDate tanggalKembaliRencana,
                     LocalDate tanggalKembaliAktual, double totalBiaya,
                     double denda, double gantiRugi, String status)
            throws DataTidakLengkapException {
        this.idTransaksi = idTransaksi;
        this.idPelanggan = idPelanggan;
        this.idVendor = idVendor;
        this.tanggalSewa = tanggalSewa;
        this.tanggalKembaliRencana = tanggalKembaliRencana;
        this.tanggalKembaliAktual = tanggalKembaliAktual;
        this.totalBiaya = totalBiaya;
        this.denda = denda;
        this.gantiRugi = gantiRugi;
        this.status = status;
        validasi();
    }

    public final void validasi() throws DataTidakLengkapException {
        if (idPelanggan <= 0) {
            throw new DataTidakLengkapException("Pelanggan wajib dipilih.");
        }
        if (idVendor <= 0) {
            throw new DataTidakLengkapException("Vendor wajib diisi.");
        }
        if (tanggalSewa == null) {
            throw new DataTidakLengkapException("Tanggal sewa wajib diisi.");
        }
        if (tanggalKembaliRencana == null) {
            throw new DataTidakLengkapException("Tanggal kembali (rencana) wajib diisi.");
        }
        if (tanggalKembaliRencana.isBefore(tanggalSewa)) {
            throw new DataTidakLengkapException(
                    "Tanggal kembali tidak boleh sebelum tanggal sewa.");
        }
        if (tanggalKembaliAktual != null && tanggalKembaliAktual.isBefore(tanggalSewa)) {
            throw new DataTidakLengkapException(
                    "Tanggal pengembalian tidak boleh sebelum tanggal sewa.");
        }
        if (totalBiaya < 0 || denda < 0 || gantiRugi < 0) {
            throw new DataTidakLengkapException(
                    "Biaya, denda, dan ganti rugi tidak boleh negatif.");
        }
        if (!STATUS_BERLANGSUNG.equals(status) && !STATUS_SELESAI.equals(status)) {
            throw new DataTidakLengkapException("Status transaksi tidak valid: " + status);
        }
    }

    public long hitungKeterlambatanHari(LocalDate tanggalKembali)
            throws DataTidakLengkapException {
        if (tanggalKembali == null) {
            throw new DataTidakLengkapException("Tanggal pengembalian wajib diisi.");
        }
        return Math.max(0, ChronoUnit.DAYS.between(tanggalKembaliRencana, tanggalKembali));
    }

    public void tandaiSelesai(LocalDate tanggalAktual, double denda, double gantiRugi)
            throws DataTidakLengkapException {
        if (STATUS_SELESAI.equals(status)) {
            throw new IllegalStateException("Transaksi ini sudah selesai.");
        }
        this.tanggalKembaliAktual = tanggalAktual;
        this.denda = denda;
        this.gantiRugi = gantiRugi;
        this.status = STATUS_SELESAI;
        try {
            validasi();
        } catch (DataTidakLengkapException e) {
            // data tidak valid, kembalikan ke kondisi semula
            this.tanggalKembaliAktual = null;
            this.denda = 0;
            this.gantiRugi = 0;
            this.status = STATUS_BERLANGSUNG;
            throw e;
        }
    }

    public boolean isSelesai() {
        return STATUS_SELESAI.equals(status);
    }

    public double getTotalBayar() {
        return totalBiaya + denda + gantiRugi;
    }

    @Override
    public String cetak() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("No. Transaksi    : %d%n", idTransaksi));
        sb.append(String.format("ID Pelanggan     : %d%n", idPelanggan));
        sb.append(String.format("Tanggal Sewa     : %s%n", tanggalSewa));
        sb.append(String.format("Batas Kembali    : %s%n", tanggalKembaliRencana));
        sb.append(String.format("Dikembalikan     : %s%n",
                tanggalKembaliAktual == null ? "-" : tanggalKembaliAktual.toString()));
        sb.append(String.format("Biaya Sewa       : %s%n", rupiah(totalBiaya)));
        sb.append(String.format("Denda            : %s%n", rupiah(denda)));
        sb.append(String.format("Ganti Rugi       : %s%n", rupiah(gantiRugi)));
        sb.append(String.format("TOTAL BAYAR      : %s%n", rupiah(getTotalBayar())));
        sb.append(String.format("Status           : %s", status));
        return sb.toString();
    }

    private static String rupiah(double nilai) {
        return String.format(new Locale("id", "ID"), "Rp%,.0f", nilai);
    }

    public int getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(int idTransaksi) { this.idTransaksi = idTransaksi; }

    public int getIdPelanggan() { return idPelanggan; }
    public int getIdVendor() { return idVendor; }
    public LocalDate getTanggalSewa() { return tanggalSewa; }
    public LocalDate getTanggalKembaliRencana() { return tanggalKembaliRencana; }
    public LocalDate getTanggalKembaliAktual() { return tanggalKembaliAktual; }
    public double getTotalBiaya() { return totalBiaya; }
    public double getDenda() { return denda; }
    public double getGantiRugi() { return gantiRugi; }
    public String getStatus() { return status; }

    public void setTotalBiaya(double totalBiaya) throws DataTidakLengkapException {
        if (totalBiaya < 0) {
            throw new DataTidakLengkapException("Total biaya tidak boleh negatif.");
        }
        this.totalBiaya = totalBiaya;
    }
}