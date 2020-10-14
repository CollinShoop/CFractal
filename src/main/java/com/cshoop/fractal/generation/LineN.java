package com.cshoop.fractal.generation;

import lombok.Data;
import lombok.Getter;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

@Data
public class LineN {

	private List<Point2D> points;
	private int n;
	private double handedness;

	public LineN() {
		points = new ArrayList<>(2);
	}

	public void add(Point2D p) {
		points.add(p);
	}

}
