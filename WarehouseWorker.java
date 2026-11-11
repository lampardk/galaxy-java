import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class WarehouseWorker implements Runnable { // task - pack items, add them to delivery vehiocle. 
    String workerName;
    BlockingQueue<CustomerOrder> packingQueue;
    BlockingQueue<CustomerOrder> deliveryQueue;
    
    private static AtomicInteger totalOrdersFulfilled = new AtomicInteger(0); // static
    private boolean keepRunning = true; // volatile

    public WarehouseWorker(String workerName, BlockingQueue<CustomerOrder> packingQueue, BlockingQueue<CustomerOrder> deliveryQueue){
        this.workerName = workerName;
        this.packingQueue = packingQueue;
        this.deliveryQueue = deliveryQueue;
    }

    public void stopRunning() {
        this.keepRunning = false;
    }

    @Override 
    public void run() {
        while(keepRunning || !packingQueue.isEmpty()) {
            try {
                CustomerOrder order = packingQueue.poll(50, TimeUnit.MILLISECONDS);
                if (order==null) continue;
                order.setStatus(OrderStatus.PACKED, "packaged by " + workerName);
                totalOrdersFulfilled.incrementAndGet(); // i++
                deliveryQueue.put(order); // .put()
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public static int getOrdersFulfilled() { // static
        return totalOrdersFulfilled.get();
    }
}
