package br.gov.lexml.borda.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import br.gov.lexml.LexMLSystem;
import br.gov.lexml.LexMLUtil;
import br.gov.lexml.borda.domain.ConjuntoItem;
import br.gov.lexml.borda.domain.RegistroItem;
import br.gov.lexml.borda.domain.RegistroItemErro;
import br.gov.lexml.borda.domain.TipoErro;
import br.gov.lexml.exceptions.ConfigFailedException;

public class ToolKitDAO {
	
	private static final Logger logger = Logger.getLogger(ToolKitDAO.class.getName());

	public void instalaBancoDeDados() throws NamingException, ConfigFailedException {
		instalaBancoDeDados(null);
	}
	
	public void instalaBancoDeDados(Properties props) throws NamingException, ConfigFailedException {

		if(props == null) {
			props = LexMLUtil.getJPAProperties();
		}
		
		if (null == props) {
			logger.error("Foi recebido um null no lugar das propriedades para o JPA.");
			throw new ConfigFailedException("Propriedades recebidas são podem ser nulas");
		}
		
		AnnotationConfiguration cfg = createAnnotationConfiguration(props);

		SchemaExport se = new SchemaExport(cfg);
		se.setOutputFile(LexMLSystem.ARQUIVO_DDL);
		se.setDelimiter(";");
		se.create(true, true);
		
		List<?> erros = se.getExceptions();
		if (!erros.isEmpty()) {
			logger.debug("Total de exceções durante a execução da criação do banco de dados" + erros.size());
			for (int i = 0; i < erros.size(); i++) {
				Exception e = (Exception) erros.get(i);
				logger.error("[" + i + "] =" + e);
			}
		}
		
	}
	
	public boolean validaBancoDeDados(final Properties props) {

	    boolean ret = true;
	    
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        
        try {
            conn = openConnection(props);
            st = conn.prepareStatement("select count(*) from conjunto_item");
            rs = st.executeQuery();
        }
        catch (Exception e) {
            ret = false;
        }
        finally {
            try { if(st != null) { st.close(); } }     catch(Exception e) { /* nada a fazer */ }
            try { if(rs != null) { rs.close(); } }     catch(Exception e) { /* nada a fazer */ }
            try { if(conn != null) { conn.close(); } } catch(Exception e) { /* nada a fazer */ }
        }
        
        return ret;
	}
	
	protected AnnotationConfiguration createAnnotationConfiguration(final Properties props) {
		
		// Cria-se uma configuração programaticamente,
		// de forma a inicializar o banco de dados
		AnnotationConfiguration cfg = new AnnotationConfiguration();

		cfg.setProperties(props);
		cfg.addAnnotatedClass(RegistroItem.class);
		cfg.addAnnotatedClass(RegistroItemErro.class);
		cfg.addAnnotatedClass(TipoErro.class);
		cfg.addAnnotatedClass(ConjuntoItem.class);
		
		return cfg;
	}

	public void testaConexaoBancoDeDados(final Properties p) throws Exception {
		Connection conn = openConnection(p);
		conn.commit();
		conn.close();
	}
	
	public Connection openConnection(final String driver, final String jdbcUrl,
	                                 final String usuario, final String senha) throws Exception {
	    Class.forName(driver);
	    Connection conn = DriverManager.getConnection(jdbcUrl, usuario, senha);
	    conn.setAutoCommit(false);
	    return conn;
	}
	
	public Connection openConnection(final Properties p) throws Exception {
	    return openConnection(
            p.getProperty("hibernate.connection.driver_class"), 
            p.getProperty("hibernate.connection.url"), 
            p.getProperty("hibernate.connection.username"), 
            p.getProperty("hibernate.connection.password")
        );
	}
	
}
