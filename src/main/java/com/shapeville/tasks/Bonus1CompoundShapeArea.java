package com.shapeville.tasks;

import com.shapeville.manager.ScoreManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Bonus1CompoundShapeArea {
    public JPanel taskPanel;
    public Runnable onReturnHome;
    private ScoreManager scoreManager;
    private JLabel score;
    public int completedTasks = 0;

    private JPanel shapeSelectPanel;
    private JPanel questionPanel;

    private JLabel imageLabel;
    private JLabel feedbackLabel;
    private JTextField answerField;

    private Map<Integer, Image> originalImages = new HashMap<>();
    private Map<Integer, Image> answerImages = new HashMap<>();
    private Map<Integer, JButton> shapeButtons = new HashMap<>();

    private int currentShapeId;
    private int attemptCount = 0;

    private Map<Integer, Double> correctAnswers = new HashMap<>();

    public Bonus1CompoundShapeArea(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        this.taskPanel = new JPanel(new CardLayout());
        this.score = new JLabel();
        score.setText("Score: 0");
        initAnswerData();
        initShapeSelectPanel();
        initQuestionPanel();
        taskPanel.add(shapeSelectPanel, "select");
        taskPanel.add(questionPanel, "question");

        taskPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                resizeCurrentImage();
            }
        });
    }

    private void initAnswerData() {
        // 正确答案
        correctAnswers.put(1, 231.0);
        correctAnswers.put(2, 310.0);
        correctAnswers.put(3, 598.0);
        correctAnswers.put(4, 288.0);
        correctAnswers.put(5, 76.0);
        correctAnswers.put(6, 187.0);

        // 加载解释图
        for (int i = 1; i <= 6; i++) {
            try {
                Image img = new ImageIcon(getClass().getClassLoader().getResource("images/Bonus1Answer" + i + ".png")).getImage();
                answerImages.put(i, img);
            } catch (Exception e) {
                System.err.println("Missing Bonus1Answer" + i + ".png");
            }
        }
    }

    private void initShapeSelectPanel() {
        shapeSelectPanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(score);
        JLabel title = new JLabel("Select a Compound Shape:");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(title);
        shapeSelectPanel.add(topPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (int i = 1; i <= 6; i++) {
            int shapeId = i;
            try {
                ImageIcon rawIcon = new ImageIcon(getClass().getClassLoader().getResource("images/Shape" + i + ".png"));
                originalImages.put(shapeId, rawIcon.getImage());

                JButton btn = new JButton(new ImageIcon(getScaledImage(rawIcon.getImage(), 200, 130)));
                btn.addActionListener(e -> showQuestion(shapeId));
                buttonPanel.add(btn);
                shapeButtons.put(shapeId, btn);
            } catch (Exception ex) {
                JButton btn = new JButton("Shape " + i);
                btn.addActionListener(e -> showQuestion(shapeId));
                buttonPanel.add(btn);
                shapeButtons.put(shapeId, btn);
            }
        }

        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        shapeSelectPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(e -> {
            if (onReturnHome != null) onReturnHome.run();
        });
        bottomPanel.add(homeButton);
        shapeSelectPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void initQuestionPanel() {
        questionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        imageLabel = new JLabel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.weighty = 1.0;
        questionPanel.add(imageLabel, gbc);

        JLabel prompt = new JLabel("Enter the calculated area:");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.weighty = 0.0;
        questionPanel.add(prompt, gbc);

        answerField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        questionPanel.add(answerField, gbc);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(this::handleSubmit);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_START;
        questionPanel.add(submitButton, gbc);
        gbc.fill = GridBagConstraints.BOTH;

        feedbackLabel = new JLabel("");
        gbc.gridx = 1;
        gbc.gridy = 3;
        questionPanel.add(feedbackLabel, gbc);

        JButton homeButton = new JButton("Back");
        homeButton.addActionListener(e -> {
            answerField.setText("");
            feedbackLabel.setText("");
            imageLabel.setIcon(null);
            attemptCount = 0;
            ((CardLayout) taskPanel.getLayout()).show(taskPanel, "select");
        });
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.LINE_END;
        questionPanel.add(homeButton, gbc);
        homeButton.setVisible(false);
    }

    private void showQuestion(int shapeId) {
        currentShapeId = shapeId;
        Image originalImage = originalImages.getOrDefault(shapeId, null);
        if (originalImage != null) {
            imageLabel.setIcon(new ImageIcon(getScaledImage(originalImage,
                    imageLabel.getWidth(), imageLabel.getHeight())));
        } else {
            imageLabel.setText("❌ Image not found");
        }

        answerField.setText("");
        feedbackLabel.setText("");
        attemptCount = 0;
        ((CardLayout) taskPanel.getLayout()).show(taskPanel, "question");
    }

    private void handleSubmit(ActionEvent e) {
        try {
            double ans = Double.parseDouble(answerField.getText());
            double correct = correctAnswers.get(currentShapeId);
            if (Math.abs(ans - correct) < 0.01) {
                int points = switch (attemptCount) {
                    case 0 -> 3;
                    case 1 -> 2;
                    case 2 -> 1;
                    default -> 0;
                };
                feedbackLabel.setText("✅ Correct! +" + points + " points");
                scoreManager.addScore(points);
                completedTasks += 1;
                updateButtonState(currentShapeId);
                showbackButton();
            } else {
                attemptCount++;
                if (attemptCount >= 3) {
                    Image explanationImg = answerImages.get(currentShapeId);
                    if (explanationImg != null) {
                        imageLabel.setIcon(new ImageIcon(getScaledImage(explanationImg,
                                imageLabel.getWidth(), imageLabel.getHeight())));
                    }
                    feedbackLabel.setText("❌ Incorrect.");
                    completedTasks += 1;
                    updateButtonState(currentShapeId);
                    showbackButton();
                } else {
                    feedbackLabel.setText("❌ Try again. Attempts left: " + (3 - attemptCount));
                }
            }
        } catch (Exception ex) {
            feedbackLabel.setText("❌ Please enter a valid number.");
        }
        score.setText("Score: " + scoreManager.getScore());
    }

    private void updateButtonState(int shapeId) {
        JButton btn = shapeButtons.get(shapeId);
        if (btn != null) {
            btn.setEnabled(false);
        }
    }

    private void showbackButton() {
        for (Component comp : questionPanel.getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals("Back")) {
                comp.setVisible(true);
                break;
            }
        }
    }

    private Image getScaledImage(Image srcImg, int w, int h) {
        if (w <= 0 || h <= 0) {
            w = 300;
            h = 250;
        }

        int originalWidth = srcImg.getWidth(null);
        int originalHeight = srcImg.getHeight(null);

        double ratio = (double) originalWidth / originalHeight;
        if (w / h > ratio) {
            w = (int) (h * ratio);
        } else {
            h = (int) (w / ratio);
        }

        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }

    private void resizeCurrentImage() {
        if (currentShapeId > 0) {
            Image img = imageLabel.getIcon() instanceof ImageIcon
                    ? ((ImageIcon) imageLabel.getIcon()).getImage()
                    : originalImages.get(currentShapeId);
            if (img != null) {
                imageLabel.setIcon(new ImageIcon(getScaledImage(img,
                        imageLabel.getWidth(), imageLabel.getHeight())));
            }
        }
    }
}