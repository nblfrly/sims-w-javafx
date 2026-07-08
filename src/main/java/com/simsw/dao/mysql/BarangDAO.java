package com.simsw.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.simsw.database.DatabaseConnection;
import com.simsw.model.Barang;

public class BarangDAO {
    public List<Barang> getAllBarang() {
        List<Barang> list = new ArrayList<>();
        String sql = "SELECT * FROM barang";
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Barang barang = new Barang(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("kategori"),
                        rs.getInt("stok"),
                        rs.getInt("stok_minimum")
                );
                list.add(barang);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertBarang(Barang barang) {
        String sql = "INSERT INTO barang(nama, kategori, stok, stok_minimum) VALUES (?, ?, ?, ?)";
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, barang.getNamaBarang());
            ps.setString(2, barang.getKategori());
            ps.setInt(3, barang.getStok());
            ps.setInt(4, barang.getStokMinimum());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateBarang(Barang barang) {
        String sql = """
            UPDATE barang
            SET nama=?,
                kategori=?,
                stok=?,
                stok_minimum=?
            WHERE id=?
            """;

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, barang.getNamaBarang());
            ps.setString(2, barang.getKategori());
            ps.setInt(3, barang.getStok());
            ps.setInt(4, barang.getStokMinimum());
            ps.setInt(5, barang.getId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteBarang(int id) {
        String sql = "DELETE FROM barang WHERE id=?";

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
