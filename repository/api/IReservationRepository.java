package repository.api;

import repository.entity.Reservation;
import repository.entity.User;

import java.util.List;
import java.util.UUID;

public interface IReservationRepository {
    Reservation add(Reservation reservation);

    Reservation findById(UUID id);

    List<Reservation> findAll();

    List<Reservation> findByUser(String login);

    boolean remove(UUID id);
}
