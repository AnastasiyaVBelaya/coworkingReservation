package model;

public class WorkspaceDTO {
    private WorkspaceType type;
    private double price;
    private boolean available;

    public WorkspaceDTO(WorkspaceType type, double price, boolean available) {
        this.type = type;
        this.price = price;
        this.available = available;
    }

    public WorkspaceType getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return available;
    }
}
