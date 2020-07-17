package fractal.generation.impl;


import fractal.generation.Line;
import fractal.generation.LineFractal;
import fractal.generation.impl.shapes.UnitShapes;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RepeatingLineFractal extends LineFractal {

	private int initialSides = 3;
	private boolean invert = false;

	public RepeatingLineFractal() {
		super();
		setLastDimOnly(false);
	}

	@Override
	protected List<Line> generateNext(List<Line> previous) {
		final List<Line> newLines = new ArrayList<>();


		final List<Line> unitShape =
//				getIteration() % 3 == 0
//				? UnitShapes.getShapeV()
//				:
//				UnitShapes.getShapeTopHexagon();
				UnitShapes.getShapePentaControlled(getRatio()/2, getThetaDegrees()/360);


		if (getIteration() == 0) {
			List<Line> initialSideLines = UnitShapes.getShape(initialSides);
			if (invert) {
				initialSideLines = Maths.reverse(initialSideLines);
			}
			newLines.addAll(initialSideLines);
		} else {
			previous.forEach(pline -> {
				Line line = (pline.isUp() ? pline : pline.reverse());
				double angleRad = Maths.getOrientationRadians(line);
				double len = Maths.getLength(line.getA(), line.getB());
				Maths.scaleAndRotate(unitShape, len / Maths.getLength(unitShape), angleRad).forEach(line1 -> {
					Line shifted = Maths.shift(line1, line.getA().getX(), line.getA().getY());
					shifted.setHandedness(line.getHandedness() + angleRad);
					newLines.add(shifted);
				});
			});
		}

		return newLines;
	}

	@Override
	protected void reset() {

	}

}
