package lab_two.database.impl;

import lab_two.database.AnswerDao;
import lab_two.database.XmlDocumentStorage;
import lab_two.model.Answer;
import lab_two.model.Question;
import org.w3c.dom.*;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.List;

public class XmlAnswerDao extends XmlDocumentStorage<Answer> implements AnswerDao {

    private static final String PATH = "answers.xml";

    public XmlAnswerDao() {
        super(PATH, "answers");
    }

    @Override
    public List<Answer> selectAnswersByQuestion(Question question) {
        List<Answer> answers = new ArrayList<>();
        try {
            answers = selectNodeList("questId", String.valueOf(question.getId()), "answer");
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        for (Answer answer : answers) {
            answer.setQuestion(question);
        }
        return answers;
    }

    @Override
    public int insert(Answer answer) {
        return created ? insertElement(answer) : 0;
    }

    @Override
    public void update(Answer answer) {
        try {
            updateObject(String.valueOf(answer.getId()), "answer", answer);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Element serializeObject(Document dom, Answer obj, int id) {
        Element element = dom.createElement("answer");
        Element text = dom.createElement("text");
        Element correct = dom.createElement("correct");
        element.setAttribute(elementId, String.valueOf(id));
        element.setAttribute("questId", String.valueOf(obj.getQuestion().getId()));
        text.appendChild(dom.createTextNode(obj.getText()));
        correct.appendChild(dom.createTextNode(String.valueOf(obj.isTrueAnswer())));
        element.appendChild(text);
        element.appendChild(correct);
        obj.setId(id);
        return element;
    }

    @Override
    protected Answer deserializableElement(Node node) {
        NodeList list = node.getChildNodes();
        NamedNodeMap map = node.getAttributes();
        Answer answer = new Answer();
        answer.setId(Integer.parseInt(map.getNamedItem(elementId).getTextContent()));
        for (int i = 0, len = list.getLength(); i < len; i++) {
            switch (list.item(i).getNodeName()) {
                case "text":
                    answer.setText(list.item(i).getTextContent());
                    break;
                case "correct":
                    answer.setTrueAnswer(Boolean.parseBoolean(list.item(i).getTextContent()));
                    break;
            }
        }
        return answer;
    }
}
