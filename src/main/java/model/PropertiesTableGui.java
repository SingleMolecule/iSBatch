package model;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PropertiesTableGui extends JFrame implements WindowListener,
		ListSelectionListener, TableModelListener, ActionListener {

	String[] columnNames = { "Property", "Value" };
	JButton btnSave;
	JButton btnCancel;
	/**
	 * 
	 */
	private Node node;
	private static final long serialVersionUID = 1L;
	private JTable table;
	private HashMap<String, String> map;

	public void display(Object[][] data) {

		setSize(400, 400);
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);

		btnSave = new JButton("Save");
		btnSave.addActionListener(this);
		panel.add(btnSave);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		panel.add(btnCancel);

		table = new JTable(data, columnNames);

		getContentPane().add(table, BorderLayout.CENTER);
		table.getModel().addTableModelListener(this);
		this.setTitle("Node Properties");
//		this.pack();
		setVisible(true);

	}

	public PropertiesTableGui(HashMap<String, String> myMap) {
		System.out.println("Opening");
		this.map = myMap;
		System.out.println(map.size());
		Object[][] myData = getObject(myMap);
		display(myData);
	}
	

	public PropertiesTableGui(Node selectedNode) {
		this.node = selectedNode;
		System.out.println("Opening");
		this.map = selectedNode.getProperties();
		System.out.println(map.size());
		Object[][] myData = getObject(map);
		display(myData);
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		dispose();
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}

	public static void main(String[] args) {
		HashMap<String, String> myMap = new HashMap<String, String>();
		myMap.put("Value10", "Key1");
		myMap.put("Value12", "Key12");
		myMap.put("Value13", "Key13");
		myMap.put("Value14", "Key14");
		myMap.put("Value15", "Key15");
		myMap.put("Value16", "Key16");
		myMap.put("Value17", "Key17");
		myMap.put("Value18", "Key18");
		myMap.put("Value19", "Key19");
		myMap.put("Value20", "Key10");

		new PropertiesTableGui(myMap);
		//PropertiesTableGui swingListenerDemo = new PropertiesTableGui(myData);
	}

	private static Object[][] getObject(HashMap<String, String> map) {

		Object[][] data = new Object[map.size()][2];
		Set<?> entries = map.entrySet();
		Iterator<?> entriesIterator = entries.iterator();

		int i = 0;
		while (entriesIterator.hasNext()) {
			Map.Entry mapping = (Map.Entry) entriesIterator.next();
			data[i][0] = mapping.getKey();
			data[i][1] = mapping.getValue();
			i++;
		}
		return data;
	}

	public static TableModel toTableModel(HashMap<?, ?> map) {
		DefaultTableModel model = new DefaultTableModel(new Object[] {
				"Property", "Value" }, 0);
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			model.addRow(new Object[] { entry.getKey(), entry.getValue() });
		}
		return model;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		int selectedRow = table.getSelectedRow();
		System.out.println(table.getColumnCount());
		System.out.println("Property : " + table.getValueAt(selectedRow, 0));
		System.out.println("Value: " + table.getValueAt(selectedRow, 1));
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		System.out.println("triggered");
		int row = e.getFirstRow();
		int column = e.getColumn();
		TableModel model = (TableModel) e.getSource();
		String columnName = model.getColumnName(column);
		Object data = model.getValueAt(row, column);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCancel) {
			dispose();

		} else if (e.getSource() == btnSave) {
			System.out.println("I will save");
			printTable(table);
			updateProperties();
			dispose();
		}
	}

	private void updateProperties() {
		//get the displayed JTable
		Object[][] data = getTableData(table);
		int nrows = data.length;
		System.out.println(map.size());
		node.getProperties().clear();
		for(int i = 0; i<nrows; i++){
			String key = (String) data[i][0];
			String value =(String) data[i][1];
			System.out.println(key + "~" + value);
			node.getProperties().put(key, value);
		}
		
	}

	public void printTable(JTable table) {
		int nRow = table.getRowCount();
		TableModel dtm =  table.getModel();
		for (int i = 0; i < nRow; i++) {
			String property = (String) dtm.getValueAt(i, 0);
			String value = (String) dtm.getValueAt(i, 1);
			System.out.println(property + " = " + value);
		}
	}

	public Object[][] getTableData(JTable table) {
		TableModel dtm =  table.getModel();
		int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
		Object[][] tableData = new Object[nRow][nCol];
		for (int i = 0; i < nRow; i++)
			for (int j = 0; j < nCol; j++)
				tableData[i][j] = dtm.getValueAt(i, j);
		return tableData;
	}

}
