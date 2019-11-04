package lab_two.database.impl;

import lab_two.database.DaoFactory;
import lab_two.database.OracleDatabase;
import lab_two.database.QuestionDao;
import lab_two.database.TestDao;
import lab_two.model.Question;
import lab_two.model.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlTestDao implements TestDao {

    @Override
    public int insert(Test test) {
        int result = 0;
        try (Connection connection = OracleDatabase.getPool().getPooledConnection().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO tests.test(TestTitle) VALUES (?)")) {
                statement.setString(1, test.getTitle());
                result = statement.executeUpdate();
            }
            test.setTestId(OracleDatabase.selectLastInsertedId(connection));
            for (Question question : test.getQuestions()) {
                SqlDaoFactory.getDaoFactory(DaoFactory.SQL).getQuestionDao().insert(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Test> selectTests() {
        try (Connection connection = OracleDatabase.getPool().getPooledConnection().getConnection()) {
            return OracleDatabase.selectData(connection, "Test", row -> {
               Test test = new Test();
               test.setTestId(row.getInt("TestId"));
               test.setTitle(row.getString("TestTitle"));
               test.setQuestions(DaoFactory.getDaoFactory(DaoFactory.SQL).getQuestionDao().selectQuestionsByTest(test));
               return test;
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public void update(Test test) {
        QuestionDao dao = DaoFactory.getDaoFactory(DaoFactory.SQL).getQuestionDao();
        for (Question question : test.getQuestions()) {
            dao.update(question);
        }
        try (Connection connection = OracleDatabase.getPool().getPooledConnection().getConnection()) {
            try (PreparedStatement statement =
                         connection.prepareStatement("UPDATE tests.test SET TestTitle = ? WHERE TestId = ?")) {
                statement.setString(1, test.getTitle());
                statement.setInt(2, test.getTestId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
