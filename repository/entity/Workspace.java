package repository.entity;

import model.WorkspaceType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public class Workspace implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private WorkspaceType type;
    private BigDecimal price;
    private boolean available;

    public Workspace(WorkspaceType type, BigDecimal price, boolean available) {
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
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
        return String.format("Workspace{id=%s, type=%s, price=%s, available=%b}",
                id, type, price, available);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workspace that = (Workspace) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
