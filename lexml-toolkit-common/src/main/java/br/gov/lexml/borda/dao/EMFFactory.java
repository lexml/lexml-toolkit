package br.gov.lexml.borda.dao;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Environment;
import org.hibernate.ejb.Ejb3Configuration;

import br.gov.lexml.LexMLSystem;
import br.gov.lexml.LexMLUtil;
import br.gov.lexml.borda.domain.ConjuntoItem;
import br.gov.lexml.borda.domain.RegistroItem;
import br.gov.lexml.borda.domain.RegistroItemErro;
import br.gov.lexml.borda.domain.TipoErro;
import br.gov.lexml.exceptions.ConfigFailedException;

/**
 * Inicializa a aplicação utilizando a variavel de ambiente LEXML_HOME o arquivo lexml-db.properties
 * 
 * @author Gabriel Franklin
 * 
 */
public class EMFFactory {

	private static Logger logger = Logger.getLogger(EMFFactory.class.getName());
	private static EntityManagerFactory emf = null;

	public static final EntityManagerFactory getEMF(final Properties properties) {
	    // Evita chamada sincronizada
		if (null != emf) {
            return emf;
        }

		logger.debug("Criando EntityManagerFactory para a Persistence Unit: " + LexMLSystem.JPA_PERSISTENCE_UNIT);
		
		synchronized(EMFFactory.class) {
		    // Evita contrução concorrente
		    if(emf != null) {
		        return emf;
		    }
		    
	        if (null == properties) {
	            logger.error("Properties passado é nulo");
	        }
	        else {
	            Ejb3Configuration cfg = new Ejb3Configuration();
	            
	            properties.put(Environment.TRANSACTION_STRATEGY, PersistenceUnitTransactionType.RESOURCE_LOCAL);

	            cfg.addProperties(properties)
	                .addAnnotatedClass(RegistroItem.class)
	                .addAnnotatedClass(RegistroItemErro.class)
	                .addAnnotatedClass(TipoErro.class)
	                .addAnnotatedClass(ConjuntoItem.class);
	            
	            emf = cfg.buildEntityManagerFactory();
	        }
		}

		if (null == emf) {
            logger.error("Não foi possivel criar EntityManagerFactory.");
        }

		return emf;
	}

	public static final EntityManagerFactory getEMF() throws ConfigFailedException {
		return getEMF(LexMLUtil.getJPAProperties());
	}

}