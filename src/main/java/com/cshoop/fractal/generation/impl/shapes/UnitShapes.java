package com.cshoop.fractal.generation.impl.shapes;

import com.cshoop.fractal.generation.impl.Maths;
import com.cshoop.fractal.generation.Line;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnitShapes {

    public static final List<Line> TRIANGLE = Arrays.asList(
        new Line(0,0, 1, 0),
        new Line(1, 0, 0.5, -Math.sqrt(3)/2),
        new Line(0.5, -Math.sqrt(3)/2, 0, 0)
    );
    public static final List<Line> LINE = Arrays.asList(
            new Line(0,0, 1, 0)
    );
    public static final List<Line> SHAPE_V = Arrays.asList(
            new Line(0, 0, 0.5, 0.5),
            new Line(0.5, 0.5, 1, 0)
    );
    public static final List<Line> SHAPE_V_EDGED = Arrays.asList(
            new Line(0, 0, 0.3333333, 0),
            new Line(0.3333333, 0, 0.5, 0.3333333),
            new Line(0.5, 0.3333333, 0.6666666, 0),
            new Line(0.6666666, 0, 1, 0)
    );
    public static final List<Line> SHAPE_TOP_HEXAGON = Arrays.asList(
            new Line(0, 0, 0.3, 0.5),
            new Line(0.3, 0.5, 0.7, 0.5),
            new Line(0.7, 0.5, 1, 0)
    );

    public static List<Line> getShapeV() {
        return SHAPE_V;
    }

    public static List<Line> getShapeVEdged() {
        return SHAPE_V_EDGED;
    }


    public static List<Line> getShapePentaControlled(double width, double height) {
        double x1 = 0.5 - (width/2);
        double x2 = 0.5 + (width/2);
        return Arrays.asList(
                new Line(0, 0, x1, height).down(),
                new Line(x1, height, x2, height),
                new Line(x2, height, 1, 0).down()
        );
    }

    public static List<Line> getShapeVEdgedControlled(double width, double height) {
        double x1 = 0.5 - (width/2);
        double x2 = 0.5 + (width/2);
        return Arrays.asList(
                new Line(0, 0, x1, 0),
                new Line(x1, 0, 0.5, height),
                new Line(0.5, height, x2, 0),
                new Line(x2, 0, 1, 0)
        );
    }

    public static List<Line> getShapeTopHexagon() {
        return SHAPE_TOP_HEXAGON;
    }

    public static List<Line> getShape(int numSides) {
        List<Point2D> pts = new ArrayList<>();
        if (numSides == 1) {
            pts.add(new Point2D.Double(0, 0));
            pts.add(new Point2D.Double(1, 0));
        }
        else {
            for (double d = 0; d < numSides; d++) {
                pts.add(Maths.rotatePoint(1, 0, -Math.PI * 2 / numSides * d));
            }
        }
        return Maths.fromPts(pts);
    }
}
