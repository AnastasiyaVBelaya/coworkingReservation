package repository.memory;

import exception.ReservationNotFoundException;
import repository.api.IReservationRepository;
import repository.entity.Reservation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InMemoryReservationRepository implements IReservationRepository {
    private final List<Reservation> reservations = new ArrayList<>();

    @Override
    public Reservation add(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null!");
        }
        reservations.add(reservation);
        return reservation;
    }

    @Override
    public Reservation findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Reservation ID cannot be null!");
        }
        return reservations.stream()
                .filter(reservation -> reservation.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ReservationNotFoundException(id));
    }

    @Override
    public List<Reservation> findByUser(String login) {
        if (login == null || login.isEmpty()) {
            throw new IllegalArgumentException("User login cannot be null or empty!");
        }
        return reservations.stream()
                .filter(reservation -> reservation.getUser() != null
                        && login.equals(reservation.getUser().getLogin()))
                .toList();
    }

    @Override
    public List<Reservation> findAll() {
        return List.copyOf(reservations);
    }

    @Override
    public boolean remove(UUID id) {
        Reservation reservation = findById(id);
        return reservations.remove(reservation);
    }
}
