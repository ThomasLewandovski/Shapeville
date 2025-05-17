package com.shapeville.tasks;

import com.shapeville.manager.ScoreManager;

import javax.swing.*;
import java.awt.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class Task3VolumeSurfaceCalculator {
    public JPanel task3;
    public Runnable onReturnHome;
    private ScoreManager scoreManager;
    public JLabel score;

    private JLabel questionLabel;
    private JTextField inputField;
    private JButton submitButton, homeButton;
    private JComboBox<String> shapeSelector;
    private Timer countdownTimer;
    private JLabel timerLabel;
    private DrawingPanel drawingPanel;

    private String currentShape;
    private int param1, param2, param3;
    private int correctAnswer;
    private int attemptsLeft;
    private int timeRemaining;

    private Set<String> completedShapes;

    public Task3VolumeSurfaceCalculator(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        this.completedShapes = new HashSet<>();

        // 使用BorderLayout作为主面板布局
        task3 = new JPanel(new BorderLayout(10, 10));
        task3.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 顶部面板 - 包含分数和标题
        JPanel topPanel = new JPanel(new BorderLayout());
        score = new JLabel("Score: 0");
        score.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(score, BorderLayout.NORTH);

        questionLabel = new JLabel("Choose a shape:");
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        questionLabel.setVerticalAlignment(JLabel.TOP);
        questionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topPanel.add(questionLabel, BorderLayout.CENTER);

        task3.add(topPanel, BorderLayout.NORTH);

        // 中间面板 - 包含形状选择和输入区域
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        shapeSelector = new JComboBox<>(new String[]{"Rectangle", "Parallelogram", "Triangle", "Trapezium"});
        shapeSelector.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        centerPanel.add(shapeSelector, gbc);

        JButton generateButton = new JButton("Generate Problem");
        generateButton.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        centerPanel.add(generateButton, gbc);

        timerLabel = new JLabel("Time left: 180s");
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        centerPanel.add(timerLabel, gbc);

        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.7;
        centerPanel.add(inputField, gbc);

        submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        centerPanel.add(submitButton, gbc);

        task3.add(centerPanel, BorderLayout.CENTER);

        // 底部面板 - 包含绘图区域和返回按钮
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));

        drawingPanel = new DrawingPanel();
        drawingPanel.setPreferredSize(new Dimension(400, 200));
        drawingPanel.setMinimumSize(new Dimension(300, 150));
        drawingPanel.setBackground(Color.WHITE);
        bottomPanel.add(drawingPanel, BorderLayout.CENTER);

        homeButton = new JButton("Home");
        homeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        homeButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JPanel homeButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        homeButtonPanel.add(homeButton);
        bottomPanel.add(homeButtonPanel, BorderLayout.SOUTH);

        task3.add(bottomPanel, BorderLayout.SOUTH);

        // 按钮事件处理
        generateButton.addActionListener(e -> start());
        submitButton.addActionListener(e -> checkAnswer());
        homeButton.addActionListener(e -> {
            if (countdownTimer != null) countdownTimer.stop();
            if (onReturnHome != null) onReturnHome.run();
        });
    }

    public void start() {
        currentShape = (String) shapeSelector.getSelectedItem();
        if (completedShapes.contains(currentShape)) {
            questionLabel.setText("You already completed " + currentShape);
            return;
        }

        // 参数生成（1~20）
        Random rand = new Random();
        param1 = rand.nextInt(20) + 1;
        param2 = rand.nextInt(20) + 1;
        param3 = rand.nextInt(20) + 1;
        attemptsLeft = 3;
        timeRemaining = 180;

        // 设置题目内容
        switch (currentShape) {
            case "Rectangle" -> {
                correctAnswer = param1 * param2;
                questionLabel.setText("📐 Rectangle: length = " + param1 + ", width = " + param2 + ". Calculate area:");
            }
            case "Parallelogram" -> {
                correctAnswer = param1 * param2;
                questionLabel.setText("📐 Parallelogram: base = " + param1 + ", height = " + param2 + ". Calculate area:");
            }
            case "Triangle" -> {
                correctAnswer = (param1 * param2) / 2;
                questionLabel.setText("📐 Triangle: base = " + param1 + ", height = " + param2 + ". Calculate area:");
            }
            case "Trapezium" -> {
                correctAnswer = ((param1 + param2) * param3) / 2;
                questionLabel.setText("📐 Trapezium: a = " + param1 + ", b = " + param2 + ", height = " + param3 + ". Calculate area:");
            }
        }

        inputField.setText("");
        if (countdownTimer != null) countdownTimer.stop();
        countdownTimer = new Timer(1000, e -> {
            timeRemaining--;
            timerLabel.setText("Time left: " + timeRemaining + "s");
            if (timeRemaining <= 0) {
                ((Timer) e.getSource()).stop();
                showExplanation();
            }
        });
        countdownTimer.start();
        drawingPanel.repaint(); // 清除旧图形
    }

    private void checkAnswer() {
        try {
            int userAns = Integer.parseInt(inputField.getText().trim());
            if (userAns == correctAnswer) {
                countdownTimer.stop();
                int score = switch (attemptsLeft) {
                    case 3 -> 3;
                    case 2 -> 2;
                    case 1 -> 1;
                    default -> 0;
                };
                scoreManager.addScore(score);
                completedShapes.add(currentShape);
                questionLabel.setText("✅ Great job! +" + score + " points");
            } else {
                attemptsLeft--;
                if (attemptsLeft <= 0) {
                    countdownTimer.stop();
                    showExplanation();
                } else {
                    questionLabel.setText("❌ Incorrect. Attempts left: " + attemptsLeft);
                }
            }
        } catch (Exception e) {
            questionLabel.setText("Please enter a valid number");
        }
        score.setText("Score: " + scoreManager.getScore());
    }

    private void showExplanation() {
        String formula = switch (currentShape) {
            case "Rectangle" -> "Area = length × width = " + param1 + " × " + param2 + " = " + correctAnswer;
            case "Parallelogram" -> "Area = base × height = " + param1 + " × " + param2 + " = " + correctAnswer;
            case "Triangle" -> "Area = base × height / 2 = " + param1 + " × " + param2 + " / 2 = " + correctAnswer;
            case "Trapezium" -> "Area = (a + b) × height / 2 = (" + param1 + " + " + param2 + ") × " + param3 + " / 2 = " + correctAnswer;
            default -> "Unknown shape.";
        };
        completedShapes.add(currentShape);
        drawingPanel.repaint();
    }

    class DrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (currentShape != null && attemptsLeft <= 0) {
                drawShapeWithLabel(g);
            }
        }

        private void drawShapeWithLabel(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(new Font("Arial", Font.PLAIN, 12));

            int width = getWidth();
            int height = getHeight();
            int padding = 20;
            int shapeWidth = width - 2 * padding;
            int shapeHeight = height - 40; // 为底部文本留出空间

            g2.setColor(Color.BLUE);

            switch (currentShape) {
                case "Rectangle" -> {
                    int rectWidth = Math.min(shapeWidth, shapeHeight * 2 / 3);
                    int rectHeight = rectWidth * 3 / 5;
                    int x = (width - rectWidth) / 2;
                    int y = (height - rectHeight - 30) / 2;

                    g2.drawRect(x, y, rectWidth, rectHeight);
                    g2.drawString("length: " + param1, x + rectWidth/2 - 20, y - 5);
                    g2.drawString("width: " + param2, x + rectWidth + 5, y + rectHeight/2);
                }
                case "Parallelogram" -> {
                    int paraWidth = Math.min(shapeWidth, shapeHeight * 2 / 3);
                    int paraHeight = paraWidth * 3 / 5;
                    int x = (width - paraWidth) / 2;
                    int y = (height - paraHeight - 30) / 2;
                    int skew = paraWidth / 5;

                    int[] xPoints = {x, x + skew, x + paraWidth, x + paraWidth - skew};
                    int[] yPoints = {y + paraHeight, y, y, y + paraHeight};

                    g2.drawPolygon(xPoints, yPoints, 4);
                    g2.drawString("base: " + param1, x + paraWidth/2 - 15, y - 5);
                    g2.drawString("height: " + param2, x - 25, y + paraHeight/2);
                }
                case "Triangle" -> {
                    int triWidth = Math.min(shapeWidth, shapeHeight);
                    int triHeight = triWidth * 3 / 4;
                    int x = (width - triWidth) / 2;
                    int y = (height - triHeight - 30) / 2;

                    int[] xPoints = {x, x + triWidth, x + triWidth/2};
                    int[] yPoints = {y + triHeight, y + triHeight, y};

                    g2.drawPolygon(xPoints, yPoints, 3);
                    g2.drawString("base: " + param1, x + triWidth/2 - 15, y + triHeight + 15);
                    g2.drawString("height: " + param2, x + triWidth/2 + 10, y + triHeight/2);
                }
                case "Trapezium" -> {
                    int trapWidth = Math.min(shapeWidth, shapeHeight * 2 / 3);
                    int trapHeight = trapWidth * 3 / 5;
                    int x = (width - trapWidth) / 2;
                    int y = (height - trapHeight - 30) / 2;

                    // 梯形的上底和下底
                    int topBase = trapWidth * 3 / 5;
                    int bottomBase = trapWidth;

                    int[] xPoints = {x + (bottomBase - topBase)/2, x + (bottomBase - topBase)/2 + topBase, x + bottomBase, x};
                    int[] yPoints = {y, y, y + trapHeight, y + trapHeight};

                    g2.drawPolygon(xPoints, yPoints, 4);
                    g2.drawString("a: " + param1, x + bottomBase/2 - 10, y - 10);
                    g2.drawString("b: " + param2, x + bottomBase/2 - 10, y + trapHeight + 20);

                    // 绘制高度线
                    int midX = x + bottomBase/2;
                    g2.setColor(Color.GRAY);
                    g2.drawLine(midX, y, midX, y + trapHeight);
                    g2.setColor(Color.BLUE);
                    g2.drawString("height: " + param3, midX - 40, y + trapHeight/2);
                }
            }

            g2.setColor(Color.RED);
            g2.drawString("Formula + Answer: " + getFormulaExplanation(), padding, height - 10);
        }
    }

    private String getFormulaExplanation() {
        return switch (currentShape) {
            case "Rectangle" -> param1 + " * " + param2 + " = " + correctAnswer;
            case "Parallelogram" -> param1 + " * " + param2 + " = " + correctAnswer;
            case "Triangle" -> param1 + " * " + param2 + " / 2 = " + correctAnswer;
            case "Trapezium" -> "(" + param1 + " + " + param2 + ") * " + param3 + " / 2 = " + correctAnswer;
            default -> "Unknown";
        };
    }
}