package br.gov.lexml.borda.install;

import java.io.File;

import br.gov.lexml.LexMLUtil;
import br.gov.lexml.borda.helper.ToolKitHelper;


/**
 * Testa a conexão com o banco
 *
 * @author frago
 */
public class PassoDbVerificarPreconfiguracao extends PassoInstalacao<ContextoInstalacao> {
	
	private static ConfigDb configDb = ConfigDb.getInstance();
	
	@Override
	public String executa() throws Exception {
		
		File file = LexMLUtil.getPathJpaProperties();
		
		if(file.isFile()) {
			System.out.print(
				"\n" +
				"Foi encontrado o arquivo de configuracao com o banco de dados\n" +
				file.getCanonicalPath() + "\n" +
				"Deseja utiliza-lo? (s/n)\n" +
				"\n" +
				"[s]: "
			);
			String resp = ToolKitHelper.readUserInput();
			
			if(resp.equals("") || resp.equalsIgnoreCase("s")) {
				contexto.setDbPreconfigurado(true);
				return PassoDbInstalarSchema.class.getName(); 
			}
			else {
				contexto.setDbPreconfigurado(false);
			}
		}
		
		return null;
	}

}
