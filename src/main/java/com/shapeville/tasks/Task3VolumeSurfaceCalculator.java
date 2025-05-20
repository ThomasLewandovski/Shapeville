package com.shapeville.tasks;

import com.shapeville.manager.ScoreManager;

import javax.swing.*;
import java.awt.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.*;

public class Task3VolumeSurfaceCalculator {
    public JPanel task3;
    public Runnable onReturnHome;
    public ScoreManager scoreManager;
    public JLabel scorelable;
    public int scores = 0;
    public Set<String> CompletedShapes;
    private JPanel centerPanel;
    private JPanel homeButtonPanel;

    private JLabel questionLabel;
    private JTextField inputField;
    private JButton submitButton, homeButton;
    private JComboBox<String> shapeSelector;
    private Timer countdownTimer;
    private JLabel timerLabel;
    private DrawingPanel drawingPanel;
    private String currentQuestionText = "";
    private JLabel mascotSpeech;
    private JLabel mascotImageLabel;
    private JPanel mascotPanel;
    private JPanel mascotWrapper;
    private ImageIcon pikaIcon;

    public String currentShape;
    public int param1;
    public int param2;
    public int param3;
    public int correctAnswer;
    public int attemptsLeft;
    public int timeRemaining;
    public Runnable onComplete;

    public Task3VolumeSurfaceCalculator(ScoreManager scoreManager) {
        Color creamyYellow = new Color(255, 242, 198);
        this.scoreManager = scoreManager;
        this.CompletedShapes = new HashSet<>();

        // 使用null布局
        task3 = new JPanel(null);
        task3.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        task3.setBackground(creamyYellow);

        // 顶部面板 - 包含分数和标题
        JPanel topPanel = new JPanel(null);
        topPanel.setBackground(creamyYellow);
        scorelable = new JLabel("Score: " + scoreManager.getScore());
        scorelable.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(scorelable);

        questionLabel = new JLabel("Choose a shape:");
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        questionLabel.setVerticalAlignment(JLabel.TOP);
        topPanel.add(questionLabel);

        task3.add(topPanel);

        // 中间面板 - 包含形状选择和输入区域
        centerPanel = new JPanel(null);
        centerPanel.setBackground(creamyYellow);

        shapeSelector = new JComboBox<>(new String[]{"Rectangle", "Parallelogram", "Triangle", "Trapezium"});
        shapeSelector.setFont(new Font("Arial", Font.PLAIN, 14));
        shapeSelector.setBackground(new Color(255, 242, 198));
        centerPanel.add(shapeSelector);

        JButton generateButton = new JButton("Generate Problem");
        generateButton.setFont(new Font("Arial", Font.PLAIN, 14));
        generateButton.setBackground(new Color(255, 242, 198));
        centerPanel.add(generateButton);

        timerLabel = new JLabel("Time left: 180s");
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        timerLabel.setBackground(new Color(255, 242, 198));
        centerPanel.add(timerLabel);

        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputField.setBackground(new Color(255, 242, 198));
        centerPanel.add(inputField);
        inputField.setEnabled(false);

        submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        submitButton.setBackground(new Color(255, 242, 198));
        centerPanel.add(submitButton);
        submitButton.setEnabled(false);

        task3.add(centerPanel);

        // 底部面板 - 包含绘图区域和返回按钮
        JPanel bottomPanel = new JPanel(null);
        bottomPanel.setBackground(creamyYellow);

        drawingPanel = new DrawingPanel();
        drawingPanel.setBackground(Color.WHITE);
        bottomPanel.add(drawingPanel);

        homeButton = new JButton("Home");
        homeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        homeButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        homeButtonPanel = new JPanel(null);
        homeButtonPanel.setBackground(creamyYellow);
        homeButtonPanel.add(homeButton);
        bottomPanel.add(homeButtonPanel);

        // 皮卡丘区域
        mascotWrapper = new JPanel(null);
        mascotWrapper.setOpaque(false);

        mascotPanel = new JPanel(null);
        mascotPanel.setOpaque(false);

        // 气泡提示
        mascotSpeech = new JLabel("<html><div style='padding:8px; background:#fff8dc; border:1px solid #ccc; border-radius:10px;'>Choose a shape to start the challenge!⚡</div></html>");
        mascotSpeech.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
        mascotPanel.add(mascotSpeech);

        // 加载Pikachu图
        mascotImageLabel = new JLabel("⚡");
        try {
            pikaIcon = new ImageIcon(getClass().getClassLoader().getResource("images/Pikachu.png"));
            if (pikaIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                Image scaled = pikaIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                mascotImageLabel = new JLabel(new ImageIcon(scaled));
            }
        } catch (Exception ex) {
            // 使用备用图标
        }
        mascotPanel.add(mascotImageLabel);

        mascotWrapper.add(mascotPanel);
        bottomPanel.add(mascotWrapper);

        task3.add(bottomPanel);

        // 按钮事件处理
        generateButton.addActionListener(e -> start());
        submitButton.addActionListener(e -> checkAnswer());
        homeButton.addActionListener(e -> {
            if (countdownTimer != null) countdownTimer.stop();
            if (onReturnHome != null) onReturnHome.run();
        });

        // 添加窗口尺寸监听器
        task3.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = task3.getSize();
                setComponentPositions(size.width, size.height);
            }
        });

        // 初始化布局
        setComponentPositions(800, 600);
    }

    private void setComponentPositions(int width, int height) {
        // 百分比布局参数
        double topPanelHeight = 0.1;
        double centerPanelHeight = 0.3;
        double bottomPanelHeight = 0.6;

        // 顶部面板
        int topPanelY = 0;
        int topPanelHeightPx = (int) (height * topPanelHeight);
        task3.getComponent(0).setBounds(0, topPanelY, width, topPanelHeightPx);

        // 分数标签
        scorelable.setBounds(10, 0, width - 20, 30);
        questionLabel.setBounds(10, topPanelHeightPx/2, width - 20, 40);

        // 中间面板
        int centerPanelY = topPanelHeightPx;
        int centerPanelHeightPx = (int) (height * centerPanelHeight);
        task3.getComponent(1).setBounds(0, centerPanelY, width, centerPanelHeightPx);

        // 形状选择框
        double shapeSelectorWidth = 0.6;
        double shapeSelectorHeight = 0.2;
        shapeSelector.setBounds(20, 20,
                (int)(width*shapeSelectorWidth), (int)(centerPanelHeightPx*shapeSelectorHeight));

        // 生成按钮
        JButton generateButton = (JButton) centerPanel.getComponent(1);
        generateButton.setBounds((int)(width*shapeSelectorWidth) + 20,
                20, width - shapeSelector.getX() - shapeSelector.getWidth() - 40,  (int)(centerPanelHeightPx*shapeSelectorHeight));

        // 计时器标签
        timerLabel.setBounds(20, shapeSelector.getY() + shapeSelector.getHeight() + 20,
                width - 40, 30);

        // 输入框和提交按钮
        double inputFieldWidth = 0.6;
        double inputFieldHeight = 0.2;
        inputField.setBounds(20, timerLabel.getY() + timerLabel.getHeight() + 20,
                (int)(width*inputFieldWidth), (int)(centerPanelHeightPx*inputFieldHeight));
        submitButton.setBounds(inputField.getWidth() + 20,
                inputField.getY(), width - inputField.getX() - inputField.getWidth() - 40, (int)(centerPanelHeightPx*inputFieldHeight));

        // 底部面板
        int bottomPanelY = centerPanelY + centerPanelHeightPx;
        int bottomPanelHeightPx = height - bottomPanelY;
        task3.getComponent(2).setBounds(0, bottomPanelY, width, bottomPanelHeightPx);

        // 绘图面板
        double drawingPanelRatio = 0.9;
        drawingPanel.setSize((int)(width*0.6), (int)(bottomPanelHeightPx*drawingPanelRatio));
        drawingPanel.setLocation((width - drawingPanel.getWidth())/30,
                (bottomPanelHeightPx - drawingPanel.getHeight())/2 - 40);

        // 返回按钮面板
        homeButtonPanel.setBounds(0, bottomPanelHeightPx - 50, width, 50);
        homeButton.setBounds(width - 100, 10, 80, 30);

        // 皮卡丘面板
        int mascotWidth = (int)(width * 0.3);
        int mascotHeight = (int)(bottomPanelHeightPx * 0.8);
        mascotWrapper.setBounds(width - mascotWidth - 20, bottomPanelHeightPx - mascotHeight - 20,
                mascotWidth, mascotHeight);
        mascotPanel.setBounds(0, 0, mascotWidth, mascotHeight);

        mascotSpeech.setBounds((int)(mascotWidth*0.1), (int)(mascotHeight*0.2), (int)(mascotWidth*0.5), (int)(mascotHeight*0.2));
        mascotImageLabel.setBounds((int)(mascotWidth*0.2), (int)(mascotHeight*0.4), (int)(mascotWidth*0.75), (int)(mascotHeight*0.5));

    }


    public void start() {
        currentShape = (String) shapeSelector.getSelectedItem();
        if (CompletedShapes.contains(currentShape)) {
            questionLabel.setText("You already completed " + currentShape);
            return;
        }

        Random rand = new Random();
        param1 = rand.nextInt(20) + 1;
        param2 = rand.nextInt(20) + 1;
        param3 = rand.nextInt(20) + 1;
        attemptsLeft = 3;
        timeRemaining = 180;

        switch (currentShape) {
            case "Rectangle" -> {
                correctAnswer = param1 * param2;
                currentQuestionText = "📐 Rectangle: length = " + param1 + ", width = " + param2 + ". Calculate area:";
            }
            case "Parallelogram" -> {
                correctAnswer = param1 * param2;
                currentQuestionText = " Parallelogram: base = " + param1 + ", height = " + param2 + ". Calculate area:";
            }
            case "Triangle" -> {
                correctAnswer = (param1 * param2) / 2;
                currentQuestionText = " Triangle: base = " + param1 + ", height = " + param2 + ". Calculate area:";
            }
            case "Trapezium" -> {
                correctAnswer = ((param1 + param2) * param3) / 2;
                currentQuestionText = " Trapezium: a = " + param1 + ", b = " + param2 + ", height = " + param3 + ". Calculate area:";
            }
        }
        questionLabel.setText(currentQuestionText);

        inputField.setText("");
        inputField.setEnabled(true);
        submitButton.setEnabled(true);
        if (countdownTimer != null) countdownTimer.stop();
        countdownTimer = new Timer(1000, e -> {
            timeRemaining--;
            timerLabel.setText("Time left: " + timeRemaining + "s");
            if (timeRemaining <= 0) {
                ((Timer) e.getSource()).stop();
                attemptsLeft = 0;
                submitButton.setEnabled(false);
                CompletedShapes.add(currentShape);
                questionLabel.setText("<html>" + currentQuestionText + "<br> Time's up! The correct answer is shown below.</html>");
                showExplanation();
            }
        });
        countdownTimer.start();
        drawingPanel.repaint();
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
                scores += score;
                CompletedShapes.add(currentShape);
                checkAllShapesCompleted();
                questionLabel.setText("<html> Great job! +" + score + " points<br>👉 Please select a new shape and click Generate Problem to continue.</html>");
                submitButton.setEnabled(false);
                attemptsLeft = 0;
                showExplanation();
            } else {
                attemptsLeft--;
                if (attemptsLeft <= 0) {
                    questionLabel.setText("<html>" + currentQuestionText + "<br> Incorrect. Attempts left: 0</html>");
                    countdownTimer.stop();
                    CompletedShapes.add(currentShape);
                    submitButton.setEnabled(false);
                    showExplanation();
                } else {
                    questionLabel.setText("<html>" + currentQuestionText + "<br> Incorrect. come on！！ try again！ Attempts left: " + attemptsLeft + "</html>");
                }
            }
        } catch (Exception e) {
            questionLabel.setText("Please enter a valid number");
        }
        scorelable.setText("Score: " + scores);
    }

    private void showExplanation() {
        String formula = switch (currentShape) {
            case "Rectangle" -> "Area = length × width = " + param1 + " × " + param2 + " = " + correctAnswer;
            case "Parallelogram" -> "Area = base × height = " + param1 + " × " + param2 + " = " + correctAnswer;
            case "Triangle" -> "Area = base × height / 2 = " + param1 + " × " + param2 + " / 2 = " + correctAnswer;
            case "Trapezium" -> "Area = (a + b) × height / 2 = (" + param1 + " + " + param2 + ") × " + param3 + " / 2 = " + correctAnswer;
            default -> "Unknown shape.";
        };
        checkAllShapesCompleted();
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

            int panelWidth = getWidth();
            int panelHeight = getHeight();
            int padding = panelWidth / 20;
            int shapeWidth = panelWidth - 2 * padding;
            int shapeHeight = panelHeight - 60;

            g2.setColor(Color.BLUE);

            switch (currentShape) {
                case "Rectangle" -> {
                    double baseScale = Math.min(shapeWidth / (double) param1, shapeHeight / (double) param2) / 2;

                    int rectWidth = (int) (param1 * baseScale);
                    int rectHeight = (int) (param2 * baseScale);

                    int x = (panelWidth - rectWidth) / 2;
                    int y = (panelHeight - rectHeight - 30) / 2;

                    g2.drawRect(x, y, rectWidth, rectHeight);
                    g2.setFont(new Font("Arial", Font.PLAIN, 12));
                    g2.drawString("length: " + param1, x + rectWidth / 2 - 20, y - 5);
                    g2.drawString("width: " + param2, x + rectWidth + 5, y + rectHeight / 2);
                }
                case "Parallelogram" -> {
                    int reservedBottomSpace = 40;
                    int availableHeight = panelHeight - reservedBottomSpace;

                    double baseScale = Math.min(shapeWidth / (double) param1, availableHeight / (double) param2) / 2;
                    int rawWidth = (int) (param1 * baseScale);
                    int rawHeight = (int) (param2 * baseScale);
                    int rawSkew = Math.max(rawWidth / 5, 10);

                    int x = (panelWidth - rawWidth) / 2;
                    int y = (availableHeight - rawHeight) / 2;

                    int[] xPoints = {x, x + rawSkew, x + rawWidth, x + rawWidth - rawSkew};
                    int[] yPoints = {y + rawHeight, y, y, y + rawHeight};

                    g2.setColor(Color.BLUE);
                    g2.drawPolygon(xPoints, yPoints, 4);

                    int extension = 40;
                    g2.setColor(Color.GRAY);
                    Stroke dashed = new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
                    g2.setStroke(dashed);
                    g2.drawLine(x + rawWidth - rawSkew, y + rawHeight, x + rawWidth + extension, y + rawHeight);
                    g2.drawLine(x + rawWidth, y, x + rawWidth + extension, y);

                    g2.drawLine(x + rawWidth, y, x + rawWidth, y + rawHeight);

                    g2.setStroke(new BasicStroke(1.2f));
                    g2.setColor(Color.BLUE);

                    g2.setFont(new Font("Arial", Font.PLAIN, 12));
                    g2.drawString("base: " + param1, x + rawWidth / 2 - 15, y - 5);
                    g2.drawString("height: " + param2, x + rawWidth + 5, y + rawHeight / 2);
                }

                case "Triangle" -> {
                    double scale = Math.min(shapeWidth / (double) param1, shapeHeight / (double) param2) / 1.5;
                    int baseLength = (int) (param1 * scale);
                    int triHeight = (int) (param2 * scale);

                    int xBaseLeft = (panelWidth - baseLength) / 2;
                    int xBaseRight = xBaseLeft + baseLength;
                    int yBase = (panelHeight + triHeight) / 2;
                    int xTop = (xBaseLeft + xBaseRight) / 2;
                    int yTop = yBase - triHeight;

                    int[] xPoints = {xBaseLeft, xBaseRight, xTop};
                    int[] yPoints = {yBase, yBase, yTop};
                    g2.setColor(Color.BLUE);
                    g2.drawPolygon(xPoints, yPoints, 3);

                    g2.setColor(Color.GRAY);
                    Stroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
                    g2.setStroke(dashed);
                    g2.drawLine(xTop, yTop, xTop, yBase);

                    g2.setStroke(new BasicStroke(1.2f));

                    g2.setColor(Color.BLUE);
                    g2.drawString("base: " + param1, xBaseLeft + baseLength / 2 - 20, yBase + 15);
                    g2.drawString("height: " + param2, xTop + 5, (yTop + yBase) / 2);
                }
                case "Trapezium" -> {
                    int reservedBottom = 40;
                    int availableHeight = panelHeight - reservedBottom;

                    double baseScale = Math.min(shapeWidth / (double) param2, availableHeight / (double) param3) / 2;
                    int aLen = (int) (param1 * baseScale);
                    int bLen = (int) (param2 * baseScale);
                    int hLen = (int) (param3 * baseScale);

                    int x = (panelWidth - bLen) / 2;
                    int y = (availableHeight - hLen) / 2;

                    int[] xPoints = {
                            x + (bLen - aLen) / 2,
                            x + (bLen - aLen) / 2 + aLen,
                            x + bLen,
                            x
                    };
                    int[] yPoints = {y, y, y + hLen, y + hLen};

                    g2.setColor(Color.BLUE);
                    g2.drawPolygon(xPoints, yPoints, 4);

                    g2.setFont(new Font("Arial", Font.PLAIN, 12));
                    g2.drawString("a: " + param1, x + bLen / 2 - 10, y - 10);
                    g2.drawString("b: " + param2, x + bLen / 2 - 10, y + hLen + 20);

                    int midX = x + bLen / 2;
                    g2.setColor(Color.GRAY);
                    Stroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
                    g2.setStroke(dashed);
                    g2.drawLine(midX, y, midX, y + hLen);

                    g2.setStroke(new BasicStroke(1.2f));
                    g2.setColor(Color.BLUE);
                    g2.drawString("height: " + param3, midX - 40, y + hLen / 2);
                }
            }

            g2.setColor(Color.RED);
            g2.drawString("Formula + Answer: " + getFormulaExplanation(), padding, panelHeight - 10);
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