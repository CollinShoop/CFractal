package fractal.util;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class ColorMapper {

    private final double min;
    private final double max;
    private final List<Color> colors;
    private final double rangePerColor;

    public ColorMapper(double min, double max, List<Color> colors) {
        this.min = min;
        this.max = max;
        this.colors = colors;
        this.rangePerColor = (max - min) / (colors.size()-1);
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public Color get(double f) {
        f -= min;
        if (f < min || f > max) {
            f = Math.abs(f % (max - min));
        }
        int ri = (int) (f / rangePerColor);
        double rm = (f / rangePerColor)%1.0;
        if (rm == 0) {
            return colors.get(ri);
        }
        Color left = colors.get(ri);
        Color right = colors.get(ri+1);
        return new Color(
                scaleToRGBv(left.getRed(), right.getRed(), rm),
                scaleToRGBv(left.getGreen(), right.getGreen(), rm),
                scaleToRGBv(left.getBlue(), right.getBlue(), rm)
        );
    }

    private static int scaleToRGBv(int left, int right, double scale) {
        return (int) (left + (right - left)*scale);
    }

    public static void main(String[] args) {
        ColorMapper mapper = new ColorMapper(0, 1, Arrays.asList(Color.black, Color.white, Color.red, Color.black));

        for(double i = 0; i < 5; i += 0.1) {
            System.out.println(i + ": " + mapper.get(i));
        }
    }

}
