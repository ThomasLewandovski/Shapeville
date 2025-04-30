package com.shapeville.tasks;

import com.shapeville.manager.ScoreManager;
import com.shapeville.data.AngleData;

import java.util.Scanner;

public class Task2AngleIdentification {
    private ScoreManager scoreManager;
    private Scanner scanner;

    public Task2AngleIdentification(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("\n📏 Task 2: Identify Angle Types");

        for (int i = 0; i < 4; i++) {  // 识别4次
            playOneRound();
        }

        System.out.println("\n🎉 You've completed the Angle Identification task!");
    }

    private void playOneRound() {
        int angle = AngleData.getRandomAngle();

        System.out.println("\nAn angle is drawn with " + angle + "°.");
        System.out.println("Identify the type (Acute / Right / Obtuse / Reflex / Straight / Full)");

        int maxAttempts = 3;
        String correctType = AngleData.classifyAngle(angle);

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            System.out.print("Your answer: ");
            String answer = scanner.nextLine().trim();

            if (checkAnswer(answer, correctType)) {
                int points;
                if (attempt == 1) {
                    points = 3;
                } else if (attempt == 2) {
                    points = 2;
                } else if (attempt == 3) {
                    points = 1;
                } else {
                    points = 0;
                }
                scoreManager.addScore(points);
                return;
            } else {
                System.out.println("❌ Incorrect. Try again.");
            }
        }

        // 3次失败
        System.out.println("⚠️ The correct answer was: " + correctType);
    }

    private boolean checkAnswer(String input, String correct) {
        return input.equalsIgnoreCase(correct);
    }
}
