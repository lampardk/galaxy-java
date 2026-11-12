import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PaymentProcessor implements Runnable { // means that it has a run() function and pool.submit(PaymentProcessor) works
    private String workerName; // task. 
    private BlockingQueue<CustomerOrder> submissionQueue; // converyeerbelt. <Data Type>
    private BlockingQueue<CustomerOrder> packingQueue;
    private InventoryManager inventoryManager;
    private NotificationService notificationService;
    // NotificationService. 

    private static AtomicInteger successfulPayments = new AtomicInteger(0); // static final. 
    private static AtomicInteger failedPayments = new AtomicInteger(0); // i = 1; +1; 2; 1+1+1 = 3;
    private static AtomicInteger outOfStockCancellations = new AtomicInteger(0); // concurrency. 

    private boolean keepRunning = true;
    // random here. 

    public PaymentProcessor(String workerName, 
                            BlockingQueue<CustomerOrder> submissionQueue,
                            BlockingQueue<CustomerOrder> packingQueue,
                            InventoryManager inventoryManager,
                            NotificationService notificationService ) {
        this.workerName = workerName;
        this.submissionQueue = submissionQueue;
        this.packingQueue = packingQueue;
        this.inventoryManager = inventoryManager;
        this.notificationService = notificationService;
    }

    public void stopWorking() {
        this.keepRunning = false;
    }

    @Override // --> run()
    public void run() {
        while (keepRunning || !submissionQueue.isEmpty()) {
            try {
                CustomerOrder order = submissionQueue.poll(50, TimeUnit.MILLISECONDS); // 50ms.
                if (order==null) continue;
                order.setStatus(OrderStatus.PAYMENT_PENDING, "Under review by " + workerName);

                boolean stockReserved = inventoryManager.reserveStock(order);
                if(!stockReserved) {
                    order.setStatus(OrderStatus.CANCELLED, "Insufficient Stock for " + order.getProductName());
                    outOfStockCancellations.incrementAndGet();
                    notificationService.notifyOrderCancelled(order, "Item out of stock");
                    continue;
                }
                // add latency 
                boolean paymentSuccess = true; // edit to have 92% success. 

                if(paymentSuccess) {
                    order.setStatus(OrderStatus.PAID, "Paymet Successful by " + workerName);
                    successfulPayments.incrementAndGet(); 
                    notificationService.notifyOrderConfirmed(order);

                    packingQueue.put(order);
                } else {
                    order.setStatus(OrderStatus.CANCELLED, "Payments Failed");
                    failedPayments.incrementAndGet();
                    inventoryManager.releaseStock(order);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // restore interrupted thread. 
                break;
            }
        }
    }
    public static int getSuccessfulPayments() { // static. 
        return successfulPayments.get();
    }
    public static int getFailedPayments() {
        return failedPayments.get(); // failedPayments.get()
    }
    public static int getOutOfOrderCancellations() {
        return outOfStockCancellations.get();
    }
    public static int getTotalCancelled() { // static - class not object. PaymentProcessor.getTotal
        return failedPayments.get() + outOfStockCancellations.get();
    }
}
