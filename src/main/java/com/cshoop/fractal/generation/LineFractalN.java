package com.cshoop.fractal.generation;


import lombok.Data;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

@Data
public abstract class LineFractalN {

	private double theta = Math.toRadians(20);

	private final List<LineN> lines = new ArrayList<>();
	private double ratio = 0.81;
	private int iteration = 0;
	private int genMax = DEFAULT_GEN_MAX;
	private boolean lastDimOnly = false;
	private double maxX = Double.MIN_VALUE;
	private double maxY = Double.MIN_VALUE;
	private double minX = Double.MAX_VALUE;
	private double minY = Double.MAX_VALUE;
	private double minHanded = Double.MAX_VALUE;
	private double maxHanded = Double.MIN_VALUE;

	private static final int DEFAULT_GEN_MAX = 3;

	public LineFractalN() {
		regen();
	}

    protected abstract List<LineN> generateNext(List<LineN> previous);

    protected abstract void reset();

	public synchronized void regen() {
		synchronized (lines) {
			lines.clear();
		}
		iteration = 0;
		maxX = Double.MIN_VALUE;
		maxY = Double.MIN_VALUE;
		minX = Double.MAX_VALUE;
		minY = Double.MAX_VALUE;
		minHanded = Double.MAX_VALUE;
		maxHanded = Double.MIN_VALUE;
        reset();
		generate(genMax);
	}

	public void setTheta(double degrees) {
		this.theta = Math.toRadians(degrees);
	}

    public double getThetaDegrees() {
        return Math.toDegrees(theta);
    }

	public List<LineN> getTreePointsSafe() {
		synchronized (lines) {
			return new ArrayList<>(lines);
		}
	}

	public void increaseGenMax() {
		genMax++;
	}

	public void decreaseGenMax() {
		genMax--;
	}

	private void generate() {
        final List<LineN> newLines = generateNext(lines);
        newLines.forEach(this::registerBounds);
		synchronized (lines) {
            if (lastDimOnly) {
                lines.clear();
            }
            lines.addAll(newLines);
		}
        iteration++;
	}

	private void generate(int n) {
		for(int i = iteration; i < n; i++) {
			generate();
		}
	}

	private void registerBounds(LineN l) {
	    l.getPoints().forEach(this::registerBounds);

		if (l.getHandedness() < minHanded) {
			minHanded = l.getHandedness();
		}
		if (l.getHandedness() > maxHanded) {
			maxHanded = l.getHandedness();
		}
	}

	private void registerBounds(Point2D p) {
		registerBounds(p.getX(), p.getY());
	}

	private void registerBounds(double x, double y) {
		if (x < minX) {
			minX = x;
		}
		if (x > maxX) {
			maxX = x;
		}
		if (y > maxY) {
			maxY = y;
		}
		if (y < minY) {
			minY = y;
		}
	}
}
