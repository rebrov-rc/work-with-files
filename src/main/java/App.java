import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import entities.Employee;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class App {


    private List<Employee> parseCSV(String[] columnMapping, String fileName) {

        try (CSVReader reader = new CSVReader(new FileReader(fileName));) {

            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();

            strategy.setType(Employee.class);


            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> staff = csv.parse();
            return staff;

        } catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
        return null;


    }

    private <E> String listToJson(List<E> list) {

        Type listType = new TypeToken<List<E>>() {
        }.getType();
        var gson = new Gson();

        return gson.toJson(list, listType);
    }

    private void writeString(final String path, String str) {

        var file = new File(path);

        try (var writer = new FileWriter(file)) {
            writer.write(str);
        } catch (IOException ex) {
            ex.getStackTrace();
        }

    }

    private List<Employee> parseXML(final String path) {

        List<Employee> list = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(path));

            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {

                Node node2 = nodeList.item(i);

                List<Element> elements = new ArrayList<>();

                if (Node.ELEMENT_NODE == node2.getNodeType()) {

                    NodeList map = node2.getChildNodes();


                    for (int j = 0; j < map.getLength(); j++) {
                        Node nd = map.item(j);
                        if (Node.ELEMENT_NODE == nd.getNodeType()) {
                            Element el = (Element) nd;
                            elements.add(el);


                        }


                    }
                    list.add(new Employee(
                            Long.parseLong(elements.get(0).getTextContent()),
                            elements.get(1).getTextContent(),
                            elements.get(2).getTextContent(),
                            elements.get(3).getTextContent(),
                            Integer.parseInt(elements.get(4).getTextContent())
                    ));
                }

            }

        } catch (ParserConfigurationException e) {
            e.getStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return list;
    }


    public void run() {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String CSVFilePath = "src\\main\\files\\data.csv";
        String jsonFilePath = "src\\main\\files\\data.json";
        String jsonFilePath2 = "src\\main\\files\\data2.json";
        String xmlFile = "src\\main\\files\\data.xml";


        List<Employee> list = this.parseCSV(columnMapping, CSVFilePath);
        String json = this.listToJson(list);
        this.writeString(jsonFilePath, json);

        List<Employee> list2 = this.parseXML(xmlFile);
        String json2 = this.listToJson(list2);
        this.writeString(jsonFilePath2, json2);


    }
}
