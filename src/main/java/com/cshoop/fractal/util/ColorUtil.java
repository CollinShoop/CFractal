package com.cshoop.fractal.util;

import java.awt.Color;

public class ColorUtil {

    public static javafx.scene.paint.Color toColorFx(Color color) {
        return new javafx.scene.paint.Color(color.getRed()/255.0, color.getGreen()/255.0, color.getBlue()/255.0, color.getAlpha()/255.0);
    }

    public static Color getContrastVersionForColor(Color c) {
        float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        hsv[0] = (hsv[0] + 0.5f) % 1;
        return new Color(Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]));
    }

    public static void main(String[] args) {
        System.out.println(getContrastVersionForColor(Color.black));
        System.out.println(getContrastVersionForColor(Color.blue));
        System.out.println(getContrastVersionForColor(Color.red));
        System.out.println(getContrastVersionForColor(Color.green));
    }

}
