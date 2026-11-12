import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Simulation {
    public void main() throws InterruptedException {
        System.out.println("=============================START=========================================");
        
        int initialStockA = 100;
        int initialStockB = 50;
        InventoryManager inventoryManager = new InventoryManager(initialStockA, initialStockB);
        NotificationService notificationService =  new NotificationService();
        DeliveryManager deliveryManager = new DeliveryManager(notificationService);

        BlockingQueue<CustomerOrder> submissionQueue = new LinkedBlockingQueue<>();
        BlockingQueue<CustomerOrder> packingQueue = new LinkedBlockingQueue<>();
        BlockingQueue<CustomerOrder> deliveryQueue = new LinkedBlockingQueue<>();

        ExecutorService paymentPool = Executors.newFixedThreadPool(3); // Thread name = new Thread
        ExecutorService wareHousePool = Executors.newFixedThreadPool(5); // thread.
        ExecutorService deliveryPool = Executors.newFixedThreadPool(4);

        List<PaymentProcessor> paymentWorkers = new ArrayList<>();
        for(int i=1;i<=3;i++) {
            PaymentProcessor worker = new PaymentProcessor("Payworker-#"+i, submissionQueue, packingQueue, inventoryManager, notificationService);
            paymentWorkers.add(worker);
            paymentPool.submit(worker);
        }

        List<WarehouseWorker> warehouseWorkers = new ArrayList<>();
        for(int i=1;i<=5;i++) {
            WarehouseWorker worker = new WarehouseWorker("Warehouse-"+i, packingQueue, deliveryQueue);
            warehouseWorkers.add(worker);
            wareHousePool.submit(worker);
        }

        java.util.concurrent.atomic.AtomicInteger deliveryRunning = new java.util.concurrent.atomic.AtomicInteger(1);

        for(int i=1;i<=4;i++) {
            deliveryPool.submit(() -> {
                while(deliveryRunning.get() == 1 || !deliveryQueue.isEmpty()) {
                    try {
                        CustomerOrder order = deliveryQueue.poll(50, TimeUnit.MILLISECONDS);
                        if (order != null) {
                            deliveryManager.deliverOrder(order);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
        int totalCustomerOrders = 1000;
        System.out.println("===========SUBMITTING ORDERS==============");
        ExecutorService customerSubmissionPool = Executors.newFixedThreadPool(20);
        for (int orderId=1;orderId<=totalCustomerOrders;orderId++) {
            final int id = orderId;
            customerSubmissionPool.submit(() -> {
                String product = (id%2==0) ? "Product A" : "Product B"; // true ? ...: ...
                int quantity = 1;
                CustomerOrder order =  new CustomerOrder(id, "customer-"+id, product, quantity);
                submissionQueue.offer(order);
            });
        }

        customerSubmissionPool.shutdown();
        customerSubmissionPool.awaitTermination(5, TimeUnit.SECONDS);

        while (!submissionQueue.isEmpty()) Thread.sleep(50);
        for (PaymentProcessor p: paymentWorkers) p.stopWorking();
        paymentPool.shutdown();
        paymentPool.awaitTermination(5, TimeUnit.SECONDS);

        while (!packingQueue.isEmpty()) Thread.sleep(50);
        for (WarehouseWorker w: warehouseWorkers) w.stopRunning();
        wareHousePool.shutdown();
        wareHousePool.awaitTermination(5, TimeUnit.SECONDS);

        while (!deliveryQueue.isEmpty()) Thread.sleep(50);
        deliveryRunning.set(0);
        deliveryPool.shutdown();
        deliveryPool.awaitTermination(10, TimeUnit.SECONDS);

        int totalOrdersReceived = totalCustomerOrders;
        int successfulPayments = PaymentProcessor.getSuccessfulPayments();
        int failedPayments = PaymentProcessor.getFailedPayments();
        int ordersFulfilled = WarehouseWorker.getOrdersFulfilled(); // static. 
        int ordersDelivered = deliveryManager.getTotalOrdersDelivered();
        int totalCancelled = PaymentProcessor.getTotalCancelled();
        int remA = inventoryManager.getStock("Product A");
        int remB = inventoryManager.getStock("Product B");

        System.out.println("====================REPORT==========================");
        System.out.println("Total Orders Received: " + totalOrdersReceived);
        System.out.println("Total Successfull Payments: " + successfulPayments);
        System.out.println("Total Failed: " + failedPayments);
        System.out.println("Total Orders Fulfilled: " + ordersFulfilled);
        System.out.println("Total Orders Delivered: " + ordersDelivered);
        System.out.println("Total Cancelled: " + totalCancelled);
        System.out.println("Remaining Product A: " + remA);
        System.out.println("remaining Product B: "+ remB);


        System.out.println("=============================END=========================================");
    }
}
/**
 * PaymentProcessor payment = new payment;
 * payment.getOrder(); // static. 
 * PaymentProcessor.getOrder()
 */
