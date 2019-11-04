package lab_two.database.impl;

import lab_two.database.AnswerDao;
import lab_two.database.DaoFactory;
import lab_two.database.QuestionDao;
import lab_two.database.XmlDocumentStorage;
import lab_two.model.Answer;
import lab_two.model.Question;
import lab_two.model.Test;
import org.w3c.dom.*;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.List;

public class XmlQuestionDao extends XmlDocumentStorage<Question> implements QuestionDao {

    private static final String PATH = "questions.xml";

    public XmlQuestionDao() {
        super(PATH, "questions");
    }

    @Override
    public List<Question> selectQuestionsByTest(Test test) {
        List<Question> questions = new ArrayList<>();
        try {
            questions = selectNodeList("testId", String.valueOf(test.getTestId()), "question");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        for (Question question : questions) {
            question.setTest(test);
        }
        return questions;
    }

    @Override
    public int insert(Question question) {
        int result = created ? insertElement(question) : 0;
        for (Answer answer : question.getAnswers()) {
            DaoFactory.getDaoFactory(DaoFactory.XML).getAnswerDao().insert(answer);
        }
        return result;
    }

    @Override
    public void update(Question question) {
        AnswerDao dao = DaoFactory.getDaoFactory(DaoFactory.XML).getAnswerDao();
        for (Answer answer : question.getAnswers()) {
            dao.update(answer);
        }
        try {
            updateObject(String.valueOf(question.getId()), "question", question);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Element serializeObject(Document dom, Question obj, int id) {
        Element element = dom.createElement("question");
        Element text = dom.createElement("text");
        Element weight = dom.createElement("weight");
        element.setAttribute(elementId, String.valueOf(id));
        element.setAttribute("testId", String.valueOf(obj.getTest().getTestId()));
        text.appendChild(dom.createTextNode(obj.getText()));
        weight.appendChild(dom.createTextNode(String.valueOf(obj.getWeight())));
        element.appendChild(text);
        element.appendChild(weight);
        obj.setId(id);
        return element;
    }

    @Override
    protected Question deserializableElement(Node node) {
        NodeList list = node.getChildNodes();
        NamedNodeMap map = node.getAttributes();
        Question question = new Question();
        question.setId(Integer.parseInt(map.getNamedItem(elementId).getTextContent()));
        for (int i = 0, len = list.getLength(); i < len; i++) {
            switch (list.item(i).getNodeName()) {
                case "text":
                    question.setText(list.item(i).getTextContent());
                    break;
                case "weight":
                    question.setWeight(Float.parseFloat(list.item(i).getTextContent()));
                    break;
            }
        }
        question.setAnswers(DaoFactory.getDaoFactory(DaoFactory.XML).getAnswerDao().selectAnswersByQuestion(question));
        return question;
    }
}
