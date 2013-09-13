package br.gov.lexml.borda.install;

import java.util.ArrayList;
import java.util.List;

public class GerenteInstalacao<K> {
	
	private List<PassoInstalacao<K>> passos = new ArrayList<PassoInstalacao<K>>();
	private K contexto;
	
	public GerenteInstalacao(K contexto) {
		this.contexto = contexto;
	}
	
	/**
	 * Executa o passo e retorna a classe do próximo passo de instalação
	 * ou null para executar o próximo passo na sequência
	 */
	public void executa() throws Exception {
		
		PassoInstalacao<K> passo = null;
		if(!passos.isEmpty()) {
			passo = passos.get(0);
		}
		
		String nomeProximo;
		while(passo != null) {
			nomeProximo = passo.executa();
			passo = getProximo(passo, nomeProximo);
		}
	}

	private PassoInstalacao<K> getProximo(PassoInstalacao<K> passoAtual, String nomeProximo) {
		
		if(nomeProximo != null) {
			// Retorna o primeiro da lista com o nome nomeProximo
			for(PassoInstalacao<K> passo: passos) {
				if(nomeProximo.equals(passo.getNome())) {
					return passo;
				}
			}
		}
		else {
			// Retorna o passo seguinte ao passo atual na lista
			int i = 0;
			for(PassoInstalacao<K> passo: passos) {
				if(passoAtual.equals(passo)) {
					// Identificou o passo atual
					break;
				}
				i++;
			}
			i++; // Próximo na lista
			if(i < passos.size()) {
				return passos.get(i);
			}
		}
		
		return null;
	}
	
	public GerenteInstalacao<K> addPasso(PassoInstalacao<K> passo) {
		passo.setContexto(contexto);
		passos.add(passo);
		return this;
	}
	
}
