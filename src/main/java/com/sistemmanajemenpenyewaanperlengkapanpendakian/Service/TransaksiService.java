package com.sistemmanajemenpenyewaanperlengkapanpendakian.Service;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.TransaksiDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.DetailTransaksiDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.PerlengkapanDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.DataTidakLengkapException;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.DetailTransaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Perlengkapan;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TransaksiService {

    private static final TransaksiDAO transaksiDAO = new TransaksiDAO();
    private static final DetailTransaksiDAO detailTransaksiDAO = new DetailTransaksiDAO();
    private static final PerlengkapanDAO perlengkapanDAO = new PerlengkapanDAO();
    private static volatile int idVendorAktif;

    public static void aturanVendorAktif(int idVendor){
        if (idVendor <=0){
            throw new IllegalArgumentException("ID vendor tidak valid");
        }
        idVendorAktif = idVendor;
    }

    public static void bersihkanVendorAktif(){
        idVendorAktif= 0;
    }

    private static int ambilIdVendorAktif(){
        if (idVendorAktif<=0){
            throw new IllegalArgumentException("Vendor belum login atau sesi vendor belum ditemukan");
        }
        return idVendorAktif;
    }

    public static int getIdVendorAktif() {
        return ambilIdVendorAktif();
    }

    public static void tambah(Transaksi transaksi, int idVendor) throws SQLException, DataTidakLengkapException{
        
        if (transaksi == null) {
            throw new IllegalArgumentException("Transaksi tidak boleh null");
        }

        if (idVendor <=0 ){
            throw new IllegalArgumentException("ID Vendor tidak valid");
        }
        
        if (transaksi.getIdTransaksi() >0){
            throw new IllegalArgumentException("Transaksi ini sudah memiliki ID dan tidak dapat ditambahkan ulang");
        }

        if (transaksi.getDaftarDetail() == null || transaksi.getDaftarDetail().isEmpty()){
            throw new DataTidakLengkapException("Perlengkapan yang disewa wajib dipilih");
        }

        int idTransaksiAwal = transaksi.getIdTransaksi();

        Map<DetailTransaksi, Integer> idDetailAwal = new IdentityHashMap<>();
        Map<DetailTransaksi, Perlengkapan> perlengkapanAwal = new IdentityHashMap<>();
        Map<DetailTransaksi, Long> hargaSewaAwal = new IdentityHashMap<>();

        for (DetailTransaksi detail : transaksi.getDaftarDetail()){
            if (detail == null){
                throw new DataTidakLengkapException("Detail transaksi tidak boleh kosong");
            }
            idDetailAwal.put(detail, detail.getIdDetailTransaksi());
            perlengkapanAwal.put(detail, detail.getPerlengkapanYangDisewa());
            hargaSewaAwal.put(detail, detail.getHargaSewaHarianSaatTransaksi());
        }

        try (Connection connection = DatabaseConnection.getConnection()){
            connection.setAutoCommit(false);
            try{
                Map <Integer, Perlengkapan> daftarPerlengkapan = new LinkedHashMap<>();
                List<DetailTransaksi> daftarDetailValidasi = new ArrayList<>();

                for (DetailTransaksi detail : transaksi.getDaftarDetail()){
                    Perlengkapan perlengkapanInput = detail.getPerlengkapanYangDisewa();
                    if (perlengkapanInput == null || perlengkapanInput.getIdPerlengkapan() <=0){
                        throw new DataTidakLengkapException("Perlengkapan pada detail transaksi tidak valid");
                    }
                    int idPerlengkapan = perlengkapanInput.getIdPerlengkapan();
                    Perlengkapan perlengkapanDatabase = daftarPerlengkapan.get(idPerlengkapan);

                    if (perlengkapanDatabase == null){
                        perlengkapanDatabase = perlengkapanDAO.ambilPerlengkapanBerdasarkanId(connection, idPerlengkapan,idVendor);
                    

                    if (perlengkapanDatabase == null){
                        throw new DataTidakLengkapException( "Perlengkapan dengan ID " + idPerlengkapan + " tidak ditemukan atau bukan milik vendor ini");
                    }
                    daftarPerlengkapan.put(idPerlengkapan, perlengkapanDatabase);
                }

                detail.setPerlengkapanYangDisewa(perlengkapanDatabase);
                detail.setHargaSewaHarianSaatTransaksi(perlengkapanDatabase.getHargaSewaHarian());
                DetailTransaksi detailValidasi = new DetailTransaksi();

                detailValidasi.setPerlengkapanYangDisewa(perlengkapanDatabase);
                detailValidasi.setKuantitasPerAlat(detail.getKuantitasPerAlat());
                daftarDetailValidasi.add(detailValidasi);
                }
                Transaksi transaksiValidasi = new Transaksi(transaksi.getPelanggan(), daftarDetailValidasi, transaksi.getTanggalTransaksi(), transaksi.getTanggalSewa(), transaksi.getDurasiSewa(), transaksi.getMetodePembayaran());

                transaksiValidasi.konfirmasiTransaksi();
                transaksi.hitungTotalHarga();
                transaksiDAO.tambahDataTransaksi(connection,transaksi, idVendor);

                int idTransaksiBaru = transaksi.getIdTransaksi();
                for (DetailTransaksi detail : transaksi.getDaftarDetail()){
                    detailTransaksiDAO.tambahDetailTransaksi(connection, detail, idTransaksiBaru);
                }
                for (Map.Entry<Integer, Perlengkapan> entry : daftarPerlengkapan.entrySet()){
                    Perlengkapan perlengkapan = entry.getValue();

                    perlengkapanDAO.updateStokPerlengkapan(connection, entry.getKey(), perlengkapan.getStok());
                }
                connection.commit();
                transaksi.tandaiTransaksiTersimpan(transaksiValidasi.getTanggalKembali());
            } catch (SQLException | DataTidakLengkapException | RuntimeException e){
                try{
                    connection.rollback();
                } catch (SQLException rollbackError){
                    e.addSuppressed(rollbackError);
                }
                transaksi.setIdTransaksi(idTransaksiAwal);
                for (DetailTransaksi detail : transaksi.getDaftarDetail()){
                    if (detail!= null){
                        detail.setIdDetailTransaksi(idDetailAwal.get(detail));

                        detail.setPerlengkapanYangDisewa(perlengkapanAwal.get(detail));
                        detail.setHargaSewaHarianSaatTransaksi(hargaSewaAwal.get(detail));
                    }
                }
                throw e;
            }
        }
    }

    public static List<Transaksi> semua(int idVendor) throws SQLException{
        if (idVendor <= 0){
            throw new IllegalArgumentException("ID vendor tidak valid");
        }

        List<Transaksi> daftarTransaksi = transaksiDAO.ambilSemuaTransaksiByVendor(idVendor);

        return Collections.unmodifiableList(daftarTransaksi);
    } 
    public static Transaksi cariById(int idVendor, int idTransaksi) throws SQLException{
        if (idTransaksi <=0){
            return null;
        }

        List<Transaksi> daftarTransaksi = semua(idVendor);
        
        for (Transaksi transaksi : daftarTransaksi) {
            if (transaksi.getIdTransaksi() == idTransaksi) {
                return transaksi;
            }
        }
        return null;
    }

public static void updateDataTransaksi(Transaksi transaksi, int idVendor) throws SQLException {
    if (transaksi == null){
        throw new IllegalArgumentException("Transaksi tidak boleh null");
    }

    if (idVendor <=0){
        throw new IllegalArgumentException("ID vendor tidak valid");
    }

    if (transaksi.getIdTransaksi() <=0){
        throw new IllegalArgumentException("ID transaksi tidak valid");
    }

    transaksiDAO.updateDataTransaksi(transaksi, idVendor);
}
    public static void tambah(Transaksi transaksi) {

        try {
            tambah(transaksi, ambilIdVendorAktif());

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Gagal menyimpan transaksi ke database", e);

        } catch (DataTidakLengkapException e) {
            throw new IllegalArgumentException(
                    "Data transaksi belum lengkap: " + e.getMessage(), e);
        }
    }
    public static List<Transaksi> semua() {

        try {
            return semua(ambilIdVendorAktif());

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Gagal mengambil data transaksi", e);
        }
    }
    public static Transaksi cariById(int idTransaksi) {

        try {
            return cariById(
                    ambilIdVendorAktif(), idTransaksi);

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Gagal mencari transaksi", e);
        }
    }
}