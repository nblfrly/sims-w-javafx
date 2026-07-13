package com.simsw;

import java.sql.Connection;
import com.simsw.database.DatabaseConnection;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Berhasil konek ke database!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
