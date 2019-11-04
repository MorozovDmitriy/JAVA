package lab_two.database.impl;

import lab_two.database.AnswerDao;
import lab_two.database.OracleDatabase;
import lab_two.model.Answer;
import lab_two.model.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlAnswerDao implements AnswerDao {

    @Override
    public List<Answer> selectAnswersByQuestion(Question question) {
        try (Connection connection = OracleDatabase.getPool().getPooledConnection().getConnection()) {
            return OracleDatabase.selectData(connection, "Answers", question.getId(), "QuestionId",
                    (rowSet -> {
                        Answer answer = new Answer();
                        answer.setId(rowSet.getInt("AnswerId"));
                        answer.setText(rowSet.getString("AnswerText"));
                        answer.setQuestion(question);
                        answer.setTrueAnswer(rowSet.getBoolean("AnswerIsTrue"));
                        return answer;
                    }));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public int insert(Answer answer) {
        int result = 0;
        try (Connection connection = OracleDatabase.getPool().getPooledConnection().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO tests.answers(AnswerText, QuestionId, AnswerIsTrue) VALUES (?, ?, ?)")) {
                statement.setString(1, answer.getText());
                statement.setInt(2, answer.getQuestion().getId());
                statement.setBoolean(3, answer.isTrueAnswer());
                result = statement.executeUpdate();
            }
            answer.setId(OracleDatabase.selectLastInsertedId(connection));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void update(Answer answer) {
        try (Connection connection = OracleDatabase.getPool().getPooledConnection().getConnection()) {
            try (PreparedStatement statement =
                         connection.prepareStatement("UPDATE tests.answers SET AnswerText = ?, AnswerIsTrue = ? WHERE AnswerId = ?")) {
                statement.setString(1, answer.getText());
                statement.setBoolean(2, answer.isTrueAnswer());
                statement.setInt(3, answer.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
