package repository.file;

import exception.WorkspaceNotFoundException;
import repository.api.IWorkspaceRepository;
import repository.entity.Workspace;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileWorkspaceRepository extends AbstractFileRepository<Workspace> implements IWorkspaceRepository {

    public FileWorkspaceRepository() {
        super("data/workspaces.ser");
    }

    @Override
    public Workspace add(Workspace workspace) {
        if (workspace == null) {
            throw new IllegalArgumentException("Workspace cannot be null!");
        }
        items.add(workspace);
        writeToFile();
        return workspace;
    }

    @Override
    public Optional<Workspace> find(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Workspace ID cannot be null!");
        }
        return items
                .stream()
                .filter(workspace -> workspace.getId().equals(id))
                .findFirst();
    }

    @Override
    public Set<Workspace> findAll() {
        return Set.copyOf(items);
    }

    @Override
    public Set<Workspace> findAvailable() {
        return items.stream().filter(Workspace::isAvailable).collect(Collectors.toSet());
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
            writeToFile();
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
        if (workspace.isPresent()) {
            boolean removed = items.remove(workspace.get());
            if (removed) {
                writeToFile();
            }
            return removed;
        }
        return false;
    }
}
