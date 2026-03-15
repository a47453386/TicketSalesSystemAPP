package Model;

public class QuestionType {
    public String id;
    public String name;

    @Override
    public String toString() {
        return name; // 🚩 這行很重要，讓 Spinner 顯示的是文字而不是記憶體位址
    }
}
