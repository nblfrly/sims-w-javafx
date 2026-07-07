package com.simsw.dao.dom;

import com.simsw.model.Barang;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
// import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class BarangXMLDAO {
    private static final String XML_PATH = "data/barang.xml";
    // GET ALL BARANG
    public List<Barang> getAllBarang() {
        List<Barang> list = new ArrayList<>();
        try {
            File file = new File(XML_PATH);
            if (!file.exists()) {
                System.out.println("barang.xml tidak ditemukan!");
                return list;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("barang");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                Barang barang = new Barang(
                        Integer.parseInt(
                                element.getElementsByTagName("id")
                                        .item(0)
                                        .getTextContent()),

                        element.getElementsByTagName("namaBarang")
                                .item(0)
                                .getTextContent(),

                        element.getElementsByTagName("kategori")
                                .item(0)
                                .getTextContent(),

                        Integer.parseInt(
                                element.getElementsByTagName("stok")
                                        .item(0)
                                        .getTextContent()),

                        Integer.parseInt(
                                element.getElementsByTagName("stokMinimum")
                                        .item(0)
                                        .getTextContent())

                );
                list.add(barang);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // LOAD DOCUMENT XML
    private Document loadDocument() {
        try {
            File file = new File(XML_PATH);

            if (!file.exists()) {
                System.out.println("barang.xml tidak ditemukan!");
                return null;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            return document;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // SAVE DOCUMENT XML
    private void saveDocument(Document document) {
        try {
            File file = new File(XML_PATH);

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);

            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // GET NEXT ID
    private int getNextId() {
        int maxId = 0;

        Document document = loadDocument();
        if (document == null) {
            return 1;
        }

        NodeList nodeList = document.getElementsByTagName("barang");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            int id = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
            if (id > maxId) {
                maxId = id;
            }
        }
        return maxId + 1;
    }

    // INSERT BARANG
    public boolean insertBarang(Barang barang) {
        try {
            Document document = loadDocument();

            if (document == null) {
                return false;
            }

            Element root = document.getDocumentElement();
            Element barangElement = document.createElement("barang");

            // ID
            Element id = document.createElement("id");
            id.setTextContent(String.valueOf(getNextId()));
            barangElement.appendChild(id);

            // NAMA BARANG
            Element namaBarang = document.createElement("namaBarang");
            namaBarang.setTextContent(barang.getNamaBarang());
            barangElement.appendChild(namaBarang);

            // KATEGORI
            Element kategori = document.createElement("kategori");
            kategori.setTextContent(barang.getKategori());
            barangElement.appendChild(kategori);

            // STOK
            Element stok = document.createElement("stok");
            stok.setTextContent(String.valueOf(barang.getStok()));
            barangElement.appendChild(stok);

            // STOK MINIMUM
            Element stokMinimum = document.createElement("stokMinimum");
            stokMinimum.setTextContent(String.valueOf(barang.getStokMinimum()));
            barangElement.appendChild(stokMinimum);

            // tambahkan ke root
            root.appendChild(barangElement);

            // simpan XML
            saveDocument(document);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // UPDATE BARANG
    public boolean updateBarang(Barang barang) {
        try {

            Document document = loadDocument();
            if (document == null) {
                return false;
            }

            NodeList nodeList = document.getElementsByTagName("barang");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                int id = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());

                // jika ID ditemukan
                if (id == barang.getId()) {
                    // UPDATE NAMA BARANG
                    element.getElementsByTagName("namaBarang")
                            .item(0)
                            .setTextContent(barang.getNamaBarang());

                    // UPDATE KATEGORI
                    element.getElementsByTagName("kategori")
                            .item(0)
                            .setTextContent(barang.getKategori());

                    // UPDATE STOK
                    element.getElementsByTagName("stok")
                            .item(0)
                            .setTextContent(String.valueOf(barang.getStok()));

                    // UPDATE STOK MINIMUM
                    element.getElementsByTagName("stokMinimum")
                            .item(0)
                            .setTextContent(String.valueOf(barang.getStokMinimum()));

                    // simpan perubahan
                    saveDocument(document);

                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // DELETE BARANG
    public boolean deleteBarang(int id) {
        try {
            Document document = loadDocument();
            if (document == null) {
                return false;
            }

            NodeList nodeList = document.getElementsByTagName("barang");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                int currentId = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
                // jika ID ditemukan
                if (currentId == id) {
                    // hapus node barang
                    element.getParentNode().removeChild(element);
                    // simpan perubahan
                    saveDocument(document);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
