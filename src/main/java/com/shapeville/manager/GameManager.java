package com.shapeville.manager;

import com.shapeville.data.ShapeData;
import com.shapeville.tasks.Task1ShapeIdentification;
import com.shapeville.tasks.Task2AngleIdentification;

import java.util.Scanner;

public class GameManager {
    private ScoreManager scoreManager;
    private Scanner scanner;

    // 这里声明各个任务
    private Task1ShapeIdentification task1;
    private Task2AngleIdentification task2;
    // 后续可以加task3、task4...

    public GameManager() {
        scoreManager = new ScoreManager();
        scanner = new Scanner(System.in);

        // 初始化各个任务
        task1 = new Task1ShapeIdentification(scoreManager);
        task2 = new Task2AngleIdentification(scoreManager);
    }

    public void startGame() {
        System.out.println("Welcome to Shapeville!");
        goHome();
    }

    public void goHome() {
        System.out.println("\n🏠 Home - Select a Task:");
        System.out.println("1. Identify 2D/3D Shapes");
        System.out.println("2. Identify Angles");
        System.out.println("3. End Session");

        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                task1.start();
                goHome(); // 完成后回首页
                break;
            case 2:
                task2.start();
                goHome();
                break;
            case 3:
                endSession();
                break;
            default:
                System.out.println("Invalid choice. Try again!");
                goHome();
        }
    }

    public void endSession() {
        System.out.println("\n🏁 Session Ended.");
        System.out.println("You have achieved " + scoreManager.getScore() + " points. Goodbye!");
        System.exit(0);
    }
}
