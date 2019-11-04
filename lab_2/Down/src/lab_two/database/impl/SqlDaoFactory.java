package lab_two.database.impl;

import lab_two.database.AnswerDao;
import lab_two.database.DaoFactory;
import lab_two.database.QuestionDao;
import lab_two.database.TestDao;

public class SqlDaoFactory extends DaoFactory {
    @Override
    public AnswerDao getAnswerDao() {
        return new SqlAnswerDao();
    }

    @Override
    public TestDao getTestDao() {
        return new SqlTestDao();
    }

    @Override
    public QuestionDao getQuestionDao() {
        return new SqlQuestionDao();
    }
}
