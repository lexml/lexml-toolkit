package br.gov.lexml.exceptions;


public class ConfigFailedException extends Exception {

	private static final long serialVersionUID = -5868981673607879838L;

	public ConfigFailedException(String string) {
		super(string);
	}
	
	public ConfigFailedException(String string,Exception e) {
		super(string,e);
	}

}
