package br.gov.lexml.borda.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.TargetTypeHelper;

import br.gov.lexml.LexMLSystem;
import br.gov.lexml.LexMLUtil;
import br.gov.lexml.borda.domain.ConjuntoItem;
import br.gov.lexml.borda.domain.RegistroItem;
import br.gov.lexml.borda.domain.RegistroItemErro;
import br.gov.lexml.borda.domain.TipoErro;
import br.gov.lexml.exceptions.ConfigFailedException;

public class ToolKitDAO {
	
	private static final Logger logger = Logger.getLogger(ToolKitDAO.class.getName());

	public void instalaBancoDeDados(Properties props) throws NamingException, ConfigFailedException {

		if(props == null) {
			props = LexMLUtil.getJPAProperties();
		}
		
		if (null == props) {
			logger.error("Foi recebido um null no lugar das propriedades para o JPA.");
			throw new ConfigFailedException("Propriedades recebidas são podem ser nulas");
		}
		
		try {
	        final BootstrapServiceRegistry bsr = new BootstrapServiceRegistryBuilder().build();
	        final StandardServiceRegistryBuilder ssrBuilder = new StandardServiceRegistryBuilder( bsr );

	        ssrBuilder.applySettings(props);

			StandardServiceRegistry serviceRegistry = ssrBuilder.build();

	        final MetadataImplementor metadata = buildMetadata(serviceRegistry);

	        new SchemaExport()
	                .setHaltOnError(false)
	                .setOutputFile(LexMLSystem.ARQUIVO_DDL)
	                .setDelimiter(";")
	                .setFormat(true)
	                .execute(TargetTypeHelper.parseLegacyCommandLineOptions(true, true, LexMLSystem.ARQUIVO_DDL),
	                        SchemaExport.Action.BOTH, metadata, serviceRegistry);
	        
		}
		catch(Exception e) {
			logger.error("Falha durante a execução da criação do banco de dados", e);
		}
		
	}
	
    private static MetadataImplementor buildMetadata(StandardServiceRegistry serviceRegistry) {

        final MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        final MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();

        metadataSources.addAnnotatedClass(RegistroItem.class);
        metadataSources.addAnnotatedClass(RegistroItemErro.class);
        metadataSources.addAnnotatedClass(TipoErro.class);
        metadataSources.addAnnotatedClass(ConjuntoItem.class);

        return (MetadataImplementor) metadataBuilder.build();
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
