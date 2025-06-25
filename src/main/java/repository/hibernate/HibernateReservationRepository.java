package repository.hibernate;

import config.HibernateUtil;
import exception.ReservationNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import repository.api.IReservationRepository;
import repository.entity.Reservation;

import java.util.*;
import java.util.stream.Collectors;

public class HibernateReservationRepository implements IReservationRepository {

    @Override
    public Reservation add(Reservation reservation) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(reservation);
            em.getTransaction().commit();
            return reservation;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error adding reservation", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Reservation findById(UUID id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Reservation reservation = em.find(Reservation.class, id);
            if (reservation == null) {
                throw new ReservationNotFoundException(id);
            }
            return reservation;
        } finally {
            em.close();
        }
    }

    @Override
    public Set<Reservation> findAll() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            TypedQuery<Reservation> query = em.createQuery("SELECT r FROM Reservation r", Reservation.class);
            List<Reservation> results = query.getResultList();
            return results.stream().collect(Collectors.toSet());
        } finally {
            em.close();
        }
    }

    @Override
    public Set<Reservation> findByUser(String login) {
        if (login == null || login.isEmpty()) {
            return Set.of();
        }
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            TypedQuery<Reservation> query = em.createQuery(
                    "SELECT r FROM Reservation r WHERE r.user.login = :login", Reservation.class);
            query.setParameter("login", login);
            List<Reservation> results = query.getResultList();
            return results.stream().collect(Collectors.toSet());
        } finally {
            em.close();
        }
    }

    @Override
    public boolean remove(UUID id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Reservation reservation = em.find(Reservation.class, id);
            if (reservation == null) {
                em.getTransaction().rollback();
                return false;
            }
            em.remove(reservation);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error removing reservation", e);
        } finally {
            em.close();
        }
    }
}
