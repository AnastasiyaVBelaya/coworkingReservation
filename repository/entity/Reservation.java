package repository.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class Reservation {
    private final UUID id;
    private final User user;
    private final Workspace workspace;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public Reservation(User user, Workspace workspace, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.workspace = workspace;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return String.format("Reservation{id=%s, user=%s, workspace=%s, date=%s, time=%s-%s}",
                id, user.getLogin(), workspace.getId(), date, startTime, endTime);
    }
}
