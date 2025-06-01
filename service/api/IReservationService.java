package service.api;

import model.ReservationDTO;
import model.UserDTO;
import repository.entity.Reservation;

import java.util.List;
import java.util.UUID;

public interface IReservationService {
    Reservation add(ReservationDTO reservationDTO, UserDTO userDTO);

    List<Reservation> findByUser(UserDTO userDTO);

    List<Reservation> findAll();

    boolean remove(UUID id);
}
