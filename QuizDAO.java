package scamprojectpractice;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QuizDAO {
    private String dbUrl;
    private String username;
    private String password;

    public QuizDAO(String dbUrl, String username, String password) {
        this.dbUrl = dbUrl;
        this.username = username;
        this.password = password;
    }

    public Question getQuestion() {
        try (Connection connection = DriverManager.getConnection(dbUrl, username, password)) {
            String query = "SELECT question_text, option_a, option_b, option_c, option_d, correct_option, explanation FROM questions ORDER BY RAND() LIMIT 1";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String questionText = resultSet.getString("question_text");
                String[] options = new String[4];
                options[0] = resultSet.getString("option_a");
                options[1] = resultSet.getString("option_b");
                options[2] = resultSet.getString("option_c");
                options[3] = resultSet.getString("option_d");
                char correctOption = resultSet.getString("correct_option").charAt(0);
                String explanation = resultSet.getString("explanation");

                return new Question(questionText, options, correctOption, explanation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no question is found
    }
}
