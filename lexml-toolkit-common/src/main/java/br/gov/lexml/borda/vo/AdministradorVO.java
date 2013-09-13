package br.gov.lexml.borda.vo;

import java.math.BigInteger;

import org.apache.log4j.Logger;

import br.gov.lexml.LexMLCrypt;

public class AdministradorVO {
	private static Logger logger = Logger.getLogger(AdministradorVO.class.getName());
	private BigInteger idProvedor;
	private BigInteger idAdministrador;
	private String email;
	private String senha;
	private long expirationTime;
	private String hash;

	/**
	 * Cria um item do catálogo de usuários que terão acesso web a esta aplicação
	 * 
	 * @param p_idProvedor
	 *           idProvedor de dados ou idPublicador
	 * @param p_idAdministrador
	 *           Id do Administrador
	 * @param p_email
	 * @param p_senha
	 */
	public AdministradorVO(final BigInteger p_idProvedor, final BigInteger p_idAdministrador, final String p_email, final String p_senha) {
		idAdministrador = p_idAdministrador;
		email = p_email;
		senha = p_senha;
		idProvedor = p_idProvedor;
	}

	public AdministradorVO(final String p_email, final String p_senha) {
		email = p_email;
		senha = p_senha;
	}

	public BigInteger getIdAdministrador() {
		return idAdministrador;
	}

	public void setIdAdministrador(final BigInteger idAdministrador) {
		this.idAdministrador = idAdministrador;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(final String senha) {
		this.senha = senha;
	}

	public final boolean validateSenha(final String p_senha) {
		String hash = LexMLCrypt.hash(p_senha);
		logger.debug("Comparando a p_senha: Crypt(" + p_senha + ") = " + hash + "  com o hash do arquivo= (" + senha + ")");
		return senha.equals(hash);
	}

	public BigInteger getIdProvedor() {
		return idProvedor;
	}

	public void setIdProvedor(final BigInteger idProvedor) {
		this.idProvedor = idProvedor;
	}

	public long getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(final long expirationTime) {
		this.expirationTime = expirationTime;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(final String hash) {
		this.hash = hash;
	}
}
