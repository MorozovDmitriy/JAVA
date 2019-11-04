package lab_two.database;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public abstract class XmlDocumentStorage<T> {

    private static final String xmlId = "identity";
    protected static final String elementId = "id";

    protected boolean created;
    private File xmlFile;

    private DocumentBuilder documentBuilder;
    private Document dom;


    protected XmlDocumentStorage(String path, String documentRoot) {
        createXmlParser(path, documentRoot);
    }

    private void createXmlParser(String fileName, String root) {
        try {
            xmlFile = new File(fileName);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            documentBuilder = factory.newDocumentBuilder();
            if (!xmlFile.exists()) {
                if (xmlFile.createNewFile()) {
                    createStartDom(root);
                } else {
                    throw new FileNotFoundException("Не удалось найти/создать xml файл");
                }
            }
            dom = documentBuilder.parse(xmlFile);
            created = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createStartDom(String root) {
        Document doc = documentBuilder.newDocument();
        Element e = doc.createElement(root);
        e.setAttribute(xmlId, "1");
        doc.appendChild(e);
        writeToXmlFile(doc, true);
    }

    private void writeToXmlFile(Document doc, boolean isNew) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            if (isNew) {
                transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            }
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(xmlFile));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    protected List<T> selectObjects(String nodeName) {
        ArrayList<T> data = new ArrayList<>();
        Element root = dom.getDocumentElement();
        NodeList list = root.getChildNodes();

        for (int i = 0, len = list.getLength(); i < len; i++) {
            if (list.item(i).getNodeName().equals(nodeName))
                data.add(deserializableElement(list.item(i)));
        }

        return data;
    }

    private Node selectNodeByAttribute(String attr, String value, String nodeName) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        Element root = dom.getDocumentElement();
        return (Node) xPath.evaluate(String.format("/%s/%s[@%s=%s]", root.getNodeName(), nodeName, attr, value),
                dom, XPathConstants.NODE);
    }

    protected List<T> selectNodeList(String attr, String value, String nodeName) throws XPathExpressionException {
        ArrayList<T> data = new ArrayList<>();
        XPath xPath = XPathFactory.newInstance().newXPath();
        Element root = dom.getDocumentElement();
        NodeList list = (NodeList) xPath.evaluate(String.format("/%s/%s[@%s=%s]", root.getNodeName(), nodeName, attr, value),
                dom, XPathConstants.NODESET);
        for (int i = 0, len = list.getLength(); i < len; i++) {
            if (list.item(i).getNodeName().equals(nodeName))
                data.add(deserializableElement(list.item(i)));
        }
        return data;
    }

    @SafeVarargs
    final protected int insertElement(T... data) {
        Element root = dom.getDocumentElement();
        int id = Integer.parseInt(root.getAttribute(xmlId));
        Element element;
        int result = 0;

        for (T t : data) {
            element = serializeObject(dom, t, id);
            if (element != null) {
                id += 1;
                root.appendChild(element);
                result++;
            }
        }
        root.setAttribute(xmlId, String.valueOf(id));
        writeToXmlFile(dom, false);
        return result;
    }

    protected void updateObject(String id, String nodeName, T object) throws XPathExpressionException {
        Node oldNode = selectNodeByAttribute(elementId, id, nodeName);
        Node newNode = serializeObject(dom, object, Integer.parseInt(id));
        dom.getDocumentElement().replaceChild(newNode, oldNode);
        writeToXmlFile(dom, false);
    }

    protected abstract Element serializeObject(Document dom, T obj, int id);
    protected abstract T deserializableElement(Node node);
}
//Класс для работы с xml
