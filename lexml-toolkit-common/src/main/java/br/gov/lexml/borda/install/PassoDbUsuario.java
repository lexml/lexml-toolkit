package br.gov.lexml.borda.install;


public class PassoDbUsuario extends PassoInstalacaoInterativo<ContextoInstalacao> {

	@Override
	public void imprimePergunta() {
		System.out.print(
				"Nome do usuario do banco de dados:\n" +
				"\n" +
				"[" + contexto.getDbUsuario() + "]: "
		);
	}
	
	@Override
	public String executaPasso(
			String resposta) {
		if(!resposta.equals("")) {
			contexto.setDbUsuario(resposta);
		}
		return null;
	}
}
