package br.gov.lexml.exceptions;

public class LexmlNotFoundException extends Exception {

	private static final long serialVersionUID = 3852728277351652376L;

	public LexmlNotFoundException() {
		super("Lexml não pode ser encontrado");

	}

}
