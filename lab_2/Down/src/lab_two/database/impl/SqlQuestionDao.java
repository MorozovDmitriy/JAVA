package lab_two.database.impl;

import lab_two.database.AnswerDao;
import lab_two.database.DaoFactory;
import lab_two.database.OracleDatabase;
import lab_two.database.QuestionDao;
import lab_two.model.Answer;
import lab_two.model.Question;
import lab_two.model.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlQuestionDao implements QuestionDao {

    @Override
    public List<Question> selectQuestionsByTest(Test test) {
        try (Connection connection = OracleDatabase.getPool().getPooledConnection().getConnection()) {
            return OracleDatabase.selectData(connection, "Questions", test.getTestId(), "TestId",
                    (rowSet -> {
                        Question question = new Question();
                        question.setId(rowSet.getInt("QuestionId"));
                        question.setText(rowSet.getString("QuestionText"));
                        question.setWeight(rowSet.getFloat("QuestionWeight"));
                        question.setTest(test);
                        question.setAnswers(DaoFactory.getDaoFactory(DaoFactory.SQL).getAnswerDao().selectAnswersByQuestion(question));
                        return question;
                    }));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public int insert(Question question) {
        int result = 0;
        try (Connection connection = OracleDatabase.getPool().getPooledConnection().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO tests.questions(QuestionText, QuestionWeight, TestId) VALUES (?, ?, ?)")) {
                statement.setString(1, question.getText());
                statement.setFloat(2, question.getWeight());
                statement.setInt(3, question.getTest().getTestId());
                result = statement.executeUpdate();
            }
            question.setId(OracleDatabase.selectLastInsertedId(connection));
            for (Answer answer : question.getAnswers()) {
                SqlDaoFactory.getDaoFactory(DaoFactory.SQL).getAnswerDao().insert(answer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void update(Question question) {
        AnswerDao dao = DaoFactory.getDaoFactory(DaoFactory.SQL).getAnswerDao();
        for (Answer answer : question.getAnswers()) {
            dao.update(answer);
        }
        try (Connection connection = OracleDatabase.getPool().getPooledConnection().getConnection()) {
            try (PreparedStatement statement = connection
                    .prepareStatement("UPDATE tests.questions SET QuestionText = ?, QuestionWeight = ? WHERE QuestionId = ?")) {
                statement.setString(1, question.getText());
                statement.setFloat(2, question.getWeight());
                statement.setInt(3, question.getId());
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
