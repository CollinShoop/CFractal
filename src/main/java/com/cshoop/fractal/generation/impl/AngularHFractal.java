package com.cshoop.fractal.generation.impl;


import com.cshoop.fractal.generation.LineFractal;
import com.cshoop.fractal.generation.Line;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class AngularHFractal extends LineFractal {

	private int genStart = 0;

	public AngularHFractal() {
		super();
	}

	@Override
	protected List<Line> generateNext(List<Line> previous) {
		int initialSize = previous.size();
		final List<Line> newLines = new ArrayList<>();

		if (getIteration() == 0) {
			newLines.add(new Line(0.5, 0.5, 0.5, 0.75, 0));
			return newLines;
		}

		for (int i = genStart; i < initialSize; i++) {
			Line line = previous.get(i);

			Point2D a = line.getA();
			Point2D b = line.getB();

			if (getIteration() == 1) {
				Point2D midpoint = line.midpoint();
				newLines.addAll(subGen(midpoint, a, getTheta(), line.getHandedness(), getIteration()));
				newLines.addAll(subGen(midpoint, b, getTheta(), line.getHandedness(), getIteration()));
				newLines.addAll(subGen(midpoint, a, -getTheta(), line.getHandedness(), getIteration()));
				newLines.addAll(subGen(midpoint, b, -getTheta(), line.getHandedness(), getIteration()));

//					subLines.addAll(subGen(midpoint, a, 2*theta, line.getHandedness(), iteration));
//					subLines.addAll(subGen(midpoint, b, 2*theta, line.getHandedness(), iteration));
//					subLines.addAll(subGen(midpoint, a, -2*theta, line.getHandedness(), iteration));
//					subLines.addAll(subGen(midpoint, b, -2*theta, line.getHandedness(), iteration));
			} else {
				newLines.addAll(subGen(a, b, getTheta(), line.getHandedness(), getIteration()));
				newLines.addAll(subGen(a, b, -getTheta(), line.getHandedness(), getIteration()));

//					subLines.addAll(subGen(a, b, 2*theta, line.getHandedness(), iteration));
//					subLines.addAll(subGen(a, b, -2*theta, line.getHandedness(), iteration));
			}

		}

		if (isLastDimOnly()) {
			genStart = 0;
		} else {
			genStart = initialSize;
		}

		return newLines;
	}


	private List<Line> subGen(Point2D a, Point2D b, double theta, double handedness, int iteration) {
		theta += randomThetaAdd(iteration);
		List<Line> results = new ArrayList<>();
		double diffX = b.getX() - a.getX();
		double diffY = b.getY() - a.getY();
		{
			// rotate positive
			double diffXRotated = diffX * Math.cos(theta) - diffY * Math.sin(theta);
			diffXRotated *= getRatio(); // scale down
			double diffYRotated = diffY * Math.cos(theta) + diffX * Math.sin(theta);
			diffYRotated *= getRatio(); // scale down
			double fx = b.getX() + diffXRotated;
			double fy = b.getY() + diffYRotated;
			Line line = new Line(b.getX(), b.getY(), fx, fy, iteration);
			line.setHandedness(handedness + theta);
			results.add(line);
		}
		return results;
	}

	private double randomThetaAdd(int n) {
		return 0.25 * (Math.random()-0.5) * n;
	}

	@Override
	protected void reset() {
		genStart = 0;
	}


}
