package com.shapeville.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;

import com.shapeville.data.ShapeData;
import com.shapeville.data.ShapeData.ShapeItem;
import com.shapeville.manager.ScoreManager;

import javax.swing.*;

public class Task1ShapeIdentification {
    private ScoreManager scoreManager;
    private Scanner scanner;

    public Task1ShapeIdentification(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        JPanel task1 = new JPanel(null);
        JLabel


        System.out.println("\n📐 Task 1: Identify 2D / 3D Shapes");
        System.out.println("1. 2D Shapes (Basic Level)");
        System.out.println("2. 3D Shapes (Advanced Level)");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // 清掉换行符

        switch (choice) {
            case 1:
                runSubtask("2D");
                break;
            case 2:
                runSubtask("3D");
                break;
            default:
                System.out.println("Invalid choice. Returning to home.");
        }
    }

    private void runSubtask(String type) {
        List<ShapeItem> shapes = new ArrayList<>((type.equals("2D") ? ShapeData.getAll2DShapes() : ShapeData.getAll3DShapes()));
        boolean isAdvanced = type.equals("3D");

        // 打乱顺序
        Collections.shuffle(shapes);

        for (ShapeItem shape : shapes) {
            askShape(shape, isAdvanced);
        }

        System.out.println("\n🎉 You've completed the " + type + " shape task!");
    }

    private void askShape(ShapeItem shape, boolean advancedLevel) {
        int maxAttempts = 3;
        int points = 0;
    
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            System.out.println("\nWhat is the name of this shape?");
            System.out.println("[Image: " + shape.getImageFilename() + "]");
            System.out.print("Your answer: ");
            String answer = scanner.nextLine().trim();
    
            if (checkAnswer(answer, shape.getName())) {
                // 计算分数（按任务书规则）
                if (attempt == 1) {
                    points = advancedLevel ? 6 : 3;
                } else if (attempt == 2) {
                    points = advancedLevel ? 4 : 2;
                } else if (attempt == 3) {
                    points = advancedLevel ? 2 : 1;
                } else {
                    points = 0;
                }
                scoreManager.addScore(points);
                return;
            } else {
                System.out.println("❌ Incorrect. Try again.");
            }
        }
    
        // 3次都错了
        System.out.println("⚠️ The correct answer was: " + shape.getName());
    }

    private boolean checkAnswer(String input, String correct) {
        return input.equalsIgnoreCase(correct);
    }
}
