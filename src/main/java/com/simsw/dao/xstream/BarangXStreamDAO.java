package com.simsw.dao.xstream;

import com.simsw.model.Barang;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class BarangXStreamDAO {
    // lokasi XML
    private static final String XML_PATH = "data/barang.xml";

    // object utama XStream
    private final XStream xstream;

    // constructor
    public BarangXStreamDAO() {
        // mesin
        xstream = new XStream();

        // izinkan semua class dibaca XStream
        // (untuk pembelajaran/projek lokal)
        xstream.addPermission(AnyTypePermission.ANY);

        // izinkan class barang
        xstream.allowTypes(new Class[]{Barang.class});

        // alias agar XML lebih rapi
        xstream.alias("barang", Barang.class);

        // alias root list
        xstream.alias("barangList", List.class);
    }

    // SAVE XML
    private void saveBarang(List<Barang> list) {
        try {
            File file = new File(XML_PATH);

            // buat folder data jika belum ada
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            // simpan seluruh List ke XML (ini intinya)
            try (FileOutputStream fos = new FileOutputStream(file)) {
                xstream.toXML(list, fos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // LOAD XML
    @SuppressWarnings("unchecked")
    private List<Barang> loadBarang() {
        try {
            File file = new File(XML_PATH);

            // jika file belum ada
            if (!file.exists()) {
                return new ArrayList<>();
            }

            // baca XML menjadi List<Barang> (ini penting karena kebalikan dari save)
            return (List<Barang>) xstream.fromXML(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    // GET ALL BARANG (isi ini pendek karena sudah tercover dengan loadBarang)
    public List<Barang> getAllBarang() {
        return loadBarang();
    }

    // INSERT BARANG
    public boolean insertBarang(Barang barang) {
        try {
            List<Barang> list = loadBarang();

            // cek ID terbesar
            int maxId = 0;
            for (Barang b : list) {
                if (b.getId() > maxId) {
                    maxId = b.getId();
                }
            }

            // auto Increment ID
            barang.setId(maxId + 1);

            // tambahkan ke List
            list.add(barang);

            // simpan kembali ke XML
            saveBarang(list);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // UPDATE BARANG 
    public boolean updateBarang(Barang barang) {
        try {
            List<Barang> list = loadBarang();
            boolean ditemukan = false;
            for (int i = 0; i < list.size(); i++) {
                Barang b = list.get(i);
                if (b.getId() == barang.getId()) {
                    list.set(i, barang);
                    ditemukan = true;
                    break;
                }
            }
            if (ditemukan) {
                saveBarang(list);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE BARANG

    public boolean deleteBarang(int id) {
        try {
            List<Barang> list = loadBarang();
            boolean berhasil = list.removeIf(barang -> barang.getId() == id);
            if (berhasil) {
                saveBarang(list);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
