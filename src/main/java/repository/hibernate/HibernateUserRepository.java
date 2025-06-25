package repository.hibernate;

import config.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import repository.api.IUserRepository;
import repository.entity.User;

import java.util.Optional;

public class HibernateUserRepository implements IUserRepository {

    @Override
    public User add(User user) {
        if (user == null || user.getLogin() == null) {
            throw new IllegalArgumentException("User or login cannot be null");
        }
        try (EntityManager em = HibernateUtil.getEntityManager()) {
            try {
                em.getTransaction().begin();
                em.persist(user);
                em.getTransaction().commit();
                return user;
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new RuntimeException("Error adding user", e);
            }
        }
    }

    @Override
    public Optional<User> find(String login) {
        if (login == null || login.isEmpty()) {
            return Optional.empty();
        }
        try (EntityManager em = HibernateUtil.getEntityManager()) {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE LOWER(u.login) = LOWER(:login)", User.class);
            query.setParameter("login", login);
            User user = query.getResultStream().findFirst().orElse(null);
            return Optional.ofNullable(user);
        }
    }
}
