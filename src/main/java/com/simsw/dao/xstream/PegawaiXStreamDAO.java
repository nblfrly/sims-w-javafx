package com.simsw.dao.xstream;

import com.simsw.model.Pegawai;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PegawaiXStreamDAO {
    private static final String XML_PATH = "data/pegawai.xml";
    private final XStream xstream;
    public PegawaiXStreamDAO() {
        xstream = new XStream();
        xstream.addPermission(AnyTypePermission.ANY);
        xstream.alias("pegawai", Pegawai.class);
        xstream.alias("pegawaiList", List.class);
        xstream.allowTypes(new Class[] { Pegawai.class });
        xstream.addPermission(AnyTypePermission.ANY);
    }

    // SAVE
    private void savePegawai(List<Pegawai> list) {
        try {
            File file = new File(XML_PATH);
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                xstream.toXML(list, fos);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // LOAD
    @SuppressWarnings("unchecked")
    private List<Pegawai> loadPegawai() {
        try {
            File file = new File(XML_PATH);
            if (!file.exists()) {
                return new ArrayList<>();
            }

            return (List<Pegawai>) xstream.fromXML(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    // GET ALL
    public List<Pegawai> getAllPegawai() {
        return loadPegawai();
    }

    // INSERT
    public boolean insertPegawai(Pegawai pegawai) {
        try {
            List<Pegawai> list = loadPegawai();
            int maxId = 0;
            for (Pegawai p : list) {
                if (p.getId() > maxId) {
                    maxId = p.getId();
                }
            }

            pegawai.setId(maxId + 1);
            list.add(pegawai);
            savePegawai(list);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // UPDATE
    public boolean updatePegawai(Pegawai pegawai) {
        try {
            List<Pegawai> list = loadPegawai();
            boolean ditemukan = false;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId() == pegawai.getId()) {

                    list.set(i, pegawai);
                    ditemukan = true;
                    break;
                }
            }

            if (ditemukan) {
                savePegawai(list);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
//woiwoisadasd
    // DELETE coba sssslagi ya
    public boolean deletePegawai(int id) {
        try {
            List<Pegawai> list = loadPegawai();
            boolean berhasil = list.removeIf(p -> p.getId() == id);
            if (berhasil) {
                savePegawai(list);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // LOGIN
    public Pegawai login(String username, String password) {
        List<Pegawai> list = loadPegawai();
        for (Pegawai p : list) {
            if (p.getUsername().equals(username)
                    && p.getPassword().equals(password)) {
                return p;
            }
        }
        return null;
    }

    // GET BY ID
    public Pegawai getPegawaiById(int id) {
        List<Pegawai> list = loadPegawai();
        for (Pegawai p : list) {

            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }
}


