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
    public ScoreManager scoreManager;
    public JLabel score;
    public int completedTasks = 0;
    public int[] completedShapes = new int[7];

    private JPanel shapeSelectPanel;
    private JPanel questionPanel;

    private JLabel imageLabel;
    private JLabel feedbackLabel;
    private JTextField answerField;
    private JButton submitButton;

    private JLabel mascotSpeechBubble;
    private JLabel mascotImageLabel;
    private JButton backButton;

    private Map<Integer, Image> originalImages = new HashMap<>();
    private Map<Integer, Image> answerImages = new HashMap<>();
    public Map<Integer, JButton> shapeButtons = new HashMap<>();

    public int currentShapeId;
    public int attemptCount = 0;

    private Map<Integer, Double> correctAnswers = new HashMap<>();

    public Bonus1CompoundShapeArea(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        this.taskPanel = new JPanel(new CardLayout());
        this.taskPanel.setBackground(new Color(255, 250, 205));
        this.score = new JLabel("Score: 0");

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
        correctAnswers.put(1, 310.0);
        correctAnswers.put(2, 598.0);
        correctAnswers.put(3, 288.0);
        correctAnswers.put(4, 18.0);
        correctAnswers.put(5, 3456.0);
        correctAnswers.put(6, 174.0);

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
        shapeSelectPanel.setBackground(new Color(255, 250, 205));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(255, 250, 205));
        topPanel.add(score);
        JLabel title = new JLabel("Select a Compound Shape:");
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        topPanel.add(title);
        shapeSelectPanel.add(topPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        buttonPanel.setBackground(new Color(255, 250, 205));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (int i = 1; i <= 6; i++) {
            int shapeId = i;
            try {
                ImageIcon rawIcon = new ImageIcon(getClass().getClassLoader().getResource("images/Shape" + i + ".png"));
                originalImages.put(shapeId, rawIcon.getImage());

                JButton btn = new JButton(new ImageIcon(getScaledImage(rawIcon.getImage(), 200, 130)));
                btn.addActionListener(e -> showQuestion(shapeId));
                buttonPanel.add(btn);
                completedShapes[i] = 0;
                shapeButtons.put(shapeId, btn);
            } catch (Exception ex) {
                JButton btn = new JButton("Shape " + i);
                btn.addActionListener(e ->{
                    submitButton.setEnabled(true);
                    showQuestion(shapeId);
                });
                buttonPanel.add(btn);
                shapeButtons.put(shapeId, btn);
            }
        }

        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.setBackground(new Color(255, 250, 205));
        scrollPane.getViewport().setBackground(new Color(255, 250, 205));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        shapeSelectPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(255, 250, 205));
        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(e -> {
            if (onReturnHome != null) onReturnHome.run();
        });
        bottomPanel.add(homeButton);
        shapeSelectPanel.add(bottomPanel, BorderLayout.SOUTH);
    }


    private void initQuestionPanel() {
        questionPanel = new JPanel(new BorderLayout());
        questionPanel.setBackground(new Color(255, 250, 205));

        // 左边：题目图片
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(new Color(255, 250, 205));
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // 右边：答题面板 + 狐狸吉祥物
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(255, 250, 205));

        // 🟢 Back 按钮单独置底
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.setOpaque(false);
        backButton = new JButton("Back");
        backButton.setVisible(false);
        backButton.addActionListener(e -> {
            answerField.setText("");
            feedbackLabel.setText("");
            imageLabel.setIcon(null);
            attemptCount = 0;
            ((CardLayout) taskPanel.getLayout()).show(taskPanel, "select");
        });
        backPanel.add(backButton);

        // ➕ 题目输入部分
        JLabel prompt = new JLabel("Enter the calculated area:");
        prompt.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        prompt.setAlignmentX(Component.LEFT_ALIGNMENT);

        answerField = new JTextField();
        answerField.setMaximumSize(new Dimension(200, 30));
        answerField.setAlignmentX(Component.LEFT_ALIGNMENT);

        submitButton = new JButton("Submit");
        submitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitButton.addActionListener(this::handleSubmit);

        feedbackLabel = new JLabel("");
        feedbackLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
        feedbackLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ➕ 狐狸吉祥物
        JPanel mascotPanel = new JPanel();
        mascotPanel.setLayout(new BoxLayout(mascotPanel, BoxLayout.Y_AXIS));
        mascotPanel.setBackground(new Color(255, 250, 205));
        mascotPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        mascotSpeechBubble = new JLabel("<html><div style='padding:10px; background:#fff8dc; border-radius:10px; border:1px solid #ccc;'>Hi! I'm Foxie! Let's solve this shape! 🦊</div></html>");
        mascotSpeechBubble.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
        mascotSpeechBubble.setAlignmentX(Component.LEFT_ALIGNMENT);

        try {
            ImageIcon foxIcon = new ImageIcon(getClass().getClassLoader().getResource("images/Fox.png"));
            Image scaled = foxIcon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
            mascotImageLabel = new JLabel(new ImageIcon(scaled));
        } catch (Exception ex) {
            mascotImageLabel = new JLabel("🦊");
        }
        mascotImageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        mascotPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mascotPanel.add(mascotSpeechBubble);
        mascotPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mascotPanel.add(mascotImageLabel);

        // ➕ 装入右侧面板
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(prompt);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(answerField);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(submitButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(feedbackLabel);
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(mascotPanel);
        rightPanel.add(backPanel);

        JPanel content = new JPanel(new GridLayout(1, 2));
        content.setBackground(new Color(255, 250, 205));
        content.add(imagePanel);
        content.add(rightPanel);

        questionPanel.add(content, BorderLayout.CENTER);
    }

    private void showQuestion(int shapeId) {
        currentShapeId = shapeId;
        Image originalImage = originalImages.getOrDefault(shapeId, null);
        if (originalImage != null) {
            imageLabel.setIcon(new ImageIcon(getScaledImage(originalImage, 400, 300)));
        } else {
            imageLabel.setText("❌ Image not found");
        }

        answerField.setText("");
        feedbackLabel.setText("");
        attemptCount = 0;
        backButton.setVisible(false);
        mascotSpeechBubble.setText("<html><div style='padding:10px; background:#fff8dc; border-radius:10px; border:1px solid #ccc;'>Hmm... can you find the area? 🦊</div></html>");
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
                mascotSpeechBubble.setText("<html><div style='padding:10px; background:#e0ffe0; border-radius:10px; border:1px solid #8bc34a;'>Yay! That’s correct! 🎉🦊</div></html>");
                scoreManager.addScore(points);
                completedTasks++;
                updateButtonState(currentShapeId);
                submitButton.setEnabled(false);
                backButton.setVisible(true);
            } else {
                attemptCount++;
                if (attemptCount >= 3) {
                    Image explanationImg = answerImages.get(currentShapeId);
                    if (explanationImg != null) {
                        imageLabel.setIcon(new ImageIcon(getScaledImage(explanationImg, 400, 300)));
                    }
                    feedbackLabel.setText("❌ Incorrect.");
                    mascotSpeechBubble.setText("<html><div style='padding:10px; background:#ffe0e0; border-radius:10px; border:1px solid #e57373;'>Oops! The correct answer is " + correct + " 🦊</div></html>");
                    completedTasks++;
                    updateButtonState(currentShapeId);
                    backButton.setVisible(true);
                    submitButton.setEnabled(false);
                } else {
                    feedbackLabel.setText("❌ Try again. Attempts left: " + (3 - attemptCount));
                    mascotSpeechBubble.setText("<html><div style='padding:10px; background:#fff3cd; border-radius:10px; border:1px solid #ffeb3b;'>Almost there! Try once more! 🦊</div></html>");
                }
            }
        } catch (Exception ex) {
            feedbackLabel.setText("❌ Please enter a valid number.");
            mascotSpeechBubble.setText("<html><div style='padding:10px; background:#ffe0e0; border-radius:10px; border:1px solid #e57373;'>Only numbers please! 🦊</div></html>");
        }
        score.setText("Score: " + scoreManager.getScore());
    }

    private void updateButtonState(int shapeId) {
        JButton btn = shapeButtons.get(shapeId);
        completedShapes[shapeId] = 1;
        if (btn != null) {
            btn.setEnabled(false);
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
        if (currentShapeId > 0 && imageLabel.getIcon() instanceof ImageIcon) {
            Image img = ((ImageIcon) imageLabel.getIcon()).getImage();
            imageLabel.setIcon(new ImageIcon(getScaledImage(img, imageLabel.getWidth(), imageLabel.getHeight())));
        }
    }
}