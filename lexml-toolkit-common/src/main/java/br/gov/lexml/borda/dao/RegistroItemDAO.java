
package br.gov.lexml.borda.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import br.gov.lexml.LexMLUtil;
import br.gov.lexml.borda.dao.LexMLDAO.CdValidacao;
import br.gov.lexml.borda.domain.ConjuntoItem;
import br.gov.lexml.borda.domain.RegistroItem;
import br.gov.lexml.borda.domain.RegistroItemErro;
import br.gov.lexml.util.hibernate.SimpleHqlBuilder;

public class RegistroItemDAO extends AbstractDAO {

    private static final Logger logger = Logger.getLogger(RegistroItemDAO.class.getName());

    /**
     * Recupera uma lista de RegistroItem baseada em uma janela de tempo entre from e until e o
     * conjunto
     * 
     * @param from
     * @param until
     * @return
     */
    public List<RegistroItem> listByTimeWindowAndSet(final Date from, final Date until, final String idConjunto,
                                                     final String lastId, final int max) {

        SimpleHqlBuilder builder = new SimpleHqlBuilder("select ri from RegistroItem ri");

        whereListByTimeWindowAndSet(builder, from, until, idConjunto, lastId);

        builder.orderBy("ri.idRegistroItem");

        Query query = createQuery(builder);

        if (max > 0) {
            query.setMaxResults(max);
        }

        return query.getResultList();
    }

    /**
     * Retorna o tamanho de uma lista de RegistroItem baseada em uma janela de tempo entre from e
     * until e o conjunto
     */
    public int countListByTimeWindowAndSet(final Date from, final Date until, final String idConjunto) {

        SimpleHqlBuilder builder = new SimpleHqlBuilder("select count(ri) from RegistroItem ri");
        whereListByTimeWindowAndSet(builder, from, until, idConjunto, null);

        Query query = createQuery(builder);

        Number row = (Number) query.getSingleResult();

        if (null != row) {
            return row.intValue();
        }

        return 0;
    }

    private void whereListByTimeWindowAndSet(final SimpleHqlBuilder builder, final Date from, final Date until,
                                             final String idConjunto, final String lastId) {
        builder.and("ri.cdValidacao = ?", CdValidacao.OK);
        if (from != null) {
            builder.and("ri.tsRegistroGmt >= ?", new Timestamp(from.getTime()));
        }
        if (until != null) {
            builder.and("ri.tsRegistroGmt <= ?", new Timestamp(AbstractDAO.fimDoDia(until).getTime()));
        }
        if (idConjunto != null) {
            builder.and("ri.conjuntoItem.idConjuntoItem = ?", idConjunto);
        }
        if (lastId != null) {
            builder.and("ri.idRegistroItem > ?", lastId);
        }
    }

    /**
     * Lista os RegistroItem que ainda não passaram com sucesso pelo processo de validação
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<RegistroItem> listNotValid(final String ultimoIdProcessado, final int tamMaxBloco) {
        EntityManager em = getEntityManager();

        SimpleHqlBuilder builder = new SimpleHqlBuilder("select ri from RegistroItem ri");
        builder.and("ri.cdValidacao in (?,?)", CdValidacao.INDEFINIDO, CdValidacao.ERRO);
        if (ultimoIdProcessado != null) {
            builder.and("ri.id > ?", ultimoIdProcessado);
        }
        builder.orderBy("ri.id");

        Query q = createQuery(builder);
        q.setMaxResults(tamMaxBloco);

        return q.getResultList();
    }

    @SuppressWarnings("boxing")
    public long countNotValid() {
        EntityManager em = getEntityManager();
        Query q = em.createQuery("SELECT count(ri) FROM RegistroItem ri WHERE ri.cdValidacao in (?1,?2)");
        q.setParameter(1, CdValidacao.INDEFINIDO);
        q.setParameter(2, CdValidacao.ERRO);
        return (Long) q.getSingleResult();
    }

    public List<ConjuntoItem> listAllSets() {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("SELECT ci FROM ConjuntoItem ci");
        return query.getResultList();
    }

    /**
     * recupera do banco o RegistroItem a partir do identifier informado
     * 
     * @param identifier
     * @return
     */
    public RegistroItem load(final String identifier) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("SELECT ri FROM RegistroItem ri WHERE ri.idRegistroItem =:rid ");
        query.setParameter("rid", identifier);
        try {
            return (RegistroItem) query.getSingleResult();
        }
        catch (javax.persistence.NoResultException e) {
            return null;
        }
    }

    public void saveList(final List< ? > p_list) {
        Iterator< ? > iter = p_list.iterator();
        while (iter.hasNext()) {
            save(iter.next());
        }
    }

    public void save(final Object p_obj) {
        timeStamp(p_obj);
        saveObject(p_obj);
    }

    /**
     * Realiza o insert ou update do RegistroItem
     * 
     * @param p_ri
     * @return
     */
    public void saveOrUpdate(final RegistroItem p_ri) {
        if (existeRegistroItem(p_ri.getIdRegistroItem())) {
            update(p_ri);
        }
        else {
            save(p_ri);
        }
    }

    /**
     * Atualiza os campos de datestamp das entidades do sistema p_obj deve ser RegistroItem ou
     * RegistroItemErro
     * 
     * @param p_obj
     */
    public void timeStamp(final Object p_obj) {

        Timestamp now = new Timestamp(LexMLUtil.nowInMillisGMT());

        if (RegistroItem.class.isInstance(p_obj)) {
            ((RegistroItem) p_obj).setTsRegistroGmt(now);
        }
        else if (RegistroItemErro.class.isInstance(p_obj)) {
            ((RegistroItemErro) p_obj).setTsProcessamento(now);
        }
    }

    public void updateList(final List< ? > p_list) {
        Iterator< ? > iter = p_list.iterator();
        while (iter.hasNext()) {
            update(iter.next());
        }
    }

    public void update(final Object p_obj) {
        timeStamp(p_obj);
        updateObject(p_obj);
    }

    @SuppressWarnings("unchecked")
    public List<RegistroItem> listAll(final int offset, final int limit) {
        EntityManager em = getEntityManager();

        Query query = em.createQuery("SELECT ri FROM RegistroItem ri ");
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    public int countAll() {
        EntityManager em = getEntityManager();

        Query query = em.createQuery("SELECT ri FROM RegistroItem ri ");

        return query.getResultList().size();
    }

    public void deleteRegistroItemErro(final RegistroItem registroItem) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("delete from RegistroItemErro where registroItem.id = ?1");
        query.setParameter(1, registroItem.getIdRegistroItem());
        query.executeUpdate();
    }

    public List<RegistroItem> listErrorsByTimeWindowAndSet(final Date from, final Date until,
                                                           final String idConjunto, final String lastId,
                                                           final int max) {

        SimpleHqlBuilder builder = new SimpleHqlBuilder("select ri from RegistroItem ri");

        whereListErrorsByTimeWindowAndSet(builder, from, until, idConjunto, lastId);

        builder.orderBy("ri.idRegistroItem");

        Query query = createQuery(builder);

        if (max > 0) {
            query.setMaxResults(max);
        }

        return query.getResultList();
    }

    public int countListErrorsByTimeWindowAndSet(final Date from, final Date until, final String idConjunto) {

        SimpleHqlBuilder builder = new SimpleHqlBuilder("select count(ri) from RegistroItem ri");
        whereListErrorsByTimeWindowAndSet(builder, from, until, idConjunto, null);

        Query query = createQuery(builder);

        Number row = (Number) query.getSingleResult();

        if (null != row) {
            return row.intValue();
        }

        return 0;
    }

    private void whereListErrorsByTimeWindowAndSet(final SimpleHqlBuilder builder, final Date from,
                                                   final Date until, final String idConjunto, final String lastId) {
        builder.and("ri.cdValidacao <> ?", CdValidacao.OK);
        if (from != null) {
            builder.and("ri.tsRegistroGmt >= ?", new Timestamp(from.getTime()));
        }
        if (until != null) {
            builder.and("ri.tsRegistroGmt <= ?", new Timestamp(AbstractDAO.fimDoDia(until).getTime()));
        }
        if (idConjunto != null) {
            builder.and("ri.conjuntoItem.idConjuntoItem = ?", idConjunto);
        }
        if (lastId != null) {
            builder.and("ri.idRegistroItem > ?", lastId);
        }
    }

    public Date getTsRegistroGmt(final String id) {
        EntityManager em = getEntityManager();
        Query q = em.createQuery("SELECT ri.tsRegistroGmt FROM RegistroItem ri WHERE ri.idRegistroItem = :id");
        q.setParameter("id", id);

        List< ? > list = q.getResultList();

        if (list.isEmpty()) {
            return null;
        }
        return (Date) list.get(0);
    }

    public boolean existeRegistroItem(final String id) {
        EntityManager em = getEntityManager();
        Query q = em.createQuery("SELECT count(ri) FROM RegistroItem ri WHERE ri.idRegistroItem = :id");
        q.setParameter("id", id);
        return ((Long) q.getSingleResult()).longValue() > 0;
    }

    /**
     * Atualiza apenas cd validação para agilizar o processo de validação IMPORTANTE!!! Bypassa o
     * JPA. Deve ser utilizado com cautela
     * 
     * @param list Lista de RegistroItem com cdValidacao a ser atualizado no BD
     */
    public void updateCdValidacao(final List<RegistroItem> list) {
        EntityManager em = getEntityManager();
        for (RegistroItem ri : list) {
            Query q = em.createNativeQuery("update registro_item set cd_validacao = ?1 where id_registro_item = ?2");
            q.setParameter(1, ri.getCdValidacao());
            q.setParameter(2, ri.getIdRegistroItem());
        }
    }

}
