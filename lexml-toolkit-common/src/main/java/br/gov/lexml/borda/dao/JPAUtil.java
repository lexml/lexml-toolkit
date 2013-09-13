package br.gov.lexml.borda.dao;

import javax.persistence.EntityManager;

import br.gov.lexml.exceptions.ConfigFailedException;

/**
 * Gestão do EntityManger e transações em ambiente Java SE.
 * 
 * Utiliza o pattern thread local session.
 */
public class JPAUtil {
    
    private static final ThreadLocal<EntityManager> EM_HOLDER = new ThreadLocal<EntityManager>();
    
    public static EntityManager getEntityManager() throws ConfigFailedException {
        EntityManager em = EM_HOLDER.get();
        if(em == null) {
            em = EMFFactory.getEMF().createEntityManager();
            em.clear();
            EM_HOLDER.set(em);
        }
        return em;
    }
    
    public static void closeEntityManager() {
        EntityManager em = EM_HOLDER.get();
        if(em != null) {
            EM_HOLDER.set(null);
            if(em.isOpen()) {
                em.close();
            }
        }
    }
    
    public static void beginTransaction() throws ConfigFailedException {
        if(!getEntityManager().getTransaction().isActive()) {
            getEntityManager().getTransaction().begin();
        }
    }
    
    public static void commit() throws ConfigFailedException {
        if (getEntityManager().getTransaction().isActive()) {
            getEntityManager().flush();
            getEntityManager().getTransaction().commit();
        }
    }
    
    public static void rollback() throws ConfigFailedException {
        if (getEntityManager().getTransaction().isActive()) {
            getEntityManager().getTransaction().rollback();
        }
    }

    public static void clear() {
        EntityManager em = EM_HOLDER.get();
        if(em != null) {
            em.clear();
        }
    }
    
}
