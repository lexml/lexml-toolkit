package br.gov.lexml.exceptions;

/**
 * Falha no processo de instalação
 * 
 * @author frago
 */
public class InstalacaoException extends RuntimeException {
	
	public InstalacaoException(String msg) {
		super(msg);
	}

	public InstalacaoException(String msg, Throwable reason) {
		super(msg, reason);
	}
	
}
