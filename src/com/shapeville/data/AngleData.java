package com.shapeville.data;

public class AngleData {
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
