package scamprojectpractice;


import javax.swing.*;

public class QuizApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String userName = JOptionPane.showInputDialog(null, "Enter your name:");
            String[] subjects = {"Java", "Software Testing", "Software Configuration Management"};
            String selectedSubject = (String) JOptionPane.showInputDialog(null, "Select your subject:", 
                                                                          "Subject Selection", JOptionPane.QUESTION_MESSAGE, 
                                                                          null, subjects, subjects[0]);
            String dbUrl;

            switch (selectedSubject) {
                case "Java":
                    dbUrl = "jdbc:mysql://localhost:3306/quizdb";
                    break;
                case "Software Testing":
                    dbUrl = "jdbc:mysql://localhost:3306/software_testing_db";
                    break;
                case "Software Configuration Management":
                    dbUrl = "jdbc:mysql://localhost:3306/software_config_management_db";
                    break;
                default:
                    dbUrl = "jdbc:mysql://localhost:3306/quizdb";
            }

            int numQuestions = Integer.parseInt(JOptionPane.showInputDialog(null, "How many questions do you want?"));
            int questionTime = 25; // Default time per question in seconds
            boolean isLevelTwo = false; // Start with Level 1

            new QuizGUI(userName, numQuestions, dbUrl, questionTime, isLevelTwo);
        });
    }
}
