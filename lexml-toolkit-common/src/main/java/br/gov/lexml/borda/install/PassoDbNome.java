package br.gov.lexml.borda.install;


public class PassoDbNome extends PassoInstalacaoInterativo<ContextoInstalacao> {

	@Override
	public void imprimePergunta() {
		System.out.print(
				"Nome do banco de dados:\n" +
				"\n" +
				"[" + contexto.getDbNome() + "]: "
		);
	}
	
	@Override
	public String executaPasso(
			String resposta) {
		if(!resposta.equals("")) {
			contexto.setDbNome(resposta);
		}
		return null;
	}
}
