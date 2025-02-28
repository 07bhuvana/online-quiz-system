package scamprojectpractice;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class QuizGUI extends JFrame implements ActionListener {
    private ArrayList<Question> questions;
    private ArrayList<Character> userAnswers;
    private QuizDAO quizDAO;
    private JPanel questionsPanel;
    private JButton submitButton;
    private JLabel timerLabel;
    private int totalQuestions;
    private int score = 0;
    private int timeRemaining;
    private Timer timer;
    private String userName;
    private int questionTime;
    private boolean isLevelTwo;

    public QuizGUI(String userName, int numQuestions, String dbUrl, int questionTime, boolean isLevelTwo) {
        super("Online Quiz");

        this.userName = userName;
        this.questionTime = questionTime;
        this.timeRemaining = numQuestions * questionTime;
        this.isLevelTwo = isLevelTwo;

        String username = "root";
        String password = "Bhuvana@2004";

        quizDAO = new QuizDAO(dbUrl, username, password);
        questions = new ArrayList<>();
        for (int i = 0; i < numQuestions; i++) {
            Question q = quizDAO.getQuestion();
            if (q != null) {
                questions.add(q);
            }
        }
        totalQuestions = questions.size();
        userAnswers = new ArrayList<>(totalQuestions);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        String levelText = isLevelTwo ? "Level 2" : "Level 1";
        JOptionPane.showMessageDialog(this, "Welcome " + userName + "! Let's start " + levelText + " of the quiz!");

        timerLabel = new JLabel("Time remaining: " + formatTime(timeRemaining), JLabel.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(timerLabel, BorderLayout.NORTH);

        questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(questionsPanel);
        add(scrollPane, BorderLayout.CENTER);

        for (int i = 0; i < totalQuestions; i++) {
            addQuestionPanel(i);
        }

        submitButton = new JButton("Submit Quiz");
        submitButton.addActionListener(this);
        add(submitButton, BorderLayout.SOUTH);

        startTimer();
        setVisible(true);
    }

    private void addQuestionPanel(int questionIndex) {
        Question question = questions.get(questionIndex);

        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setBorder(BorderFactory.createTitledBorder("Question " + (questionIndex + 1)));
        questionPanel.add(new JLabel(question.getQuestionText()));

        ButtonGroup optionsGroup = new ButtonGroup();
        String[] options = question.getOptions();
        for (int i = 0; i < options.length; i++) {
            String optionText = isLevelTwo ? options[i] : (char) ('A' + i) + ". " + options[i];
            JRadioButton optionButton = new JRadioButton(optionText);
            optionButton.setActionCommand(String.valueOf((char) ('A' + i)));
            optionButton.addActionListener(e -> {
                while (userAnswers.size() <= questionIndex) {
                    userAnswers.add(' ');
                }
                userAnswers.set(questionIndex, e.getActionCommand().charAt(0));
            });
            optionsGroup.add(optionButton);
            questionPanel.add(optionButton);
        }

        questionsPanel.add(questionPanel);
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                timeRemaining--;
                timerLabel.setText("Time remaining: " + formatTime(timeRemaining));

                if (timeRemaining <= 0) {
                    timer.cancel();
                    evaluatePerformance();
                }
            }
        }, 1000, 1000);
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            timer.cancel();
            evaluatePerformance();
        }
    }

    private void evaluatePerformance() {
        score = 0;
        StringBuilder feedback = new StringBuilder("Quiz Results:\n");

        for (int i = 0; i < totalQuestions; i++) {
            Question question = questions.get(i);
            char userAnswer = userAnswers.size() > i ? userAnswers.get(i) : ' ';
            char correctAnswer = question.getCorrectOption();

            if (Character.toUpperCase(userAnswer) == Character.toUpperCase(correctAnswer)) {
                score++;
                feedback.append("Question ").append(i + 1).append(": Correct Answer!\n");
            } else {
                feedback.append("Question ").append(i + 1).append(": Incorrect. Correct Answer is ")
                        .append(correctAnswer).append(". Explanation: ")
                        .append(question.getExplanation()).append("\n");
            }
        }

        double percentage = (double) score / totalQuestions * 100;
        feedback.append("\nScore: ").append(score).append("/").append(totalQuestions)
                .append("\nPercentage: ").append(String.format("%.2f", percentage)).append("%");

        JOptionPane.showMessageDialog(this, feedback.toString());

        if (isLevelTwo) {
            JOptionPane.showMessageDialog(this, "Thank you for completing the quiz!");
            System.exit(0);
        } else if (percentage >= 70.0) {
            JOptionPane.showMessageDialog(this, "Congratulations! You've passed Level 1. Proceeding to Level 2.");
            loadLevelTwo();
        } else {
            JOptionPane.showMessageDialog(this, "You are not eligible for Level 2. Please try again to improve your score.");
            System.exit(0);
        }
    }

    private void loadLevelTwo() {
        this.dispose();
        new QuizGUI(userName, totalQuestions, "jdbc:mysql://localhost:3306/quizdb_level2", 30, true);
    }
}
