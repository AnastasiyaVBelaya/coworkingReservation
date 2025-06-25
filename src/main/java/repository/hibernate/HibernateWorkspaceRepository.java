package repository.hibernate;

import config.HibernateUtil;
import exception.WorkspaceNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import repository.api.IWorkspaceRepository;
import repository.entity.Workspace;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class HibernateWorkspaceRepository implements IWorkspaceRepository {

    @Override
    public Workspace add(Workspace workspace) {
        if (workspace == null) {
            throw new IllegalArgumentException("Workspace cannot be null");
        }
        try (EntityManager em = HibernateUtil.getEntityManager()) {
            try {
                em.getTransaction().begin();
                em.persist(workspace);
                em.getTransaction().commit();
                return workspace;
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new RuntimeException("Error adding workspace", e);
            }
        }
    }

    @Override
    public Optional<Workspace> find(UUID id) {
        try (EntityManager em = HibernateUtil.getEntityManager()) {
            Workspace workspace = em.find(Workspace.class, id);
            return Optional.ofNullable(workspace);
        }
    }

    @Override
    public Set<Workspace> findAll() {
        try (EntityManager em = HibernateUtil.getEntityManager()) {
            TypedQuery<Workspace> query = em.createQuery("SELECT w FROM Workspace w", Workspace.class);
            List<Workspace> resultList = query.getResultList();
            return new HashSet<>(resultList);
        }
    }

    @Override
    public Set<Workspace> findAvailable() {
        try (EntityManager em = HibernateUtil.getEntityManager()) {
            TypedQuery<Workspace> query = em.createQuery("SELECT w FROM Workspace w WHERE w.available = true",
                    Workspace.class);
            List<Workspace> resultList = query.getResultList();
            return new HashSet<>(resultList);
        }
    }

    @Override
    public Workspace update(Workspace workspace) {
        if (workspace == null) {
            throw new IllegalArgumentException("Workspace cannot be null");
        }
        try (EntityManager em = HibernateUtil.getEntityManager()) {
            try {
                em.getTransaction().begin();
                Workspace merged = em.merge(workspace);
                em.getTransaction().commit();
                return merged;
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new WorkspaceNotFoundException(workspace.getId());
            }
        }
    }

    @Override
    public boolean remove(UUID id) {
        try (EntityManager em = HibernateUtil.getEntityManager()) {
            try {
                em.getTransaction().begin();
                Workspace workspace = em.find(Workspace.class, id);
                if (workspace == null) {
                    em.getTransaction().rollback();
                    return false;
                }
                em.remove(workspace);
                em.getTransaction().commit();
                return true;
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new RuntimeException("Error removing workspace", e);
            }
        }
    }
}
