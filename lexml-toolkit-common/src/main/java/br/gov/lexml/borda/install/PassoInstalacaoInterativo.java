package br.gov.lexml.borda.install;

import br.gov.lexml.borda.helper.ToolKitHelper;

public abstract class PassoInstalacaoInterativo<K> extends PassoInstalacao<K> {
	
	@Override
	public final String executa() throws Exception {

		System.out.println();
		
		String resposta = null;
		do {
			imprimePergunta();
			resposta = ToolKitHelper.readUserInput();
		} while(!validaResposta(resposta));
		
		return executaPasso(resposta);
	}

	public abstract void imprimePergunta() throws Exception;
	
	public boolean validaResposta(String resposta) throws Exception {
		return true;
	}
	
	/**
	 * Executa o passo e retorna a o nome do próximo passo de instalação
	 * ou null para executar o próximo passo na sequência
	 */
	public String executaPasso(String resposta) throws Exception {
		return null;
	}

	protected boolean validaObrigatorio(String resposta) {
		if(resposta.equals("")) {
			System.out.println("\nO valor informado nao pode ser vazio.\n");
			return false;
		}
		return true;
	}
}
