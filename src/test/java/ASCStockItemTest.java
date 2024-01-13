/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author mac
 */

import uk.ac.tees.scedt.d3742204.ASCStockItem;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ASCStockItemTest {
     @Test
     //method to test selling stock
    public void testBuyStock() {
        ASCStockItem stockItem = new ASCStockItem(2,"RUN1234567", "Run-Tech Running shorts", "High-quality running shorts",
                10, 0, 10);

        stockItem.buyStock(5);
        assertEquals(15, stockItem.getQuantityInStock());
    }

    @Test
    //method to test buying stock
    public void testSellStock() {
        ASCStockItem stockItem = new ASCStockItem(2,"RUN1234567", "Run-Tech Running shorts", "High-quality running shorts",
                10, 0, 10);

        boolean result = stockItem.sellStock(7);
        assertTrue(result);
        assertEquals(3, stockItem.getQuantityInStock());
    }

    @Test
    //method to test insufficient stock
    public void testSellStockInsufficientStock() {
        ASCStockItem stockItem = new ASCStockItem(2,"RUN1234567", "Run-Tech Running shorts", "High-quality running shorts",
                10, 0, 3);

        boolean result = stockItem.sellStock(7);
        assertFalse(result);
        assertEquals(3, stockItem.getQuantityInStock());
    }
}
