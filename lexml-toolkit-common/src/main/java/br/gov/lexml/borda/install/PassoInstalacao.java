package br.gov.lexml.borda.install;

public abstract class PassoInstalacao<K> {
	
	protected K contexto;
	
	public void setContexto(K contexto) {
		this.contexto = contexto;
	}
	
	public String getNome() {
		return getClass().getName();
	}
	
	/**
	 * Executa o passo e retorna a o nome do próximo passo de instalação
	 * ou null para executar o próximo passo na sequência
	 */
	public abstract String executa() throws Exception;
	
}
