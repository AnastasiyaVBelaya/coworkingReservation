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
        Workspace existingWorkspace = Optional.ofNullable(idIndex.get(workspace.getId()))
                .orElseThrow(() -> new WorkspaceNotFoundException(workspace.getId()));

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
        return Optional.ofNullable(idIndex.remove(id))
                .map(workspace -> {
                    boolean removed = workspaces.remove(workspace);
                    if (!removed) {
                        idIndex.put(id, workspace);
                    }
                    return removed;
                })
                .orElse(false);
    }
}
