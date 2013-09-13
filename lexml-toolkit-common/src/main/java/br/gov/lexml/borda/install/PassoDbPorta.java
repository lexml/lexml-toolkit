package br.gov.lexml.borda.install;



public class PassoDbPorta extends PassoInstalacaoInterativo<ContextoInstalacao> {

	@Override
	public void imprimePergunta() {
		System.out.print(
				"Porta TCP do seu servidor de banco de dados:\n" +
				"\n" +
				"[" + contexto.getDbPorta() + "]: "
		);
	}
	
	@Override
	public boolean validaResposta(String resposta) throws Exception {
		
		if(resposta.trim().equals("")) {
			// usa default
			return true; 
		}
		
		try {
			contexto.setDbPorta(Integer.parseInt(resposta));
		}
		catch(NumberFormatException e) {
			return false;
		}
		
		return true;
	}
	
}
