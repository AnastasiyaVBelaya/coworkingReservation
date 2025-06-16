package repository.memory;

import exception.ReservationNotFoundException;
import repository.api.IReservationRepository;
import repository.entity.Reservation;

import java.util.*;

public class InMemoryReservationRepository implements IReservationRepository {
    private final Set<Reservation> reservations = new HashSet<>();
    private final Map<UUID, Reservation> idIndex = new HashMap<>();
    private final Map<String, Set<Reservation>> userIndex = new HashMap<>();

    @Override
    public Reservation add(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null!");
        }
        reservations.add(reservation);
        idIndex.put(reservation.getId(), reservation);

        String login = reservation.getUser() != null ? reservation.getUser().getLogin() : null;
        if (login != null) {
            userIndex.computeIfAbsent(login, k -> new HashSet<>()).add(reservation);
        }

        return reservation;
    }

    @Override
    public Reservation findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Reservation ID cannot be null!");
        }
        Reservation reservation = idIndex.get(id);
        if (reservation == null) {
            throw new ReservationNotFoundException(id);
        }
        return reservation;
    }

    @Override
    public Set<Reservation> findByUser(String login) {
        if (login == null || login.isEmpty()) {
            throw new IllegalArgumentException("User login cannot be null or empty!");
        }
        return userIndex.getOrDefault(login, Collections.emptySet());
    }

    @Override
    public Set<Reservation> findAll() {
        return Set.copyOf(reservations);
    }

    @Override
    public boolean remove(UUID id) {
        Reservation reservation = idIndex.remove(id);
        if (reservation == null) {
            return false;
        }
        boolean removed = reservations.remove(reservation);
        if (removed) {
            String login = reservation.getUser() != null ? reservation.getUser().getLogin() : null;
            if (login != null) {
                Set<Reservation> userReservations = userIndex.get(login);
                if (userReservations != null) {
                    userReservations.remove(reservation);
                    if (userReservations.isEmpty()) {
                        userIndex.remove(login);
                    }
                }
            }
        } else {
            idIndex.put(id, reservation);
        }
        return removed;
    }
}
