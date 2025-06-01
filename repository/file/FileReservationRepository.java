package repository.file;

import exception.ReservationNotFoundException;
import repository.api.IReservationRepository;
import repository.entity.Reservation;

import java.util.List;
import java.util.UUID;

public class FileReservationRepository extends AbstractFileRepository<Reservation> implements IReservationRepository {

    public FileReservationRepository() {
        super("data/reservations.ser");
    }

    @Override
    public Reservation add(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null!");
        }
        items.add(reservation);
        writeToFile();
        return reservation;
    }

    @Override
    public Reservation findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Reservation ID cannot be null!");
        }
        return items.stream()
                .filter(reservation -> reservation.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ReservationNotFoundException(id));
    }

    @Override
    public List<Reservation> findByUser(String login) {
        if (login == null || login.isEmpty()) {
            throw new IllegalArgumentException("User login cannot be null or empty!");
        }
        return items.stream()
                .filter(reservation -> reservation.getUser() != null
                        && login.equals(reservation.getUser().getLogin()))
                .toList();
    }

    @Override
    public List<Reservation> findAll() {
        return List.copyOf(items);
    }

    @Override
    public boolean remove(UUID id) {
        Reservation reservation = findById(id);
        boolean removed = items.remove(reservation);
        if (removed) {
            writeToFile();
        }
        return removed;
    }
}
