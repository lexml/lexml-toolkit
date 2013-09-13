package br.gov.lexml.exceptions;


public class NullLexmlException extends Exception {
	
	private static final long serialVersionUID = 235700172572775772L;

	public NullLexmlException(){
		super("Lexml não pode ser objeto nulo");		
	}

}
