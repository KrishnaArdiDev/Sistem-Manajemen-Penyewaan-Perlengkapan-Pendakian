package com.sistemmanajemenpenyewaanperlengkapanpendakian.dao;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Vendor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VendorDAO {
    // Penambahan
    public Vendor login(String emailAtauusername, String password) throws SQLException{
        String sql = "SELECT id_vendor, username_adm, email, nama_vendor " + "FROM vendor " + "WHERE (username_adm = ? OR email = ?) " + "AND pw_adm = ? " + "LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, emailAtauusername);
                statement.setString(2, emailAtauusername);
                statement.setString(3, password);

                try(ResultSet result = statement.executeQuery()){
                    if (result.next()){
                        Vendor vendor = new Vendor();

                        vendor.setIdVendor(result.getInt("id_vendor"));
                        vendor.setUsernameAdm(result.getString("username_adm"));
                        vendor.setEmail(result.getString("email"));
                        vendor.setNamaVendor(result.getString("nama_vendor"));

                        return vendor;
                    }
                    return null;
                }
            }
    }
}
