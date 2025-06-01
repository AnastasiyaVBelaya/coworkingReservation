package repository;

import repository.api.IReservationRepository;
import repository.api.IUserRepository;
import repository.api.IWorkspaceRepository;
import repository.file.FileReservationRepository;
import repository.file.FileUserRepository;
import repository.file.FileWorkspaceRepository;


public class RepositoryFactory {
    private static final RepositoryFactory INSTANCE = new RepositoryFactory();

    private final IUserRepository userRepository;
    private final IReservationRepository reservationRepository;
    private final IWorkspaceRepository workspaceRepository;

    private RepositoryFactory() {
        this.userRepository = new FileUserRepository();
        this.reservationRepository = new FileReservationRepository();
        this.workspaceRepository = new FileWorkspaceRepository();
    }

    public static RepositoryFactory getInstance() {
        return INSTANCE;
    }

    public IUserRepository getUserRepository() {
        return userRepository;
    }

    public IReservationRepository getReservationRepository() {
        return reservationRepository;
    }

    public IWorkspaceRepository getWorkspaceRepository() {
        return workspaceRepository;
    }
}
