
package br.gov.lexml.borda.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import br.gov.lexml.borda.domain.RegistroItemErro;
import br.gov.lexml.exceptions.ConfigFailedException;
import br.gov.lexml.util.hibernate.SimpleHqlBuilder;

public abstract class AbstractDAO {

    private static final Logger logger = Logger.getLogger(AbstractDAO.class.getName());

    private static boolean loaded_auto_ids = false;

    protected final EntityManager getEntityManager() {

        EntityManager em = null;

        try {
            em = JPAUtil.getEntityManager();
        }
        catch (ConfigFailedException e) {
            logger.error("Não foi possível a criação de um EntityManager!!!", e);
        }

        if (loaded_auto_ids || checkTablesReady(em)) {
            return em;
        }

        return null;
    }

    /**
     * Realiza as seguintes operações sobre o banco disponibilizado:
     * <ul>
     * <li>Verifica se as tabelas existem, se não ele providencia a criação</li>
     * <li>Le e armazena o próximo ID da tabela RegistroItemErro (abrimos mão de autoincrements)</li>
     * </ul>
     * 
     * @return true se o banco estiver pronto para o uso.
     */
    private static boolean checkTablesReady(final EntityManager em) {

        synchronized (AbstractDAO.class) {

            if (loaded_auto_ids) {
                return true;
            }

            /* Le o proximo ID valido para a tabela registro_item_erro */
            try {
                RegistroItemErro.setNextId(1);
                Query queryE = em
                        .createQuery("SELECT max(rie.idRegistroItemErro) FROM RegistroItemErro rie");
                queryE.setMaxResults(1);
                Number maxId = (Number) queryE.getSingleResult();

                if (null != maxId) {
                    RegistroItemErro.setNextId(maxId.intValue() + 1);
                }

            }
            catch (javax.persistence.NoResultException e) {
                logger.debug("Utilizando o valor 1 para o maximo valor da chave para a tabela regitro_item_erro.");
            }

            loaded_auto_ids = true;
        }

        return loaded_auto_ids;
    }

    /**
     * Cria uma transação para acumular as alterações em massa.
     */
    public final synchronized void beginTransaction() {
        try {
            JPAUtil.beginTransaction();
        }
        catch (ConfigFailedException e) {
            logger.error("Falha ao iniciar transação!!!", e);
        }
    }

    /**
     * Finaliza uma transação comitando as alterações
     */
    public final void commit() {
        try {
            JPAUtil.commit();
        }
        catch (ConfigFailedException e) {
            logger.error("Falha ao comprometer transação!!!", e);
        }
    }

    public final void rollback() {
        try {
            JPAUtil.rollback();
        }
        catch (ConfigFailedException e) {
            logger.error("Falha ao desfazer transação!!!", e);
        }
    }
    
    public final void clear() {
        JPAUtil.clear();
    }

    /**
     * Faz update do objeto passado
     * 
     * @param p_obj
     * @return
     * @throws ConfigFailedException
     */
    protected void updateObject(final Object p_obj) {
        getEntityManager().merge(p_obj);
        getEntityManager().flush();
    }

    /**
     * Salva o Objeto
     * 
     * @param p_obj
     * @return
     * @throws ConfigFailedException
     */
    protected void saveObject(final Object p_obj) {
        getEntityManager().persist(p_obj);
        getEntityManager().flush();
    }

    protected Query createQuery(final SimpleHqlBuilder builder) {
        
        if(logger.isDebugEnabled()) {
            logger.debug("------------------------------------");
            logger.debug("Query: " + builder.getHql());
            int i = 1;
            for(Object p: builder.getParams()) {
                logger.debug("Parametro " + i++ + ": " + p);
            }
            logger.debug("------------------------------------");
        }
        
        Query q = getEntityManager().createQuery(builder.getHql());
        int i = 1;
        for(Object p: builder.getParams()) {
            q.setParameter(i++, p);
        }
        return q;
    }
    
    public static Date fimDoDia(Date date) {
        Calendar c = new GregorianCalendar();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

}
