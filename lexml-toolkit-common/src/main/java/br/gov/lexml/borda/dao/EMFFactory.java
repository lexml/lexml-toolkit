package br.gov.lexml.borda.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;

import br.gov.lexml.LexMLSystem;
import br.gov.lexml.LexMLUtil;
import br.gov.lexml.exceptions.ConfigFailedException;

/**
 * Inicializa a aplicação utilizando a variavel de ambiente LEXML_HOME o arquivo lexml-db.properties
 * 
 * @author Gabriel Franklin
 * 
 */
public class EMFFactory {
	
	public static final String PERSISTENCE_UNIT = "lexml-toolkit";
	
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
	        	
	        	Map<String, String> map = new HashMap<String, String>();
	        	for (final String name: properties.stringPropertyNames()) {
	        	    map.put(name, properties.getProperty(name));	       
	        	}
	        	
	            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, map);
	            
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