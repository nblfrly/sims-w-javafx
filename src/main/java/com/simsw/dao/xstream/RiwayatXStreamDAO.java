package com.simsw.dao.xstream;

import com.simsw.model.Riwayat;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class RiwayatXStreamDAO {
    // Lokasi XML
    private static final String XML_PATH = "data/riwayat.xml";

    // Object XStream
    private final XStream xstream;

    public RiwayatXStreamDAO() {
        xstream = new XStream();
        xstream.addPermission(AnyTypePermission.ANY);
        xstream.alias("riwayat", Riwayat.class);
        xstream.alias("riwayatList", List.class);

    }

    // SAVE XML
    private void saveRiwayat(List<Riwayat> list) {
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

    // LOAD XML
    @SuppressWarnings("unchecked")
    private List<Riwayat> loadRiwayat() {
        try {
            File file = new File(XML_PATH);
            if (!file.exists()) {
                return new ArrayList<>();
            }
             // tambahkan ini
            if (file.length() == 0) {
                return new ArrayList<>();
            }
            return (List<Riwayat>) xstream.fromXML(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    // GET ALL
    public List<Riwayat> getAllRiwayat() {
        return loadRiwayat();
    }

    // INSERT
    public boolean insertRiwayat(Riwayat riwayat) {
        try {
            List<Riwayat> list = loadRiwayat();
            int maxId = 0;
            for (Riwayat r : list) {
                if (r.getId() > maxId) {
                    maxId = r.getId();
                }
            }

            riwayat.setId(maxId + 1);
            list.add(0, riwayat);
            saveRiwayat(list);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}