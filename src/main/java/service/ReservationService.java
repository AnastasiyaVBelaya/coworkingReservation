package service;

import model.ReservationDTO;
import model.UserDTO;
import model.WorkspaceDTO;
import repository.api.IReservationRepository;
import repository.entity.Reservation;
import repository.entity.User;
import repository.entity.Workspace;
import service.api.IReservationService;
import service.api.IUserService;
import service.api.IWorkspaceService;

import java.util.Set;
import java.util.UUID;

public class ReservationService implements IReservationService {
    private final IReservationRepository reservationRepository;
    private final IUserService userService;
    private final IWorkspaceService workspaceService;

    public ReservationService(IReservationRepository reservationRepository,
                              IUserService userService,
                              IWorkspaceService workspaceService) {
        this.reservationRepository = reservationRepository;
        this.userService = userService;
        this.workspaceService = workspaceService;
    }

    @Override
    public Reservation add(ReservationDTO reservationDTO, UserDTO userDTO) {
        if (reservationDTO == null) {
            throw new IllegalArgumentException("ReservationDTO cannot be null");
        }
        if (userDTO == null || isBlank(userDTO.getLogin())) {
            throw new IllegalArgumentException("UserDTO or login cannot be null or empty");
        }
        if (reservationDTO.getWorkspaceId() == null) {
            throw new IllegalArgumentException("Workspace ID cannot be null");
        }
        if (reservationDTO.getDate() == null) {
            throw new IllegalArgumentException("Reservation date cannot be null");
        }
        if (reservationDTO.getStartTime() == null || reservationDTO.getEndTime() == null) {
            throw new IllegalArgumentException("Start time and end time cannot be null");
        }
        User user = userService.login(userDTO);
        Workspace workspace = workspaceService.find(reservationDTO.getWorkspaceId());
        Reservation reservation = new Reservation(
                user,
                workspace,
                reservationDTO.getDate(),
                reservationDTO.getStartTime(),
                reservationDTO.getEndTime()
        );
        Reservation savedReservation = reservationRepository.add(reservation);
        workspace.setAvailability(false);
        workspaceService.update(workspace.getId(),
                new WorkspaceDTO(workspace.getType(), workspace.getPrice(), workspace.isAvailable()));
        return savedReservation;
    }

    @Override
    public Set<Reservation> findByUser(UserDTO userDTO) {
        if (userDTO == null || isBlank(userDTO.getLogin())) {
            throw new IllegalArgumentException("UserDTO or login cannot be null or empty");
        }

        return reservationRepository.findByUser(userDTO.getLogin());
    }

    @Override
    public Set<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    public boolean remove(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        Reservation reservation = reservationRepository.findById(id);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found for ID: " + id);
        }
        boolean removed = reservationRepository.remove(id);
        if (removed) {
            Workspace workspace = reservation.getWorkspace();
            workspace.setAvailability(true);
            workspaceService.update(workspace.getId(),
                    new WorkspaceDTO(workspace.getType(), workspace.getPrice(), workspace.isAvailable()));
        }

        return removed;
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
