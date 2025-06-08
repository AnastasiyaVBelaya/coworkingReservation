package repository.memory;

import exception.WorkspaceNotFoundException;
import repository.api.IWorkspaceRepository;
import repository.entity.Workspace;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryWorkspaceRepository implements IWorkspaceRepository {
    private final List<Workspace> workspaces = new ArrayList<>();
    private final Map<UUID, Workspace> idIndex = new HashMap<>();

    @Override
    public Workspace add(Workspace workspace) {
        if (workspace == null) {
            throw new IllegalArgumentException("Workspace cannot be null!");
        }
        workspaces.add(workspace);
        idIndex.put(workspace.getId(), workspace);
        return workspace;
    }

    @Override
    public Optional<Workspace> find(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Workspace ID cannot be null!");
        }
        return Optional.ofNullable(idIndex.get(id));
    }

    @Override
    public Set<Workspace> findAll() {
        return Set.copyOf(workspaces);
    }

    @Override
    public Set<Workspace> findAvailable() {
        return workspaces.stream()
                .filter(Workspace::isAvailable)
                .collect(Collectors.toSet());
    }

    @Override
    public Workspace update(Workspace workspace) {
        if (workspace == null) {
            throw new IllegalArgumentException("Workspace cannot be null!");
        }
        Workspace existingWorkspace = idIndex.get(workspace.getId());
        if (existingWorkspace == null) {
            throw new WorkspaceNotFoundException(workspace.getId());
        }
        // Обновляем поля
        existingWorkspace.setType(workspace.getType());
        existingWorkspace.setPrice(workspace.getPrice());
        existingWorkspace.setAvailability(workspace.isAvailable());
        return existingWorkspace;
    }

    @Override
    public boolean remove(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Workspace ID cannot be null!");
        }
        Workspace workspace = idIndex.remove(id);
        if (workspace == null) {
            return false;
        }
        boolean removed = workspaces.remove(workspace);
        if (!removed) {
            idIndex.put(id, workspace);
        }
        return removed;
    }
}
