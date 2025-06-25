package config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;


public final class HibernateUtil {

    private static final EntityManagerFactory factory =
            Persistence.createEntityManagerFactory("coworking");

    private HibernateUtil() {}

    public static EntityManager getEntityManager() {
        return factory.createEntityManager();
    }

    public static void close() {
        if (factory.isOpen()) {
            factory.close();
        }
    }
}
