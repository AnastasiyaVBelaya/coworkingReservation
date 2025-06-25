import controller.MenuController;
import service.ServiceFactory;
import service.api.IUserService;
import service.api.IWorkspaceService;
import service.api.IReservationService;
import config.HibernateUtil;

import java.util.Scanner;

public class ApplicationStart {
    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down application, closing Hibernate EntityManagerFactory...");
            HibernateUtil.close();
        }));

        Scanner scanner = new Scanner(System.in);
        ServiceFactory serviceFactory = ServiceFactory.getInstance();
        IUserService userService = serviceFactory.getUserService();
        IWorkspaceService workspaceService = serviceFactory.getWorkspaceService();
        IReservationService reservationService = serviceFactory.getReservationService();
        MenuController menuController = new MenuController(scanner, userService, workspaceService, reservationService);
        menuController.run();
    }
}
