package model.cell;

import java.util.Vector;

public class Cell {
	Cell parent;
	Vector<Cell> children;
	private String name;

	public Cell(String name) {
		this.name = name;
		parent = null;
		children = new Vector<Cell>();
	}

	/**
	 * Link together all members of a family.
	 *
	 * @param Parent
	 *            Parent bacteria
	 * @param kids
	 *            Children bacteria
	 */
	public static void linkCells(Cell pa, Cell[] kids) {
		for (Cell kid : kids) {
			pa.children.addElement(kid);
			kid.parent = pa;
		}
	}

	// / getter methods ///////////////////////////////////

	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public Cell getParent() {
		return parent;
	}

	public int getChildCount() {
		return children.size();
	}

	public Cell getChildAt(int i) {
		return (Cell) children.elementAt(i);
	}

	public int getIndexOfChild(Cell kid) {
		return children.indexOf(kid);
	}
}
