package repository.api;

import repository.entity.Workspace;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IWorkspaceRepository {
    Workspace add(Workspace workspace);

    Optional<Workspace> find(UUID id);

    List<Workspace> findAll();

    List<Workspace> findAvailable();

    Workspace update(Workspace workspace);

    boolean remove(UUID id);
}
