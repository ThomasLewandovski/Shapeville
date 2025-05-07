package com.shapeville.tasks;

import com.shapeville.data.ShapeData;
import com.shapeville.data.ShapeData.ShapeItem;
import com.shapeville.manager.ScoreManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Task1ShapeIdentification {
    private final int[] is_played_task1;
    private final String[] encouragements = {
        "🎉 Well done!",
        "👏 Excellent!",
        "🌟 You're a shape master!",
        "👍 Great job!",
        "💡 Smart thinking!",
        "🔥 Keep it up!"
    };
    private ScoreManager scoreManager;
    public Runnable onReturnHome;
    public JButton nextButton;
    public JButton goHomeButton;
    public JPanel task1;
    public JLabel img;
    public JLabel output;
    public JTextField input;
    public JLabel score;
    // 修改变量名首字母为小写，遵循 Java 命名规范
    private KeyAdapter keyAdapter;

    private List<ShapeItem> currentShapes;
    private ShapeItem currentShape;
    private int currentIndex = 0;
    private int attempt = 1;
    private boolean isAdvanced = false;

    private String getRandomEncouragement() {
        int idx = (int) (Math.random() * encouragements.length);
        return encouragements[idx];
    }

    public Task1ShapeIdentification(ScoreManager scoreManager, int[] is_played_task1) {
        this.scoreManager = scoreManager;
        this.task1 = new JPanel(null);
        this.img = new JLabel();
        this.output = new JLabel();
        this.input = new JTextField();
        this.score = new JLabel();
        this.is_played_task1 = is_played_task1;
        score.setText("points: 0");
        score.setBounds(10, 0, 200, 40);
        img.setBounds(100, 0, 400, 400);
        input.setBounds(100, 450, 400, 20);
        output.setBounds(100, 370, 800, 80);
        task1.add(score);
        task1.add(img);
        task1.add(output);
        task1.add(input);

        //加入返回主界面按钮
        goHomeButton = new JButton("🏠 Return to Home");
        goHomeButton.setBounds(100, 500, 200, 30);
        goHomeButton.setVisible(true);
        task1.add(goHomeButton);

        goHomeButton.addActionListener(e -> {
            if (onReturnHome != null) onReturnHome.run();
        });

        nextButton = new JButton("Next Question ▶");
        nextButton.setBounds(320, 500, 200, 30);
        nextButton.setVisible(false);
        task1.add(nextButton);

        nextButton.addActionListener(e -> {
            currentIndex++;  // ✅ 先推进到下一题
            attempt = 1;     // ✅ 重置尝试次数
            if (currentIndex < currentShapes.size()) {
                currentShape = currentShapes.get(currentIndex);
                showShape();
                input.setEnabled(true);
                input.requestFocus();
                nextButton.setVisible(false);
            } else {
                finishTask();
            }
        });

        // 只添加一次按键监听器
        keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (currentShapes == null) {
                        handleInput();
                    } else {
                        handleShapeAnswer();
                    }
                }
            }
        };
        input.addKeyListener(keyAdapter);
    }


    public void start() {
        // 添加 <br> 标签，使文本换行显示
        output.setText("<html>📐 Task 1: Identify 2D / 3D Shapes<br>" +
                "1. 2D Shapes (Basic Level)" +
                "2. 3D Shapes (Advanced Level)</html>");
    }

    private void handleInput() {
        String choice = input.getText().trim();
        switch (choice) {
            case "2D":
                if (is_played_task1[0] == 0) {
                    is_played_task1[0] = 1;
                    startSubtask("2D");
                } else {
                    JOptionPane.showMessageDialog(null, "<html>you have played this module,<br>" +
                            "please try other modules</html>", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case "3D":
                if (is_played_task1[1] == 0) {
                    is_played_task1[1] = 1;
                    startSubtask("3D");
                } else {
                    JOptionPane.showMessageDialog(null, "<html>you have played this module,<br>" +
                            "please try other modules</html>", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            default:
                System.out.println("Invalid choice. Returning to home.");
        }
        input.setText("");
    }

    private void startSubtask(String type) {
        currentShapes = new ArrayList<>((type.equals("2D") ? ShapeData.getAll2DShapes() : ShapeData.getAll3DShapes()));
        isAdvanced = type.equals("3D");
        Collections.shuffle(currentShapes);
        currentIndex = 0;
        attempt = 1;
        // 限制每个子任务只有三个题目
        int maxQuestions = 4;
        if (currentShapes.size() > maxQuestions) {
            currentShapes = currentShapes.subList(0, maxQuestions);
        }
        if (currentIndex < currentShapes.size()) {
            currentShape = currentShapes.get(currentIndex);
            showShape();
        }
    }

    private void showShape() {
        String imgPath = "images/" + currentShape.getImageFilename();
        URL imageUrl = getClass().getClassLoader().getResource(imgPath);
        if (imageUrl != null) {
            ImageIcon imageIcon = new ImageIcon(imageUrl);
            img.setIcon(imageIcon);
        } else {
            System.out.println("Image not found: " + imgPath);
        }
        output.setText("<html>What is the name of this shape?<br>" +
                "Your answer: ");
    }

    private void handleShapeAnswer() {
        String answer = input.getText().trim();
        input.setText("");
        if (checkAnswer(answer, currentShape.getName())) {
            int points = calculatePoints();
            scoreManager.addScore(points);
            score.setText("points: " + scoreManager.getScore());
            System.out.println("✅ Correct! You earned " + points + " points.");
        
            // ✅ 显示鼓励语句
            String encouragement = getRandomEncouragement();
            output.setText("<html>✅ Correct! +" + points + " points.<br>" + encouragement + "</html>");
        
            input.setEnabled(false);
            nextButton.setVisible(true);
    
            // ❌ 原本是立即切到下一题的逻辑，现注释掉
            // currentIndex++;
            // attempt = 1;
            // if (currentIndex < currentShapes.size()) {
            //     currentShape = currentShapes.get(currentIndex);
            //     showShape();
            // } else {
            //     output.setText("\n🎉 You've completed the " + (isAdvanced ? "3D" : "2D") + " shape task!");
            // }
            
            // ✅ 显示鼓励语句（如需的话你可以加上）
            // output.setText("✅ Correct! +X points. Great job!");
        } else {
            attempt++;
            if (attempt <= 3) {
                output.setText("❌ Incorrect. Try again.");
            } else {
                output.setText("⚠️ The correct answer was: " + currentShape.getName());
    
                // ✅ 禁用输入，显示“下一题”按钮
                input.setEnabled(false);
                nextButton.setVisible(true);
    
                // ❌ 原本是立即切到下一题的逻辑，现注释掉
                // currentIndex++;
                // attempt = 1;
                // if (currentIndex < currentShapes.size()) {
                //     currentShape = currentShapes.get(currentIndex);
                //     showShape();
                // } else {
                //     output.setText("\n🎉 You've completed the " + (isAdvanced ? "3D" : "2D") + " shape task!");
                //     input.removeKeyListener(keyAdapter);
                //     goHomeButton.setVisible(true);
                // }
            }
        }
    }

    private int calculatePoints() {
        if (attempt == 1) {
            return isAdvanced ? 6 : 3;
        } else if (attempt == 2) {
            return isAdvanced ? 4 : 2;
        } else if (attempt == 3) {
            return isAdvanced ? 2 : 1;
        }
        return 0;
    }

    private boolean checkAnswer(String input, String correct) {
        return input.equalsIgnoreCase(correct);
    }

    public int[] getIs_played_task1() {
        return is_played_task1;
    }

    // private void finishTask() {
    //     output.setText("<html>🎉 You've completed the " + (isAdvanced ? "3D" : "2D") + " shape task!<br>Click 'Return to Home' to go back.</html>");
    //     input.setEnabled(false);
    //     input.removeKeyListener(keyAdapter);
    //     nextButton.setVisible(false);       // 隐藏下一题按钮
    //     goHomeButton.setVisible(true);      // 显示返回主页面按钮
    // }
    private void finishTask() {
        int finalScore = scoreManager.getScore();  // ✅ 获取当前总得分
        output.setText("<html>🎉 You've completed the " + (isAdvanced ? "3D" : "2D") + " shape task!<br>" +
                "🏆 Your total score: <b>" + finalScore + "</b> points.<br>" +
                "Click 'Return to Home' to go back.</html>");
        
        input.setEnabled(false);
        input.removeKeyListener(keyAdapter);
        nextButton.setVisible(false);       // 隐藏下一题按钮
        goHomeButton.setVisible(true);      // 显示返回主页面按钮
    }

}