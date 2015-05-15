/*
 * 
 */
package analysis;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

// TODO: Auto-generated Javadoc
/**
 * The Class Plot.
 */
public class Plot extends JComponent implements ActionListener {

	/**
	 * The Enum Type.
	 */
	protected enum Type {
		
		/** The line. */
		LINE,
		
		/** The scatter. */
		SCATTER,
		
		/** The histogram. */
		HISTOGRAM,
		
		/** The error bars. */
		ERROR_BARS
	};
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The transform. */
	private AffineTransform transform;
	
	/** The plot types. */
	protected ArrayList<Type> plotTypes = new ArrayList<Type>();
	
	/** The plot colors. */
	protected ArrayList<Color> plotColors = new ArrayList<Color>();
	
	/** The plot line widths. */
	protected ArrayList<Float> plotLineWidths = new ArrayList<Float>();
	
	/** The font. */
	protected Font font = new Font("Courier", Font.PLAIN, 14);
	
	/** The x axis label. */
	protected String xAxisLabel = "x";
	
	/** The y axis label. */
	protected String yAxisLabel = "y";
	
	/** The caption. */
	protected String caption = "";
	
	/** The left margin. */
	protected int leftMargin = 100;
	
	/** The bottom margin. */
	protected int bottomMargin = 50;
	
	/** The plot coordinates. */
	protected ArrayList<double[]> plotCoordinates = new ArrayList<double[]>();
	
	/** The pixel coordinates. */
	protected ArrayList<double[]> pixelCoordinates = new ArrayList<double[]>();
	
	/** The original bounds. */
	protected Rectangle2D.Double originalBounds;
	
	/** The bounds. */
	protected Rectangle2D.Double bounds;
	
	/** The mouse position. */
	protected Point mousePosition = new Point();
	
	/** The menu. */
	private JPopupMenu menu = new JPopupMenu();
	
	/** The zoom in menu item. */
	private JRadioButtonMenuItem zoomInMenuItem = new JRadioButtonMenuItem("Zoom In", true);
	
	/** The zoom out menu item. */
	private JRadioButtonMenuItem zoomOutMenuItem = new JRadioButtonMenuItem("Zoom Out");
	
	/** The save menu item. */
	private JMenuItem saveMenuItem = new JMenuItem("Save");
	
	/** The copy menu item. */
	private JMenuItem copyMenuItem = new JMenuItem("Copy");
	
	/** The adjust axis menu item. */
	private JMenuItem adjustAxisMenuItem = new JMenuItem("Adjust Axis");
	
	/** The select region menu item. */
	private JMenuItem selectRegionMenuItem = new JMenuItem("Select Region");
	
	/** The zoom factor. */
	private double zoomFactor = 1.5;
	
	/** The current directory. */
	private File currentDirectory;
	
	/** The is region selection. */
	private boolean isRegionSelection = false;
	
	/** The region selection. */
	private Rectangle regionSelection = new Rectangle();
	
	/**
	 * Instantiates a new plot.
	 */
	public Plot() {
		
		saveMenuItem.addActionListener(this);
		copyMenuItem.addActionListener(this);
		adjustAxisMenuItem.addActionListener(this);
		selectRegionMenuItem.addActionListener(this);
		
		menu.add(zoomInMenuItem);
		menu.add(zoomOutMenuItem);
		menu.add(selectRegionMenuItem);
		menu.add(saveMenuItem);
		menu.add(copyMenuItem);
		menu.add(adjustAxisMenuItem);
		
		ButtonGroup group = new ButtonGroup();
	    group.add(zoomInMenuItem);
	    group.add(zoomOutMenuItem);
	    
		addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				
				if (!isRegionSelection) {
					if (e.getWheelRotation() < 0)
						zoom(1 / zoomFactor);
					else
						zoom(zoomFactor);
				}
			}
			
		});
		
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				mousePosition = e.getPoint();
				regionSelection.setLocation(e.getPoint()); 
				repaint();
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				
				if (isRegionSelection) {
					regionSelection.width = e.getX() - regionSelection.x;
					regionSelection.height = e.getY() - regionSelection.y;
				}
				else {
					double dx = mousePosition.getX() - e.getPoint().getX();
					double dy = mousePosition.getY() - e.getPoint().getY();
					
					mousePosition = e.getPoint();
					
					bounds.x += dx / transform.getScaleX();
					bounds.y += dy / transform.getScaleY();
				}
				
				repaint();
			}
		});
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					resetBounds();
				else if (e.getButton() == MouseEvent.BUTTON1) {
					
					if (zoomInMenuItem.isSelected())
						zoom(1 / 1.5);
					else
						zoom(1.5);
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e){
				if (e.isPopupTrigger() || (e.isShiftDown() && e.getButton() == MouseEvent.BUTTON1))
					menu.show(e.getComponent(), e.getX(), e.getY());
				
				if (isRegionSelection) {
					
					try {
						Point2D.Double upperLeft = new Point2D.Double(regionSelection.x, regionSelection.y);
						Point2D.Double lowerRight = new Point2D.Double(regionSelection.x + regionSelection.width, regionSelection.y + regionSelection.height);

						transform.inverseTransform(upperLeft, upperLeft);
						transform.inverseTransform(lowerRight, lowerRight);
						
						bounds = new Rectangle2D.Double(upperLeft.x, upperLeft.y, 0, 0);
						bounds.add(lowerRight);
						
						isRegionSelection = false;
						
						repaint();
					} catch (NoninvertibleTransformException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
				
			}
		});
		
	}
	
	/**
	 * Sets the bounds.
	 *
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 */
	public void setBounds(double x, double y, double width, double height) {
		bounds.x = x;
		bounds.y = y;
		bounds.width = width;
		bounds.height = height;
		repaint();
	}
	
	/**
	 * Sets the original bounds.
	 *
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 */
	public void setOriginalBounds(double x, double y, double width, double height) {
		originalBounds.x = x;
		originalBounds.y = y;
		originalBounds.width = width;
		originalBounds.height = height;
		setBounds(x, y, width, height);
	}
	
	/**
	 * Reset bounds.
	 */
	public void resetBounds() {
		bounds = (Rectangle2D.Double)originalBounds.clone();
		repaint();
	}

	/**
	 * Adds the line plot.
	 *
	 * @param x the x
	 * @param y the y
	 * @param from the from
	 * @param to the to
	 * @param c the c
	 * @param lineWidth the line width
	 */
	public void addLinePlot(double[] x, double[] y, int from, int to, Color c, float lineWidth) {
		addLinePlot(Arrays.copyOfRange(x, from, to), Arrays.copyOfRange(y, from, to), c, lineWidth);
	}
	
	/**
	 * Adds the scatter plot.
	 *
	 * @param x the x
	 * @param y the y
	 * @param from the from
	 * @param to the to
	 * @param c the c
	 * @param lineWidth the line width
	 */
	public void addScatterPlot(double[] x, double[] y, int from, int to, Color c, float lineWidth) {
		addScatterPlot(Arrays.copyOfRange(x, from, to), Arrays.copyOfRange(y, from, to), c, lineWidth);
	}
	
	/**
	 * Adds the histogram.
	 *
	 * @param y the y
	 * @param from the from
	 * @param to the to
	 * @param bins the bins
	 * @param c the c
	 * @param lineWidth the line width
	 */
	public void addHistogram(double[] y, int from, int to, int bins, Color c, float lineWidth) {
		addHistogram(Arrays.copyOfRange(y, from, to), bins, c, lineWidth);
	}
	
	/**
	 * Adds the line plot.
	 *
	 * @param x the x
	 * @param y the y
	 * @param c the c
	 * @param lineWidth the line width
	 */
	public void addLinePlot(double[] x, double[] y, Color c, float lineWidth) {
		addPlot(x, y, Type.LINE, c, lineWidth);
	}

	/**
	 * Adds the error bars.
	 *
	 * @param x the x
	 * @param y the y
	 * @param error the error
	 * @param c the c
	 * @param lineWidth the line width
	 */
	public void addErrorBars(double[] x, double[] y, double[] error, Color c, float lineWidth) {
		
		double[] x1 = new double[x.length * 2];
		double[] y1 = new double[x1.length];
		
		for (int i = 0; i < x.length; i++) {
			x1[i * 2] = x[i];
			x1[i * 2 + 1] = x[i];
			y1[i * 2] = y[i] - error[i];
			y1[i * 2 + 1] = y[i] + error[i];
		}
		
		addPlot(x1, y1, Type.ERROR_BARS, c, lineWidth);
	}
	
	/**
	 * Adds the scatter plot.
	 *
	 * @param x the x
	 * @param y the y
	 * @param c the c
	 * @param lineWidth the line width
	 */
	public void addScatterPlot(double[] x, double[] y, Color c, float lineWidth) {
		addPlot(x, y, Type.SCATTER, c, lineWidth);
	}
	
	/**
	 * Adds the histogram.
	 *
	 * @param y the y
	 * @param bins the bins
	 * @param c the c
	 * @param lineWidth the line width
	 */
	public void addHistogram(double[] y, int bins, Color c, float lineWidth) {
		double min = y[0];
		double max = min;
		
		for (int i = 0; i < y.length; i++) {
			if (y[i] < min) min = y[i];
			if (y[i] > max) max = y[i];
		}
		
		double width = (max - min) / (bins - 1);
		double[] yCounts = new double[bins + 1];
		double[] xBins = new double[bins + 1];
		
		for (int i = 0; i < y.length; i++) {
			int bin = (int)((y[i] - min) / width);
			yCounts[bin]++;
		}
		
		for (int i = 0; i < bins; i++)
			xBins[i] = min + i * width; 
		
		xBins[bins] = xBins[bins - 1];
		yCounts[bins] = yCounts[bins - 1];
				
		addPlot(xBins, yCounts, Type.HISTOGRAM, c, lineWidth);
	}
	
	/**
	 * Adds the plot.
	 *
	 * @param x the x
	 * @param y the y
	 * @param type the type
	 * @param c the c
	 * @param lineWidth the line width
	 */
	private void addPlot(double[] x, double[] y, Type type, Color c, float lineWidth) {
		double[] plot = new double[x.length * 2];
		
		if (originalBounds == null)
			originalBounds = new Rectangle2D.Double(x[0], y[0], 0, 0);
		
		for (int i = 0; i < x.length; i++) {
			plot[i * 2] = x[i];
			plot[i * 2 + 1] = y[i];
			originalBounds.add(x[i], y[i]);
		}
		
		if (bounds == null)
			bounds = (Rectangle2D.Double)originalBounds.clone();
		else
			bounds.add(originalBounds);
		
		plotCoordinates.add(plot);
		pixelCoordinates.add(new double[plot.length]);	// make space for translated coordinates
		plotTypes.add(type);
		
		plotColors.add(c);
		plotLineWidths.add(lineWidth);
	}
	
	/**
	 * Clear.
	 */
	public void clear() {
		plotCoordinates.clear();
		pixelCoordinates.clear();
		plotColors.clear();
		plotLineWidths.clear();
		plotTypes.clear();
		bounds = null;
	}
	
	/**
	 * Sets the x axis label.
	 *
	 * @param xAxisLabel the new x axis label
	 */
	public void setxAxisLabel(String xAxisLabel) {
		this.xAxisLabel = xAxisLabel;
	}

	/**
	 * Sets the y axis label.
	 *
	 * @param yAxisLabel the new y axis label
	 */
	public void setyAxisLabel(String yAxisLabel) {
		this.yAxisLabel = yAxisLabel;
	}
	
	/**
	 * Sets the caption.
	 *
	 * @param caption the new caption
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * Sets the font.
	 *
	 * @param font the new font
	 */
	public void setFont(Font font) {
		this.font = font;			
	}
	
	/**
	 * Paint axis.
	 *
	 * @param g the g
	 */
	public void paintAxis(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		int width = getWidth() - leftMargin;
		int height = getHeight() - bottomMargin;
		
		double xStepSize = getStepSize(bounds.width, width / 50);	// draw a number every 50 pixels
		double yStepSize = getStepSize(bounds.height, height / 50);
		
		for (double x = bounds.x - (bounds.x % xStepSize); x < bounds.x + bounds.width; x += xStepSize) {
			Point2D.Double p = new Point2D.Double(x, 0);
			transform.transform(p, p);
			
			if (p.x > leftMargin) {
				g2d.setColor(Color.LIGHT_GRAY);
				g2d.drawLine((int)p.x, 0, (int)p.x, height);
				g2d.setColor(Color.BLACK);
				g2d.drawLine((int)p.x, height, (int)p.x, height + 5);
				g2d.drawString(String.format("%2.3g", x), (int)p.x - 10, height + 15);
			}
		}
		
		AffineTransform at = g2d.getTransform();
		g2d.translate(leftMargin - 85, height / 2 + g.getFontMetrics().stringWidth(yAxisLabel) / 2);
		g2d.rotate(-Math.PI / 2);
		g2d.drawString(yAxisLabel, 0, 0);		
		g2d.setTransform(at);
		
		for (double y = bounds.y - (bounds.y % yStepSize); y < bounds.y + bounds.height; y += yStepSize) {
			Point2D.Double p = new Point2D.Double(0, y);
			transform.transform(p, p);
			
			if (p.y < height) {
				g2d.setColor(Color.LIGHT_GRAY);
				g2d.drawLine(leftMargin, (int)p.y, getWidth(), (int)p.y);
				g2d.setColor(Color.BLACK);
				g2d.drawLine(leftMargin - 5, (int)p.y, leftMargin, (int)p.y);
				g2d.drawString(String.format("%2.3g", y), (int)leftMargin - 75, (int)p.y + 5);
			}
		}
		
		g2d.drawString(xAxisLabel, leftMargin + width / 2 - g.getFontMetrics().stringWidth(xAxisLabel) / 2, height + 40);
		g2d.drawString(caption, leftMargin + 20, 20);
		
		// box around plot
		g.drawRect(leftMargin, 0, width, height);
	}
	
	/**
	 * Paint plot.
	 *
	 * @param g the g
	 */
	public void paintPlot(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		Point selectedPoint = new Point();
		Point2D.Double selectedCoordinate = new Point2D.Double();
		
		int width = getWidth() - leftMargin;
		int height = getHeight() - bottomMargin;
		
		// clip to the plot area (to prevent drawing outside of the plot area)
		g.clipRect(leftMargin, 0, width, height);
		
		// draw plots
		for (int i = 0; i < plotCoordinates.size(); i++) {
			double[] coordinates = pixelCoordinates.get(i);
			
			g2d.setColor(plotColors.get(i));
			g2d.setStroke(new BasicStroke(plotLineWidths.get(i)));
			
			for (int j = 0; j < coordinates.length; j += 2) {
				
				switch (plotTypes.get(i)) {
				case LINE:
					if (j + 2 < coordinates.length)
						g2d.drawLine((int)coordinates[j], (int)coordinates[j + 1], (int)coordinates[j + 2], (int)coordinates[j + 3]);
					break;
				case SCATTER:
					g2d.drawOval((int)coordinates[j] - 1, (int)coordinates[j + 1] - 1, 2, 2);
					break;
				case HISTOGRAM:
					
					if (j + 2 < coordinates.length) {
						g2d.drawLine((int)coordinates[j], (int)coordinates[j + 1], (int)coordinates[j + 2], (int)coordinates[j + 1]);
						g2d.drawRect((int)coordinates[j], (int)coordinates[j + 1], (int)(coordinates[j + 2] - coordinates[j]), (int)(transform.getTranslateY() - coordinates[j + 1]));
					}
					
					break;
				case ERROR_BARS:
					
					if (j % 4 == 0) {
						g2d.drawLine((int)coordinates[j] - 1, (int)coordinates[j + 1], (int)coordinates[j + 2] + 1, (int)coordinates[j + 1]);
						g2d.drawLine((int)coordinates[j] - 1, (int)coordinates[j + 3], (int)coordinates[j + 2] + 1, (int)coordinates[j + 3]);
						g2d.drawLine((int)coordinates[j], (int)coordinates[j + 1], (int)coordinates[j + 2], (int)coordinates[j + 3]);
					}
					
				}
				
				if (mousePosition.distance((int)coordinates[j], (int)coordinates[j + 1]) < mousePosition.distance(selectedPoint)) {
					selectedPoint = new Point((int)coordinates[j], (int)coordinates[j + 1]);
					selectedCoordinate = new Point2D.Double(plotCoordinates.get(i)[j], plotCoordinates.get(i)[j + 1]);
				}
					
			}
		}
		
		g2d.setStroke(new BasicStroke(1.0f));
		
		// draw selected point
		if (mousePosition.distance(selectedPoint) < 100) {
			g2d.setColor(Color.RED);
			g2d.drawOval(selectedPoint.x - 3, selectedPoint.y - 3, 6, 6);
			g2d.drawString(String.format("%.3g, %.3g", selectedCoordinate.x, selectedCoordinate.y), selectedPoint.x - 3, selectedPoint.y - 3);
		}
		
		// draw selection region
		if (isRegionSelection) {
			g2d.setColor(Color.RED);
			g2d.drawRect(regionSelection.x, regionSelection.y, regionSelection.width, regionSelection.height);
		}
	}
	
	/**
	 * Paint.
	 *
	 * @param g the g
	 */
	@Override
	public void paint(Graphics g) {
		
		int width = getWidth() - leftMargin;
		int height = getHeight() - bottomMargin;
		
		transform = new AffineTransform();
		transform.translate(leftMargin, height);
		transform.scale(width / bounds.width, -height / bounds.height);
		transform.translate(-bounds.x, -bounds.y);
		
		// transform all plot coordinates to pixel coordinates
		for (int i = 0; i < plotCoordinates.size(); i++) {
			double[] src = plotCoordinates.get(i);
			double[] dst = pixelCoordinates.get(i);
			
			transform.transform(src, 0, dst, 0, src.length / 2);
		}
		
		g.setFont(font);
		paintAxis(g);
		paintPlot(g);
	}
	
	/**
	 * Gets the step size.
	 *
	 * @param range the range
	 * @param steps the steps
	 * @return the step size
	 */
	private double getStepSize(double range, int steps) {
		double step = range / steps;    // e.g. 0.00321
		double magnitude = Math.pow(10, Math.floor(Math.log10(step)));  // e.g. 0.001
		double mostSignificantDigit = Math.ceil(step / magnitude); // e.g. 3.21
		
        if (mostSignificantDigit > 5.0)
            return magnitude * 10.0;
        else if (mostSignificantDigit > 2.0)
            return magnitude * 5.0;
        else
            return magnitude * 2.0;
	}
	
	/**
	 * Show plot.
	 *
	 * @param title the title
	 */
	public void showPlot(String title) {
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(500, 500);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(this, BorderLayout.CENTER);
		frame.setVisible(true);
	}
	
	/**
	 * Zoom.
	 *
	 * @param factor the factor
	 */
	public void zoom(double factor) {
		bounds.x -= bounds.width * (factor - 1) / 2;
		bounds.y -= bounds.height * (factor - 1) / 2;
		bounds.width *= factor;
		bounds.height *= factor;
		repaint();
		
	}
	
	/**
	 * Action performed.
	 *
	 * @param e the e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == saveMenuItem) {
			savePlot();
		}
		else if (e.getSource() == copyMenuItem) {
			copyPlot();
		}
		else if (e.getSource() == adjustAxisMenuItem) {
			adjustAxis();
		}
		else if (e.getSource() == selectRegionMenuItem) {
			regionSelection.width = 0;
			regionSelection.height = 0;
			isRegionSelection = true;
		}
		
	}
	
	/**
	 * Save plot.
	 */
	public void savePlot() {
		JFileChooser fileChooser = new JFileChooser(currentDirectory);
		fileChooser.setFileFilter(new FileNameExtensionFilter("Portable Network Graphics (*.png)", "png"));
		
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			savePlot(file);
		}
	}
	
	/**
	 * Save plot.
	 *
	 * @param file the file
	 */
	public void savePlot(File file) {
		currentDirectory = file.getParentFile();
		
		// make sure the file extension is .png
		if (!file.getName().toLowerCase().endsWith(".png"))
			file = new File(file.getPath() + ".png");
		
		if (file.exists()) {
			if (JOptionPane.showConfirmDialog(this, String.format("Overwrite existing file: %s ?", file.getName())) != JOptionPane.OK_OPTION)
				return;
		}
		
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		paint(image.createGraphics());
		
		try {
			ImageIO.write(image, "png", file);
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(this, String.format("could not save image : %s", e.getMessage()), "Exception", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Copy plot.
	 */
	public void copyPlot() {
		
		Transferable transferable = new Transferable() {
			
			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return DataFlavor.imageFlavor.equals(flavor);
			}
			
			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[]{DataFlavor.imageFlavor, DataFlavor.stringFlavor};
			}
			
			@Override
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				
				if (flavor.equals(DataFlavor.imageFlavor)) {				
					BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
					
					Graphics g = image.createGraphics();
					g.setColor(Color.WHITE);
					g.fillRect(0, 0, getWidth(), getHeight());
					paint(g);
					
			        return image;
				}
				else if (flavor.equals(DataFlavor.stringFlavor)) {
					String csv = "";
					
					for (int i = 0; i < plotCoordinates.size(); i++) {
						double[] values = plotCoordinates.get(i);
						
						for (int j = 0; j < values.length; j += 2)
							csv += String.format("%d\t%f\t%f\n", i, values[j], values[j + 1]);
					}
					
					return csv;
				}
				else {
					throw new UnsupportedFlavorException(flavor);
				}
			}
		};
		
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
	}
	
	/**
	 * Adjust axis.
	 */
	public void adjustAxis() {
		JTextField xFromTextField = new JTextField(Double.toString(bounds.x));
		JTextField xToTextField = new JTextField(Double.toString(bounds.x + bounds.width));
		JTextField yFromTextField = new JTextField(Double.toString(bounds.y));
		JTextField yToTextField = new JTextField(Double.toString(bounds.y + bounds.height));
		
		Object[] obj = new Object[] {
				new JLabel("x from"), xFromTextField,
				new JLabel("x to"), xToTextField,
				new JLabel("y from"), yFromTextField,
				new JLabel("y to"), yToTextField,
		};
		
		int option = JOptionPane.showConfirmDialog(this, obj, "Adjust Axis", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		
		if (option == JOptionPane.OK_OPTION) {
			setBounds(Double.parseDouble(xFromTextField.getText()),
					Double.parseDouble(yFromTextField.getText()),
					Double.parseDouble(xToTextField.getText()) - bounds.x,
					Double.parseDouble(yToTextField.getText()) - bounds.y);
		}
	}
	
	/**
	 * Export data.
	 */
	public void exportData() {
		JFileChooser fileChooser = new JFileChooser(currentDirectory);
		fileChooser.setFileFilter(new FileNameExtensionFilter("Comma Separated Values (*.csv)", "csv"));
		
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			exportData(file);
		}
	}
	
	/**
	 * Export data.
	 *
	 * @param file the file
	 */
	public void exportData(File file) {
		currentDirectory = file.getParentFile();
		
		// make sure the file extension is .csv
		if (!file.getName().toLowerCase().endsWith(".csv"))
			file = new File(file.getPath() + ".csv");
		
		if (file.exists()) {
			if (JOptionPane.showConfirmDialog(this, String.format("Overwrite existing file: %s ?", file.getName())) != JOptionPane.OK_OPTION)
				return;
		}
		
		try {
			FileOutputStream fos = new FileOutputStream(file);
			
			fos.write("dataset,row,x,y\r\n".getBytes("utf-8"));
			
			for (int i = 0; i < plotCoordinates.size(); i++) {
				double[] values = plotCoordinates.get(i);
				
				for (int j = 0; j < values.length; j += 2)
					fos.write(String.format("%d,%d,%f,%f\r\n", i, j, values[j], values[j + 1]).getBytes("utf-8"));
			}
			
			fos.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, String.format("could not export data : %s", e.getMessage()), "Exception", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		Plot plot = new Plot();
		
		int n = 20;
		double[] x = new double[n];
		double[] y = new double[n];
		double[] x2 = new double[n];
		double[] y2 = new double[n];
		double[] error = new double[n];
		
		for (int i = 0; i < n; i++) {
			x[i] = (2 * Math.PI * i) / n;
			y[i] = Math.sin(x[i]);
			x2[i] = (2 * Math.PI * i) / n;
			y2[i] = Math.tan(x[i]);
			error[i] = Math.random() * 0.2;
		}
		
		//plot.addHistogram(y, (int)Math.sqrt(n), Color.BLUE, 3.0f);
		plot.addLinePlot(x, y, Color.BLACK, 1.0f);
		plot.addLinePlot(x2, y2, Color.RED, 1.0f);
		plot.addErrorBars(x, y, error, Color.GRAY, 1.0f);
		plot.setOriginalBounds(0, -1, Math.PI * 2, 2);
		plot.showPlot("test");
	}

}