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
    public ScoreManager scoreManager;
    public JLabel score;
    public Set<String> CompletedShapes;

    private JLabel questionLabel;
    private JTextField inputField;
    private JButton submitButton, homeButton;
    private JComboBox<String> shapeSelector;
    private Timer countdownTimer;
    private JLabel timerLabel;
    private DrawingPanel drawingPanel;

    public String currentShape;
    public int param1;
    public int param2;
    public int param3;
    public int correctAnswer;
    public int attemptsLeft;
    public int timeRemaining;
    public Runnable onComplete;

    public Task3VolumeSurfaceCalculator(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        this.CompletedShapes = new HashSet<>();

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
        drawingPanel.setPreferredSize(new Dimension(400, 300));
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

        start();
    }

    public void start() {
        currentShape = (String) shapeSelector.getSelectedItem();
        if (CompletedShapes.contains(currentShape)) {
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
                CompletedShapes.add(currentShape);
                checkAllShapesCompleted(); // 新增完成检测
                questionLabel.setText("✅ Great job! +" + score + " points");
            } else {
                attemptsLeft--;
                if (attemptsLeft <= 0) {
                    countdownTimer.stop();
                    CompletedShapes.add(currentShape);
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
        checkAllShapesCompleted(); // 新增完成检测
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
                    // 👉 原始逻辑：基于参数决定比例
                    double baseScale = 15.0;  // 默认每单位显示 15px（可调节）

                    int rectWidth = (int) (param1 * baseScale);
                    int rectHeight = (int) (param2 * baseScale);

                    // 🛑 溢出检查：如果长宽有一项超出画板最大尺寸，缩放
                    double overflowScale = Math.min(
                        shapeWidth / (double) rectWidth,
                        shapeHeight *0.7/ (double) rectHeight
                    );
                    if (overflowScale < 1.0) {
                        rectWidth = (int) (rectWidth * overflowScale);
                        rectHeight = (int) (rectHeight * overflowScale);
                    }

                    int x = (width - rectWidth) / 2;
                    int y = (height - rectHeight - 30) / 2;

                    g2.drawRect(x, y, rectWidth, rectHeight);
                    g2.setFont(new Font("Arial", Font.PLAIN, 12));
                    g2.drawString("length: " + param1, x + rectWidth / 2 - 20, y - 5);
                    g2.drawString("width: " + param2, x + rectWidth + 5, y + rectHeight / 2);
                }
                case "Parallelogram" -> {
                    // 预留底部文字空间
                    int reservedBottomSpace = 40;
                    int availableHeight = height - reservedBottomSpace;

                    // 使用基础比例（像素/单位）
                    double baseScale = 15.0;
                    int rawWidth = (int) (param1 * baseScale);
                    int rawHeight = (int) (param2 * baseScale);
                    int rawSkew = Math.max(rawWidth / 5, 10); // 倾斜宽度

                    // 判断是否需要缩放
                    double overflowScale = Math.min(
                        shapeWidth / (double) (rawWidth + rawSkew), // 宽度包括倾斜偏移
                        availableHeight*0.7 / (double) rawHeight
                    );

                    if (overflowScale < 1.0) {
                        rawWidth = (int) (rawWidth * overflowScale);
                        rawHeight = (int) (rawHeight * overflowScale);
                        rawSkew = (int) (rawSkew * overflowScale);
                    }

                    int x = (width - rawWidth) / 2;
                    int y = (availableHeight - rawHeight) / 2;

                    int[] xPoints = {x, x + rawSkew, x + rawWidth, x + rawWidth - rawSkew};
                    int[] yPoints = {y + rawHeight, y, y, y + rawHeight};

                    g2.setColor(Color.BLUE);
                    g2.drawPolygon(xPoints, yPoints, 4);
                    
                    // 🔵 绘制高度虚线（从左上角垂直到底边）
                    g2.setColor(Color.GRAY);
                    Stroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
                    g2.setStroke(dashed);
                    g2.drawLine(x + rawSkew, y, x + rawSkew, y + rawHeight);
                    g2.setStroke(new BasicStroke(1.2f)); // 恢复实线
                    

                    // 标签绘制
                    g2.setColor(Color.BLUE);
                    g2.setFont(new Font("Arial", Font.PLAIN, 12));
                    g2.drawString("base: " + param1, x + rawWidth / 2 - 15, y - 5);
                    g2.drawString("height: " + param2, x - 40, y + rawHeight / 2);
                }
                case "Triangle" -> {
                    // 计算比例缩放
                    double scale = Math.min(shapeWidth / (double)param1, shapeHeight / (double)param2);
                    int baseLength = (int)(param1 * scale);
                    int triHeight = (int)(param2 * scale);

                    // 计算底边起点和三角形顶点坐标（居中显示）
                    int xBaseLeft = (width - baseLength) / 2;
                    int xBaseRight = xBaseLeft + baseLength;
                    int yBase = (height + triHeight) / 2;
                    int xTop = (xBaseLeft + xBaseRight) / 2;
                    int yTop = yBase - triHeight;

                    // 绘制三角形
                    int[] xPoints = {xBaseLeft, xBaseRight, xTop};
                    int[] yPoints = {yBase, yBase, yTop};
                    g2.setColor(Color.BLUE);
                    g2.drawPolygon(xPoints, yPoints, 3);

                    // 🔵 绘制垂直高度虚线（从顶点到底边中点）
                    g2.setColor(Color.GRAY);
                    Stroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
                    g2.setStroke(dashed);
                    g2.drawLine(xTop, yTop, xTop, yBase);

                    // 还原为实线画笔
                    g2.setStroke(new BasicStroke(1.2f));

                    // 🟦 添加标签文字
                    g2.setColor(Color.BLUE);
                    g2.drawString("base: " + param1, xBaseLeft + baseLength / 2 - 20, yBase + 15);
                    g2.drawString("height: " + param2, xTop + 5, (yTop + yBase) / 2);
                }
                case "Trapezium" -> {
                    int reservedBottom = 40;
                    int availableHeight = height - reservedBottom;

                    double baseScale = 15.0;
                    int aLen = (int)(param1 * baseScale); // 上底
                    int bLen = (int)(param2 * baseScale); // 下底
                    int hLen = (int)(param3 * baseScale);

                    // 🔁 判断是否溢出 → 缩放
                    double overflowScale = Math.min(
                        shapeWidth / (double)bLen,
                        availableHeight*0.7 / (double)hLen
                    );
                    if (overflowScale < 1.0) {
                        aLen = (int)(aLen * overflowScale);
                        bLen = (int)(bLen * overflowScale);
                        hLen = (int)(hLen * overflowScale);
                    }

                    int x = (width - bLen) / 2;
                    int y = (availableHeight - hLen) / 2;

                    // 梯形坐标（等腰梯形）
                    int[] xPoints = {
                        x + (bLen - aLen) / 2,        // 左上
                        x + (bLen - aLen) / 2 + aLen, // 右上
                        x + bLen,                     // 右下
                        x                             // 左下
                    };
                    int[] yPoints = {y, y, y + hLen, y + hLen};

                    g2.setColor(Color.BLUE);
                    g2.drawPolygon(xPoints, yPoints, 4);

                    // 标签 a b
                    g2.setFont(new Font("Arial", Font.PLAIN, 12));
                    g2.drawString("a: " + param1, x + bLen / 2 - 10, y - 10);
                    g2.drawString("b: " + param2, x + bLen / 2 - 10, y + hLen + 20);

                    // 高度线：虚线
                    int midX = x + bLen / 2;
                    g2.setColor(Color.GRAY);
                    Stroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
                    g2.setStroke(dashed);
                    g2.drawLine(midX, y, midX, y + hLen);

                    // 恢复实线，绘制高度标注
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.setColor(Color.BLUE);
                    g2.drawString("height: " + param3, midX - 40, y + hLen / 2);
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

    private void checkAllShapesCompleted() {
        if (CompletedShapes.size() == 4 && onComplete != null) {
            onComplete.run();
        }
    }
}