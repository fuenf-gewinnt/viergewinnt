package gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ColorRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 6123477726001189302L;
	private Object[][] data;

	public ColorRenderer(Object[][] data2) {
		this.data = data2;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (data[row][column] != null && data[row][column].toString().equals("o")) {
			/*
			 * Sonst Null Pointer Exception
			 */
			l.setBackground(new java.awt.Color(224, 0, 0));
		} else if (data[row][column] != null && data[row][column].toString().equals("x")) {
			/*
			 * Farbe alternativ mit new java.awt.Color(0, 255, 0) oder Color.red
			 */
			l.setBackground(new java.awt.Color(255, 211, 43));

		} else {
			l.setBackground(Color.white);
		}

		return l;
	}

}