package com.sistemmanajemenpenyewaanperlengkapanpendakian.dao;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Perlengkapan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PerlengkapanDAO {

    // PENAMBAHAN
    public void tambahDataPerlengkapan(Perlengkapan perlengkapan, int idVendor) throws SQLException {
        if (perlengkapan == null) {
            throw new IllegalArgumentException("Data perlengkapan tidak boleh null");
        }
        // Template query INSERT perlengkapan
    }

    // PENAMBAHAN
    public void updateDataPerlengkapan(Perlengkapan perlengkapan, int idVendor) throws SQLException {
        if (perlengkapan == null) {
            throw new IllegalArgumentException("Data perlengkapan tidak boleh null");
        }
        // Template query UPDATE perlengkapan
    }

    // PENAMBAHAN
    public void hapusDataPerlengkapan(Perlengkapan perlengkapan, int idVendor) throws SQLException {
        if (perlengkapan == null) {
            throw new IllegalArgumentException("Data perlengkapan tidak boleh null");
        }
        // Template query DELETE perlengkapan
    }
}
