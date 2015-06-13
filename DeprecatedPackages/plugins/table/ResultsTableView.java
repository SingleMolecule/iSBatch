/*
 * 
 */
package table;

import ij.IJ;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

// TODO: Auto-generated Javadoc
/**
 * The Class ResultsTableView.
 */
public class ResultsTableView implements ActionListener, PlugIn {

	/** The results. */
	private ResultsTable results;
	
	/** The frame. */
	private JFrame frame;
	
	/** The table. */
	private JTable table;
	
	/** The table model. */
	private AbstractTableModel tableModel;
	
	/** The save as menu item. */
	private JMenuItem saveAsMenuItem = new JMenuItem("Save As", KeyEvent.VK_S);
	
	/** The rename menu item. */
	private JMenuItem renameMenuItem = new JMenuItem("Rename", KeyEvent.VK_R);
	
	/** The duplicate menu item. */
	private JMenuItem duplicateMenuItem = new JMenuItem("Duplicate", KeyEvent.VK_D);
	
	/** The cut menu item. */
	private JMenuItem cutMenuItem = new JMenuItem("Cut");
	
	/** The copy menu item. */
	private JMenuItem copyMenuItem = new JMenuItem("Copy", KeyEvent.VK_C);;
	
	/** The clear menu item. */
	private JMenuItem clearMenuItem = new JMenuItem("Clear");
	
	/** The select all menu item. */
	private JMenuItem selectAllMenuItem = new JMenuItem("Select All", KeyEvent.VK_A);
	
	/** The sort menu item. */
	private JMenuItem sortMenuItem = new JMenuItem("Sort", KeyEvent.VK_S);
	
	/** The plot menu item. */
	private JMenuItem plotMenuItem = new JMenuItem("Plot", KeyEvent.VK_P);
	
	/** The filter menu item. */
	private JMenuItem filterMenuItem = new JMenuItem("Filter", KeyEvent.VK_F);
	
	/** The to image j results table menu item. */
	private JMenuItem toImageJResultsTableMenuItem = new JMenuItem("To ImageJ Results Table");
	
	/**
	 * Instantiates a new results table view.
	 */
	public ResultsTableView() {
		
	}
	
	/**
	 * Instantiates a new results table view.
	 *
	 * @param results the results
	 * @param title the title
	 */
	public ResultsTableView(ResultsTable results, String title) {
		
		this.results = results;
		
		createFrame(title);
		
		// hide the original results table
		Window resultsTableWindow = WindowManager.getWindow(title);
		
		if (resultsTableWindow != null) {
			resultsTableWindow.setVisible(false);
			WindowManager.removeWindow(resultsTableWindow);
		}
		
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				WindowManager.removeWindow(frame);
			}
			
		});
		
		// add window to window manager
		WindowManager.addWindow(frame);
		
	}
	
	/**
	 * Creates the frame.
	 *
	 * @param title the title
	 */
	private void createFrame(String title) {
		
		tableModel = new AbstractTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				if (columnIndex == 0)
					return rowIndex + 1;
				
				return results.getStringValue(columnIndex - 1, rowIndex);
			}
			
			@Override
			public String getColumnName(int columnIndex) {
				if (columnIndex == 0)
					return "Row";
				
				return results.getColumnHeading(columnIndex - 1);
			}

			@Override
			public int getRowCount() {
				return results.getCounter();
			}
			
			@Override
			public int getColumnCount() {
				return results.getLastColumn() + 2;
			}
			
			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				double value = Double.parseDouble((String)aValue);
				results.setValue(columnIndex - 1, rowIndex, value);
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return columnIndex > 0;
			}
			
		};
		
		table = new JTable(tableModel);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		for (int i = 0; i < table.getColumnCount(); i++)
			table.getColumnModel().getColumn(i).setPreferredWidth(75);
		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane scrollPane = new JScrollPane(table);
		
		JMenuBar mb = new JMenuBar();
		
		// file menu
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		fileMenu.add(saveAsMenuItem);
		fileMenu.add(renameMenuItem);
		fileMenu.add(duplicateMenuItem);
		
		mb.add(fileMenu);
		
		// edit menu
		JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);
		
		editMenu.add(cutMenuItem);
		editMenu.add(copyMenuItem);
		editMenu.add(clearMenuItem);
		editMenu.add(selectAllMenuItem);
		editMenu.addSeparator();
		editMenu.add(filterMenuItem);
		editMenu.add(sortMenuItem);
		editMenu.add(plotMenuItem);
		editMenu.addSeparator();
		editMenu.add(toImageJResultsTableMenuItem);
		
		mb.add(editMenu);

		// set action listeners
		saveAsMenuItem.addActionListener(this);
		renameMenuItem.addActionListener(this);
		duplicateMenuItem.addActionListener(this);
		cutMenuItem.addActionListener(this);
		copyMenuItem.addActionListener(this);
		clearMenuItem.addActionListener(this);
		selectAllMenuItem.addActionListener(this);
		filterMenuItem.addActionListener(this);
		sortMenuItem.addActionListener(this);
		plotMenuItem.addActionListener(this);
		toImageJResultsTableMenuItem.addActionListener(this);
		
		frame = new JFrame(title);
		frame.setSize(400, 300);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(scrollPane);
		frame.setJMenuBar(mb);
		frame.setVisible(true);
	}
	
	/**
	 * Gets the results.
	 *
	 * @return the results
	 */
	public ResultsTable getResults() {
		return results;
	}
	
	/**
	 * Run.
	 *
	 * @param arg0 the arg0
	 */
	@Override
	public void run(String arg0) {
		
		String[] resultsTableTitles = ResultsTableUtil.getResultsTableTitles();
		
		if (resultsTableTitles.length == 0) {
			IJ.showMessage("No open results tables!");
			return;
		}
		
		GenericDialog dialog = new GenericDialog("SMB Results Table");
		dialog.addChoice("Table", resultsTableTitles, resultsTableTitles[0]);
		dialog.showDialog();
		
		if (dialog.wasCanceled())
			return;
		
		String title = dialog.getNextChoice();
		ResultsTable rt = ResultsTableUtil.getResultsTable(title);
		
		// hide the original results table
		WindowManager.getWindow(title).setVisible(false);
		
		new ResultsTableView(rt, title);
	}
	
	/**
	 * Action performed.
	 *
	 * @param e the e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == saveAsMenuItem)
			saveAs();
		else if (e.getSource() == renameMenuItem)
			rename();
		else if (e.getSource() == duplicateMenuItem)
			duplicate();
		else if (e.getSource() == cutMenuItem)
			cut();
		else if (e.getSource() == copyMenuItem)
			copy();
		else if (e.getSource() == clearMenuItem)
			clear();
		else if (e.getSource() == selectAllMenuItem)
			selectAll();
		else if (e.getSource() == filterMenuItem)
			filter();
		else if (e.getSource() == sortMenuItem)
			sort();
		else if (e.getSource() == plotMenuItem)
			plot();
		else if (e.getSource() == toImageJResultsTableMenuItem)
			toImageJResultsTable();
		
	}
	
	/**
	 * Save as.
	 */
	protected void saveAs() {
		try {
			results.saveAs("");
		} catch (IOException e) {
			IJ.showMessage(e.getMessage());
		}
	}
	
	/**
	 * Rename.
	 */
	protected void rename() {
		String title = JOptionPane.showInputDialog("Table name", frame.getTitle());
		
		if (title != null) {
			WindowManager.removeWindow(frame);
			frame.setTitle(title);
			WindowManager.addWindow(frame);
		}
	}
	
	/**
	 * Duplicate.
	 */
	protected void duplicate() {
		new ResultsTableView((ResultsTable) results.clone(), WindowManager.getUniqueName(frame.getTitle()));
	}
	
	/**
	 * Cut.
	 */
	protected void cut() {
		copy();
		clear();
	}
	
	/**
	 * Copy.
	 */
	protected void copy() {
		
		final int[] selectedRows = table.getSelectedRows();
		
		Transferable transferable = new Transferable() {
			
			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return DataFlavor.imageFlavor.equals(flavor);
			}
			
			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[]{DataFlavor.stringFlavor};
			}
			
			@Override
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				
				if (flavor.equals(DataFlavor.stringFlavor)) {
					String csv = "";
					
					for (int i = 0; i < selectedRows.length; i++)
						csv += results.getRowAsString(selectedRows[i]) + "\n";
					
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
	 * Clear.
	 */
	protected void clear() {
		ResultsTableUtil.delete(results, table.getSelectedRows());
		
		table.clearSelection();
		tableModel.fireTableDataChanged();
	}
	
	/**
	 * Select all.
	 */
	protected void selectAll() {
		table.selectAll();
	}
	
	/**
	 * Filter.
	 */
	protected void filter() {
		ResultsTableFilter filter = new ResultsTableFilter(results);
		filter.run("");
		tableModel.fireTableDataChanged();
	}
	
	/**
	 * Sort.
	 */
	protected void sort() {
		ResultsTableSorter sorter = new ResultsTableSorter(results);
		sorter.run("");
		tableModel.fireTableDataChanged();
	}
	
	/**
	 * Plot.
	 */
	protected void plot() {
		ResultsTablePlotter plotter = new ResultsTablePlotter(results);
		plotter.run("");
	}
	
	/**
	 * To image j results table.
	 */
	protected void toImageJResultsTable() {
		
		WindowManager.removeWindow(frame);
		
		//frame.setVisible(false);
		//frame.dispose();
		
		//results.show(frame.getTitle());
		
	}
	
}
