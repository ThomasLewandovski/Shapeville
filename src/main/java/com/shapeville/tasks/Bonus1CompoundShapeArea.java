// File: Bonus1CompoundShapeArea.java
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

    private JPanel shapeSelectPanel;
    private JPanel questionPanel;

    private JLabel imageLabel;
    private JLabel feedbackLabel;
    private JTextField answerField;

    // 存储原始图像，用于缩放
    private Map<Integer, Image> originalImages = new HashMap<>();

    public int currentShapeId;
    public int attemptCount = 0;
    private Map<Integer, Double> correctAnswers;
    private Map<Integer, String> explanations;

    public Bonus1CompoundShapeArea(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        this.taskPanel = new JPanel(new CardLayout());
        this.score = new JLabel();
        score.setText("Score: 0");
        initializeAnswerData();
        initShapeSelectPanel();
        initQuestionPanel();
        taskPanel.add(shapeSelectPanel, "select");
        taskPanel.add(questionPanel, "question");

        // 添加组件监听器，处理窗口大小变化
        taskPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                resizeCurrentImage();
            }
        });
    }

    private void initializeAnswerData() {
        correctAnswers = new HashMap<>();
        explanations = new HashMap<>();

        correctAnswers.put(1, 231.0);
        explanations.put(1, "Area = 14×14 + ½×5×14 = 196 + 35 = 231 cm²");

        correctAnswers.put(2, 310.0);
        explanations.put(2, "Area = 21×20 – 11×10 = 420 -110 = 310 cm²");

        correctAnswers.put(3, 598.0);
        explanations.put(3, "Area = 18×19 + 16×16 =342 + 256 = 598 cm²");

        correctAnswers.put(4, 288.0);
        explanations.put(4, "Area = 12×12 + 24×6  = 144 + 144 = 288 cm²");

        correctAnswers.put(5, 76.0);
        explanations.put(5, "Area = 16×4 = 64 + ½×(4+2)×4 = 12 → 76 cm²");

        correctAnswers.put(6, 187.0);
        explanations.put(6, "Area = ½×(20+14)×11 = 187 cm²");

        correctAnswers.put(7, 196.0);
        explanations.put(7, "Area = 14×12 = 168 + ½×14×4 = 28 → 196 cm²");

        correctAnswers.put(8, 3456.0);
        explanations.put(8, "Area = 36×36 + 36×60 = 3456 cm²");

        correctAnswers.put(9, 174.0);
        explanations.put(9, "Area = 10×11 + 8×8 = 110 + 64 = 174 cm²");
    }

    private void initShapeSelectPanel() {
        // 使用BorderLayout作为主布局
        shapeSelectPanel = new JPanel(new BorderLayout());

        // 顶部面板 - 标题和分数
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(score);
        JLabel title = new JLabel("Select a Compound Shape:");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(title);
        shapeSelectPanel.add(topPanel, BorderLayout.NORTH);

        // 中间面板 - 形状选择按钮
        JPanel buttonPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (int i = 1; i <= 9; i++) {
            int shapeId = i;
            try {
                // 加载图像并保存原始图像
                ImageIcon rawIcon = new ImageIcon(getClass().getClassLoader().getResource("images/Shape" + i + ".png"));
                originalImages.put(shapeId, rawIcon.getImage());

                // 创建可缩放的图像按钮
                JButton btn = new JButton(new ImageIcon(getScaledImage(rawIcon.getImage(), 200, 130)));
                btn.addActionListener(e -> showQuestion(shapeId));
                buttonPanel.add(btn);
            } catch (Exception ex) {
                JButton btn = new JButton("Shape " + i);
                btn.addActionListener(e -> showQuestion(shapeId));
                buttonPanel.add(btn);
            }
        }

        // 使用JScrollPane支持滚动
        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        shapeSelectPanel.add(scrollPane, BorderLayout.CENTER);

        // 底部面板 - 主页按钮
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(e -> {
            if (onReturnHome != null) onReturnHome.run();
        });
        bottomPanel.add(homeButton);
        shapeSelectPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void initQuestionPanel() {
        // 使用GridBagLayout作为主布局
        questionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // 图像标签
        imageLabel = new JLabel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.weighty = 1.0;
        questionPanel.add(imageLabel, gbc);

        // 提示标签
        JLabel prompt = new JLabel("Enter the calculated area:");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.weighty = 0.0;
        questionPanel.add(prompt, gbc);

        // 答案输入框
        answerField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        questionPanel.add(answerField, gbc);

        // 提交按钮
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(this::handleSubmit);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_START;
        questionPanel.add(submitButton, gbc);
        gbc.fill = GridBagConstraints.BOTH;
//        submitButton.setVisible(false);

        // 反馈标签
        feedbackLabel = new JLabel("");
        gbc.gridx = 1;
        gbc.gridy = 3;
        questionPanel.add(feedbackLabel, gbc);

        // 主页按钮
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
        try {
            // 获取原始图像
            Image originalImage = originalImages.getOrDefault(shapeId, null);
            if (originalImage != null) {
                // 根据当前容器大小缩放图像
                imageLabel.setIcon(new ImageIcon(getScaledImage(originalImage,
                        imageLabel.getWidth(), imageLabel.getHeight())));
            } else {
                imageLabel.setText("❌ Image not found");
            }
        } catch (Exception ex) {
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
                int score = switch (attemptCount) {
                    case 0 -> 3;
                    case 1 -> 2;
                    case 2 -> 1;
                    default -> 0;
                };
                feedbackLabel.setText("✅ Correct! +" + score + " points");
                scoreManager.addScore(score);
                showbackButton();
                completedTasks+=1;
            } else {
                attemptCount++;
                if (attemptCount >= 3) {
                    feedbackLabel.setText("❌ Incorrect. " + explanations.get(currentShapeId));
                    showbackButton();
                    completedTasks+=1;
                } else {
                    feedbackLabel.setText("❌ Try again. Attempts left: " + (3 - attemptCount));
                }
            }
        } catch (Exception ex) {
            feedbackLabel.setText("❌ Please enter a valid number.");
        }
        score.setText("Score: " + scoreManager.getScore());
    }

    private void showbackButton() {
        // 查找并显示"Back"按钮（在GridBagLayout的位置）
        Component[] components = questionPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals("Back")) {
                comp.setVisible(true);
                break;
            }
        }
    }

    // 缩放图像方法
    private Image getScaledImage(Image srcImg, int w, int h) {
        if (w <= 0 || h <= 0) {
            // 设置默认大小
            w = 300;
            h = 250;
        }

        // 保持图像比例
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

    // 处理窗口大小变化时的图像缩放
    private void resizeCurrentImage() {
        if (currentShapeId > 0 && originalImages.containsKey(currentShapeId)) {
            Image originalImage = originalImages.get(currentShapeId);
            imageLabel.setIcon(new ImageIcon(getScaledImage(originalImage,
                    imageLabel.getWidth(), imageLabel.getHeight())));
        }
    }
}