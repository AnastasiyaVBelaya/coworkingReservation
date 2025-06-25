package repository.db;

import exception.UserNotFoundException;
import exception.WorkspaceNotFoundException;
import repository.api.IReservationRepository;
import repository.entity.Reservation;
import repository.entity.User;
import repository.entity.Workspace;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class DBReservationRepository implements IReservationRepository {

    private static final String INSERT_SQL =
            "INSERT INTO reservations (id, user_login, workspace_id, reservation_date, start_time, end_time) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SELECT_BY_ID_SQL =
            "SELECT * FROM reservations WHERE id = ?";

    private static final String SELECT_ALL_SQL =
            "SELECT * FROM reservations";

    private static final String SELECT_BY_USER_SQL =
            "SELECT * FROM reservations WHERE user_login = ?";

    private static final String DELETE_SQL =
            "DELETE FROM reservations WHERE id = ?";

    private final DBUserRepository userRepository;
    private final DBWorkspaceRepository workspaceRepository;

    public DBReservationRepository() {
        this.userRepository = new DBUserRepository();
        this.workspaceRepository = new DBWorkspaceRepository();
    }

    @Override
    public Reservation add(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }

        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

            stmt.setObject(1, reservation.getId());
            stmt.setString(2, reservation.getUser().getLogin());
            stmt.setObject(3, reservation.getWorkspace().getId());
            stmt.setDate(4, Date.valueOf(reservation.getDate()));
            stmt.setTime(5, Time.valueOf(reservation.getStartTime()));
            stmt.setTime(6, Time.valueOf(reservation.getEndTime()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding reservation to DB", e);
        }
        return reservation;
    }

    @Override
    public Reservation findById(UUID id) {
        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding reservation by ID", e);
        }
        throw new NoSuchElementException("Reservation with ID " + id + " not found");
    }

    @Override
    public Set<Reservation> findAll() {
        Set<Reservation> reservations = new HashSet<>();

        try (Connection conn = DBManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {

            while (rs.next()) {
                reservations.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all reservations", e);
        }

        return reservations;
    }

    @Override
    public Set<Reservation> findByUser(String login) {
        if (login == null || login.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Reservation> reservations = new HashSet<>();

        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_USER_SQL)) {

            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding reservations by user", e);
        }

        return reservations;
    }

    @Override
    public boolean remove(UUID id) {
        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setObject(1, id);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error removing reservation", e);
        }
    }

    private Reservation mapRow(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        String userLogin = rs.getString("user_login");
        UUID workspaceId = (UUID) rs.getObject("workspace_id");
        LocalDate date = rs.getDate("reservation_date").toLocalDate();
        LocalTime startTime = rs.getTime("start_time").toLocalTime();
        LocalTime endTime = rs.getTime("end_time").toLocalTime();

        User user = userRepository.find(userLogin)
                .orElseThrow(() -> new UserNotFoundException(userLogin));
        Workspace workspace = workspaceRepository.find(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));

        return new Reservation(user, workspace, date, startTime, endTime);
    }
}
