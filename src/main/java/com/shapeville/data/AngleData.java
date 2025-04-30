package com.shapeville.data;

import java.util.Random;

public class AngleData {
    private static final Random random = new Random();

    // 获取随机角度（0-360之间，10的倍数）
    public static int getRandomAngle() {
        int[] multiplesOfTen = new int[37]; // 0-360有37个10的倍数
        for (int i = 0; i <= 36; i++) {
            multiplesOfTen[i] = i * 10;
        }
        return multiplesOfTen[random.nextInt(multiplesOfTen.length)];
    }

    // 判断角度类型
    public static String classifyAngle(int angle) {
        if (angle == 0 || angle == 360) {
            return "Full";
        } else if (angle > 0 && angle < 90) {
            return "Acute";
        } else if (angle == 90) {
            return "Right";
        } else if (angle > 90 && angle < 180) {
            return "Obtuse";
        } else if (angle == 180) {
            return "Straight";
        } else if (angle > 180 && angle < 360) {
            return "Reflex";
        } else {
            return "Invalid"; // 理论上不会走到这里
        }
    }
}
