import java.util.concurrent.atomic.AtomicInteger;

public class DeliveryVehicle {
    private int vehicleid;
    private String licensePlate;
    AtomicInteger tripsCompleted = new AtomicInteger(0);

    public DeliveryVehicle(int vehicleId, String licensePlate) {
        this.vehicleid = vehicleId;
        this.licensePlate = licensePlate;
    }
    public int getVehicleid() {
        return vehicleid;
    }
    public String getLicensePlate() {
        return licensePlate;
    }
    public void incrementTrips() {
        tripsCompleted.incrementAndGet();
    }

    @Override 
    public String toString() { // print(obj) 
        return "Vehicle #" + vehicleid + "(" 
        + licensePlate + ")" + tripsCompleted + "trips completed";
    }
}
