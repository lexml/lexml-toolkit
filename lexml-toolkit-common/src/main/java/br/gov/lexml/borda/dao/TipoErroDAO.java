package br.gov.lexml.borda.dao;

import javax.persistence.Query;

import br.gov.lexml.borda.domain.TipoErro;

public class TipoErroDAO extends AbstractDAO {

	// private static final Logger logger =
	// Logger.getLogger(TipoErroDAO.class.getName());

	public TipoErro load(int p_cod) {
		Query query = getEntityManager().createQuery("select r from TipoErro r where r.idTipoErro =:ite ");
		query.setParameter("ite", p_cod);
		try {
			return (TipoErro) query.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

	public void save(TipoErro p_obj) {
		saveObject(p_obj);
	}

}
