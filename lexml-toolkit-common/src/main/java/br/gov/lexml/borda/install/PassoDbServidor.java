package br.gov.lexml.borda.install;


public class PassoDbServidor extends PassoInstalacaoInterativo<ContextoInstalacao> {

	@Override
	public void imprimePergunta() {
		System.out.print(
				"Endereco IP ou o nome do seu servidor de banco de dados:\n" +
				"\n" +
				"[" + contexto.getDbServidor() + "]: "
		);
	}
	
	@Override
	public String executaPasso(
			String resposta) {
		if(!resposta.equals("")) {
			contexto.setDbServidor(resposta);
		}
		return null;
	}

}
