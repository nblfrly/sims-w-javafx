package com.simsw.dao.xstream;

import com.simsw.model.Resep;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ResepXStreamDAO {

    private static final String XML_PATH = "data/resep.xml";
    private final XStream xstream;

    public ResepXStreamDAO() {
        xstream = new XStream();
        xstream.addPermission(AnyTypePermission.ANY);
        xstream.alias("resep", Resep.class);
        xstream.alias("resepList", List.class);
    }

    // SAVE 
    private void saveResep(List<Resep> list) {
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
    private List<Resep> loadResep() {
        try {
            File file = new File(XML_PATH);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            if (file.length() == 0) {
                return new ArrayList<>();
            }
            return (List<Resep>) xstream.fromXML(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    // GET ALL 
    public List<Resep> getAllResep() {
        return loadResep();
    }

    // INSERT 
    public boolean insertResep(Resep resep) {
        try {
            List<Resep> list = loadResep();
            int maxId = 0;
            for (Resep r : list) {
                if (r.getId() > maxId) {
                    maxId = r.getId();
                }
            }
            resep.setId(maxId + 1);
            list.add(resep);
            saveResep(list);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // UPDATE 
    public boolean updateResep(Resep resep) {
        try {
            List<Resep> list = loadResep();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId() == resep.getId()) {
                    list.set(i, resep);
                    saveResep(list);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // DELETE 
    public boolean deleteResep(int id) {
        try {
            List<Resep> list = loadResep();
            boolean removed = list.removeIf(r -> r.getId() == id);
            if (removed) {
                saveResep(list);
            }
            return removed;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //  GET BY MENU 
    public List<Resep> getResepByMenu(int idMenu) {
        List<Resep> hasil = new ArrayList<>();
        List<Resep> list = loadResep();
        for (Resep r : list) {
            if (r.getIdMenu() == idMenu) {
                hasil.add(r);
            }
        }
        return hasil;
    }

    public Resep getResepById(int id){
        for(Resep r : loadResep()){
            if(r.getId()==id){
                return r;
            }
        }
        return null;
    }

    public List<Resep> getResepByBarang(int idBarang){
        List<Resep> hasil=new ArrayList<>();
        for(Resep r:loadResep()){
            if(r.getIdBarang()==idBarang){
                hasil.add(r);
            }
        }
        return hasil;
    }

    public boolean exists(int idMenu,int idBarang){
        for(Resep r:loadResep()){
            if(r.getIdMenu()==idMenu &&
            r.getIdBarang()==idBarang){
                return true;
            }
        }
        return false;
    }

        public void deleteByMenu(int idMenu){
        List<Resep> list=loadResep();
        list.removeIf(r->r.getIdMenu()==idMenu);
        saveResep(list);
    }

    public void deleteByBarang(int idBarang){
        List<Resep> list=loadResep();
        list.removeIf(r->r.getIdBarang()==idBarang);
        saveResep(list);
    }
}