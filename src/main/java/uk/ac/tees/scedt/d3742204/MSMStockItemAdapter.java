/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uk.ac.tees.scedt.d3742204;

/**
 * The MSMStockItemAdapter class adapts MSMStockItem to ASCStockItem by providing compatibility.
 * This adapter is used to convert MSMStockItem instances to ASCStockItem instances.
 *
 * @author D3742204
 */
public class MSMStockItemAdapter extends ASCStockItem {
    private final MSMStockItem msmStockItem;

    /**
     * Constructing an MSMStockItemAdapter with the specified MSMStockItem.
     *
     */
    public MSMStockItemAdapter(MSMStockItem msmStockItem) {
        super(msmStockItem.getDepartmentId(), msmStockItem.getCode(), msmStockItem.getName(),
                msmStockItem.getDescription(), msmStockItem.getUnitPrice() / 100,
                msmStockItem.getUnitPrice() % 100, msmStockItem.getQuantityInStock());
        this.msmStockItem = msmStockItem;
    }

    /**
     * Get the original MSMStockItem being adapted.
     *
     */
    public MSMStockItem getMsmStockItem() {
        return msmStockItem;
    }
}