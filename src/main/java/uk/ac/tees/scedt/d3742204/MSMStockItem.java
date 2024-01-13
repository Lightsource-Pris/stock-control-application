/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uk.ac.tees.scedt.d3742204;

/**
 *
 * @author D3742204
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** Represents a Mengda's Sportymart stock item.
 * @author Steven Mead
 * @version 1.0
 * @since 1.0
 */
public class MSMStockItem {
    private final int departmentId;
    private final String code;
    private final String nameAndDescription;
    private final int unitPrice;
    private int quantityInStock;
    
    
    
    /**
     * Constructor
     * 
     * @param departmentId
     * @param code
     * @param titleAndDescription
     * @param unitPrice
     * @param quantityInStock
     * @since 1.0
     */
    public MSMStockItem(int departmentId, String code, String titleAndDescription, int unitPrice, int quantityInStock) {
        this.departmentId = departmentId;
        this.code = code;
        this.nameAndDescription = titleAndDescription;
        this.unitPrice = unitPrice;
        this.quantityInStock = quantityInStock;
    }

    /**
     * Returns the department ID used by Mead's Modernity
     * @return the department id.
     * @since 1.0
     */
    public int getDepartmentId() {
        return departmentId;
    }

    /**
     * Returns the code of this stock item.
     * @return a string with the 6-digit code.
     * @since 1.0
     */
    public String getCode() {
        return code;
    }
    
    /**
     * Returns the name of this stock item.
     * @return a string with the name.
     * @since 1.0
     */
    public String getName() {            
        return nameAndDescription.substring(0, 59).replaceAll("\u00a0", "").stripTrailing();
    }
    
    /**
     * Return the description of this stock item.
     * @return a string with the description.
     * @since 1.0
     */
    public String getDescription() {
        return nameAndDescription.substring(60, nameAndDescription.length());
    }

    /**
     * Returns the stock item's unit price in pence
     * @return an integer that represents the unit price.
     * @since 1.0
     */
    public int getUnitPrice() {
        return unitPrice;
    }

    /**
     * Return the quantity of this stock item currently available in stock.
     * @return an integer that represents to stock quantity available.
     * @since 1.0
     */
    public int getQuantityInStock() {
        return quantityInStock;
    }
    
    public String getHumanFriendlyUnitPrice() {
        
        final int pounds = getUnitPrice() / 100;
        final int pence = getUnitPrice() % 100;
        return String.format("%d.%02d", pounds, pence);
    }
    
    public void setQuanity(int newQuantity) {
        if(newQuantity >= 0) {
            quantityInStock = newQuantity;
        }
    }
    
    @Override
    public String toString() {
        return String.format("%d-%s - %s - %s - UNIT PRICE: £%s - QTY: %d",
                getDepartmentId(),
                getCode(),
                getName(),
                getDescription(),
                getHumanFriendlyUnitPrice(),
                getQuantityInStock());
    }

    /**
     * Load a collection of MSMStockItems into a list
     * @return a list of the Mengda's Sportymart stock items.
     * @since 1.0
     */
    public static List<MSMStockItem> loadStock() {
        final List<MSMStockItem> loadedStock = new ArrayList<>();
        
        //------------------------------------------------------------------
        // TODO: Add code to load CSV file.
        //------------------------------------------------------------------
        
        //Code to clean and load CSV file
        String csvFilePath = System.getProperty("user.dir") + "/src/main/MengdasSportyMart.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split the line into fields
                String[] data = line.split(",");

                int departmentId;
                try {
                    departmentId = Integer.parseInt(data[0].trim().replaceAll("[^\\d]", ""));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    continue; // Skip this line if parsing fails
                }

                String code = data[1].trim();
                String titleAndDescription = data[2].replaceAll("\u00a0", " ").trim();

                int unitPrice;
                try {
                    unitPrice = Integer.parseInt(data[3].trim());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    continue; 
                }

                int quantityInStock;
                try {
                    quantityInStock = Integer.parseInt(data[4].trim());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    continue; // Skip this line if parsing fails
                }

                MSMStockItem stockItem = new MSMStockItem(departmentId, code, titleAndDescription, unitPrice, quantityInStock);

                // Add the stock item to the list
                loadedStock.add(stockItem);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
            return loadedStock;
    }

}