package lab_two.database;

import lab_two.database.impl.SqlDaoFactory;
import lab_two.database.impl.XmlDaoFactory;

public abstract class DaoFactory {

    public static final int SQL = 0;
    public static final int XML = 1;

    public abstract AnswerDao getAnswerDao();
    public abstract QuestionDao getQuestionDao();
    public abstract TestDao getTestDao();

    public static DaoFactory getDaoFactory(int type) {
        if (type == SQL) {
            return new SqlDaoFactory();
        } else {
            return new XmlDaoFactory();
        }
    }
}
