package fractal.generation.impl;

import fractal.generation.Line;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Maths {

    public static double getLength(Point2D start, Point2D end) {
        return Math.sqrt(Math.pow(end.getX() - start.getX(), 2) + Math.pow(end.getY() - start.getY(), 2));
    }

    public static double getLength(List<Line> lines) {
        return getLength(lines.get(0).getA(), lines.get(lines.size()-1).getB());
    }

    public static double getOrientationRadians(Line line) {
        double diffX = line.getB().getX() - line.getA().getX();
        double diffY = line.getB().getY() - line.getA().getY();

        if (diffX == 0) {
            if (diffY < 0) {
                return Math.PI / -2;
            }
            return Math.PI / 2;
        }
        return Math.atan2(diffY, diffX);
    }

    public static Line shift(Line line, double x, double y) {
        return new Line(
                new Point2D.Double(line.getA().getX() + x, line.getA().getY() + y),
                new Point2D.Double(line.getB().getX() + x, line.getB().getY() + y),
                line.getN()
        ) {{
            setHandedness(line.getHandedness());
        }};
    }

    public static List<Line> scaleAndRotate(List<Line> lines, double scale, double theta) {
        Point2D origin = lines.get(0).getA();
        return lines.stream().map(line -> new Line(
                rotateAndScalePointAbout(line.getA(), origin, scale, theta),
                rotateAndScalePointAbout(line.getB(), origin, scale, theta),
                line.getN()) {{
                    setHandedness(line.getHandedness());
                }}
        ).collect(Collectors.toList());
    }

    public static Point2D rotateAndScalePointAbout(Point2D p, Point2D origin, double scale, double theta) {
        double x = (p.getX() - origin.getX()) * scale;
        double y = (p.getY() - origin.getY()) * scale;
        Point2D r = rotatePoint(x, y, theta);
        return new Point2D.Double(r.getX() + origin.getX(), r.getY() + origin.getY());
    }

    public static Point2D rotatePoint(double x, double y, double theta) {
        double xr = x * Math.cos(theta) - y * Math.sin(theta);
        double yr = y * Math.cos(theta) + x * Math.sin(theta);
        return new Point2D.Double(xr, yr);
    }

    public static List<Line> reverse(List<Line> shape) {
        List<Line> cp = new ArrayList<>(shape);
        cp.forEach(line -> {
            Point2D tmp = line.getA();
            line.setA(line.getB());
            line.setB(tmp);
        });
        return cp;
    }

    public static List<Line> fromPts(List<Point2D> pts) {
        List<Line> lines = new ArrayList<>();
        for(int i = 0; i < pts.size() - 1; i++) {
            lines.add(new Line(pts.get(i), pts.get(i+1)));
        }
        if (pts.size() > 2) {
            lines.add(new Line(pts.get(pts.size() - 1), pts.get(0)));
        }
        return lines;
    }

}
