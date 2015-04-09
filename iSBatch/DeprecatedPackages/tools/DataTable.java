package tools;

import ij.IJ;
import ij.util.Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

/*
 * This table structure is based on the ImageJ results table, adapted to the needs of this code.
 * http://rsb.info.nih.gov/ij/developer/source/ij/measure/DataTable.java.html
 */
public class DataTable implements Cloneable {
	 public static final int MAX_COLUMNS = 150;
	    
	    public static final int COLUMN_NOT_FOUND = -1;
	    public static final int COLUMN_IN_USE = -2;
	    public static final int TABLE_FULL = -3; // no longer used
	    
	    public static final int AREA=0, MEAN=1, STD_DEV=2, MODE=3, MIN=4, MAX=5,
	        X_CENTROID=6, Y_CENTROID=7, X_CENTER_OF_MASS=8, Y_CENTER_OF_MASS=9,
	        PERIMETER=10, ROI_X=11, ROI_Y=12, ROI_WIDTH=13, ROI_HEIGHT=14,
	        MAJOR=15, MINOR=16, ANGLE=17, CIRCULARITY=18, FERET=19, 
	        INTEGRATED_DENSITY=20, MEDIAN=21, SKEWNESS=22, KURTOSIS=23, 
	        AREA_FRACTION=24, RAW_INTEGRATED_DENSITY=25, CHANNEL=26, SLICE=27, FRAME=28, 
	        FERET_X=29, FERET_Y=30, FERET_ANGLE=31, MIN_FERET=32, ASPECT_RATIO=33,
	        ROUNDNESS=34, SOLIDITY=35, LAST_HEADING=35;
	    private static final String[] defaultHeadings = {"Area","Mean","StdDev","Mode","Min","Max",
	        "X","Y","XM","YM","Perim.","BX","BY","Width","Height","Major","Minor","Angle",
	        "Circ.", "Feret", "IntDen", "Median","Skew","Kurt", "%Area", "RawIntDen", "Ch", "Slice", "Frame", 
	         "FeretX", "FeretY", "FeretAngle", "MinFeret", "AR", "Round", "Solidity"};

	    private int maxRows = 100; // will be increased as needed
	    private int maxColumns = MAX_COLUMNS; // will be increased as needed
	    private String[] headings = new String[maxColumns];
	    private boolean[] keep = new boolean[maxColumns];
	    private int counter;
	    private double[][] columns = new double[maxColumns][];
	    private String[] rowLabels;
	    private int lastColumn = -1;
	    private StringBuilder sb;
	    private int precision = 3;
	    private String rowLabelHeading = "";
	    private char delimiter = '\t';
	    private boolean headingSet; 
	    private boolean showRowNumbers = true;
	    private boolean autoFormat = true;
	    private Hashtable stringColumns;

    
    
    
    public static DataTable open(String path) throws IOException {
        final String lineSeparator =  "\n";
        final String cellSeparator =  ",\t";
        String text =IJ.openAsString(path);
        if (text==null)
            return null;
        if (text.startsWith("Error:"))
            throw new IOException("Error opening "+path);
        String[] lines = Tools.split(text, lineSeparator);
        if (lines.length==0)
            throw new IOException("Table is empty or invalid");
        String[] headings = Tools.split(lines[0], cellSeparator);
        if (headings.length==1)
            throw new IOException("This is not a tab or comma delimited text file.");
        int numbersInHeadings = 0;
        for (int i=0; i<headings.length; i++) {
            if (headings[i].equals("NaN") || !Double.isNaN(Tools.parseDouble(headings[i])))
                numbersInHeadings++;
        }
        boolean allNumericHeadings = numbersInHeadings==headings.length;
        if (allNumericHeadings) {
            for (int i=0; i<headings.length; i++)
                headings[i] = "C"+(i+1);
        }
        int firstColumn = headings[0].equals(" ")?1:0;
        for (int i=0; i<headings.length; i++)
            headings[i] = headings[i].trim();
        int firstRow = allNumericHeadings?0:1;
        boolean labels = firstColumn==1 && headings[1].equals("Label");
        int type=getTableType(path, lines, firstRow, cellSeparator);
        if (!labels && (type==1||type==2))
            labels = true;
        int labelsIndex = (type==2)?0:1;
        if (lines[0].startsWith("\t")) {
            String[] headings2 = new String[headings.length+1];
            headings2[0] = " ";
            for (int i=0; i<headings.length; i++)
                headings2[i+1] = headings[i];
            headings = headings2;
            firstColumn = 1;
        }
        DataTable rt = new DataTable();
        for (int i=firstRow; i<lines.length; i++) {
            rt.incrementCounter();
            String[] items=Tools.split(lines[i], cellSeparator);
            for (int j=firstColumn; j<items.length; j++) {
                if (j==labelsIndex&&labels)
                    rt.addLabel(headings[labelsIndex], items[labelsIndex]);
                else if (j<headings.length) {
                    double value = Tools.parseDouble(items[j]);
                    if (Double.isNaN(value))
                        rt.addValue(headings[j], items[j]);
                    else
                        rt.addValue(headings[j], value);
                }
            }
        }
        return rt;
    }
    private static int getTableType(String path, String[] lines, int firstRow, String cellSeparator) {
        if (lines.length<2) return 0;
        String[] items=Tools.split(lines[1], cellSeparator);
        int nonNumericCount = 0;
        int nonNumericIndex = 0;
        for (int i=0; i<items.length; i++) {
            if (!items[i].equals("NaN") && Double.isNaN(Tools.parseDouble(items[i]))) {
                nonNumericCount++;
                nonNumericIndex = i;
            }
        }
        boolean csv = path.endsWith(".csv");
        if (nonNumericCount==0)
            return 0; // assume this is all-numeric table
        if (nonNumericCount==1 && nonNumericIndex==1)
            return 1; // assume this is an ImageJ Results table with row numbers and row labels
        if (nonNumericCount==1 && nonNumericIndex==0)
            return 2; // assume this is an ImageJ Results table without row numbers and with row labels
        return 3;
    }
    
    /** Adds a value to the end of the given column. Counter must be >0.*/
    public void addValue(int column, double value) {
        if (column>=maxColumns)
            addColumns();
        if (column<0 || column>=maxColumns)
            throw new IllegalArgumentException("Column out of range");
        if (counter==0)
            throw new IllegalArgumentException("Counter==0");
        if (columns[column]==null) {
            columns[column] = new double[maxRows];
            if (headings[column]==null)
                headings[column] = "---";
            if (column>lastColumn) lastColumn = column;
        }
        columns[column][counter-1] = value;
    }
    
    /** Obsolete; the addValue() method automatically adds columns as needed.
     * @see #addValue(String, double)
     */
     public synchronized void addColumns() {
         String[] tmp1 = new String[maxColumns*2];
         System.arraycopy(headings, 0, tmp1, 0, maxColumns);
         headings = tmp1;
         double[][] tmp2 = new double[maxColumns*2][];
         for (int i=0; i<maxColumns; i++)
             tmp2[i] = columns[i];
         columns = tmp2;
         boolean[] tmp3 = new boolean[maxColumns*2];
         System.arraycopy(keep, 0, tmp3, 0, maxColumns);
         keep = tmp3;
         maxColumns *= 2;
     }
     /** Sets the value of the given column and row, where
     where 0&lt;=row&lt;counter. If the specified column does 
     not exist, it is created. When adding columns, 
     <code>show()</code> must be called to update the 
     window that displays the table.*/
 public void setValue(String column, int row, double value) {
     if (column==null)
         throw new IllegalArgumentException("Column is null");
     int col = getColumnIndex(column);
     if (col==COLUMN_NOT_FOUND) {
         col = getFreeColumn(column);
     }
     setValue(col, row, value);
 }

 /** Sets the value of the given column and row, where
     where 0&lt;=column&lt;=(lastRow+1 and 0&lt;=row&lt;=counter. */
 public void setValue(int column, int row, double value) {
     if (column>=maxColumns)
         addColumns();
     if (column<0 || column>=maxColumns)
         throw new IllegalArgumentException("Column out of range");
     if (row>=counter) {
         if (row==counter)
             incrementCounter();
         else
             throw new IllegalArgumentException("row>counter");
     }
     if (columns[column]==null) {
         columns[column] = new double[maxRows];
         if (column>lastColumn) lastColumn = column;
     }
     columns[column][row] = value;
 }

 /** Sets the string value of the given column and row, where
     where 0&lt;=row&lt;counter. If the specified column does 
     not exist, it is created. When adding columns, 
     <code>show()</code> must be called to update the 
     window that displays the table.*/
 public void setValue(String column, int row, String value) {
     if (column==null)
         throw new IllegalArgumentException("Column is null");
     int col = getColumnIndex(column);
     if (col==COLUMN_NOT_FOUND)
         col = getFreeColumn(column);
     setValue(col, row, value);
 }

 /** Sets the string value of the given column and row, where
     where 0&lt;=column&lt;=(lastRow+1 and 0&lt;=row&lt;=counter. */
 public void setValue(int column, int row, String value) {
     setValue(column, row, Double.NaN);
     if (stringColumns==null)
         stringColumns = new Hashtable();
     ArrayList stringColumn = (ArrayList)stringColumns.get(new Integer(column));
     if (stringColumn==null) {
         stringColumn = new ArrayList();
         stringColumns.put(new Integer(column), stringColumn);
     }
     int size = stringColumn.size();
     if (row>=size) {
         for (int i=size; i<row; i++)
             stringColumn.add(i, "");
     }
     if (row==stringColumn.size())
         stringColumn.add(row, value);
     else
         stringColumn.set(row, value);
 }
     /** Returns the current value of the measurement counter. */
     public int getCounter() {
         return counter;
     }
    /** Sets the heading of the the first available column and
    returns that column's index. Returns COLUMN_IN_USE
     if this is a duplicate heading. */
public int getFreeColumn(String heading) {
    for(int i=0; i<headings.length; i++) {
        if (headings[i]==null) {
            columns[i] = new double[maxRows];
            headings[i] = heading;
            if (i>lastColumn) lastColumn = i;
            return i;
        }
        if (headings[i].equals(heading))
            return COLUMN_IN_USE;
    }
    addColumns();
    lastColumn++;
    columns[lastColumn] = new double[maxRows];
    headings[lastColumn] = heading;
    return lastColumn;
}
    /** Adds a value to the end of the given column. If the column
        does not exist, it is created.  Counter must be >0.
        There is an example at:<br>
        http://imagej.nih.gov/ij/plugins/sine-cosine.html
        */
    public void addValue(String column, double value) {
        if (column==null)
            throw new IllegalArgumentException("Column is null");
        int index = getColumnIndex(column);
        if (index==COLUMN_NOT_FOUND)
            index = getFreeColumn(column);
        addValue(index, value);
        keep[index] = true;
    }
    
    /** Adds a string value to the end of the given column. If the column
        does not exist, it is created.  Counter must be >0. */
    public void addValue(String column, String value) {
        if (column==null)
            throw new IllegalArgumentException("Column is null");
        int index = getColumnIndex(column);
        if (index==COLUMN_NOT_FOUND)
            index = getFreeColumn(column);
        addValue(index, Double.NaN);
        setValue(column, getCounter()-1, value);
        keep[index] = true;
    }
    /** Returns the index of the first column with the given heading.
    heading. If not found, returns COLUMN_NOT_FOUND. */
public int getColumnIndex(String heading) {
    for (int i=0; i<headings.length; i++) {
        if (headings[i]==null)
            return COLUMN_NOT_FOUND;
        else if (headings[i].equals(heading))
            return i;
    }
    return COLUMN_NOT_FOUND;
}
    public void addLabel(String label) {
        if (rowLabelHeading.equals(""))
            rowLabelHeading = "Label";
        addLabel(rowLabelHeading, label);
    }

    /** Adds a label to the beginning of the current row. Counter must be >0. */
    public void addLabel(String columnHeading, String label) {
        if (counter==0)
            throw new IllegalArgumentException("Counter==0");
        if (rowLabels==null)
            rowLabels = new String[maxRows];
        rowLabels[counter-1] = label;
        if (columnHeading!=null)
            rowLabelHeading = columnHeading;
    }
    public synchronized void incrementCounter() {
        counter++;
        if (counter==maxRows) {
            if (rowLabels!=null) {
                String[] s = new String[maxRows*2];
                System.arraycopy(rowLabels, 0, s, 0, maxRows);
                rowLabels = s;
            }
            for (int i=0; i<=lastColumn; i++) {
                if (columns[i]!=null) {
                    double[] tmp = new double[maxRows*2];
                    System.arraycopy(columns[i], 0, tmp, 0, maxRows);
                    columns[i] = tmp;
                }
            }
            maxRows *= 2;
        }
    }
    
}
