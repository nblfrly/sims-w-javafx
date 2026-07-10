package com.simsw.dao.xstream;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.simsw.model.Riwayat;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;

public class RiwayatXStreamDAO {
    private static final String XML_PATH = "data/riwayat.xml";
    private final XStream xstream;
    public RiwayatXStreamDAO() {
        xstream = new XStream();
        xstream.addPermission(AnyTypePermission.ANY);
        xstream.alias("riwayat", Riwayat.class);
        xstream.alias("riwayatList", List.class);
        xstream.allowTypes(new Class[] { Riwayat.class });
    }

    @SuppressWarnings("unchecked")
    private List<Riwayat> loadRiwayat() {
        try {
            File file = new File(XML_PATH);
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            return (List<Riwayat>) xstream.fromXML(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<Riwayat> getAllRiwayat() {
        return loadRiwayat();
    }

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

    public void tambahRiwayat(Riwayat riwayat) {
        List<Riwayat> list = loadRiwayat();

        riwayat.setId(list.size() + 1);

        list.add(riwayat);
        saveRiwayat(list);
    }   
}
