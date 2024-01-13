/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uk.ac.tees.scedt.d3742204;

// Import statements for necessary Java Swing and I/O classes
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Main class representing the Stock Control Application for buying and selling of stocks.
 * GUI components are created to interact with the application 
 * @author D3742204
 */

public class StockControlApplication {
    // Constant for the file path of sales transactions CSV
    private static final String SALES_TRANSACTIONS_CSV_FILE_PATH = System.getProperty("user.dir") +  "/src/main/sales_transactions.csv";

    // List to store ASCStockItem objects
    private static List<ASCStockItem> stockItems;

    // Observer for low stock notifications
    private static LowStockObserver lowStockObserver;

    // Table to display stock information
    private static JTable table;

    // Currently selected stock item
    private static ASCStockItem selectedStockItem;

    // Count of items in MengdasSportyMart
    private static int itemsInMengdasSportyMart = 0;

    // Main JFrame for the application
    private static JFrame frame;

    // Main method to launch the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StockControlApplication stockControlApplication = new StockControlApplication();
            stockControlApplication.initUI();
        });
    }
    
    // Method to initialize the user interface
    private void initUI(){
        // Load stock items from CSV and create low stock observer
        stockItems = ASCStockItem.loadStockFromCSV();
        lowStockObserver = new LowStockObserver(stockItems);

        // Create the main JFrame
        frame = new JFrame("Stock Control Application");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        // Create a table model and set column names
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Product Code");
        model.addColumn("Product Title");
        model.addColumn("Description");
        model.addColumn("Unit Price (Pounds)");
        model.addColumn("Unit Price (Pence)");
        model.addColumn("Quantity in Stock");

        // Populate table model with stock data
        for (ASCStockItem item : stockItems) {
            model.addRow(new Object[]{
                    item.getProductCode(),
                    item.getProductTitle(),
                    item.getProductDescription(),
                    item.getUnitPricePounds(),
                    item.getUnitPricePence(),
                    item.getQuantityInStock()
            });
        }

        // Create JTable with a list selection listener
        table = new JTable(model);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        selectedStockItem = stockItems.get(selectedRow);
                    }
                }
            }
        });
        
        // Highlight column 0 based on the count of MSM items
        int msmItemCount = loadMSMStockItemsFromCSV();
        table.getColumnModel().getColumn(0).setCellRenderer(new HighlightCellRenderer(Color.LIGHT_GRAY, msmItemCount));

        // Create a JScrollPane for the table
        JScrollPane scrollPane = new JScrollPane(table);

        // Load items from MengdasSportyMart.csv and set the count
        List<MSMStockItem> mengdaItems = MSMStockItem.loadStock();
        itemsInMengdasSportyMart = mengdaItems.size();
        
        // Buttons for buying and selling stock
        JButton buyStockButton = new JButton("Buy Stock");
        JButton sellStockButton = new JButton("Sell Stock");

        // ActionListener for Buy Stock button
        buyStockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedStockItem != null) {
                    int quantityToBuy = getQuantityFromUser("Enter quantity to buy:");
                    if (quantityToBuy > 0) {
                        selectedStockItem.buyStock(quantityToBuy);
                        updateTableModel();
                        updateStockCSV();
                    }
                } else {
                    // Prompt user to select a product
                    JOptionPane.showMessageDialog(frame, "Please select a product before buying stock.",
                            "Product Selection Required", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // ActionListener for Sell Stock button
        sellStockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedStockItem != null) {
                    int quantityToSell = getQuantityFromUser("Enter quantity to sell:");
                    if (quantityToSell > 0 && quantityToSell <= selectedStockItem.getQuantityInStock()) {
                        // Selling stock
                        selectedStockItem.sellStock(quantityToSell);

                        //recordSalesTransaction(selectedStockItem.getProductCode(), quantityToSell,
                        // selectedStockItem.getUnitPricePounds(),selectedStockItem.getUnitPricePence());
                        updateTableModel();
                        updateStockCSV();
                    } else {
                        if(quantityToSell!=-1)
                        JOptionPane.showMessageDialog(frame, "Invalid quantity or insufficient stock!");
                    }
                } else {
                    // Prompt user to select a product
                    JOptionPane.showMessageDialog(frame, "Please select a product before selling stock.",
                            "Product Selection Required", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Create a JPanel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buyStockButton);
        buttonPanel.add(sellStockButton);

        // Creating the layout and adding components to the content pane
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // Set frame size and make visible
        frame.setSize(600, 400);
        frame.setVisible(true);

        // Check and notify low stock on application startup
        lowStockObserver.checkLowStock();
        
        // Add window listener to show sales record frame when closing the main frame
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                showSalesRecordFrame();
            }
        });
    }

    // Method to load MSM stock items from CSV
    private static int loadMSMStockItemsFromCSV() {
        List<ASCStockItem> stockItems = new ArrayList<>();
        List<MSMStockItem> msmStockItems = MSMStockItem.loadStock();

        // Convert MSMStockItem to ASCStockItem using an adapter
        for (MSMStockItem msmStockItem : msmStockItems) {
            ASCStockItem adaptedItem = new MSMStockItemAdapter(msmStockItem);
            stockItems.add(adaptedItem);
        }

        return stockItems.size();
    }
    
    // Method to update the table model
    private static void updateTableModel() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        // Load MSM items and update the count
        List<MSMStockItem> mengdaItems = MSMStockItem.loadStock();
        itemsInMengdasSportyMart = mengdaItems.size();
        int mengdaItemCount = itemsInMengdasSportyMart;

        // Populate the table model with stock data
        for (int i = 0; i < stockItems.size(); i++) {
            ASCStockItem item = stockItems.get(i);
            model.addRow(new Object[]{
                    item.getProductCode(),
                    item.getProductTitle(),
                    item.getProductDescription(),
                    item.getUnitPricePounds(),
                    item.getUnitPricePence(),
                    item.getQuantityInStock()
            });
        }

        // Set the custom renderer for the product code column
        table.getColumnModel().getColumn(0).setCellRenderer(new HighlightCellRenderer(Color.LIGHT_GRAY, mengdaItemCount));

        // Repaint table to apply changes
        table.repaint();

        // Check and notify low stock
        lowStockObserver.checkLowStock();
    }

    // Method to update the stock CSV file
    private static void updateStockCSV() {
        ASCStockItem.saveStockToCSV(stockItems);
    }

    // Method to show the sales record frame
    private static void showSalesRecordFrame() {
        JFrame salesRecordFrame = new JFrame("Sales Record");
        salesRecordFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Creating a table model
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Date and Time");
        model.addColumn("Product Code");
        model.addColumn("Quantity Sold");
        model.addColumn("Unit Price (Pounds)");
        model.addColumn("Unit Price (Pence)");

        // Populate table model with sales data
        List<String[]> salesRecords = readSalesTransactionsCSV();
        for (String[] record : salesRecords) {
            model.addRow(record);
        }

        // Create JTable for sales record
        JTable salesTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(salesTable);

        // Scroll pane to the sales record frame
        salesRecordFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Set frame size and make visible
        salesRecordFrame.setSize(600, 400);
        salesRecordFrame.setVisible(true);
    }

    // Method to read sales transactions from CSV
    private static List<String[]> readSalesTransactionsCSV() {
        List<String[]> salesRecords = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(SALES_TRANSACTIONS_CSV_FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                salesRecords.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return salesRecords;
    }

    // Method to record a sales transaction
    private static void recordSalesTransaction(String productCode, int quantitySold, int unitPricePounds, int unitPricePence) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SALES_TRANSACTIONS_CSV_FILE_PATH, true))) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTime = dateFormat.format(new Date());

            bw.write(String.format("%s,%s,%d,%d,%s\n", dateTime, productCode, quantitySold, unitPricePounds,
                    unitPricePence));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to get quantity from the user using JOptionPane
    private static int getQuantityFromUser(String message) {
        String input = JOptionPane.showInputDialog(null, message);
        if (input == null) {
            return -1; // User canceled the input
        }

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return 0; // Invalid input
        }
    }
}

