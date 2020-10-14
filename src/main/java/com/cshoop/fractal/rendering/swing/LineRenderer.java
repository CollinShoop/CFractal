package com.cshoop.fractal.rendering.swing;

import com.cshoop.fractal.generation.Line;
import com.cshoop.fractal.generation.LineFractal;
import com.cshoop.fractal.generation.impl.RepeatingLineFractal;
import com.cshoop.fractal.util.ColorMapper;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.MouseInputAdapter;


public class LineRenderer extends JFrame {
	
	public LineRenderer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 800);
		setLocation(100, 100);
		setLayout(new BorderLayout());
		setFocusable(true);

		final LineFractal tree = new RepeatingLineFractal();
		final NavigationRenderingProperties renderingProperties = new NavigationRenderingProperties();
		final TreePanel treePanel = new TreePanel(tree, renderingProperties);

		JSlider sliderDegrees = new JSlider(JSlider.HORIZONTAL,
				0, 360, (int)tree.getThetaDegrees());
		sliderDegrees.setMajorTickSpacing(10);
		sliderDegrees.setMinorTickSpacing(1);
		sliderDegrees.setPaintLabels(true);
		sliderDegrees.setPaintTicks(true);
		sliderDegrees.setFocusable(false);
		sliderDegrees.addChangeListener(e -> {
			JSlider source = (JSlider)e.getSource();
			if (source.getValueIsAdjusting()) {
				tree.setTheta(source.getValue());
				tree.regen();
				treePanel.repaint();
			}
		});

		JSlider sliderRatio = new JSlider(JSlider.HORIZONTAL,
				1, 99, (int) (tree.getRatio() * 100));
		sliderRatio.setMajorTickSpacing(10);
		sliderRatio.setMinorTickSpacing(1);
		sliderRatio.setPaintLabels(true);
		sliderRatio.setPaintTicks(true);
		sliderRatio.setFocusable(false);
		sliderRatio.addChangeListener(e -> {
			JSlider source = (JSlider)e.getSource();
			if (source.getValueIsAdjusting()) {
				tree.setRatio(source.getValue() / 100.0);
				tree.regen();
				treePanel.repaint();
			}
		});


		getContentPane().add(treePanel, BorderLayout.CENTER);
		getContentPane().add(sliderDegrees, BorderLayout.SOUTH);
		getContentPane().add(sliderRatio, BorderLayout.NORTH);

		addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				int code = e.getExtendedKeyCode();
				double speed = 0.05;
				switch(code) {
					case KeyEvent.VK_UP:
						renderingProperties.shift(0, -speed);
						repaint();
						break;
					case KeyEvent.VK_DOWN:
						renderingProperties.shift(0, speed);
						repaint();
						break;
					case KeyEvent.VK_LEFT:
						renderingProperties.shift(-speed, 0);
						repaint();
						break;
					case KeyEvent.VK_RIGHT:
						renderingProperties.shift(speed, 0);
						repaint();
						break;
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				int code = e.getKeyChar();
				System.out.println("Code " + code + " " + e.getKeyChar());
				switch(code) {
					case '+':
					case '=':
						tree.increaseGenMax();
						tree.regen();
						repaint();
						break;
					case '-':
					case '_':
						tree.decreaseGenMax();
						tree.regen();
						repaint();
						break;
				}
			}
		});


		addMouseWheelListener(new MouseInputAdapter() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				renderingProperties.zoom(0.05*-e.getWheelRotation());
				repaint();
			}
		});

		MouseAdapter movementAdapter = new MouseAdapter() {

			private double x;
			private double y;

			@Override
			public void mouseDragged(MouseEvent e) {
				System.out.println("Mouse dragged " + e.getButton());
				if (e.getButton() != MouseEvent.NOBUTTON) {
					return;
				}

				double diffX = x - e.getXOnScreen();
				double diffY = y - e.getYOnScreen();
				renderingProperties.shift(0.0015 * diffX, 0.0015 * diffY);
				repaint();
				x = e.getXOnScreen();
				y = e.getYOnScreen();
				System.out.println("Mouse dragged "+  x + "," + y + ": diff " + + diffX + ", " + diffY);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				System.out.println("Mouse pressed " + e.getButton());
				if (e.getButton() != MouseEvent.BUTTON1) {
					return;
				}
				x = e.getXOnScreen();
				y = e.getYOnScreen();
				System.out.println("Mouse pressed "+  x + "," + y);
			}

			//
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				if (SwingUtilities.isLeftMouseButton(e)) {
//					renderingProperties.zoom(0.1);
//					repaint();
//				} else if (SwingUtilities.isRightMouseButton(e)) {
//					renderingProperties.zoom(-0.1);
//					repaint();
//				}
//			}
		};

		addMouseListener(movementAdapter);
		addMouseMotionListener(movementAdapter);

	}

	private class NavigationRenderingProperties {

		private double x1 = 0;
		private double w = 1;
		private double y1 = 0;
		private double h = 1;

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

		public double getX1() {
			return x1;
		}

		public double getY1() {
			return y1;
		}

		public double getW() {
			return w;
		}

		public double getH() {
			return h;
		}

		@Override
		public String toString() {
			return "NavigationRenderingProperties{" +
					"x1=" + x1 +
					", w=" + w +
					", y1=" + y1 +
					", h=" + h +
					'}';
		}
	}

	private class TreePanel extends JPanel {
		private LineFractal htree;
		private NavigationRenderingProperties properties;

		public TreePanel(LineFractal htree, NavigationRenderingProperties properties) {
			super();

			this.htree = htree;
			this.properties = properties;
		}

		@Override
		public void paint(java.awt.Graphics g) {
			final Graphics2D g2 = (Graphics2D) g;
			final RenderingHints rh = new RenderingHints(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHints(rh);
			g2.setColor(Color.white);
			g2.fillRect(0, 0, getWidth(), getHeight());

			final ColorMapper colorMapper = new ColorMapper(
//					htree.getMinHanded(), htree.getMaxHanded(),
					0, htree.getGenMax(),
					Arrays.asList(
//							HexToColor("6E83D3"),
//							HexToColor("677CCE"),
//							HexToColor("6075C8"),
//							HexToColor("6CC259"),
//							HexToColor("BC536A")
							Color.DARK_GRAY,
							Color.GREEN
					));

			final long startTime = System.currentTimeMillis();

			for(Line line : htree.getTreePointsSafe()) {
				// TODO center line is NOT RENDERING WHEN ZOOMED IN WTF
//				boolean linePartialInside = line.atLeastPartialInside(properties.getX1(), properties.getY1(), properties.getW(), properties.getH());
//				if (!linePartialInside) {
//					boolean intersect = line.intersect(properties.getX1(), properties.getY1(), properties.getW(), properties.getH());
//					if (intersect) {
//						continue;
//					}
//				}

				// TODO skip if outside fractal.rendering area
				// calculate stroke size
				double dimRatio = (1-((double)line.getN() / htree.getGenMax()));
//				dimRatio = 1- (Math.abs(line.getHandedness()) / Math.abs(htree.getMaxHanded()));
//				dimRatio = (line.getHandedness()-htree.getMinHanded())/(htree.getMaxHanded()-htree.getMinHanded());
				float stroke = 0.5f + (float)(1.0 * (dimRatio));
				// increase line width according to zoom
//				stroke *= properties.getZoom();

				// calculate color
				int alpha = 10+(int)(245*dimRatio);
//				Color color = colorMapper.get(line.getHandedness());
				Color color = colorMapper.get(line.getN());
				color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);

				// calculate line start/ends
				double x1 = line.getA().getX();
				double y1 = line.getA().getY();
				double x2 = line.getB().getX();
				double y2 = line.getB().getY();

				// apply fractal.rendering zoom
				x1 = (x1 - properties.getX1()) * properties.getZoom();
				y1 = (y1 - properties.getY1()) * properties.getZoom();
				x2 = (x2 - properties.getX1()) * properties.getZoom();
				y2 = (y2 - properties.getY1()) * properties.getZoom();

				// scale to window
				int minDim = Math.min(getWidth(), getHeight());

				x1 *= minDim;
				y1 *= minDim;
				x2 *= minDim;
				y2 *= minDim;

				// draw
				g2.setColor(color);
				g2.setStroke(new BasicStroke(stroke));

				g2.drawLine(
						(int) x1,
						(int) y1,
						(int) x2,
						(int) y2
				);
			}

			final long duration = System.currentTimeMillis() - startTime;
			System.out.println("Rendering took " + duration + "ms");
		}
	}

	public static Color HexToColor(String hex)
	{
		hex = hex.replace("#", "");
		switch (hex.length()) {
			case 6:
				return new Color(
						Integer.valueOf(hex.substring(0, 2), 16),
						Integer.valueOf(hex.substring(2, 4), 16),
						Integer.valueOf(hex.substring(4, 6), 16));
			case 8:
				return new Color(
						Integer.valueOf(hex.substring(0, 2), 16),
						Integer.valueOf(hex.substring(2, 4), 16),
						Integer.valueOf(hex.substring(4, 6), 16),
						Integer.valueOf(hex.substring(6, 8), 16));
		}
		return null;
	}

}
