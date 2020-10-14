package com.cshoop.fractal.generation;

import lombok.Data;

import java.awt.geom.Point2D;

@Data
public class Line {

	private Point2D a;
	private Point2D b;
	private int n;
	private double handedness;
	private boolean up = true;

	public Line(Point2D a, Point2D b) {
		this(a, b, 0);
	}

	public Line(Point2D a, Point2D b, int n) {
		this.a = a;
		this.b = b;
		this.n = n;
	}

	public Line clone() {
		Line reverse = new Line(a, b, n);
		reverse.setHandedness(handedness);
		reverse.setUp(up);
		return reverse;
	}

	public Line(double x1, double y1, double x2, double y2, int n) {
		this.a = new Point2D.Double(x1, y1);
		this.b = new Point2D.Double(x2, y2);
		this.n = n;
	}

	public Line(double x1, double y1, double x2, double y2) {
		this(x1, y1, x2, y2, 0);
	}

	public Line reverse() {
		Line reverse = clone();
		reverse.setA(b);
		reverse.setB(a);
		return reverse;
	}

	public Line down() {
		setUp(false);
		return this;
	}

	public Point2D midpoint() {
		return new Point2D.Double((a.getX()+b.getX())/2, (a.getY()+b.getY())/2);
	}

	public boolean intersect(double x, double y, double width, double height) {
		return
				intersect(new Line(x, y, x+width, y, 0)) // top
				|| intersect(new Line(x, y, x, y+height, 0)) // left
				|| intersect(new Line(x + width, y, x+width, y+height, 0)) // right
				|| intersect(new Line(x, y+height, x+width, y+height, 0)) // bottom
		;
	}

	public boolean atLeastPartialInside(double x, double y, double width, double height) {
		return pointInside(getA(), x, y, width, height)
				|| pointInside(getB(), x, y, width, height);
	}

	private boolean pointInside(Point2D p, double x, double y, double width, double height) {
		return p.getX() >= x && p.getX() <= x + width
				&& p.getY() >= y && p.getY() <= y + height;
	}

	public boolean intersect(Line line) {
		// http://www.jeffreythompson.org/collision-detection/line-rect.php
		double x1 = getA().getX(),
				x2 = getB().getX(),
				x3 = line.getA().getX(),
				x4 = line.getB().getX();
		double y1 = getA().getY(),
				y2 = getB().getY(),
				y3 = line.getA().getY(),
				y4 = line.getB().getY();
		double uV = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
		double uA = ((x4-x3)*(y1-y3) - (y4-y3)*(x1-x3)) / uV;
		double uB = ((x2-x1)*(y1-y3) - (y2-y1)*(x1-x3)) / uV;

		// if uA and uB are between 0-1, lines are colliding
		return uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1;
	}

}
