package repository.file;

import exception.ReservationNotFoundException;
import repository.api.IReservationRepository;
import repository.entity.Reservation;

import java.util.*;

public class FileReservationRepository extends AbstractFileRepository<Reservation> implements IReservationRepository {

    private final Map<UUID, Reservation> idIndex;
    private final Map<String, Set<Reservation>> userIndex;

    public FileReservationRepository() {
        super("data/reservations.ser");
        this.idIndex = new HashMap<>();
        this.userIndex = new HashMap<>();

        for (Reservation r : items) {
            idIndex.put(r.getId(), r);
            indexByUser(r);
        }
    }

    private void indexByUser(Reservation reservation) {
        if (reservation.getUser() != null && reservation.getUser().getLogin() != null) {
            userIndex.computeIfAbsent(reservation.getUser().getLogin(), k -> new HashSet<>())
                    .add(reservation);
        }
    }

    private void deindexByUser(Reservation reservation) {
        if (reservation.getUser() != null && reservation.getUser().getLogin() != null) {
            Set<Reservation> set = userIndex.get(reservation.getUser().getLogin());
            if (set != null) {
                set.remove(reservation);
                if (set.isEmpty()) {
                    userIndex.remove(reservation.getUser().getLogin());
                }
            }
        }
    }

    @Override
    public Reservation add(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null!");
        }
        items.add(reservation);
        idIndex.put(reservation.getId(), reservation);
        indexByUser(reservation);
        writeToFile();
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
        return userIndex.containsKey(login)
                ? Set.copyOf(userIndex.get(login))
                : Collections.emptySet();
    }

    @Override
    public Set<Reservation> findAll() {
        return Set.copyOf(items);
    }

    @Override
    public boolean remove(UUID id) {
        Reservation reservation = idIndex.remove(id);
        if (reservation == null) {
            return false;
        }
        boolean removed = items.remove(reservation);
        if (removed) {
            deindexByUser(reservation);
            writeToFile();
        } else {
            idIndex.put(id, reservation);
        }
        return removed;
    }
}
