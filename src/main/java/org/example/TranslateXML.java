package org.example;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

//Этот скрипт на Java использует библиотеку JAXP для парсинга XML-файла.
//Он загружает сохраненные переводы из JSON-файла в HashMap.
//Он итерируется по элементам <string> в XML-файле и обновляет их содержимое соответствующими переводами из HashMap.
//Если перевод не найден, он выводит сообщение об ошибке.
//Наконец, он сохраняет измененный XML-файл в новый файл.

public class TranslateXML {

    public static void main(String[] args) {
        try {
            // Загрузить XML-файл
            File xmlFile = new File("strings.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // Загрузить сохраненные переводы
            HashMap<String, String> savedTranslations = new HashMap<>();
            try (Scanner scanner = new Scanner(new File("ru.json"))) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(":");
                    savedTranslations.put(parts[0].replace("\"", ""), parts[1].replace("\"", ""));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Перевести и обновить строки
            NodeList nList = doc.getElementsByTagName("string");
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String originalText = eElement.getTextContent();
                    if (savedTranslations.containsKey(originalText)) {
                        String translatedText = savedTranslations.get(originalText);
                        eElement.setTextContent(translatedText);
                        System.out.println("Обновлено: " + originalText + " -> " + translatedText);
                    } else {
                        System.out.println("Не найдено: " + originalText);
                    }
                }
            }

            // Сохранить измененный XML-файл
            doc.getDocumentElement().normalize();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("strings_updated.xml"));
            transformer.transform(source, result);

            System.out.println("Файл успешно обновлен.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
