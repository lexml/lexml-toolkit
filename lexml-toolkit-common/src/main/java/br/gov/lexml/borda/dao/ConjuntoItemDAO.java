package br.gov.lexml.borda.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.gov.lexml.borda.domain.ConjuntoItem;

public class ConjuntoItemDAO extends AbstractDAO {
//	private static final Logger logger = Logger.getLogger(TipoErroDAO.class.getName());

	public ConjuntoItem load(String p_id) {
		EntityManager em;
		em = getEntityManager();
		Query query = em.createQuery("SELECT r FROM ConjuntoItem r WHERE r.idConjuntoItem =:ite ");
		query.setParameter("ite", p_id);

		try {
			ConjuntoItem cj = (ConjuntoItem) query.getSingleResult();
			return cj;
		} catch (Exception e) {
			return null;
		}

	}

	public void save(ConjuntoItem p_obj) {
		saveObject(p_obj);
	}

	public List<ConjuntoItem> list() {
		EntityManager em = getEntityManager();
		Query query = em.createQuery("SELECT ci FROM ConjuntoItem ci");

		return query.getResultList();

	}

}
