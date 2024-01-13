/**
 * With LowStockObserver we, observe the stock levels of ASCStockItems
 * and notifies admin about low stock items through a warning message.
 * 
 * @author D3742204
 */
package uk.ac.tees.scedt.d3742204;

import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

/**
 * The LowStockObserver class monitors the stock levels of ASCStockItems and alerts admin about low stock.
 */
class LowStockObserver {
    private static List<ASCStockItem> stockItems;

    /**
     * Constructs for LowStockObserver with the specified list of ASCStockItems to monitor.
     */
    public LowStockObserver(List<ASCStockItem> stockItems) {
        this.stockItems = stockItems;
    }

    /**
     * Checks for low stock items and displays a warning message if any are found.
     */
    public void checkLowStock() {
        // Filter ASCStockItems with quantityInStock less than 5
        List<ASCStockItem> lowStockItems = stockItems.stream()
                .filter(item -> item.getQuantityInStock() < 5)
                .collect(Collectors.toList());

        // Display a warning message if low stock items are found
        if (!lowStockItems.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Low stock warning: " + lowStockItems,
                    "Low Stock Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    public static void checkLowStockConsole() {
        // Filter ASCStockItems with quantityInStock less than 5
        List<ASCStockItem> lowStockItems = stockItems.stream()
                .filter(item -> item.getQuantityInStock() < 5)
                .collect(Collectors.toList());

        // Display a warning message if low stock items are found
        if (!lowStockItems.isEmpty()) {
            System.out.println("Low stock warning: " + lowStockItems);
        }
    }
}