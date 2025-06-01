package repository.entity;

import model.WorkspaceType;

import java.util.UUID;

public class Workspace {
    private final UUID id;
    private WorkspaceType type;
    private double price;
    private boolean available;

    public Workspace(WorkspaceType type, double price, boolean available) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.price = price;
        this.available = available;
    }

    public UUID getId() {
        return id;
    }

    public WorkspaceType getType() {
        return type;
    }

    public void setType(WorkspaceType type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailability(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return String.format("Workspace{id=%s, type=%s, price=%.2f, available=%b}", id, type, price, available);
    }
}
