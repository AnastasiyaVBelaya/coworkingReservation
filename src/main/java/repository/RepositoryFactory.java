package repository;

import repository.api.IReservationRepository;
import repository.api.IUserRepository;
import repository.api.IWorkspaceRepository;
import repository.db.DBReservationRepository;
import repository.db.DBUserRepository;
import repository.db.DBWorkspaceRepository;

public class RepositoryFactory {
    private static final RepositoryFactory INSTANCE = new RepositoryFactory();

    private final IUserRepository userRepository;
    private final IReservationRepository reservationRepository;
    private final IWorkspaceRepository workspaceRepository;

    private RepositoryFactory() {
        this.userRepository = new DBUserRepository();
        this.reservationRepository = new DBReservationRepository();
        this.workspaceRepository = new DBWorkspaceRepository();
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
