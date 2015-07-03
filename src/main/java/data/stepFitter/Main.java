/************************************************************************
 * 				iSBatch  Copyright (C) 2015  							*
 *		Victor E. A. Caldas -  v.e.a.caldas at rug.nl					*
 *		C. Michiel Punter - c.m.punter at rug.nl						*
 *																		*
 *	This program is distributed in the hope that it will be useful,		*
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of		*
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the		*
 *	GNU General Public License for more details.						*
 *	You should have received a copy of the GNU General Public License	*
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ***********************************************************************/
package data.stepFitter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

// TODO: Auto-generated Javadoc
/**
 * The Class Main.
 */
public class Main implements ActionListener {
	
	/** The frame. */
	JFrame frame = new JFrame("step fitter");
	
	/** The open button. */
	JButton openButton = new JButton("open file");
	
	/** The dec button. */
	JButton decButton = new JButton("decrease steps");
	
	/** The inc button. */
	JButton incButton = new JButton("increase steps");
	
	/** The export button. */
	JButton exportButton = new JButton("export steps");
	
	/** The export all button. */
	JButton exportAllButton = new JButton("export all fits");
	
	/** The save image button. */
	JButton saveImageButton = new JButton("save plot");
	
	/** The statistics text field. */
	JLabel statisticsTextField = new JLabel("chi^2 : NaN chi^2 Counter Fit : NaN Ratio : NaN");
	
	/** The current directory. */
	File currentDirectory;
	
	/** The x. */
	double[] x;
	
	/** The y. */
	double[] y;
	
	/** The plot. */
	Plot plot = new Plot();
	
	/** The fitter. */
	StepFitter fitter;
	
	/** The steps. */
	int steps = 0;
	
	/** The max steps. */
	int maxSteps;
	
	/**
	 * Instantiates a new main.
	 */
	public Main() {
		
		openButton.addActionListener(this);
		decButton.addActionListener(this);
		incButton.addActionListener(this);
		exportButton.addActionListener(this);
		exportAllButton.addActionListener(this);
		saveImageButton.addActionListener(this);
		
		frame.setSize(800, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		JPanel controlsPanel = new JPanel();
		controlsPanel.add(openButton);
		controlsPanel.add(decButton);
		controlsPanel.add(incButton);
		controlsPanel.add(exportButton);
		controlsPanel.add(exportAllButton);
		controlsPanel.add(saveImageButton);
		
		contentPane.add(statisticsTextField, BorderLayout.NORTH);
		contentPane.add(controlsPanel, BorderLayout.SOUTH);
		contentPane.add(plot, BorderLayout.CENTER);
		
		frame.setVisible(true);
	}
	
	
	/**
	 * Open file.
	 */
	public void openFile() {
		JFileChooser fileChooser = new JFileChooser();
		
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File f = fileChooser.getSelectedFile();
			
			currentDirectory = f.getParentFile();
			
			try {
				Scanner scanner = new Scanner(f);
				
				ArrayList<Double> xValues = new ArrayList<Double>();
				ArrayList<Double> yValues = new ArrayList<Double>();
				
				while (scanner.hasNext()) {
					xValues.add(scanner.nextDouble());
					yValues.add(scanner.nextDouble());
				}
				
				scanner.close();
				
				maxSteps = xValues.size();
				x = new double[xValues.size()];
				y = new double[yValues.size()];
				
				for (int i = 0; i < xValues.size(); i++) {
					x[i] = xValues.get(i);
					y[i] = yValues.get(i);
				}
				
				fitter = new StepFitter(y, 1);
				showSteps();
				
			} catch (FileNotFoundException e) { // $codepro.audit.disable logExceptions
				JOptionPane.showMessageDialog(frame, "could not open file");
			}
		}
		
	}
	
	/**
	 * Save file.
	 */
	public void saveFile() {
		JFileChooser fileChooser = new JFileChooser(currentDirectory);
		
		if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File f = fileChooser.getSelectedFile();
			
			try {
				FileOutputStream os = new FileOutputStream(f);
				
				double[] x = fitter.getStepsX();
				double[] y = fitter.getStepsY();
				
				os.write(String.format("chi^2\t%f\r\n", fitter.getChiSquared()).getBytes());
				os.write(String.format("counter chi^2\t%f\r\n", fitter.getCounterChiSquared()).getBytes());
				os.write(String.format("ratio\t%f\r\n", fitter.getChiSquared() / fitter.getCounterChiSquared()).getBytes());
				
				for (int i = 0; i < x.length; i += 2) {
					os.write(String.format("%f\t%f\t%f\t%f\r\n", x[i], x[i + 1], x[i + 1] - x[i], y[i]).getBytes());
				}
				
				os.close();
				
				JOptionPane.showMessageDialog(frame, "file saved as " + f.getPath());
				
			} catch (Exception e) {
				JOptionPane.showMessageDialog(frame, "could not save file");
			}
		}
	}
	
	/**
	 * Save image.
	 */
	public void saveImage() {
		JFileChooser fileChooser = new JFileChooser(currentDirectory);
		
		if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			
			// make sure it is saved as a png file
			if (!file.getName().toLowerCase().endsWith(".png"))
				file = new File(file.getPath() + ".png");
			
			BufferedImage image = new BufferedImage(plot.getWidth(), plot.getHeight(), BufferedImage.TYPE_INT_ARGB);
			plot.paint(image.createGraphics());
			
			try {
				ImageIO.write(image, "png", file);
			}
			catch (IOException e) {
				JOptionPane.showMessageDialog(frame, "could not save image");
			}
		}
	}
	
	/**
	 * Save all fits.
	 */
	public void saveAllFits() {
		
		JFileChooser fileChooser = new JFileChooser(currentDirectory);
		
		if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			File f = fileChooser.getSelectedFile();
			
			try {
				FileOutputStream os = new FileOutputStream(f);
				StepFitter fitter2 = new StepFitter(y, 1);
				
				os.write("steps\tchi\tcounter chi\tratio\r\n".getBytes());
				
				for (int n = 1; n < x.length - 1; n++) {
					os.write(String.format("%d\t", n).getBytes());
					os.write(String.format("%f\t", fitter2.getChiSquared()).getBytes());
					os.write(String.format("%f\t", fitter2.getCounterChiSquared()).getBytes());
					os.write(String.format("%f\r\n", fitter2.getChiSquared() / fitter2.getCounterChiSquared()).getBytes());
					
					fitter2.addStep();					
				}
				
				os.close();
				
				JOptionPane.showMessageDialog(frame, "file saved as " + f.getPath());
				
			} catch (Exception e) {
				e.printStackTrace();
				//JOptionPane.showMessageDialog(frame, "could not save file");
			}
		}
	}

	/**
	 * Show steps.
	 */
	public void showSteps() {
		if (fitter == null)
			return;
		
		plot.clear();
		plot.add(x, y, Color.BLACK, Plot.LINE, 1);
		
		fitter.clear();
		for (int i = 0; i < steps; i++)
			 fitter.addStep();
		
		plot.add(fitter.getCounterStepsX(), fitter.getCounterStepsY(), Color.BLUE, Plot.LINE, 2);
		plot.add(fitter.getStepsX(), fitter.getStepsY(), Color.RED, Plot.LINE, 2);
		
		statisticsTextField.setText(String.format("steps : %d chi^2 : %f chi^2 Counter Fit : %f Ratio : %f", steps + 1, fitter.getChiSquared(), fitter.getCounterChiSquared(), fitter.getChiSquared() / fitter.getCounterChiSquared()));
	}
	
	/**
	 * Action performed.
	 *
	 * @param e the e
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == openButton) {
			openFile();
		}
		else if (e.getSource() == incButton) {
			if (steps < maxSteps - 1)
				steps++;
			showSteps();
		}
		else if (e.getSource() == decButton) {
			if (steps > 0)
				steps--;
			
			showSteps();
		}
		else if (e.getSource() == exportButton) {
			saveFile();
		}
		else if (e.getSource() == saveImageButton) {
			saveImage();
		}
		else if (e.getSource() == saveImageButton) {
			saveAllFits();
		}
		else if (e.getSource() == exportAllButton) {
			saveAllFits();
		}
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
		
		new Main();
	}

}
