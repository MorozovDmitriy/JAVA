package lab_two.database.impl;

import lab_two.database.DaoFactory;
import lab_two.database.QuestionDao;
import lab_two.database.TestDao;
import lab_two.database.XmlDocumentStorage;
import lab_two.model.Question;
import lab_two.model.Test;
import org.w3c.dom.*;

import javax.xml.xpath.XPathExpressionException;
import java.util.List;

public class XmlTestDao extends XmlDocumentStorage<Test> implements TestDao {

    private static final String PATH = "tests.xml";

    public XmlTestDao() {
        super(PATH, "tests");
    }

    @Override
    public int insert(Test test) {
        int result = created ? insertElement(test) : 0;
        for (Question question : test.getQuestions()) {
            DaoFactory.getDaoFactory(DaoFactory.XML).getQuestionDao().insert(question);
        }
        return result;
    }

    @Override
    public List<Test> selectTests() {
        return selectObjects("test");
    }

    @Override
    public void update(Test test) {
        QuestionDao dao = DaoFactory.getDaoFactory(DaoFactory.XML).getQuestionDao();
        for (Question question : test.getQuestions()) {
            dao.update(question);
        }
        try {
            updateObject(String.valueOf(test.getTestId()), "test", test);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Element serializeObject(Document dom, Test obj, int id) {
        Element element = dom.createElement("test");
        Element title = dom.createElement("title");
        element.setAttribute(elementId, String.valueOf(id));
        title.appendChild(dom.createTextNode(obj.getTitle()));
        element.appendChild(title);
        obj.setTestId(id);
        return element;
    }

    @Override
    protected Test deserializableElement(Node node) {
        NodeList list = node.getChildNodes();
        NamedNodeMap map = node.getAttributes();
        Test test = new Test();
        test.setTestId(Integer.parseInt(map.getNamedItem(elementId).getTextContent()));
        if (list.item(1).getNodeName().equals("title")) {
            test.setTitle(list.item(1).getTextContent());
        }
        test.setQuestions(DaoFactory.getDaoFactory(DaoFactory.XML).getQuestionDao().selectQuestionsByTest(test));
        return test;
    }
}
