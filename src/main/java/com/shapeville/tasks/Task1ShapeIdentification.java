package com.shapeville.tasks;

import com.shapeville.data.ShapeData;
import com.shapeville.data.ShapeData.ShapeItem;
import com.shapeville.manager.ScoreManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Task1ShapeIdentification {
    private ScoreManager scoreManager;
    public Runnable onReturnHome;
    public JButton goHomeButton;
    public JPanel task1;
    public JLabel img;
    public JLabel output;
    public JTextField input;
    public int result =0;
    public JLabel score;

    private List<ShapeItem> currentShapes;
    private List<ShapeData.Shape2D> Shapes2D;
    private List<ShapeData.Shape3D> Shapes3D;
    private ShapeItem currentShape;
    private int currentIndex = 0;
    private int attempt = 1;
    private boolean isAdvanced = false;

    public Task1ShapeIdentification(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        this.task1 = new JPanel(null);
        this.img = new JLabel();
        this.output = new JLabel();
        this.input = new JTextField();
        this.Shapes2D = new ArrayList<>(ShapeData.getAll2DShapes());
        this.Shapes3D = new ArrayList<>(ShapeData.getAll3DShapes());
        this.currentShapes = new ArrayList<>();
        this.currentShapes.addAll(Shapes2D);
        this.currentShapes.addAll(Shapes3D);
        this.score = new JLabel();
        score.setText("points: 0");
        score.setBounds(10, 10, 200, 40);
        img.setBounds(100, 10, 400, 400);
        input.setBounds(100, 450, 400, 20);
        output.setBounds(100, 420, 400, 20);
        task1.add(score);
        task1.add(img);
        task1.add(output);
        task1.add(input);

        //加入返回主界面按钮
        goHomeButton = new JButton("🏠 Return to Home");
        goHomeButton.setBounds(100, 500, 200, 30);
        goHomeButton.setVisible(false);
        task1.add(goHomeButton);

        goHomeButton.addActionListener(e -> {
            if (onReturnHome != null) onReturnHome.run();
        });
    }

    public void start() {
        runSubtask();
    }

    private void runSubtask() {
        Collections.shuffle(currentShapes);
        currentIndex = 0;
        showNextShape();
    }

    private void showNextShape() {
        if (currentIndex >= currentShapes.size()) {
            output.setText("🎉 Task Complete!");
            input.setVisible(false);
            goHomeButton.setVisible(true); // ✅ 显示按钮
            resetInputListeners();
            return;
        }
        currentShape = currentShapes.get(currentIndex++);
        if(currentShape instanceof ShapeData.Shape3D) isAdvanced = true;
        else isAdvanced = false;
        attempt = 1;
        displayShape(currentShape);
    }

    private void displayShape(ShapeItem shape) {
        output.setText("🔍 What is the name of this shape?");
        input.setText("");

        // Load image from resources
        try {
            URL imageUrl = getClass().getClassLoader().getResource("images/" + shape.getImageFilename());
            if (imageUrl != null) {
                img.setIcon(new ImageIcon(imageUrl));
            } else {
                img.setText("[Image not found: " + shape.getImageFilename() + "]");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        resetInputListeners();
        input.addActionListener(e -> handleAnswer());
    }

    private void handleAnswer() {
        String answer = input.getText().trim();
        if (checkAnswer(answer, currentShape.getName())) {
            int points = switch (attempt) {
                case 1 -> isAdvanced ? 6 : 3;
                case 2 -> isAdvanced ? 4 : 2;
                case 3 -> isAdvanced ? 2 : 1;
                default -> 0;
            };
            scoreManager.addScore(points);
            score.setText("points:" + (result+points));
            output.setText("✅ Correct! +" + points + " points.");
            showNextShape();
        } else {
            attempt++;
            if (attempt > 3) {
                output.setText("❌ The correct answer was: " + currentShape.getName());
                showNextShape();
            } else {
                output.setText("❌ Incorrect, try again.");
                input.setText("");
            }
        }

    }

    private void resetInputListeners() {
        for (ActionListener al : input.getActionListeners()) {
            input.removeActionListener(al);
        }
    }

    private boolean checkAnswer(String input, String correct) {
        return input.equalsIgnoreCase(correct);
    }
}