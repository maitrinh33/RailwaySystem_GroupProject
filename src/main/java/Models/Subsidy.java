package Models;

public class Subsidy {
    private int id;
    private String description;
    private double amount;

    // Constructor
    public Subsidy(int id, String description, double amount) {
        this.id = id;
        this.description = description;
        this.amount = amount;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}