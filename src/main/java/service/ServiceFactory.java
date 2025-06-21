package service;

import repository.RepositoryFactory;
import service.api.IReservationService;
import service.api.IUserService;
import service.api.IUserValidator;
import service.api.IWorkspaceService;

public class ServiceFactory {
    private static final ServiceFactory INSTANCE = new ServiceFactory();

    private final IUserService userService;
    private final IWorkspaceService workspaceService;
    private final IReservationService reservationService;

    private ServiceFactory() {
        RepositoryFactory repositoryFactory = RepositoryFactory.getInstance();
        IUserValidator userValidator = new UserValidator();
        this.userService = new UserService(repositoryFactory.getUserRepository(), userValidator);
        this.workspaceService = new WorkspaceService(repositoryFactory.getWorkspaceRepository());
        this.reservationService = new ReservationService(
                repositoryFactory.getReservationRepository(),
                userService,
                workspaceService
        );
    }

    public static ServiceFactory getInstance() {
        return INSTANCE;
    }

    public IUserService getUserService() {
        return userService;
    }

    public IWorkspaceService getWorkspaceService() {
        return workspaceService;
    }

    public IReservationService getReservationService() {
        return reservationService;
    }
}
