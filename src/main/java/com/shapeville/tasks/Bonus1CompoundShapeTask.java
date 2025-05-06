package com.shapeville.tasks;

import com.shapeville.manager.ScoreManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class Bonus1CompoundShapeTask {
    public JPanel mainPanel; // 外部访问用于 CardLayout 添加
    public Runnable onReturnHome;

    private ScoreManager scoreManager;
    private JPanel selectionPanel, questionPanel;
    private CardLayout layout;

    private static class Question {
        int shapeId;
        String imageLabel;
        int correctAnswer;
        String explanation;

        Question(int shapeId, String imageLabel, int correctAnswer, String explanation) {
            this.shapeId = shapeId;
            this.imageLabel = imageLabel;
            this.correctAnswer = correctAnswer;
            this.explanation = explanation;
        }
    }

    private HashMap<Integer, Question> questionMap = new HashMap<>();
    private JLabel imageLabel;
    private JTextField answerField;
    private JLabel feedbackLabel;
    private int currentAttempts;
    private Question currentQuestion;

    public Bonus1CompoundShapeTask(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        this.mainPanel = new JPanel();
        this.layout = new CardLayout();
        this.mainPanel.setLayout(layout);

        initQuestions();
        createSelectionPanel();
        createQuestionPanel();

        mainPanel.add(selectionPanel, "select");
        mainPanel.add(questionPanel, "question");
    }

    private void initQuestions() {
        questionMap.put(1, new Question(1, "Compound Shape 1", 36, "Area = 6×4 = 24 + 2×6 = 12 → 36"));
        questionMap.put(2, new Question(2, "Compound Shape 2", 45, "Area = 5×5 = 25 + 2×10 = 20 → 45"));
        questionMap.put(3, new Question(3, "Compound Shape 3", 52, "Area = 6×6 = 36 + 4×4 = 16 → 52"));
        questionMap.put(4, new Question(4, "Compound Shape 4", 40, "Area = 8×5 = 40"));
        questionMap.put(5, new Question(5, "Compound Shape 5", 30, "Area = 6×5 = 30"));
        questionMap.put(6, new Question(6, "Compound Shape 6", 49, "Area = 7×7 = 49"));
        questionMap.put(7, new Question(7, "Compound Shape 7", 54, "Area = 6×9 = 54"));
        questionMap.put(8, new Question(8, "Compound Shape 8", 32, "Area = 4×8 = 32"));
        questionMap.put(9, new Question(9, "Compound Shape 9", 48, "Area = 6×8 = 48"));
    }

    private void createSelectionPanel() {
        selectionPanel = new JPanel(null);

        JLabel title = new JLabel("🔢 Select a Compound Shape:");
        title.setBounds(250, 20, 300, 30);
        selectionPanel.add(title);

        int x = 50, y = 70;
        for (int i = 1; i <= 9; i++) {
            JButton imgButton = new JButton("Shape " + i);
            imgButton.setBounds(x, y, 120, 60);
            int finalI = i;
            imgButton.addActionListener(e -> loadQuestion(finalI));
            selectionPanel.add(imgButton);

            x += 140;
            if (i % 3 == 0) {
                x = 50;
                y += 80;
            }
        }

        JButton homeBtn = new JButton("Home");
        homeBtn.setBounds(600, 470, 100, 30);
        homeBtn.addActionListener(e -> {
            if (onReturnHome != null) onReturnHome.run();
        });
        selectionPanel.add(homeBtn);
    }

    private void createQuestionPanel() {
        questionPanel = new JPanel(null);

        imageLabel = new JLabel("📐 [Image of Shape]");
        imageLabel.setBounds(100, 40, 500, 40);
        questionPanel.add(imageLabel);

        JLabel prompt = new JLabel("Enter the calculated area:");
        prompt.setBounds(100, 100, 300, 30);
        questionPanel.add(prompt);

        answerField = new JTextField();
        answerField.setBounds(100, 140, 200, 30);
        questionPanel.add(answerField);

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(320, 140, 100, 30);
        questionPanel.add(submitButton);

        feedbackLabel = new JLabel("");
        feedbackLabel.setBounds(100, 180, 500, 30);
        questionPanel.add(feedbackLabel);

        JButton homeBtn = new JButton("Home");
        homeBtn.setBounds(600, 470, 100, 30);
        homeBtn.addActionListener(e -> {
            layout.show(mainPanel, "select");
        });
        questionPanel.add(homeBtn);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAnswer();
            }
        });
    }

    private void loadQuestion(int id) {
        currentQuestion = questionMap.get(id);
        imageLabel.setText("📐 " + currentQuestion.imageLabel);
        feedbackLabel.setText("");
        answerField.setText("");
        currentAttempts = 3;
        layout.show(mainPanel, "question");
    }

    private void checkAnswer() {
        try {
            int userAns = Integer.parseInt(answerField.getText().trim());
            if (userAns == currentQuestion.correctAnswer) {
                int score = switch (currentAttempts) {
                    case 3 -> 3;
                    case 2 -> 2;
                    case 1 -> 1;
                    default -> 0;
                };
                feedbackLabel.setText("✅ Correct! +" + score + " points.");
                scoreManager.addScore(score);
            } else {
                currentAttempts--;
                if (currentAttempts <= 0) {
                    feedbackLabel.setText("❌ Correct answer: " + currentQuestion.correctAnswer + " | " + currentQuestion.explanation);
                } else {
                    feedbackLabel.setText("❌ Incorrect. Attempts left: " + currentAttempts);
                }
            }
        } catch (Exception ex) {
            feedbackLabel.setText("⚠️ Please enter a valid integer.");
        }
    }
}