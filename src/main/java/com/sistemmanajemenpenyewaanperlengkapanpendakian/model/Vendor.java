package com.sistemmanajemenpenyewaanperlengkapanpendakian.model;

import java.sql.SQLException;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.PerlengkapanDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.TransaksiDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.VendorDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.DataTidakLengkapException;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.LoginGagalException;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService;


public class Vendor {
    private String usernameAdm;  
    private String pwAdm;
    private String email;
    private String namaVendor;
    private String emailAtauUsername;
    private int idVendor;
    private String lokasiToko;
    private final VendorDAO vendorDAO;
    private final PerlengkapanDAO perlengkapanDAO;
    private final TransaksiDAO transaksiDAO;
    private Perlengkapan perlengkapanYangDikelola;
    private Transaksi transaksiYangDikelola;
    private String fotoProfile;
    
    public String getEmailAtauUsername(){
        return emailAtauUsername;
    }

    public void setEmailAtauUsername(String emailAtauUsername){
        this.emailAtauUsername = emailAtauUsername;
    } 


    public String getLokasiToko(){
        return lokasiToko;
    }

    public void setLokasiToko(String lokasiToko){
        this.lokasiToko = lokasiToko;
    }

    

    public String getFotoProfile(){
        return fotoProfile;
    }

    public void setFotoProfile(String fotoProfile){
        this.fotoProfile = fotoProfile;
    }
    public Vendor(){
        this.vendorDAO = new VendorDAO();
        this.perlengkapanDAO = new PerlengkapanDAO();
        this.transaksiDAO = new TransaksiDAO();
    }
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
            this.lokasiToko = vendorDitemukan.getLokasiToko();
            this.fotoProfile = vendorDitemukan.getFotoProfile();
            return true;
        } catch (SQLException e){
            throw new IllegalStateException("Gagal melakukan login: " + e.getMessage(), e);
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

        try{
            TransaksiService.tambah(transaksiYangDikelola, idVendor);;
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

    public int getIdVendor(){
        return idVendor;
    }

    public void setIdVendor(int idVendor){
        this.idVendor = idVendor;
    }
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
    public void setPerlengkapanYangDikelola(Perlengkapan perlengkapan){
        this.perlengkapanYangDikelola = perlengkapan;
    }

    public void setTransaksiYangDikelola(Transaksi transaksi){
        this.transaksiYangDikelola = transaksi;
    }
}
