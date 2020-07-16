package fractal.generation;


import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public abstract class LineFractal {

	private double theta = Math.toRadians(100);
	
	private final List<Line> lines = new ArrayList<>();
	private double ratio = 0.1;
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
	
	public LineFractal() {

	}

    protected abstract List<Line> generateNext(List<Line> previous);

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

	public List<Line> getTreePointsSafe() {
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
        final List<Line> newLines = generateNext(lines);
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

	private void registerBounds(Line l) {
		registerBounds(l.getA());
		registerBounds(l.getB());

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

    public double getTheta() {
        return theta;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public int getIteration() {
        return iteration;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    public int getGenMax() {
        return genMax;
    }

    public void setGenMax(int genMax) {
        this.genMax = genMax;
    }

    public boolean isLastDimOnly() {
        return lastDimOnly;
    }

    public void setLastDimOnly(boolean lastDimOnly) {
        this.lastDimOnly = lastDimOnly;
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public double getMinX() {
        return minX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public double getMinY() {
        return minY;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public double getMinHanded() {
        return minHanded;
    }

    public void setMinHanded(double minHanded) {
        this.minHanded = minHanded;
    }

    public double getMaxHanded() {
        return maxHanded;
    }

    public void setMaxHanded(double maxHanded) {
        this.maxHanded = maxHanded;
    }

    @Override
    public String toString() {
        return "LineFractal{" +
                "theta=" + theta +
                ", ratio=" + ratio +
                ", iteration=" + iteration +
                ", genMax=" + genMax +
                ", lastDimOnly=" + lastDimOnly +
                ", maxX=" + maxX +
                ", maxY=" + maxY +
                ", minX=" + minX +
                ", minY=" + minY +
                ", minHanded=" + minHanded +
                ", maxHanded=" + maxHanded +
                '}';
    }
}
