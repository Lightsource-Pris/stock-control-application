/**
 * HighlightCellRenderer is a custom TableCellRenderer for the JTable cells.
 * We are able to highlight Mengda's Sportymart added items.
 * 
 * @author D3742204
 */
package uk.ac.tees.scedt.d3742204;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class HighlightCellRenderer extends DefaultTableCellRenderer {
    private Color highlightColor;
    private int highlightCount;

    /**
     * Constructs a HighlightCellRenderer with the specified highlight color and count.
     * @param highlightColor The color used for highlighting cells.
     * @param highlightCount The number of rows from the end to be highlighted.
     */
    public HighlightCellRenderer(Color highlightColor, int highlightCount) {
        this.highlightColor = highlightColor;
        this.highlightCount = highlightCount;
    }

    /**
     * Overrides the getTableCellRendererComponent method to customize cell rendering.
     * @param table The JTable to which this renderer is applied.
     * @param value The value of the cell.
     * @param isSelected Whether the cell is selected.
     * @param hasFocus Whether the cell has focus.
     * @param row The row index of the cell.
     * @param column The column index of the cell.
     * @return The customized component for rendering the cell.
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Set the foreground color to black when the cell is selected
        if (isSelected) {
            setForeground(Color.BLACK);
        }

        // Check if the current row should be highlighted
        if (row >= table.getRowCount() - highlightCount) {
            c.setBackground(highlightColor);
        } else {
            c.setBackground(table.getBackground());
        }

        return c;
    }
}