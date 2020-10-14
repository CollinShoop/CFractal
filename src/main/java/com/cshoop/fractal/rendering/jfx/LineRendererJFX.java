package com.cshoop.fractal.rendering.jfx;

import com.cshoop.fractal.util.ColorUtil;
import com.cshoop.fractal.generation.Line;
import com.cshoop.fractal.generation.impl.RepeatingLineFractal;
import com.cshoop.fractal.util.ColorMapper;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;


public class LineRendererJFX extends Application {

	private static final int DEFAULT_PANE_PADDING = 10;

	final RepeatingLineFractal fractalGen = new RepeatingLineFractal();
	final NavigationRenderingProperties renderingProps = new NavigationRenderingProperties();
	final NavigationMouseProperties navigationMouseProps = new NavigationMouseProperties();

	final Canvas canvas = new Canvas(800, 800);

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Drawing Operations Test");

		HBox root = new HBox();

		VBox canvasBox = new VBox();
		canvasBox.getChildren().add(canvas);

		VBox controlBox = new VBox();
		controlBox.getChildren().add(new VBox(buildThetaControl(), buildRatioControl(), buildInitialSidesControl()));
		controlBox.getChildren().add(buildToggleControl());

		root.getChildren().add(canvasBox);
		root.getChildren().add(controlBox);

		canvas.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
			System.out.println("Mouse dragged " + e.getButton());
			if (e.getButton() != MouseButton.PRIMARY) {
				return;
			}

			double diffX = navigationMouseProps.getX() - e.getSceneX();
			double diffY = navigationMouseProps.getY() - e.getSceneY();
			renderingProps.shift(0.0015 * diffX, 0.0015 * diffY);

			repaint();
			navigationMouseProps.setX(e.getSceneX());
			navigationMouseProps.setY(e.getSceneY());
			System.out.println("Mouse dragged "+  e.getSceneX() + "," + e.getSceneY() + ": diff " + + diffX + ", " + diffY);
		});

		canvas.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
			System.out.println("Mouse pressed " + e.getButton());
			if (e.getButton() != MouseButton.PRIMARY) {
				return;
			}
			navigationMouseProps.setX(e.getSceneX());
			navigationMouseProps.setY(e.getSceneY());
			System.out.println("Mouse pressed "+  e.getSceneX() + "," + e.getSceneY());
		});

		primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			System.out.println("Key typed: " + e.getCharacter() + ", code: " + e.getCode());
			KeyCode code = e.getCode();
			switch (code) {
				case PLUS:
				case EQUALS:
					fractalGen.increaseGenMax();
					fractalGen.regen();
					repaint();
					break;
				case MINUS:
				case UNDERSCORE:
					fractalGen.decreaseGenMax();
					fractalGen.regen();
					repaint();
					break;
			}
		});

		canvas.setOnScroll(e -> {
			double scroll = 0;
			if (e.getDeltaX() != 0) {
				if (Math.abs(e.getDeltaX()) > scroll) {
//					scroll = e.getDeltaX();
				}
			}
			if (e.getDeltaY() != 0) {
				if (Math.abs(e.getDeltaY()) > scroll) {
					scroll = e.getDeltaY();
				}
			}
			renderingProps.zoom(0.001*scroll);
			repaint();
		});


		primaryStage.setScene(new Scene(root, -1, -1, true, SceneAntialiasing.BALANCED));
		primaryStage.show();


		fractalGen.regen();
		repaint();
	}

	private Pane buildThetaControl() {
		return buildSlidingControl("Theta", 0, 360, fractalGen.getThetaDegrees(), val -> {
			fractalGen.setTheta(val);
			fractalGen.regen();
			repaint();
		});
	}

	private Pane buildRatioControl() {
		return buildSlidingControl("Ratio", 0, 100, fractalGen.getRatio()*100, val -> {
			fractalGen.setRatio(val / 100.0);
			fractalGen.regen();
			repaint();
		});
	}

	private Pane buildInitialSidesControl() {
		return buildSlidingControl("Sides", 1, 20, fractalGen.getInitialSides(), val -> {
			System.out.println("Refreshing Rendering 'sides control'");
			if (fractalGen.getInitialSides() != val.intValue()) {
				fractalGen.setInitialSides(val.intValue());
				fractalGen.regen();
				repaint();
			}
		});
	}

	private Pane buildToggleControl() {
		HBox hbox = new HBox();
		hbox.setMaxWidth(Double.POSITIVE_INFINITY); // TODO not working
		hbox.setPadding(new Insets(DEFAULT_PANE_PADDING));

		ToggleButton invertButton = new ToggleButton("Invert");
		invertButton.setSelected(fractalGen.isInvert());
		invertButton.addEventFilter(ActionEvent.ANY, event -> {
			fractalGen.setInvert(!fractalGen.isInvert());
			fractalGen.regen();
			repaint();
		});


		ToggleButton showAllLinesButton = new ToggleButton("Show All Levels");
		showAllLinesButton.setSelected(!fractalGen.isLastDimOnly());
		showAllLinesButton.addEventFilter(ActionEvent.ANY, event -> {
			fractalGen.setLastDimOnly(!fractalGen.isLastDimOnly());
			fractalGen.regen();
			repaint();
		});


		ToggleButton aliasingButton = new ToggleButton("Aliasing");
		aliasingButton.setSelected(renderingProps.isAliasing());
		aliasingButton.addEventFilter(ActionEvent.ANY, event -> {
			renderingProps.setAliasing(!renderingProps.isAliasing());
			fractalGen.regen();
			repaint();
		});

		ToggleButton awtRenderingButton = new ToggleButton("AWT Rendering");
		awtRenderingButton.setSelected(renderingProps.isUseAwtRendering());
		awtRenderingButton.addEventFilter(ActionEvent.ANY, event -> {
			renderingProps.setUseAwtRendering(!renderingProps.isUseAwtRendering());
			fractalGen.regen();
			repaint();
		});

		hbox.getChildren().addAll(invertButton, showAllLinesButton, aliasingButton, awtRenderingButton);
		return hbox;
	}

	private Pane buildSlidingControl(String name, double minValue, double maxValue, double initialValue, Consumer<Double> valueConsumer) {
		// Build Slider control for theta
		HBox hbox = new HBox();
		hbox.setMaxWidth(Double.POSITIVE_INFINITY); // TODO not working
		hbox.setPadding(new Insets(DEFAULT_PANE_PADDING));

		final Text sliderNameLabel = new Text(name); // show a name for the slider
		final Label sliderValueLabel = new Label(String.valueOf(initialValue)); // show value
		final Slider slider = new Slider(minValue, maxValue, initialValue); // slider control

		// slider.setBlockIncrement(1);
		slider.showTickLabelsProperty().set(true);
		slider.showTickMarksProperty().set(true);
		slider.setMaxWidth(Double.POSITIVE_INFINITY);

		slider.valueProperty().addListener((observable, oldValue, newValue) -> {
			sliderValueLabel.textProperty().setValue(NumberFormat.getInstance().format(newValue.doubleValue()));
			valueConsumer.accept(newValue.doubleValue());
		});

		hbox.getChildren().add(sliderNameLabel);
		hbox.getChildren().add(slider);
		hbox.getChildren().add(sliderValueLabel);
		return hbox;
	}

	public void repaint() {
		draw(canvas.getGraphicsContext2D());
	}

	private void draw(GraphicsContext gc) {
//			final RenderingHints rh = new RenderingHints(
//					RenderingHints.KEY_ANTIALIASING,
//					RenderingHints.VALUE_ANTIALIAS_ON);
//			g2.setRenderingHints(rh);
		try {
			Color backgroundColor = Color.white;
			gc.setFill(ColorUtil.toColorFx(backgroundColor));
			gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
			gc.setLineCap(StrokeLineCap.ROUND);
			gc.setLineJoin(StrokeLineJoin.ROUND);


			BufferedImage contentImage = null;
			Graphics2D g2 = null;
			if (renderingProps.isUseAwtRendering()) {
				contentImage = new BufferedImage((int) gc.getCanvas().getWidth(), (int) gc.getCanvas().getHeight(), BufferedImage.TYPE_INT_RGB);
				g2 = (Graphics2D) contentImage.getGraphics();

				if (renderingProps.isAliasing()) {
					final RenderingHints rh = new RenderingHints(
							RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setRenderingHints(rh);
				}
				g2.setColor(backgroundColor);
				g2.fillRect(0, 0, (int) gc.getCanvas().getWidth(), (int) gc.getCanvas().getHeight());
			}

			// TODO gen image
            List<Line> treeLines = fractalGen.getTreePointsSafe();
			final ColorMapper colorMapper = new ColorMapper(
					0, treeLines.size(),
					Arrays.asList(
							Color.BLACK,
							Color.RED
					));

			final long startTime = System.currentTimeMillis();

			int i = 0;
			for (Line line : treeLines) {
				Color color = colorMapper.get(i++);
				if (Math.random() <= renderingProps.getInvertRatio()) {
					color = ColorUtil.getContrastVersionForColor(color);
				}
//				Color color = Color.BLACK;
				double stroke = 1.2;

				// calculate line start/ends
				double x1 = line.getA().getX();
				double y1 = line.getA().getY();
				double x2 = line.getB().getX();
				double y2 = line.getB().getY();

				// apply fractal.rendering zoom
				x1 = (x1 - renderingProps.getX1()) * renderingProps.getZoom();
				y1 = (y1 - renderingProps.getY1()) * renderingProps.getZoom();
				x2 = (x2 - renderingProps.getX1()) * renderingProps.getZoom();
				y2 = (y2 - renderingProps.getY1()) * renderingProps.getZoom();

				// scale to window
				double minDim = Math.min(gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

				x1 *= minDim;
				y1 *= minDim;
				x2 *= minDim;
				y2 *= minDim;

				if (g2 != null) {
					g2.setColor(color);
					g2.setStroke(new BasicStroke((float) stroke));

					g2.drawLine(
							(int) x1,
							(int) y1,
							(int) x2,
							(int) y2
					);
				} else {
					gc.setStroke(ColorUtil.toColorFx(color));
					gc.setLineWidth(stroke);
					gc.strokeLine(x1, y1, x2, y2);
				}
			}

			if (g2 != null) {
				g2.dispose();
				gc.drawImage(SwingFXUtils.toFXImage(contentImage, null), 0, 0);
			}

			final long duration = System.currentTimeMillis() - startTime;
			System.out.println("Rendering took " + duration + "ms; perLine=" + ((double) duration / treeLines.size()));

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Data
	private static class NavigationMouseProperties {
		private double x;
		private double y;
	}

	@Data
	private static class NavigationRenderingProperties {

		private double x1 = 0;
		private double w = 1;
		private double y1 = 0;
		private double h = 1;

		private double invertRatio = 0.0;

		private boolean aliasing = false;
		private boolean useAwtRendering = false;

		public double getZoom() {
			return 1/Math.min(w, h);
		}

		public void shift(double x, double y) {
			x1 += x * w;
			y1 += y * h;
		}

		public void zoom(double factor) {
			x1 += factor*w;
			w -= (factor*w*2);
			y1 += factor*h;
			h -= (factor*h*2);
		}
	}


}
