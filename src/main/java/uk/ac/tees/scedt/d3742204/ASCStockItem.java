/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package uk.ac.tees.scedt.d3742204;

/**
 * ASCStockItem represents an item in Asher's Sports Collective inventory.
 * It includes methods to manage stock, record sales transactions, and load/save data from/to CSV files.
 * It also handles integration with Mengda's Sportymart.
 *
 * @author D3742204
 */
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ASCStockItem {
    // File paths for CSV files
    private static final String CSV_FILE_PATH = System.getProperty("user.dir") + "/src/main/AshersSportsCollective.csv";
    private static final String MENGA_CSV_FILE_PATH = System.getProperty("user.dir") + "/src/main/MengdasSportyMart.csv";
    static String SALES_TRANSACTIONS_CSV_FILE_PATH = System.getProperty("user.dir") + "/src/main/sales_transactions.csv";

    // Instance variables for stock item properties
    private int departmentId = 0;
    private String productCode;
    private String productTitle;
    private String productDescription;
    private int unitPricePounds;
    private int unitPricePence;
    private int quantityInStock;

    // Constructor for ASCStockItem
    public ASCStockItem(int departmentCode,String productCode, String productTitle, String productDescription,
                        int unitPricePounds, int unitPricePence, int quantityInStock) {
        this.departmentId = departmentCode;
        this.productCode = productCode;
        this.productTitle = productTitle;
        this.productDescription = productDescription;
        this.unitPricePounds = unitPricePounds;
        this.unitPricePence = unitPricePence;
        this.quantityInStock = quantityInStock;
    }

    // Getters and setters
    public String getProductCode() {
        return productCode;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public int getUnitPricePounds() {
        return unitPricePounds;
    }

    public int getUnitPricePence() {
        return unitPricePence;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    // Methods to manage stock
    public void buyStock(int quantity) {
        this.quantityInStock += quantity;
        saveStockToCSV();
    }

    public boolean sellStock(int quantity) {
        if (quantity <= quantityInStock) {
            quantityInStock -= quantity;
            saveStockToCSV();
            recordSalesTransaction(quantity);
            return true; // Stock sold successfully
        }
        return false; // Insufficient stock
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }
        
    private void saveStockToCSV() {
        List<ASCStockItem> stockItems = loadAshersForSaving();
        boolean found = false;
        for (ASCStockItem item : stockItems) {
            if (item.getProductCode().equals(productCode)) {
                found = true;
                item.setQuantityInStock(quantityInStock);
                break;
            }
        }
        
        if(found){
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FILE_PATH))) {
                for (ASCStockItem item : stockItems) {
                    bw.write(String.format("%s,%s,%s,%d,%d,%d\n", item.productCode, item.productTitle,
                            item.productDescription, item.unitPricePounds, item.unitPricePence, item.quantityInStock));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            saveStockToMengaCSV();
        }

    }
    
    private void saveStockToMengaCSV() {
        List<ASCStockItem> stockItems = loadMengaForSaving();

        for (ASCStockItem item : stockItems) {
            if (item.getProductCode().equals(productCode)) {
                item.setQuantityInStock(quantityInStock);
                break;
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MENGA_CSV_FILE_PATH))) {
            for (ASCStockItem item : stockItems) {
                int nameLength = item.productTitle.length();
                int remain = 60 - nameLength;
                String space = "";
                for (int i = 0; i < remain; i++) {
                    space += " ";
                }
                bw.write(String.format("%d,%s,%s,%d,%d\n",item.departmentId, item.productCode, item.productTitle +space+item.productDescription, item.unitPricePence, item.quantityInStock));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void saveStockToMengaCSV(String productCode,int quantity) {
        List<ASCStockItem> stockItems = loadMengaForSaving();

        for (ASCStockItem item : stockItems) {
            if (item.getProductCode().equals(productCode)) {
                item.setQuantityInStock(quantity);
                break;
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MENGA_CSV_FILE_PATH))) {
            for (ASCStockItem item : stockItems) {
                int nameLength = item.productTitle.length();
                int remain = 60 - nameLength;
                String space = "";
                for (int i = 0; i < remain; i++) {
                    space += " ";
                }
                bw.write(String.format("%d,%s,%s,%d,%d\n",item.departmentId, item.productCode, item.productTitle +space+item.productDescription, item.unitPricePence, item.quantityInStock));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to record sales transactions
    private void recordSalesTransaction(int quantitySold) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SALES_TRANSACTIONS_CSV_FILE_PATH, true))) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTime = dateFormat.format(new Date());

            bw.write(String.format("%s,%s,%d,%d,%s\n", dateTime, productCode, quantitySold,
                    unitPricePounds, unitPricePence));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Methods for loading stock from CSV
    public static List<ASCStockItem> loadStockFromCSV() {
        List<ASCStockItem> stockItems = new ArrayList<>();

        // Load stock from AshersSportsCollective.csv
        stockItems.addAll(loadStockItemsFromCSV(CSV_FILE_PATH));

        // Load stock from Mengda's Sportymart
        stockItems.addAll(loadMSMStockItemsFromCSV());

        return stockItems;
    }
    
    public static List<ASCStockItem> loadAshersForSaving() {
        List<ASCStockItem> stockItems = new ArrayList<>();

        // Load stock from AshersSportsCollective.csv
        stockItems.addAll(loadStockItemsFromCSV(CSV_FILE_PATH));

        return stockItems;
    }
    
    public static List<ASCStockItem> loadMengaForSaving() {
        List<ASCStockItem> stockItems = new ArrayList<>();

        // Load stock from Mengda's Sportymart
        stockItems.addAll(loadMSMStockItemsFromCSV());

        return stockItems;
    }

    private static List<ASCStockItem> loadStockItemsFromCSV(String filePath) {
        List<ASCStockItem> stockItems = new ArrayList<>();

       try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
           String line;
           while ((line = br.readLine()) != null) {
               String[] data = line.split(",");
               // Trim whitespace from the beginning and end of each value
               for (int i = 0; i < data.length; i++) {
                   data[i] = data[i].trim();
               }
               stockItems.add(new ASCStockItem(0,data[0], data[1], data[2],
                       Integer.parseInt(data[3]), Integer.parseInt(data[4]), Integer.parseInt(data[5])));
           }
       } catch (IOException e) {
           e.printStackTrace();
       }

       return stockItems;
    }
    
    private static List<ASCStockItem> loadMSMStockItemsFromCSV() {
        List<ASCStockItem> stockItems = new ArrayList<>();

        List<MSMStockItem> msmStockItems = MSMStockItem.loadStock();
        for (MSMStockItem msmStockItem : msmStockItems) {
            // Use an adapter to convert MSMStockItem to ASCStockItem
            ASCStockItem adaptedItem = new MSMStockItemAdapter(msmStockItem);

            // Add the adapted item to the list
            stockItems.add(adaptedItem);
        }

        return stockItems;
    }

    // Methods for saving stock to CSV
    public static void saveStockToCSV(List<ASCStockItem> stockItems) {
        List<ASCStockItem> nstockItems = loadAshersForSaving();
        boolean found = false;
        String product = "";
        int quantity = 0;
        for (ASCStockItem item : nstockItems) {
            if (item.getProductCode().equals(item.productCode)) {
                found = true;
                product = item.productCode;
                quantity = item.quantityInStock;
                break;
            }
        }
        if(found){
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FILE_PATH))) {
                for (ASCStockItem item : nstockItems) {
                    bw.write(String.format("%s,%s,%s,%d,%d,%d\n", item.productCode, item.productTitle,
                item.productDescription, item.unitPricePounds, item.unitPricePence, item.quantityInStock));
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            saveStockToMengaCSV(product,quantity);
        }
        
    }

    // method for a readable representation
    @Override
    public String toString() {
        return String.format("Code: %s, Product: %s, Quantity: %d", productCode, productTitle, quantityInStock);
    }
}
