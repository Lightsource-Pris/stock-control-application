/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uk.ac.tees.scedt.d3742204;

/**
 * ConsoleStockApplication is the console-based stock management application for ASCStockItems.
 * It allows users to display current stock, sell or buy items, and view sales transactions.
 * @author D3742204
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ConsoleStockApplication {
    
    // Observer for low stock notifications
    private static LowStockObserver lowStockObserver;
    
    public static void main(String[] args) {
        // Load stock from CSV
        List<ASCStockItem> stockItems = ASCStockItem.loadStockFromCSV();
        // Display current stock
        displayStock(stockItems);

        // Get user choice; sell or buy
        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        boolean validInput = false;

        // Perform action based on user choice
        while (!validInput) {
            try {
                System.out.print("Do you want to (1) Sell or (2) Buy? Enter 1 or 2: ");
                String input = scanner.nextLine();
                choice = Integer.parseInt(input);

                if (choice == 1 || choice == 2) {
                    validInput = true; // Break the loop if choice is valid
                } else {
                    System.out.println("Invalid choice. Please enter 1 or 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }

        switch (choice) {
            case 1:
                sellStock(scanner, stockItems);
                break;
            case 2:
                buyStock(scanner, stockItems);
                break;
            default:
                System.out.println("Invalid choice.");
        }

        scanner.close();
    }

    // Method to display current stock
    private static void displayStock(List<ASCStockItem> stockItems) {
        lowStockObserver = new LowStockObserver(stockItems);
        System.out.println("Current Stock:");
        for (ASCStockItem item : stockItems) {
            System.out.println(item);
        }
        LowStockObserver.checkLowStockConsole();
        System.out.println();
    }

    //Method to display sales transactions
    private static void displaySalesTransactions() {
        System.out.println("Sales Transactions:");
        try (BufferedReader br = new BufferedReader(new FileReader(ASCStockItem.SALES_TRANSACTIONS_CSV_FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    // Find stock item by product code
    private static ASCStockItem findStockItemByCode(List<ASCStockItem> stockItems, String productCode) {
        for (ASCStockItem item : stockItems) {
            if (item.getProductCode().equals(productCode)) {
                return item;
            }
        }
        return null;
    }
    
    // Sell stock
    private static void sellStock(Scanner scanner, List<ASCStockItem> stockItems) {
        System.out.print("Enter product code to sell: ");
        String productCode = scanner.nextLine();

        ASCStockItem selectedStockItem = findStockItemByCode(stockItems, productCode);

        if (selectedStockItem != null) {
            int quantityToSell = 0;
            boolean validInput = false;

            while (!validInput) {
                try {
                    System.out.print("Enter quantity to sell: ");
                    String input = scanner.nextLine();
                    quantityToSell = Integer.parseInt(input);
                    validInput = true; // Break the loop if parsing succeeds
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid integer.");
                }
            }

            if (quantityToSell > 0 && selectedStockItem.sellStock(quantityToSell)) {
                System.out.println("Stock sold successfully.");
                displayStock(stockItems);
                displaySalesTransactions();
            } else {
                System.out.println("Invalid quantity or insufficient stock.");
            }
        } else {
            System.out.println("Product not found.");
        }
    }

    // Buy stock
    private static void buyStock(Scanner scanner, List<ASCStockItem> stockItems) {
        System.out.print("Enter product code to buy: ");
        String productCode = scanner.nextLine();

        ASCStockItem selectedStockItem = findStockItemByCode(stockItems, productCode);

        if (selectedStockItem != null) {
            System.out.print("Enter quantity to buy: ");
            int quantityToBuy = scanner.nextInt();

            if (quantityToBuy > 0) {
                selectedStockItem.buyStock(quantityToBuy);
                System.out.println("Stock bought successfully.");
                displayStock(stockItems);
            } else {
                System.out.println("Invalid quantity.");
            }
        } else {
            System.out.println("Product not found.");
        }
    }
}
