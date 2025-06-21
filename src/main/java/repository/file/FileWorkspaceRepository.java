package repository.file;

import exception.WorkspaceNotFoundException;
import repository.api.IWorkspaceRepository;
import repository.entity.Workspace;

import java.util.*;
import java.util.stream.Collectors;

public class FileWorkspaceRepository extends AbstractFileRepository<Workspace> implements IWorkspaceRepository {

    private final Map<UUID, Workspace> idIndex;

    public FileWorkspaceRepository() {
        super("data/workspaces.ser");
        this.idIndex = items.stream()
                .collect(Collectors.toMap(Workspace::getId, ws -> ws));
    }

    @Override
    public Workspace add(Workspace workspace) {
        if (workspace == null) {
            throw new IllegalArgumentException("Workspace cannot be null!");
        }
        items.add(workspace);
        idIndex.put(workspace.getId(), workspace);
        writeToFile();
        return workspace;
    }

    @Override
    public Optional<Workspace> find(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Workspace ID cannot be null!");
        }
        return java.util.Optional.ofNullable(idIndex.get(id));
    }

    @Override
    public Set<Workspace> findAll() {
        return Set.copyOf(items);
    }

    @Override
    public Set<Workspace> findAvailable() {
        return items.stream()
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
        writeToFile();
        return existingWorkspace;
    }

    @Override
    public boolean remove(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Workspace ID cannot be null!");
        }
        return Optional.ofNullable(idIndex.remove(id))
                .map(workspace -> {
                    boolean removed = items.remove(workspace);
                    if (removed) {
                        writeToFile();
                    } else {
                        idIndex.put(id, workspace);
                    }
                    return removed;
                })
                .orElse(false);
    }
}
