import java.io.*;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

// Main Class
public class TakeoutRestaurantApp {
    public static void main(String[] args) {
        Restaurant restaurant = new Restaurant("Tasty Bites");
        restaurant.loadOrdersFromJson("orders.json");

        // Add some orders
        restaurant.addOrder(new DineInOrder("John Doe", "Burger", 2, 20.00));
        restaurant.addOrder(new TakeoutOrder("Jane Smith", "Pasta", 1, 15.00, "123 Main St"));
        restaurant.addOrder(new DeliveryOrder("Mike Johnson", "Pizza", 3, 30.00, "456 Elm St", "Mike's Pizza"));

        // Display orders
        restaurant.displayOrders();

        // Modify an order
        restaurant.modifyOrder("Jane Smith", "Pasta", new TakeoutOrder("Jane Smith", "Pasta", 2, 30.00, "123 Main St"));

        // Delete an order
        restaurant.deleteOrder("John Doe", "Burger");

        // Display orders after deletion
        restaurant.displayOrders();

        // Save orders to JSON
        restaurant.saveOrdersToJson("orders.json");
    }
}

// Order Class
abstract class Order {
    private String customerName;
    private String itemName;
    private int quantity;
    private double price;

    public Order(String customerName, String itemName, int quantity, double price) {
        this.customerName = customerName;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return customerName + " ordered " + quantity + " x " + itemName + " for $" + price;
    }

    public abstract JSONObject toJson();

    public static Order fromJson(JSONObject json) {
        String type = json.getString("type");
        if (type.equals("DineInOrder")) {
            return new DineInOrder(
                json.getString("customerName"),
                json.getString("itemName"),
                json.getInt("quantity"),
                json.getDouble("price")
            );
        } else if (type.equals("TakeoutOrder")) {
            return new TakeoutOrder(
                json.getString("customerName"),
                json.getString("itemName"),
                json.getInt("quantity"),
                json.getDouble("price"),
                json.getString("address")
            );
        } else if (type.equals("DeliveryOrder")) {
            return new DeliveryOrder(
                json.getString("customerName"),
                json.getString("itemName"),
                json.getInt("quantity"),
                json.getDouble("price"),
                json.getString("address"),
                json.getString("deliveryCompany")
            );
        } else {
            throw new IllegalArgumentException("Unknown order type: " + type);
        }
    }
}

// DineInOrder Class
class DineInOrder extends Order {
    public DineInOrder(String customerName, String itemName, int quantity, double price) {
        super(customerName, itemName, quantity, price);
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("type", "DineInOrder");
        json.put("customerName", getCustomerName());
        json.put("itemName", getItemName());
        json.put("quantity", getQuantity());
        json.put("price", getPrice());
        return json;
    }

    @Override
    public String toString() {
        return super.toString() + " (DineIn)";
    }
}

// TakeoutOrder Class
class TakeoutOrder extends Order {
    private String address;

    public TakeoutOrder(String customerName, String itemName, int quantity, double price, String address) {
        super(customerName, itemName, quantity, price);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("type", "TakeoutOrder");
        json.put("customerName", getCustomerName());
        json.put("itemName", getItemName());
        json.put("quantity", getQuantity());
        json.put("price", getPrice());
        json.put("address", address);
        return json;
    }

    @Override
    public String toString() {
        return super.toString() + " (Takeout: " + address + ")";
    }
}

// DeliveryOrder Class
class DeliveryOrder extends TakeoutOrder {
    private String deliveryCompany;

    public DeliveryOrder(String customerName, String itemName, int quantity, double price, String address, String deliveryCompany) {
        super(customerName, itemName, quantity, price, address);
        this.deliveryCompany = deliveryCompany;
    }

    public String getDeliveryCompany() {
        return deliveryCompany;
    }

    public void setDeliveryCompany(String deliveryCompany) {
        this.deliveryCompany = deliveryCompany;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("type", "DeliveryOrder");
        json.put("customerName", getCustomerName());
        json.put("itemName", getItemName());
        json.put("quantity", getQuantity());
        json.put("price", getPrice());
        json.put("address", getAddress());
        json.put("deliveryCompany", deliveryCompany);
        return json;
    }

    @Override
    public String toString() {
        return super.toString() + " (Delivery by " + deliveryCompany + ")";
    }
}

// Restaurant Class
import java.nio.file.Files;
import java.nio.file.Paths;

class Restaurant {
    private String name;
    private ArrayList<Order> orders;

    public Restaurant(String name) {
        this.name = name;
        this.orders = new ArrayList<>();
    }

    public void addOrder(Order order) {
        orders.add(order);
        System.out.println("Added: " + order);
    }

    public void modifyOrder(String customerName, String itemName, Order newOrder) {
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (order.getCustomerName().equalsIgnoreCase(customerName) && order.getItemName().equalsIgnoreCase(itemName)) {
                orders.set(i, newOrder);
                System.out.println("Modified: " + newOrder);
                return;
            }
        }
        System.out.println("Order not found: " + customerName + " " + itemName);
    }

    public void deleteOrder(String customerName, String itemName) {
        for (Order order : orders) {
            if (order.getCustomerName().equalsIgnoreCase(customerName) && order.getItemName().equalsIgnoreCase(itemName)) {
                orders.remove(order);
                System.out.println("Deleted: " + order);
                return;
            }
        }
        System.out.println("Order not found: " + customerName + " " + itemName);
    }

    public void displayOrders() {
        System.out.println("\nOrders at " + name + ":");
        for (Order order : orders) {
            System.out.println(order);
        }
    }

    public void loadOrdersFromJson(String filename) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filename)));
            JSONArray jsonArray = new JSONArray(content);
            orders.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                orders.add(Order.fromJson(jsonArray.getJSONObject(i)));
            }
            System.out.println("Loaded orders from " + filename);
        } catch (IOException e) {
            System.out.println("Error reading file " + filename + ": " + e.getMessage());
        }
    }

    public void saveOrdersToJson(String filename) {
        JSONArray jsonArray = new JSONArray();
        for (Order order : orders) {
            jsonArray.put(order.toJson());
        }
        try (FileWriter file = new FileWriter(filename)) {
            file.write(jsonArray.toString());
            System.out.println("Saved orders to " + filename);
        } catch (IOException e) {
            System.out.println("Error writing to file " + filename + ": " + e.getMessage());
        }
    }
}
