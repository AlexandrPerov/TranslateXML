package org.example;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
//Этот скрипт на Java использует библиотеку JAXP для парсинга XML-файлов.
//Он загружает сохраненные переводы из JSON-файла в HashMap.
//Он сравнивает количество строк в двух XML-файлах.
// Если количество строк не совпадает, он выводит сообщение об ошибке.
//Он итерируется по элементам <string> в обоих XML-файлах и сравнивает их содержимое.
// Если содержимое не совпадает, он выводит сообщение об ошибке.
//Он также проверяет, есть ли сохраненный перевод для каждой строки, и сравнивает его с
// переводом в XML-файле. Если сохраненный перевод не совпадает, он выводит сообщение об ошибке.
//Этот скрипт помогает выявить несоответствия между XML-файлами и сохраненными переводами, что может быть полезно для обеспечения согласованности и точности переводов.

public class CompareXML {

    public static void main(String[] args) {
        try {
            // Загрузить XML-файлы
            File xmlFile1 = new File("strings.xml");
            DocumentBuilderFactory dbFactory1 = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder1 = dbFactory1.newDocumentBuilder();
            Document doc1 = dBuilder1.parse(xmlFile1);
            doc1.getDocumentElement().normalize();

            File xmlFile2 = new File("stringss.xml");
            DocumentBuilderFactory dbFactory2 = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder2 = dbFactory2.newDocumentBuilder();
            Document doc2 = dBuilder2.parse(xmlFile2);
            doc2.getDocumentElement().normalize();

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

            // Сравнить XML-файлы и обновить переводы
            NodeList nList1 = doc1.getElementsByTagName("string");
            NodeList nList2 = doc2.getElementsByTagName("string");
            if (nList1.getLength() != nList2.getLength()) {
                System.out.println("Файлы имеют разное количество строк.");
            } else {
                for (int i = 0; i < nList1.getLength(); i++) {
                    Node nNode1 = nList1.item(i);
                    Node nNode2 = nList2.item(i);
                    if (nNode1.getNodeType() == Node.ELEMENT_NODE && nNode2.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement1 = (Element) nNode1;
                        Element eElement2 = (Element) nNode2;
                        String originalText = eElement1.getTextContent();
                        String translatedText = eElement2.getTextContent();
                        if (!originalText.equals(translatedText)) {
                            System.out.println("Несоответствие перевода: " + originalText + " -> " + translatedText);
                        }
                        if (savedTranslations.containsKey(originalText)) {
                            String savedTranslation = savedTranslations.get(originalText);
                            if (!savedTranslation.equals(translatedText)) {
                                System.out.println("Несоответствие сохраненного перевода: " + originalText + " -> " + savedTranslation + " (в файле: " + translatedText + ")");
                            }
                        } else {
                            System.out.println("Отсутствует сохраненный перевод: " + originalText);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
