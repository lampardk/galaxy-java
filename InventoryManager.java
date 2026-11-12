import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class InventoryManager {
    private ConcurrentHashMap<String, Integer> stock = new ConcurrentHashMap<>(); // key: value, A: 100, B: 50
    private ConcurrentHashMap<String, Integer> totalSold = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> totalCancelledStock = new ConcurrentHashMap<>();

    private final ReentrantLock inventoryLock = new ReentrantLock(true); // allows for FIFO. 

    public InventoryManager(int stockA, int stockB) { // storage. inv(100, 50)
        stock.put("Product A", stockA);
        stock.put("Product B", stockB);
        totalSold.put("Product A", 0);
        totalSold.put("Product B", 0);
        totalCancelledStock.put("Product A", 0);
        totalCancelledStock.put("Product B", 0);
    }

    public boolean reserveStock(CustomerOrder order) {
        inventoryLock.lock();
        try {
            String product = order.getProductName();
            int qty = order.getQuantity();
            int available = stock.get(product); // numer of items of that specific product.
            if (available >= qty) {
                stock.put(product, available - qty); // 1--, 2--
                totalSold.put(product, totalSold.get(product) + qty);
                return true;
            }
            else {
                return false;
            }
        } finally {
            inventoryLock.unlock();
        }
    }
    
    public void releaseStock(CustomerOrder order) {
        inventoryLock.lock();
        try {
            String product = order.getProductName();
            int qty = order.getQuantity();

            stock.put(product, stock.get(product) + qty);
            totalSold.put(product, totalSold.get(product) - qty);
            totalCancelledStock.put(product, totalCancelledStock.get(product) + qty);

            System.out.println("[STOCK RELEASED] Order #" + order.getOrderId() + "cancelled! " 
            + qty + "units of " + product + "Remaining" + stock.get(product));

        } finally {
            inventoryLock.unlock();
        }
    }
    
    public int getStock(String product) { // getStock(A): 100
        inventoryLock.lock();
        try {
            return stock.getOrDefault(product, 0);
        } finally {
            inventoryLock.unlock();
        }
    }
    public int getTotalSold(String product) {
        inventoryLock.lock();
        try {
            // ....
            return totalSold.getOrDefault(product, 0); // .get(), .getordefault()
        } finally {
            inventoryLock.unlock();
        }
    }
    public int getTotalCancelled(String product) {
        inventoryLock.lock();
        try {
        return totalCancelledStock.getOrDefault(product, 0);
        } finally {
            inventoryLock.unlock();
        }
    }
}
