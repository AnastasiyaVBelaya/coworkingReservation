package repository.memory;

import exception.WorkspaceNotFoundException;
import repository.entity.Workspace;
import repository.api.IWorkspaceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryWorkspaceRepository implements IWorkspaceRepository {
    private final List<Workspace> workspaces = new ArrayList<>();

    public Workspace add(Workspace workspace) {
        if (workspace == null) {
            throw new IllegalArgumentException("Workspace cannot be null!");
        }
        workspaces.add(workspace);
        return workspace;
    }

    @Override
    public Optional<Workspace> find(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Workspace ID cannot be null!");
        }
        return workspaces
                .stream()
                .filter(workspace -> workspace.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Workspace> findAll() {
        return List.copyOf(workspaces);
    }

    @Override
    public List<Workspace> findAvailable() {
        return workspaces.stream().filter(Workspace::isAvailable).toList();
    }

    @Override
    public Workspace update(Workspace workspace) {
        if (workspace == null) {
            throw new IllegalArgumentException("Workspace cannot be null!");
        }
        Optional<Workspace> existingWorkspace = find(workspace.getId());

        if (existingWorkspace.isPresent()) {
            Workspace updatedWorkspace = existingWorkspace.get();
            updatedWorkspace.setType(workspace.getType());
            updatedWorkspace.setPrice(workspace.getPrice());
            updatedWorkspace.setAvailability(workspace.isAvailable());
            return updatedWorkspace;
        }
        throw new WorkspaceNotFoundException(workspace.getId());
    }

    @Override
    public boolean remove(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Workspace ID cannot be null!");
        }
        Optional<Workspace> workspace = find(id);
        return workspace.map(workspaces::remove).orElse(false);
    }
}
