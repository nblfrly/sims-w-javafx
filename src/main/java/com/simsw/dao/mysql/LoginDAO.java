package com.simsw.dao.mysql;

import com.simsw.database.DatabaseConnection;
import com.simsw.model.Pegawai;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginDAO {
    public Pegawai login(String username, String password) {
        String sql = """
                SELECT *
                FROM pegawai
                WHERE username = ?
                AND password = ?
                """;

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Pegawai(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

