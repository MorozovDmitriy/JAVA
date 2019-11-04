package lab_two.database;

import lab_two.model.Test;

import java.util.List;

public interface TestDao {
    int insert(Test test);
    List<Test> selectTests();
    void update(Test test);
}
