package repository.db;

import model.Role;
import repository.api.IUserRepository;
import repository.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DBUserRepository implements IUserRepository {

    private static final String INSERT_SQL =
            "INSERT INTO users (login, role) VALUES (?, ?) ON CONFLICT (login) DO NOTHING";
    private static final String SELECT_SQL =
            "SELECT login, role FROM users WHERE LOWER(login) = LOWER(?)";

    @Override
    public User add(User user) {
        if (user == null || user.getLogin() == null) {
            throw new IllegalArgumentException("User or login cannot be null");
        }

        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getRole().name());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error adding user to DB", e);
        }
        return user;
    }

    @Override
    public Optional<User> find(String login) {
        if (login == null || login.isEmpty()) return Optional.empty();

        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_SQL)) {

            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(Role.valueOf(rs.getString("role")), rs.getString("login"));
                return Optional.of(user);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user in DB", e);
        }
        return Optional.empty();
    }
}
