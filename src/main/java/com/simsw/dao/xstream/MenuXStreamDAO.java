package com.simsw.dao.xstream;

import com.simsw.model.Menu;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MenuXStreamDAO {
    // lokasi file XML
    private static final String XML_PATH = "data/menu.xml";
    private final XStream xstream;

    public MenuXStreamDAO() {
        xstream = new XStream();

        xstream.addPermission(AnyTypePermission.ANY);

        xstream.alias("menu", Menu.class);
        xstream.alias("menuList", List.class);
    }

    // SAVE XML

    private void saveMenu(List<Menu> list) {
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
    private List<Menu> loadMenu() {
        try {
            File file = new File(XML_PATH);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            if (file.length() == 0) {
                return new ArrayList<>();
            }
            return (List<Menu>) xstream.fromXML(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    // GET ALL
    public List<Menu> getAllMenu() {
        return loadMenu();
    }

    // INSERT
    public boolean insertMenu(Menu menu) {
        try {
            List<Menu> list = loadMenu();
            int maxId = 0;
            for (Menu m : list) {
                if (m.getId() > maxId) {
                    maxId = m.getId();
                }
            }
            menu.setId(maxId + 1);
            list.add(menu);
            saveMenu(list);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // UPDATE
    public boolean updateMenu(Menu menu) {
        try {
            List<Menu> list = loadMenu();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId() == menu.getId()) {
                    list.set(i, menu);
                    saveMenu(list);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // DELETE
    public boolean deleteMenu(int id) {
        try {
            List<Menu> list = loadMenu();
            boolean removed = list.removeIf(menu -> menu.getId() == id);
            if (removed) {
                saveMenu(list);
            }
            return removed;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // GET BY ID
    public Menu getMenuById(int id) {
        List<Menu> list = loadMenu();
        for (Menu menu : list) {
            if (menu.getId() == id) {
                return menu;
            }
        }
        return null;
    }
}