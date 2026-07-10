package com.simsw.utill;

import com.simsw.model.Pegawai;

public class Session {
    private static Pegawai currentUser;

    public static void login(Pegawai user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static Pegawai getCurrentUser() {
        return currentUser;
    }

    public static boolean isLogin() {
        return currentUser != null;
    }
}