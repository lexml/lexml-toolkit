package br.gov.lexml.exceptions;


public class NotValidLexmlException extends Exception {
	
	private static final long serialVersionUID = 235700172572775772L;

	public NotValidLexmlException(){
		super("Lexml da classe correta ou não é valido");		
	}

}
