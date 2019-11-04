package lab_two.database;

import lab_two.model.Question;
import lab_two.model.Test;

import java.util.List;

public interface QuestionDao {

    List<Question> selectQuestionsByTest(Test test);
    int insert(Question question);
    void update(Question question);
}
