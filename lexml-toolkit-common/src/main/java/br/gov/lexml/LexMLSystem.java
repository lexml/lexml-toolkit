package br.gov.lexml;


/**
 * Informacoes para rodar os sistemas componentes da rede LexML
 * 
 * @author Gabriel Franklin
 *
 */
public interface LexMLSystem {
	/**
	 * Nome da persistence-unit para o JPA
	 */
	public static final String JPA_PERSISTENCE_UNIT = "borda-db";
	
	/**
	 * Nome do arquivo de properties para ser utilizado pelo Hibernate/JPA
	 */
	public static final String JPA_PROPERTIES_FILE = "lexml-db.properties";
	
	/**
	 * Nome do arquivo de perfil de publicador de um repositório LexML
	 */
	public static final String PERFIL_NODO_BORDA_XML = "lexml_nbconfig.xml";
	
	/**
	 * Nome do arquivo com a DDL gerada durante a instalacao
	 */
	public static final String ARQUIVO_DDL = "lexml_create_ddl.sql";
	
	/**
	 * URL do esquema xsd do metadado oai_lexml
	 */
	public static String OAI_LEXML_LOCATION_SCHEMA = "http://projeto.lexml.gov.br/esquemas/oai_lexml.xsd";
	public static String OAI_LEXML_NAMESPACE = "http://www.lexml.gov.br/oai_lexml";
	
	
	/**
	 * URL do esquema xsd do perfil de provedor de dados para a rede LexML.
	 */
	public static String PROFILE_LEXML_LOCATION_SCHEMA = "http://projeto.lexml.gov.br/esquemas/provedor_profile_lexml.xsd";

}
