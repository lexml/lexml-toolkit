package br.gov.lexml.borda.install;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Properties;

import br.gov.lexml.LexMLUtil;
import br.gov.lexml.borda.dao.ToolKitDAO;
import br.gov.lexml.borda.helper.ToolKitHelper;
import br.gov.lexml.borda.install.ConfigDb.DbInfo;
import br.gov.lexml.exceptions.InstalacaoException;
import de.schlichtherle.io.FileInputStream;


/**
 * Testa a conexão com o banco
 *
 * @author frago
 */
public class PassoDbInstalarSchema extends PassoInstalacaoInterativo<ContextoInstalacao> {
	
	private static ConfigDb configDb = ConfigDb.getInstance();

	@Override
	public void imprimePergunta() throws Exception {

		if(contexto.isDbPreconfigurado()) {
			System.out.println(
				"Lembre-se de copiar o .jar do driver do seu banco de dados\n" +
				"para a pasta /lib.\n\n" +
				"O dados abaixo para conexao com o banco de dados estao corretos? (s/n)\n");
			
			Properties p = loadJpaProperties();
			for(Object key: p.keySet()) {
				System.out.println(key + " = " + p.get(key));
			}
		}
		else {
			geraPropertyFile();
			
			System.out.println("O dados abaixo para conexao com o banco de dados estao corretos? (s/n)\n");
			
			System.out.println("Tipo:       " + configDb.getDbInfo(contexto.getDbSgbd()).getSgbd());
			System.out.println("Servidor:   " + contexto.getDbServidor());
			System.out.println("Porta:      " + contexto.getDbPorta());
			System.out.println("Nome do BD: " + contexto.getDbNome());
			System.out.println("Usuario:    " + contexto.getDbUsuario());
			System.out.println("Senha:      " + contexto.getDbSenha());
		}
		
		System.out.print("\n[s]: ");
	}
	
	@Override
	public String executaPasso(final String resposta) throws Exception {
		
		if(resposta.equals("") || resposta.equalsIgnoreCase("s")) {
			
			Properties p = loadJpaProperties();
			
			ToolKitDAO dao = new ToolKitDAO();
			
			try {
				dao.testaConexaoBancoDeDados(p);
			}
			catch(Exception e) {
				throw new InstalacaoException("Nao foi possivel conectar com o banco de dados.", e);
			}
				
			try {
				boolean existeSchema = dao.validaBancoDeDados(p);
				
				if(!existeSchema || confirmaSobreposicao()) {
					System.out.println(
						"\n" +
						"Instalando schema do banco de dados.\n" +
						"\n" +
						"Ignore erros relativos a nao existencia de objetos no banco de dados.\n");
					dao.instalaBancoDeDados(p);
					contexto.setBdOk(true);
				}
				
				if(existeSchema) {
					contexto.setBdOk(true);
				}
				
			} catch (Exception e) {
				throw new InstalacaoException("Falha ao instalar o schema " +
					"do banco de dados.", e);
			}
			
			return null;
		}
		else if(contexto.isDbPreconfigurado()) {
			return PassoDbVerificarPreconfiguracao.class.getName();
		}
		else {
			return PassoDbSgbd.class.getName();
		}
	}

	private Properties loadJpaProperties() throws FileNotFoundException, IOException {
		Properties p = new Properties();
		File jpaProperties = LexMLUtil.getPathJpaProperties();
		p.load(new FileInputStream(jpaProperties));
		return p;
	}

	private boolean confirmaSobreposicao() throws Exception {
		
		System.out.print(
			"\n" +
			"As tabelas do lexml ja existem no banco de dados.\n" +
			"Voce deseja apaga-las? (s/n)\n" +
			"\n" +
			"[n]: ");
		
		String resposta = ToolKitHelper.readUserInput();
		
		return resposta.equalsIgnoreCase("s");
	}

	private File geraPropertyFile() throws Exception {
		
		File file = LexMLUtil.getPathJpaProperties();
		
		System.out.println("Gerando arquivo " + file.getCanonicalPath() + " ...\n");
		
		PrintWriter writer = new PrintWriter(file);
		writeProperties(writer, contexto);
		writer.close();
		
		return file;
	}
	
	private static void writeProperties(final Writer writer, final ContextoInstalacao contexto) throws Exception {
		DbInfo info = ConfigDb.getInstance().getDbInfo(contexto.getDbSgbd());
		String jdbcUrl = info.getUrl(contexto.getDbServidor(), contexto.getDbPorta(), contexto.getDbNome());
		writer.write("hibernate.dialect=" + info.getDialect() + "\n");
		writer.write("hibernate.connection.driver_class=" + info.getDriver() + "\n");
		writer.write("hibernate.connection.url=" + jdbcUrl + "\n");
		writer.write("hibernate.connection.username=" + contexto.getDbUsuario() + "\n");
		writer.write("hibernate.connection.password=" + contexto.getDbSenha() + "\n");
	}
}
