package lab_two.database.impl;

import lab_two.database.AnswerDao;
import lab_two.database.DaoFactory;
import lab_two.database.QuestionDao;
import lab_two.database.TestDao;

public class XmlDaoFactory extends DaoFactory {
    @Override
    public AnswerDao getAnswerDao() {
        return new XmlAnswerDao();
    }

    @Override
    public QuestionDao getQuestionDao() {
        return new XmlQuestionDao();
    }

    @Override
    public TestDao getTestDao() {
        return new XmlTestDao();
    }
}
