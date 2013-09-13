package br.gov.lexml.borda.install;


public class PassoDbSenha extends PassoInstalacaoInterativo<ContextoInstalacao> {

	@Override
	public void imprimePergunta() {
		System.out.print(
			"Senha do banco de dados:\n" +
			"\n" +
			"[" + contexto.getDbSenha() + "]: "
		);
	}
	
	@Override
	public boolean validaResposta(String resposta) throws Exception {
		return !contexto.getDbSenha().equals("") || validaObrigatorio(resposta);
	}
	
	@Override
    public String executaPasso(String resposta) {
        if (!resposta.equals("")) {
            contexto.setDbSenha(resposta);
        }
        return null;
    }
	
}
