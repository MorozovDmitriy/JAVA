package lab_two.database;

import lab_two.model.Answer;
import lab_two.model.Question;

import java.util.List;

public interface AnswerDao {

    List<Answer> selectAnswersByQuestion(Question question);
    int insert(Answer answer);
    void update(Answer answer);
    //Интерфейс который описывает методы доступа к данным из хранилища
}
