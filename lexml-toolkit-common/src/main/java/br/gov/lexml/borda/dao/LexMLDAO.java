package br.gov.lexml.borda.dao;

/**
 * Armazena os valores usadors para os campos cd_status e cd_validacao da tabela registro_item
 * 
 * @author Gabriel Franklin
 * 
 */
public interface LexMLDAO {

	public interface CdStatus {

		/**
		 * Valor DEFAULT para a coluna REGISTRO_ITEM.CD_STATUS, valor default para todo registro novo no sistema
		 */
		public static final String NOVO = "N";

		/**
		 * Valor para a coluna REGISTRO_ITEM.CD_STATUS, será usado para propagar a informação de remoção de registros através do OAI-PMH
		 */
		
		public static final String DELETADO = "D";
		
		public static final String DEFAULT_CD_STATUS = NOVO;
		

	}

	public interface CdValidacao {
		
		

		/**
		 * INDEFINIDO significa que o registro não validado ainda pelo Validador
		 */
		public static final String INDEFINIDO = "I";

		/**
		 * ERRO significa que o registro apresenta algum erro que impede que seja publicado
		 */
		public static final String ERRO = "E";

		/**
		 * OK significa que o registro passou pelo processo de validação
		 */
		public static final String OK = "O";

		/**
		 * FALHA é um status reservado ao nodo central e significa que o registro nãso pode ser fracionado nas tabelas do banco relacional.
		 */
		public static final String FALHA = "F";
		
		public static final String DEFAULT_VALIDACAO_STATUS = INDEFINIDO;
	}

}
