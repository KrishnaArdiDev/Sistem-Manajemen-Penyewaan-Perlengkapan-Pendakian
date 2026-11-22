package com.sistemmanajemenpenyewaanperlengkapanpendakian.Service;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.PelangganDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.DataTidakLengkapException;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Pelanggan;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class PelangganService {

    private static final PelangganDAO pelangganDAO = new PelangganDAO();
    public static Pelanggan prosesDataPelanggan(String nama, String noIdentitas) throws DataTidakLengkapException, SQLException{
        if (nama == null || nama.trim().isEmpty()) {
            throw new DataTidakLengkapException("Nama pelanggan wajib diisi.");
        }

        if (noIdentitas == null || noIdentitas.trim().isEmpty()) {
            throw new DataTidakLengkapException("Nomor identitas pelanggan wajib diisi.");
        }

        String namaBersih = nama.trim();
        String noIdBersih = noIdentitas.trim();

        if (namaBersih.length() >150){
            throw new DataTidakLengkapException("Nama pelanggan maksimal 150 karakter");
        }

        if (noIdBersih.length() >50){
            throw new DataTidakLengkapException("Nomor identitas pelanggan maksimal 50 karakter");
        }

        List<Pelanggan> daftarPelanggan = pelangganDAO.ambilBerdasarkanNoIdentitas(noIdBersih);
        if (daftarPelanggan.size() == 1) {
            return daftarPelanggan.get(0);
        }

        if (daftarPelanggan.size() > 1) {
            Pelanggan pelangganYangCocok = null;
            int jumlahNamaCocok = 0;

            for (Pelanggan pelanggan : daftarPelanggan) {
                String namaTersimpan = pelanggan.getNamaPelanggan();

                if (namaTersimpan != null
                        && namaTersimpan.trim().equalsIgnoreCase(namaBersih)) {
                    pelangganYangCocok = pelanggan;
                    jumlahNamaCocok++;
                }
            }

            if (jumlahNamaCocok == 1) {
                return pelangganYangCocok;
            }

            throw new DataTidakLengkapException(
                    "Nomor identitas ditemukan pada beberapa pelanggan. "
                    + "Nama pelanggan tidak dapat menentukan data yang benar."
            );
        }
        Pelanggan pelangganBaru = new Pelanggan();
        
        pelangganBaru.setNamaPelanggan(namaBersih);
        pelangganBaru.setNoIdentitasPelanggan(noIdBersih);
        pelangganDAO.tambahDataPelanggan(pelangganBaru);

        return pelangganBaru;
    }
    
    public static Pelanggan cariByNoIdentitas(String noIdentitas) throws SQLException {
        if (noIdentitas == null){
            return null;
        }
        String identitasDicari = noIdentitas.trim();
        if (identitasDicari.isEmpty()){
            return null;
        }
        List<Pelanggan> daftarPelanggan = pelangganDAO.ambilBerdasarkanNoIdentitas(identitasDicari);
        
        if (daftarPelanggan.isEmpty()){
            return null;
        }
        if (daftarPelanggan.size() > 1){
            throw new SQLException("Nomor identitas dimiliki oleh beberapa pelanggan. " + "Data tidak dapat ditentukan secara otomatis.");
        }
        return daftarPelanggan.get(0);
    }
    public static List<Pelanggan> semua() throws SQLException{
        List<Pelanggan> daftarPelanggan = pelangganDAO.ambilSemuaPelanggan();
    
        return Collections.unmodifiableList(daftarPelanggan);
    }
}