package com.simsw.dao.mysql;

import com.simsw.database.DatabaseConnection;
import com.simsw.model.Pegawai;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PegawaiDAO {
    // GET ALL PEGAWAI
    public List<Pegawai> getAllPegawai() {
        List<Pegawai> list = new ArrayList<>();
        String sql = "SELECT * FROM pegawai";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Pegawai pegawai = new Pegawai(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
                list.add(pegawai);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;

    }

    // INSERT PEGAWAI
    public boolean insertPegawai(Pegawai pegawai) {
        String sql = """
                INSERT INTO pegawai
                (nama, username, password, role)
                VALUES (?, ?, ?, ?)
                """;
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, pegawai.getNama());
            ps.setString(2, pegawai.getUsername());
            ps.setString(3, pegawai.getPassword());
            ps.setString(4, pegawai.getRole());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // UPDATE PEGAWAI
    public boolean updatePegawai(Pegawai pegawai) {
        String sql = """
                UPDATE pegawai
                SET nama=?,
                    username=?,
                    password=?,
                    role=?
                WHERE id=?
                """;
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, pegawai.getNama());
            ps.setString(2, pegawai.getUsername());
            ps.setString(3, pegawai.getPassword());
            ps.setString(4, pegawai.getRole());
            ps.setInt(5, pegawai.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    // DELETE PEGAWAI
    public boolean deletePegawai(int id) {
        String sql = "DELETE FROM pegawai WHERE id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}