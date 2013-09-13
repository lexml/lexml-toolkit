package br.gov.lexml.borda.install;

import java.io.File;

import br.gov.lexml.LexMLUtil;
import br.gov.lexml.borda.helper.ToolKitHelper;


public class PassoVerificarArquivoConf extends PassoInstalacao<ContextoInstalacao> {
	
	@Override
	public String executa() throws Exception {

		File file = LexMLUtil.getPathPerfilNodoBorda();
		if(!file.isFile()) {
			return null; // Próximo passo
		}
			
		System.out.print(
			"\n" +
			"Foi encontrado o arquivo de configuracao de perfil\n" +
			file.getCanonicalPath() + "\n" +
			"Deseja utiliza-lo? (s/n)\n" +
			"\n" +
			"[s]: "
		);
		String resp = ToolKitHelper.readUserInput();
		
		if(resp.equals("") || resp.equalsIgnoreCase("s")) {
			LexMLUtil.validaArquivoPerfil(file);
			return PassoDbVerificarPreconfiguracao.class.getName(); 
		}

		return null; // Próximo passo
	}

}
