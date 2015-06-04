package model;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class PropertiesTable extends JFrame implements ListSelectionListener{
	private static final long serialVersionUID = 1L;
	JTable table;

	public PropertiesTable(HashMap<String, String> properties) {
		 this.table =new JTable(toTableModel(properties));
		 
	     this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);       

		 
	     table.setCellSelectionEnabled(true);
		 ListSelectionModel cellSelectionModel = table.getSelectionModel();
		 cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		 cellSelectionModel.addListSelectionListener(this);
		
		
		this.add(new JScrollPane(table));
		this.setTitle("Node Properties");
        this.pack();
        this.setVisible(true);
		
		
		
	}
	

	public PropertiesTable(Node selectedNode) {
		new PropertiesTable(selectedNode.getProperties());
	}


	public static void main(String[] args) {
		HashMap<String, String> myMap = new HashMap<String, String>();
		myMap.put("Value1", "Key1");
		myMap.put("Value12", "Key12");
		myMap.put("Value13", "Key13");
		myMap.put("Value14", "Key14");
		myMap.put("Value15", "Key15");
		myMap.put("Value16", "Key16");
		myMap.put("Value17", "Key17");
		myMap.put("Value18", "Key18");
		myMap.put("Value19", "Key19");
		myMap.put("Value10", "Key10");
		
		
	
//		{
//	        SwingUtilities.invokeLater(new Runnable() {
//	            @Override
//	            public void run() {
	                new PropertiesTable(myMap);
//	            }
//	        });
//	    }   
		
		System.out.println("Done");
		
	}
	
	public static TableModel toTableModel(HashMap<?,?> map) {
	    DefaultTableModel model = new DefaultTableModel(
	        new Object[] { "Property", "Value" }, 0
	    );
	    for (Map.Entry<?,?> entry : map.entrySet()) {
	        model.addRow(new Object[] {entry.getKey(), entry.getValue() });
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
		
	}
	
	
	
	
