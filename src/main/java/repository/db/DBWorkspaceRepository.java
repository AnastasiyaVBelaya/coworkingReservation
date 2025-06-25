package repository.db;

import exception.WorkspaceNotFoundException;
import model.WorkspaceType;
import repository.api.IWorkspaceRepository;
import repository.entity.Workspace;

import java.sql.*;
import java.util.*;

public class DBWorkspaceRepository implements IWorkspaceRepository {

    private static final String INSERT_SQL =
            "INSERT INTO workspaces (id, type, price, available) VALUES (?, ?, ?, ?)";
    private static final String SELECT_BY_ID_SQL =
            "SELECT * FROM workspaces WHERE id = ?";
    private static final String SELECT_ALL_SQL =
            "SELECT * FROM workspaces";
    private static final String SELECT_AVAILABLE_SQL =
            "SELECT * FROM workspaces WHERE available = true";
    private static final String UPDATE_SQL =
            "UPDATE workspaces SET type = ?, price = ?, available = ? WHERE id = ?";
    private static final String DELETE_SQL =
            "DELETE FROM workspaces WHERE id = ?";

    @Override
    public Workspace add(Workspace workspace) {
        if (workspace == null) {
            throw new IllegalArgumentException("Workspace cannot be null");
        }

        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

            stmt.setObject(1, workspace.getId());
            stmt.setString(2, String.valueOf(workspace.getType()));
            stmt.setBigDecimal(3, workspace.getPrice());
            stmt.setBoolean(4, workspace.isAvailable());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error adding workspace to DB", e);
        }
        return workspace;
    }

    @Override
    public Optional<Workspace> find(UUID id) {
        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding workspace by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Set<Workspace> findAll() {
        return getWorkspaces(SELECT_ALL_SQL);
    }

    @Override
    public Set<Workspace> findAvailable() {
        return getWorkspaces(SELECT_AVAILABLE_SQL);
    }

    @Override
    public Workspace update(Workspace workspace) {
        if (workspace == null) {
            throw new IllegalArgumentException("Workspace cannot be null");
        }

        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, String.valueOf(workspace.getType()));
            stmt.setBigDecimal(2, workspace.getPrice());
            stmt.setBoolean(3, workspace.isAvailable());
            stmt.setObject(4, workspace.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new WorkspaceNotFoundException(workspace.getId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating workspace", e);
        }
        return workspace;
    }

    @Override
    public boolean remove(UUID id) {
        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setObject(1, id);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error removing workspace", e);
        }
    }

    private Workspace mapRow(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        String typeStr = rs.getString("type");
        WorkspaceType type = WorkspaceType.valueOf(typeStr);
        java.math.BigDecimal price = rs.getBigDecimal("price");
        boolean available = rs.getBoolean("available");

        return new Workspace(id, type, price, available);
    }

    private Set<Workspace> getWorkspaces(String sql) {
        Set<Workspace> workspaces = new HashSet<>();

        try (Connection conn = DBManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                workspaces.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching workspaces", e);
        }
        return workspaces;
    }
}
