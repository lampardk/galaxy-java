public class CustomerOrder {
    private int orderId; // orderid.
    private String customerId;
    private String productName; // A and B
    private int quantity;

    private OrderStatus status; // enum.
    private int assignedVehicleid = -1; // 4. 

    // Add list here for history tracking. 
    
    public CustomerOrder(int orderId, String customerId, String productName, int quantity) {
        this.orderId = orderId; // this - current object. obj.orderid. 
        this.customerId = customerId;
        this.productName = productName;
        this.quantity = quantity;
        this.status = OrderStatus.SUBMITTED;
        // Add the log to statusHistory. 
    }

    // Access the Attributes outside the class. 
    public int getOrderId(){ // getter. 
        return orderId;
    }
    public String getCustomerId() {
        return customerId;
    }
    public String getProductName() {
        return productName;
    }
    public int getQuantity() {
        return quantity;
    }
    public OrderStatus getStatus() {
        return status;
    }
    public void setStatus(OrderStatus newStatus, String reason) { //  setter. 
        this.status = newStatus;
        // addHistory - StatusHistory. 
    }
    public int getAssignedVehicleId() { // setter. 
        return assignedVehicleid;
    }

    public void setAssignedvehicleId(int vehicleId) {
        this.assignedVehicleid = vehicleId;
    }

    // addHistory and getStatushistory functions. 
    
    // system.out.println(order);  --> javaHand.CustomerOrder@sfhrbhj
    @Override 
    public String toString()  {
        return "Order #" + orderId + "[" + customerId + "|" 
        + quantity + "x" + productName + status + "]"; // Order #42 [custromer-1] | 3x productA SUMITTED]
    }
}
