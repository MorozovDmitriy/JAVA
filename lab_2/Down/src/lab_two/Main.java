package lab_two;

import lab_two.database.DaoFactory;
import lab_two.model.*;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static Scanner console = new Scanner(System.in);
    private static DaoFactory dao;

    public static void main(String[] args) {
        System.out.println("Хранилище данных: ");
        System.out.println("1. Database");
        System.out.println("2. XML");
        if (new Main().readPositiveInt(console) == 1) {
            dao = DaoFactory.getDaoFactory(DaoFactory.SQL);
        } else {
            dao = DaoFactory.getDaoFactory(DaoFactory.XML);
        }
        new Main().startMenu();
    }

    private void startMenu() {
        System.out.println("1. Я тьютор");
        System.out.println("2. Я студент");
        int choice = readPositiveInt(console);
        if (choice == 1) tutorMenu();
        else studentMenu();
    }

    private void studentMenu() {
        int choice = -1;
        while (choice != 0) {
            try {
                System.out.println("1. Пройти тест");
                System.out.println("0. Выход");
                choice = readPositiveInt(console);
                if (choice == 1) {
                    selectTest();
                }
            } catch (Exception e) {
                System.err.println("Handler say: " + e.getMessage());
            }
        }
    }

    private void selectTest() {
        List<Test> tests = dao.getTestDao().selectTests();
        System.out.println("Выберите номер теста: (цифру введите) ");
        listNicePrint(tests);
        int choice = readPositiveInt(console) - 1;
        if (choice < tests.size() && choice >= 0) {
            startTest(tests.get(choice));
        }
    }

    private void startTest(Test test) {
        float result = 0;
        float maxResult = 0;
        System.out.println("Название теста - " + test.getTitle());
        int i = 1;
        for (Question question : test.getQuestions()) {
            maxResult += question.getWeight();
            System.out.println("Вопрос №" + i + ". " + question.getText());
            System.out.println("Варианты ответа(если их несколько, укажите через пробел): ");
            String[] arr;
            int r = 0;
            do {
                listNicePrint(question.getAnswers());
                System.out.print("Ответ(ы): ");
                String ans = console.nextLine().trim();
                arr = ans.split(" ");
            } while ((r = calculateAnswerMark(question, arr)) == -1);
            result += r;
            i++;
        }
        System.out.println("Поздравляем. Ваш результат: " + result + " из " + maxResult);
        System.out.println("Нажмите enter, чтобы выйти");
        console.nextLine();
    }

    private int calculateAnswerMark(Question question, String[] arr) {
        int trueCount = countOfCorrectAnswers(question.getAnswers());
        float oneTrueAnswer = question.getWeight() / trueCount;
        float oneFalseAnswer = question.getWeight() / (question.getAnswers().size() - trueCount);
        try {
            float result = 0;
            for (String s : arr) {
                if (question.getAnswers().get(Integer.parseInt(s) - 1).isTrueAnswer()) {
                    result += oneTrueAnswer;
                } else {
                    result -= oneFalseAnswer;
                }
            }
            if (result > 0) {
                return Math.round(result);
            } else {
                return 0;
            }
        } catch (NumberFormatException e) {
            System.out.println("Неккоректный ввод ответа.");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Неккоректный номер ответа");
        }

        return -1;
    }

    private int countOfCorrectAnswers(List<Answer> answers)
    {
        int result = 0;

        for (Answer answer : answers) {
            if (answer.isTrueAnswer()) result++;
        }

        return result;
    }

    private void tutorMenu() {
        int choice = -1;
        while (choice != 0) {
            try {
                System.out.println("1. Создать тест");
                System.out.println("2. Изменить тест");
                System.out.println("0. Выход");
                choice = readPositiveInt(console);
                if (choice == 1) {
                    createTest();
                } else if (choice == 2) {
                    changeTest();
                }
            } catch (Exception e) {
                System.out.println("Handler say: " + e.getMessage());
            }
        }
    }

    private void changeTest() {
        List<Test> tests = dao.getTestDao().selectTests();
        System.out.println("Тест для изменения: ");
        listNicePrint(tests);
        int i = readPositiveInt(console) - 1;
        if (i >= 0 && i < tests.size()) {
            Test forChange = tests.get(i);
            System.out.println("Что вы желаете изменить?");
            System.out.println("1. Название теста");
            System.out.println("2. Вопросы к тесту");
            int choice = readPositiveInt(console);
            if (choice == 1) {
                System.out.println("Введите новое название теста: ");
                forChange.setTitle(console.nextLine());
                dao.getTestDao().update(forChange);
                System.out.println("Изменение успешно.");
            } else if (choice == 2) {
                changeQuestions(forChange.getQuestions());
                dao.getTestDao().update(forChange);
                System.out.println("Изменение успешно.");
            }
        }
    }

    private void changeQuestions(List<Question> questions) {
        System.out.println("Выберите вопрос, который хотите изменить: ");
        listNicePrint(questions);
        int i = readPositiveInt(console) - 1;
        if (i >= 0 && i < questions.size()) {
            Question forChange = questions.get(i);
            System.out.println("Что вы хотите изменить?");
            System.out.println("1. Название вопроса.");
            System.out.println("2. Балл вопроса.");
            System.out.println("3. Ответы.");
            int choice = readPositiveInt(console);
            if (choice == 1) {
                System.out.println("Введите новое название: ");
                forChange.setText(console.nextLine());
            } else if (choice == 2) {
                System.out.println("Введите новый балл: ");
                forChange.setWeight(console.nextFloat());
                console.nextLine();
            } else if (choice == 3) {
                changeAnswers(forChange.getAnswers());
            }
        }
    }

    private void changeAnswers(List<Answer> answers) {
        System.out.println("Выберите ответ, который хотите изменить: ");
        listNicePrint(answers);
        int i = readPositiveInt(console) - 1;
        if (i >= 0 && i < answers.size()) {
            Answer forChange = answers.get(i);
            System.out.println("1. Изменить название ответа.");
            if (forChange.isTrueAnswer()) {
                System.out.println("2. Сделать неправильным ответом.");
            } else {
                System.out.println("2. Сделать правильным ответом.");
            }
            int choice = readPositiveInt(console);
            if (choice == 1) {
                System.out.println("Введите новое название ответа: ");
                forChange.setText(console.nextLine());
            } else if (choice == 2) {
                forChange.setTrueAnswer(!forChange.isTrueAnswer());
            }
        }
    }

    private void createTest() {
        Test test = new Test();
        System.out.print("Введите название теста: ");
        test.setTitle(console.nextLine());
        System.out.print("Введите кол-во вопросов: ");
        int count = readPositiveInt(console);
        List<Question> questions = new ArrayList<>(count);
        for (int i = 0; i < count ; i++) {
            Question question = new Question();
            question.setTest(test);
            System.out.print("Введите вопрос: ");
            question.setText(console.nextLine());
            System.out.print("Введите балл за правильный ответ: ");
            question.setWeight(readPositiveInt(console));
            System.out.print("Введите кол-во ответов: ");
            question.setAnswers(createAnswers(readPositiveInt(console), question));
            questions.add(question);
        }
        test.setQuestions(questions);
        confirmTest(test);
    }

    private void confirmTest(Test test) {
        System.out.println("Название теста: " + test.getTitle());
        System.out.println("Список вопросов и ответов: ");
        int i = 1;
        for (Question question : test.getQuestions()) {
            System.out.println(i + ". " + question.getText());
            System.out.println("Ответы: ");
            listNicePrint(question.getAnswers());
            i++;
        }
        System.out.print("Вы желаете добавить этот тест? (+/-, да/нет, 1/0)");
        String str = console.nextLine();
        if (str.equals("+") || str.equals("1") || str.equalsIgnoreCase("да")) {
            if (dao.getTestDao().insert(test) > 0) {
                System.out.println("Тест успешно добавлен.");
            } else {
                System.out.println("Тест не добавлен");
            }
        }
    }

    private List<Answer> createAnswers(int count, Question q) {
        List<Answer> answers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Answer answer = new Answer();
            answer.setQuestion(q);
            System.out.print("Введите текст ответа: ");
            answer.setText(console.nextLine());
            System.out.print("Ответ правильный? (+/-, да/нет, 1/0)");
            String str = console.nextLine();
            answer.setTrueAnswer(str.equals("+") || str.equals("1") || str.equalsIgnoreCase("да"));
            System.out.println();

            answers.add(answer);
        }
        return answers;
    }

    private void listNicePrint(Iterable<?> list) {
        if (list == null) {
            System.out.println("Список отсутствует");
            return;
        }
        int index = 1;
        for (Object item : list) {
            System.out.printf("%d. %s%n", index, item);
            ++index;
        }
        if (index == 1) {
            System.out.println("Список пуст");
        }
    }

    private int readPositiveInt(Scanner s) {
        try {
            int i = s.nextInt();
            console.nextLine();
            if (i >= 0) return i;
        } catch (InputMismatchException e) { /*ignore*/ }
        return -1;
    }
}