public class NotificationService {
    public void notifyOrderConfirmed(CustomerOrder order) {
        //
    }
    public void notifyOrderDispatched(CustomerOrder order, int vehicleId) {
        System.out.println("[SMS to " + order.getCustomerId() + "] Order #" + order.getOrderId()
                + " is OUT FOR DELIVERY in Vehicle #" + vehicleId + "!");
    }
    public void notifyOrderDelivered(CustomerOrder order) {
        System.out.println("[SMS to " + order.getCustomerId() + "] Order #" + order.getOrderId()
                + " has been successfully DELIVERED! Thank you for shopping with us.");
    }
    public void notifyOrderCancelled(CustomerOrder order, String reason) {
        System.out.println("[SMS to " + order.getCustomerId() + "] Order #" + order.getOrderId()
                + " was CANCELLED (" + reason + "). Any reserved funds or stock have been released.");
    }
}
