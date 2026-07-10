package com.simsw.utill;

import com.simsw.model.Pegawai;

public class Session {

    private static Pegawai currentPegawai;

    public static void setCurrentPegawai(Pegawai pegawai) {
        currentPegawai = pegawai;
    }

    public static Pegawai getCurrentPegawai() {
        return currentPegawai;
    }
}