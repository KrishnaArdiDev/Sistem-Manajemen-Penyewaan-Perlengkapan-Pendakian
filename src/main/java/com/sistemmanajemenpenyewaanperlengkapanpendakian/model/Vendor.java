package com.sistemmanajemenpenyewaanperlengkapanpendakian.model;

import java.sql.SQLException;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.PerlengkapanDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.TransaksiDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.VendorDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.DataTidakLengkapException;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.LoginGagalException;


public class Vendor {
    private String usernameAdm;  
    private String pwAdm;
    private String email;
    private String namaVendor;

    // ini atribut tambahan, biar vendor bisa login dengan usn atau email
    private String emailAtauUsername;

    public String getEmailAtauUsername(){
        return emailAtauUsername;
    }

    public void setEmailAtauUsername(String emailAtauUsername){
        this.emailAtauUsername = emailAtauUsername;
    } 

    // Tambahan buat nyimpan id vendor
    private int idVendor;

    // tambahan untuk DAO
    private final VendorDAO vendorDAO;
    private final PerlengkapanDAO perlengkapanDAO;
    private final TransaksiDAO transaksiDAO;

    // Penambahan, ini untuk data yang akan dikelola oleh vendor
    private Perlengkapan perlengkapanYangDikelola;
    private Transaksi transaksiYangDikelola;

    // Ini constructor untuk si Vendor
    public Vendor(){
        this.vendorDAO = new VendorDAO();
        this.perlengkapanDAO = new PerlengkapanDAO();
        this.transaksiDAO = new TransaksiDAO();
    }

    // Ini method yg memang dibikin di rancangan
    //#region
    public boolean login() throws LoginGagalException{
        try{
            Vendor vendorDitemukan = vendorDAO.login(emailAtauUsername, pwAdm);
            if (vendorDitemukan == null){
                throw new LoginGagalException("Username atau password tidak sesuai");
            }

            this.idVendor = vendorDitemukan.getIdVendor();
            this.email = vendorDitemukan.getEmail();
            this.usernameAdm = vendorDitemukan.getUsernameAdm();
            this.namaVendor = vendorDitemukan.getNamaVendor();
            return true;
        } catch (SQLException e){
            System.err.println("Gagal melakukan login: " + e.getMessage());
            return false;
        }
    }

    public void tambahDataPerlengkapan(){
        try{
            if (perlengkapanYangDikelola == null){
                throw new IllegalStateException("Data perlengkapan belum ditentukan");
            }
            perlengkapanYangDikelola.setNamaVendor(namaVendor);

            perlengkapanDAO.tambahDataPerlengkapan(perlengkapanYangDikelola, idVendor);
        } catch (SQLException e){
            throw new IllegalStateException("Gagal menambahkan perlengkapan", e);
        }
    }

    public void updateDataPerlengkapan(){
        try{
            if (perlengkapanYangDikelola == null){
                throw new IllegalStateException("Data perlengkapan belum ditentukan");
            }
            perlengkapanYangDikelola.setNamaVendor(namaVendor);
            perlengkapanDAO.updateDataPerlengkapan(perlengkapanYangDikelola, idVendor);
        } catch (SQLException e){
            throw new IllegalStateException("Gagal memperbarui data perlengkapan", e);
        }
    }

    public void hapusDataPerlengkapan(){
        try{
            if (perlengkapanYangDikelola == null){
                throw new IllegalStateException("Data perlengkapan belum ditentukan");
            }
            perlengkapanDAO.hapusDataPerlengkapan(perlengkapanYangDikelola, idVendor);
        } catch (SQLException e){
            throw new IllegalStateException("Gagal menghapus perlengkapan", e);
        }
    }

    public void tambahDataTransaksi() throws DataTidakLengkapException{
        if (transaksiYangDikelola == null){
            throw new DataTidakLengkapException("Data transaksi belum ditentukan");
        }
        transaksiYangDikelola.konfirmasiTransaksi();

        try{
            transaksiDAO.tambahDataTransaksi(transaksiYangDikelola, idVendor);
        } catch (SQLException e){
            throw new IllegalStateException("Gagal menambahkan transaksi", e);
        }
    }

    public void updateDataTransaksi(){
        try{
            if (transaksiYangDikelola == null){
                throw new IllegalStateException("Data transaksi belum ditentukan");
            }
            transaksiDAO.updateDataTransaksi(transaksiYangDikelola, idVendor);
        } catch (SQLException e){
            throw new IllegalStateException("Gagal memperbarui transaksi", e);
        }
    }
    //#endregion

    //#region
    // penambahan getter dan setter ID vendor
    public int getIdVendor(){
        return idVendor;
    }

    public void setIdVendor(int idVendor){
        this.idVendor = idVendor;
    }
    // getter setter atribut
    public String getUsernameAdm(){
        return usernameAdm;
    }

    public void setUsernameAdm(String usernameAdm){
        this.usernameAdm = usernameAdm;
    }

    public String getPwAdm(){
        return pwAdm;
    }

    public void setPwAdm(String pwAdm){
        this.pwAdm = pwAdm;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getNamaVendor(){
        return namaVendor;
    }

    public void setNamaVendor(String namaVendor){
        this.namaVendor = namaVendor;
    }
    //#endregion

    //#region
    // ada penambahan, menentukan objek
    public void setPerlengkapanYangDikelola(Perlengkapan perlengkapan){
        this.perlengkapanYangDikelola = perlengkapan;
    }

    public void setTransaksiYangDikelola(Transaksi transaksi){
        this.transaksiYangDikelola = transaksi;
    }
    //#endregion
}
