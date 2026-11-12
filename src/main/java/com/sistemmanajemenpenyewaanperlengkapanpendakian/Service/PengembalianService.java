package com.sistemmanajemenpenyewaanperlengkapanpendakian.Service;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.DataTidakLengkapException;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.DetailTransaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PengembalianService {

    public static List<Transaksi> getTransaksiAktif() {
        List<Transaksi> hasil = new ArrayList<>();
        for (Transaksi t : TransaksiService.semua()) {
            if (!isSelesai(t)) {
                hasil.add(t);
            }
        }
        return hasil;
    }

    public static boolean isSelesai(Transaksi t) {
        if (t.getDaftarDetail() == null || t.getDaftarDetail().isEmpty()) {
            return false;
        }
        for (DetailTransaksi d : t.getDaftarDetail()) {
            if (d != null && d.getTanggalPengembalianBarang() == null) {
                return false;
            }
        }
        return true;
    }

    public static HasilPengembalian prosesPengembalian(Transaksi t, DetailTransaksi detail, LocalDate tglKembaliAktual, String kondisi) throws DataTidakLengkapException {
        if (t == null) {
            throw new DataTidakLengkapException("Transaksi tidak ditemukan.");
        }
        if (detail == null) {
            throw new DataTidakLengkapException("Detail transaksi tidak valid.");
        }
        if (tglKembaliAktual == null) {
            throw new DataTidakLengkapException("Tanggal pengembalian aktual wajib diisi.");
        }
        if (kondisi == null || kondisi.trim().isEmpty()) {
            throw new DataTidakLengkapException("Kondisi perlengkapan wajib diisi.");
        }

        long gantiRugi = detail.prosesPengembalianBarang(tglKembaliAktual, kondisi);

        long denda = t.hitungDenda();

        return new HasilPengembalian(denda, gantiRugi);
    }

    public static class HasilPengembalian {
        private final long denda;
        private final long gantiRugi;

        public HasilPengembalian(long denda, long gantiRugi) {
            this.denda = denda;
            this.gantiRugi = gantiRugi;
        }

        public long getDenda() {
            return denda;
        }

        public long getGantiRugi() {
            return gantiRugi;
        }

        public long getTotalTagihan() {
            return Math.addExact(denda, gantiRugi);
        }
    }
}