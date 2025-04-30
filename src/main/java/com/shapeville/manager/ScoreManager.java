package com.shapeville.manager;

import java.util.Random;

public class ScoreManager {
    private int score;
    private final Random random;

    public ScoreManager() {
        this.score = 0;
        this.random = new Random();
    }

    public void addScore(int points) {
        score += points;
        printPraise();  // 每次得分都鼓励一下
    }

    public int getScore() {
        return score;
    }

    public void resetScore() {
        score = 0;
    }

    // 可选方法：打印鼓励语
    private void printPraise() {
        String[] praises = {
            "🎉 Great job!",
            "🌟 Well done!",
            "👏 Keep it up!",
            "👍 You're doing great!",
            "✅ Correct! Keep going!"
        };
        System.out.println(praises[random.nextInt(praises.length)]);
    }
}
