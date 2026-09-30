package nti.service;

public class InventoryServiceImpl implements InventoryService {

    @Override
    public int checkStock(String sku) {
        System.out.println("  [REAL] Checking stock for SKU=" + sku);
        // fake inventory data
        return switch (sku) {
            case "SKU-001" -> 42;
            case "SKU-002" -> 7;
            default        -> 0;
        };
    }

    @Override
    public void reserveStock(String sku, int qty) {
        System.out.println("  [REAL] Reserving " + qty + " units of SKU=" + sku);

        if (qty > 100) {
            throw new IllegalStateException(
                    "Cannot reserve " + qty + " units of " + sku + " (max 100 per order)");
        }

        System.out.println("  [REAL] Reserved " + qty + " units of SKU=" + sku);
    }
}