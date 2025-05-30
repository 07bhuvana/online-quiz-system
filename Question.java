package scamprojectpractice;



public class Question {
    private String questionText;
    private String[] options;
    private char correctOption;
    private String explanation;

    public Question(String questionText, String[] options, char correctOption, String explanation) {
        this.questionText = questionText;
        this.options = options;
        this.correctOption = correctOption;
        this.explanation = explanation;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String[] getOptions() {
        return options;
    }

    public char getCorrectOption() {
        return correctOption;
    }

    public String getExplanation() {
        return explanation;
    }
}
