import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class DeliveryManager {
    private List<DeliveryVehicle> vehicleFleet = new ArrayList<>();
    private ConcurrentLinkedQueue<DeliveryVehicle> idleVehicles = new ConcurrentLinkedQueue<>();
    private Semaphore vehicleSemaphore = new Semaphore(4, true); // Shared resource. 
    private AtomicInteger totalOrdersDelivered = new AtomicInteger();

    public DeliveryManager() {
        for(int i=1;i<=4;i++) {
            DeliveryVehicle vehicle = new DeliveryVehicle(i, "KDD-" + 100+i + "X");
            vehicleFleet.add(vehicle);
            idleVehicles.add(vehicle);
        }
    }

    public boolean deliverOrder(CustomerOrder order) {
        try {
            vehicleSemaphore.acquire(); // A vehicle. 
            DeliveryVehicle vehicle = idleVehicles.poll(); // the top. FIFO. 
            // if vehicle == null, release and return false. 
            order.setAssignedvehicleId(vehicle.getVehicleid());
            order.setStatus(OrderStatus.OUT_FOR_DELIVERY, "Loaded to #" + vehicle.getVehicleid());

            // Thread sleep
            Thread.sleep(30);

            order.setStatus(OrderStatus.DELIVERED, "Delivered! by #" + vehicle.getVehicleid());
            totalOrdersDelivered.incrementAndGet(); // 1++
            vehicle.incrementTrips();

            idleVehicles.add(vehicle);
            vehicleSemaphore.release();
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    public int getTotalOrdersDelivered() {
        return totalOrdersDelivered.get();
    }
    public List<DeliveryVehicle> getVehicleFleet() {
        return vehicleFleet;
    }
    // Vehicle Use report!!
}
