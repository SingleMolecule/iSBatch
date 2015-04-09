package stepfitter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JFrame;

public class Plot extends Component implements Runnable {
	private static final long serialVersionUID = 1L;
	
	public static final int LINE = 1;
	public static final int CIRCLE = 2;
	
	public double zoomFactor = 2;
	public int leftMargin = 50;
	public int bottomMargin = 50;
	public double xSelected = Double.NaN;
	public double ySelected = Double.NaN;
	
	// contains the data points for each plot  
	private ArrayList<double[]> xValues = new ArrayList<double[]>();
	private ArrayList<double[]> yValues = new ArrayList<double[]>();
	private ArrayList<Color> colors = new ArrayList<Color>();
	private ArrayList<Integer> types = new ArrayList<Integer>();	
	private ArrayList<Integer> thickness = new ArrayList<Integer>();
	
	private Rectangle2D.Double plotBounds = new Rectangle2D.Double();
	private Rectangle2D.Double visibleBounds = new Rectangle2D.Double();
	private Rectangle2D.Double targetedVisibleBounds = new Rectangle2D.Double();
	
	private ArrayList<double[]> markValues = new ArrayList<double[]>();
	private ArrayList<Color> markColors = new ArrayList<Color>();
	
	private Object lock = new Object();
	
	public Plot() {
		
		addMouseMotionListener(new MouseMotionAdapter() {
			int x, y;	// previous mouse position
			
			@Override
			public void mouseDragged(MouseEvent e) {
				synchronized (lock) {
					double sx = (getWidth() - leftMargin) / visibleBounds.width;
					double sy = (getHeight() - bottomMargin) / visibleBounds.height;
					
					double dx = (e.getX() - x) / sx;
					double dy = (e.getY() - y) / sy;
					
					x = e.getX();
					y = e.getY();
					
					targetedVisibleBounds.x -= dx;
					targetedVisibleBounds.y += dy;
					
					lock.notify();
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				x = e.getX();
				y = e.getY();
				
				double sx = (getWidth() - leftMargin) / visibleBounds.width;
				double sy = (getHeight() - bottomMargin) / visibleBounds.height;
				
				double x1 = (x - leftMargin) / sx + visibleBounds.x;
				double y1 = ((getHeight() - bottomMargin) - y) / sy + visibleBounds.y;
				
				double minDistance = Math.sqrt(400 / (sx * sx) + 400 / (sy * sy));
				xSelected = Double.NaN;
				ySelected = Double.NaN;
				
				for (int i = 0; i < xValues.size(); i++) {
					double[] xx = xValues.get(i);
					double[] yy = yValues.get(i);
					double distance, dx, dy;
					
					// (binary) search for nearest x position
					int l = 0;
					int r = xx.length;
					int c = r / 2;
					
					while (r - l > 1 && xx[c] != x1) {
						if (x1 > xx[c])
							l = c;
						else
							r = c;
						
						c = (l + r) / 2;
					}
					
					dx = xx[c] - x1;
					dy = yy[c] - y1;
					distance = Math.sqrt(dx * dx + dy * dy);
					
					if (distance < minDistance) {
						minDistance = distance;
						xSelected = xx[c];
						ySelected = yy[c];
					}
				}
				
				repaint();
			}
		});
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				synchronized (lock) {
					if (e.getClickCount() == 2) {
						targetedVisibleBounds = (Rectangle2D.Double)plotBounds.clone();
					}
					else {
						double sx = (getWidth() - leftMargin) / visibleBounds.width;
						double sy = (getHeight() - bottomMargin) / visibleBounds.height;
						
						// mouse position
						double mx = (e.getX() - leftMargin) / sx + visibleBounds.x;
						double my = ((getHeight() - bottomMargin) - e.getY()) / sy + visibleBounds.y;
						
						if (e.getButton() == MouseEvent.BUTTON1) {
							targetedVisibleBounds.width /= zoomFactor;
							targetedVisibleBounds.height /= zoomFactor;
						}
						else if (e.getButton() == MouseEvent.BUTTON3) {
							targetedVisibleBounds.width *= zoomFactor;
							targetedVisibleBounds.height *= zoomFactor;
						}
						
						targetedVisibleBounds.x = mx - targetedVisibleBounds.width / 2;
						targetedVisibleBounds.y = my - targetedVisibleBounds.height / 2;
					}
					
					lock.notify();
				}
			}
			
		});
		
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		double dx, dy, dw, dh, sx, sy;
		
		targetedVisibleBounds = (Rectangle2D.Double)visibleBounds.clone();
		
		while (true) {
			dx = (targetedVisibleBounds.x - visibleBounds.x) / 3;
			dy = (targetedVisibleBounds.y - visibleBounds.y) / 3;
			dw = (targetedVisibleBounds.width - visibleBounds.width) / 3;
			dh = (targetedVisibleBounds.height - visibleBounds.height) / 3;
			
			visibleBounds.x += dx;
			visibleBounds.y += dy;
			visibleBounds.width += dw;
			visibleBounds.height += dh;
			
			sx = getWidth() / visibleBounds.width;
			sy = getHeight() / visibleBounds.height;
			
			repaint();
			
			if (Math.abs(dx * sx) < 1 && Math.abs(dy * sy) < 1 && Math.abs(dw * sx) < 1 && Math.abs(dh * sy) < 1) {
				synchronized (lock) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						//
					}
				}
			}
			else {
				try {
					Thread.sleep(40);
				} catch (InterruptedException e) {
					//
				}
			}
		}
	}
	
	public void add(double[] x, double[] y, Color c, int type, int w) {		
		xValues.add(x);
		yValues.add(y);
		colors.add(c);
		types.add(type);
		thickness.add(w);
		
		if (visibleBounds.width == 0)
			visibleBounds = new Rectangle2D.Double(x[0], y[0], 0, 0);
		
		for (int i = 0; i < x.length; i++)
			visibleBounds.add(x[i], y[i]);
		
		targetedVisibleBounds = (Rectangle2D.Double)visibleBounds.clone();
		plotBounds = (Rectangle2D.Double)visibleBounds.clone();
		
		repaint();
	}
	
	public void clear() {
		visibleBounds = new Rectangle2D.Double();
		
		xValues.clear();
		yValues.clear();
		colors.clear();
		types.clear();
		
		markClear();
	}
	
	public void mark(double start, double end, Color c) {
		markValues.add(new double[]{start, end});
		markColors.add(c);
	}
	
	public void markClear() {
		markValues.clear();
		markColors.clear();
	}
	
	public void setPlotBounds(double xMin, double yMin, double xMax, double yMax) {
		synchronized (lock) {
			targetedVisibleBounds.setRect(xMin, yMin, xMax - xMin, yMax - yMin);
			lock.notify();
		}
	}
	
	public Rectangle2D.Double getPlotBounds() {
		synchronized (lock) {
			return (Rectangle2D.Double)targetedVisibleBounds.clone();
		}
	}
	
	private final float getStepSize(double range, int steps) {
		double step = range / steps;    // e.g. 0.00321
		double magnitude = Math.pow(10, Math.floor(Math.log10(step)));  // e.g. 0.001
		double mostSignificantDigit = Math.ceil(step / magnitude); // e.g. 3.21

        if (mostSignificantDigit > 5.0)
            return (float)(magnitude * 10.0);
        else if (mostSignificantDigit > 2.0)
            return (float)(magnitude * 5.0);
        else
            return (float)(magnitude * 2.0);
	}
	
	@Override
	public void paint(Graphics g) {
		int plotWidth = getWidth() - leftMargin;
		int plotHeight = getHeight() - bottomMargin;
		
		// scale
		double sx = plotWidth / visibleBounds.width; 
		double sy = plotHeight / visibleBounds.height;
		
		// translate
		double tx = visibleBounds.x;
		double ty = visibleBounds.y;
		
		// draw axis
		float stepx = getStepSize(visibleBounds.width, plotWidth / 50);
		float stepy = getStepSize(visibleBounds.height, plotHeight / 30);
		
		for (double x = Math.ceil(visibleBounds.x / stepx) * stepx; x <= visibleBounds.x + visibleBounds.width; x += stepx) {
			int x1 = leftMargin + (int)((x - tx) * sx);
			
			g.setColor(Color.LIGHT_GRAY);
			g.drawLine(x1, 0, x1, plotHeight);
			g.setColor(Color.BLACK);
			g.drawLine(x1, plotHeight, x1, plotHeight - 5);
			g.drawString(String.format("%3.2f", x), x1 - 10, plotHeight + 15);
		}
		
		for (double y = Math.ceil(visibleBounds.y / stepy) * stepy; y <= visibleBounds.y + visibleBounds.height; y += stepy) {
			int y1 = plotHeight - (int)((y - ty) * sy);
			
			g.setColor(Color.LIGHT_GRAY);
			g.drawLine(leftMargin, y1, getWidth(), y1);
			g.setColor(Color.BLACK);
			g.drawLine(leftMargin, y1, leftMargin + 5, y1);
			g.drawString(String.format("%3.2f", y), leftMargin - 50, y1 + 4);
		}

		g.drawLine(leftMargin, plotHeight, getWidth(), plotHeight);
		g.drawLine(leftMargin, 0, leftMargin, plotHeight);
		
		// draw plots
		int x1, y1, x2, y2;
		
		g.setClip(leftMargin, 0, plotWidth, plotHeight);

		Graphics2D g2d = (Graphics2D)g;
		
		for (int i = 0; i < xValues.size(); i++) {
			double[] x = xValues.get(i);
			double[] y = yValues.get(i);
			
			g.setColor(colors.get(i));
			g2d.setStroke(new BasicStroke(thickness.get(i)));
			
			switch (types.get(i)) {
			case LINE:
				x2 = leftMargin + (int)((x[0] - tx) * sx);
				y2 = plotHeight - (int)((y[0] - ty) * sy);
				
				for (int j = 1; j < x.length; j++) {
					x1 = x2;
					y1 = y2;
					x2 = leftMargin + (int)((x[j] - tx) * sx);
					y2 = plotHeight - (int)((y[j] - ty) * sy);
					
					g.drawLine(x1, y1, x2, y2);
				}
				
				break;
			case CIRCLE:
				
				for (int j = 1; j < x.length; j++) {
					x1 = leftMargin + (int)((x[j] - tx) * sx);
					y1 = plotHeight - (int)((y[j] - ty) * sy);
					
					g.drawOval(x1 - 2, y1 - 2, 4, 4);
				}
				
				break;
			}
		}
		
		// draw selected point
		if (!Double.isNaN(xSelected) && !Double.isNaN(ySelected)) {
			x1 = leftMargin + (int)((xSelected - tx) * sx);
			y1 = plotHeight - (int)((ySelected - ty) * sy);
		
			g.setColor(Color.RED);
			g.drawOval(x1 - 4, y1 - 4, 8, 8);
		}
		
	}
	
	public static void main(String[] args) {
		
		int size = 100000;
		double[] x = new double[size];
		double[] y = new double[size];
		
		for (int i = 0; i < size; i++) {
			x[i] = (4 * Math.PI / size) * i;
			y[i] = Math.sin(x[i]);
		}
		
		Plot plot = new Plot();
		plot.add(x, y, Color.BLACK, Plot.LINE, 1);
			
		JFrame frame = new JFrame("Plot test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 500);
		frame.getContentPane().add(plot);
		frame.setVisible(true);
	}
		
}
