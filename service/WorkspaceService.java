package service;

import exception.WorkspaceNotFoundException;
import model.WorkspaceDTO;
import repository.api.IWorkspaceRepository;
import repository.entity.Workspace;
import service.api.IWorkspaceService;

import java.util.List;
import java.util.UUID;

public class WorkspaceService implements IWorkspaceService {
    private final IWorkspaceRepository workspaceRepository;

    public WorkspaceService(IWorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    public Workspace add(WorkspaceDTO workspaceDTO) {
        if (workspaceDTO == null) {
            throw new IllegalArgumentException("Workspace cannot be null!");
        }
        Workspace workspace = new Workspace(workspaceDTO.getType(), workspaceDTO.getPrice(), workspaceDTO.isAvailable());
        return workspaceRepository.add(workspace);
    }

    @Override
    public Workspace find(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Workspace ID cannot be null!");
        }
        return workspaceRepository.find(id).orElseThrow(() ->
                new WorkspaceNotFoundException("Workspace with ID " + id + " not found"));
    }

    @Override
    public List<Workspace> findAll() {
        return workspaceRepository.findAll();
    }

    @Override
    public List<Workspace> findAvailable() {
        return workspaceRepository.findAvailable();
    }

    @Override
    public Workspace update(UUID id, WorkspaceDTO workspaceDTO) {
        if (id == null || workspaceDTO == null) {
            throw new IllegalArgumentException("WorkspaceDTO or ID cannot be null!");
        }

        Workspace existingWorkspace = find(id);
        existingWorkspace.setType(workspaceDTO.getType());
        existingWorkspace.setPrice(workspaceDTO.getPrice());
        existingWorkspace.setAvailability(workspaceDTO.isAvailable());

        return workspaceRepository.update(existingWorkspace);
    }

    @Override
    public boolean remove(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Workspace ID cannot be null!");
        }
        return workspaceRepository.remove(id);
    }
}
