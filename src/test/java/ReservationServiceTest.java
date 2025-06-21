import model.ReservationDTO;
import model.UserDTO;
import model.WorkspaceDTO;
import model.WorkspaceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.api.IReservationRepository;
import repository.entity.Reservation;
import repository.entity.User;
import repository.entity.Workspace;
import service.ReservationService;
import service.api.IUserService;
import service.api.IWorkspaceService;
import exception.ReservationNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    private IReservationRepository reservationRepository;
    private IUserService userService;
    private IWorkspaceService workspaceService;
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationRepository = mock(IReservationRepository.class);
        userService = mock(IUserService.class);
        workspaceService = mock(IWorkspaceService.class);
        reservationService = new ReservationService(reservationRepository, userService, workspaceService);
    }

    @Test
    void add_ShouldCreateAndReturnReservation() {
        Workspace workspace = new Workspace(WorkspaceType.PRIVATE, new BigDecimal("100.00"), true);
        UUID workspaceId = workspace.getId();

        UserDTO userDTO = new UserDTO("user");
        ReservationDTO reservationDTO = new ReservationDTO(
                workspaceId,
                "user",
                LocalDate.now(),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0)
        );

        User user = new User(model.Role.CUSTOMER, "user");
        Reservation expectedReservation = new Reservation(
                user,
                workspace,
                reservationDTO.getDate(),
                reservationDTO.getStartTime(),
                reservationDTO.getEndTime()
        );

        when(userService.login(userDTO)).thenReturn(user);
        when(workspaceService.find(workspaceId)).thenReturn(workspace);
        when(reservationRepository.add(any(Reservation.class))).thenReturn(expectedReservation);

        Reservation actualReservation = reservationService.add(reservationDTO, userDTO);

        assertSame(expectedReservation, actualReservation);
        verify(userService).login(userDTO);
        verify(workspaceService).find(workspaceId);
        verify(reservationRepository).add(any(Reservation.class));
        verify(workspaceService).update(eq(workspaceId), any(WorkspaceDTO.class));
    }

    @Test
    void add_ShouldThrowException_WhenReservationDtoIsNull() {
        UserDTO userDTO = new UserDTO("user");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reservationService.add(null, userDTO));
        assertEquals("ReservationDTO cannot be null", ex.getMessage());
        verifyNoInteractions(userService, workspaceService, reservationRepository);
    }

    @Test
    void add_ShouldThrowException_WhenUserDtoIsNull() {
        Workspace workspace = new Workspace(WorkspaceType.PRIVATE, new BigDecimal("100.00"), true);
        UUID workspaceId = workspace.getId();

        ReservationDTO reservationDTO = new ReservationDTO(
                workspaceId,
                "user",
                LocalDate.now(),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0)
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reservationService.add(reservationDTO, null));
        assertEquals("UserDTO or login cannot be null or empty", ex.getMessage());
        verifyNoInteractions(userService, workspaceService, reservationRepository);
    }

    @Test
    void add_ShouldThrowException_WhenUserLoginIsBlank() {
        Workspace workspace = new Workspace(WorkspaceType.PRIVATE, new BigDecimal("100.00"), true);
        UUID workspaceId = workspace.getId();

        ReservationDTO reservationDTO = new ReservationDTO(
                workspaceId,
                "user",
                LocalDate.now(),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0)
        );

        UserDTO userDTO = new UserDTO("   ");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reservationService.add(reservationDTO, userDTO));
        assertEquals("UserDTO or login cannot be null or empty", ex.getMessage());
        verifyNoInteractions(userService, workspaceService, reservationRepository);
    }

    @Test
    void add_ShouldThrowException_WhenWorkspaceIdIsNull() {
        ReservationDTO reservationDTO = new ReservationDTO(null,
                "user", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0));
        UserDTO userDTO = new UserDTO("user");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reservationService.add(reservationDTO, userDTO));
        assertEquals("Workspace ID cannot be null", ex.getMessage());
        verifyNoInteractions(userService, workspaceService, reservationRepository);
    }

    @Test
    void add_ShouldThrowException_WhenDateIsNull() {
        Workspace workspace = new Workspace(WorkspaceType.PRIVATE, new BigDecimal("100.00"), true);
        UUID workspaceId = workspace.getId();

        ReservationDTO reservationDTO = new ReservationDTO(
                workspaceId,
                "user",
                null,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0)
        );

        UserDTO userDTO = new UserDTO("user");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reservationService.add(reservationDTO, userDTO));
        assertEquals("Reservation date cannot be null", ex.getMessage());
        verifyNoInteractions(userService, workspaceService, reservationRepository);
    }

    @Test
    void add_ShouldThrowException_WhenStartOrEndTimeIsNull() {
        Workspace workspace = new Workspace(WorkspaceType.PRIVATE, new BigDecimal("100.00"), true);
        UUID workspaceId = workspace.getId();

        UserDTO userDTO = new UserDTO("user");

        ReservationDTO reservationDTO1 = new ReservationDTO(
                workspaceId,
                "user",
                LocalDate.now(),
                null,
                LocalTime.of(10, 0)
        );
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> reservationService.add(reservationDTO1, userDTO));
        assertEquals("Start time and end time cannot be null", ex1.getMessage());

        ReservationDTO reservationDTO2 = new ReservationDTO(
                workspaceId,
                "user",
                LocalDate.now(),
                LocalTime.of(9, 0),
                null
        );
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> reservationService.add(reservationDTO2, userDTO));
        assertEquals("Start time and end time cannot be null", ex2.getMessage());

        verifyNoInteractions(userService, workspaceService, reservationRepository);
    }

    @Test
    void findByUser_ShouldReturnReservations() {
        String login = "user";
        UserDTO userDTO = new UserDTO(login);
        Set<Reservation> reservations = Set.of(mock(Reservation.class));

        when(reservationRepository.findByUser(login)).thenReturn(reservations);

        Set<Reservation> result = reservationService.findByUser(userDTO);

        assertEquals(reservations, result);
        verify(reservationRepository).findByUser(login);
    }

    @Test
    void findByUser_ShouldThrowException_WhenUserDtoIsNullOrLoginBlank() {
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> reservationService.findByUser(null));
        assertEquals("UserDTO or login cannot be null or empty", ex1.getMessage());

        UserDTO userDTO = new UserDTO("   ");
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> reservationService.findByUser(userDTO));
        assertEquals("UserDTO or login cannot be null or empty", ex2.getMessage());

        verifyNoInteractions(reservationRepository);
    }

    @Test
    void findAll_ShouldReturnAllReservations() {
        Set<Reservation> allReservations = Set.of(mock(Reservation.class));
        when(reservationRepository.findAll()).thenReturn(allReservations);

        Set<Reservation> result = reservationService.findAll();

        assertEquals(allReservations, result);
        verify(reservationRepository).findAll();
    }

    @Test
    void remove_ShouldReturnTrueAndUpdateWorkspace_WhenReservationExists() {
        User user = new User(model.Role.CUSTOMER, "user");
        Workspace workspace = new Workspace(WorkspaceType.PRIVATE, new BigDecimal("100.00"), true);
        UUID reservationId = UUID.randomUUID();
        Reservation reservation = new Reservation(user, workspace, LocalDate.now(),
                LocalTime.of(9, 0), LocalTime.of(10, 0));

        when(reservationRepository.findById(reservationId)).thenReturn(reservation);
        when(reservationRepository.remove(reservationId)).thenReturn(true);

        boolean result = reservationService.remove(reservationId);

        assertTrue(result);
        verify(reservationRepository).remove(reservationId);
        verify(workspaceService).update(eq(workspace.getId()), any(WorkspaceDTO.class));
        assertTrue(workspace.isAvailable());
    }

    @Test
    void remove_ShouldReturnFalse_WhenReservationNotRemoved() {
        User user = new User(model.Role.CUSTOMER, "user");
        Workspace workspace = new Workspace(WorkspaceType.PRIVATE, new BigDecimal("100.00"), true);
        UUID reservationId = UUID.randomUUID();
        Reservation reservation = new Reservation(user, workspace, LocalDate.now(),
                LocalTime.of(9, 0), LocalTime.of(10, 0));

        when(reservationRepository.findById(reservationId)).thenReturn(reservation);
        when(reservationRepository.remove(reservationId)).thenReturn(false);

        boolean result = reservationService.remove(reservationId);

        assertFalse(result);
        verify(reservationRepository).remove(reservationId);
        verify(workspaceService, never()).update(any(), any());
    }

    @Test
    void remove_ShouldThrowReservationNotFoundException_WhenReservationNotFound() {
        UUID reservationId = UUID.randomUUID();
        when(reservationRepository.findById(reservationId)).thenReturn(null);

        ReservationNotFoundException ex = assertThrows(ReservationNotFoundException.class,
                () -> reservationService.remove(reservationId));
        assertEquals("Reservation with ID " + reservationId + " not found", ex.getMessage());

        verify(reservationRepository).findById(reservationId);
        verifyNoMoreInteractions(reservationRepository, workspaceService);
    }

    @Test
    void remove_ShouldThrowException_WhenIdIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reservationService.remove(null));
        assertEquals("ID cannot be null", ex.getMessage());

        verifyNoInteractions(reservationRepository, workspaceService);
    }
}
