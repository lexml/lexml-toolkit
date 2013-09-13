package br.gov.lexml.borda.install;

public class ContextoInstalacao {

	private boolean dbPreconfigurado;
	private int dbSgbd = 0;
	private String dbServidor = "127.0.0.1";
	private int dbPorta;
	private String dbNome = "lexml";
	private String dbUsuario = "lexml";
	private String dbSenha = "";
	private String pathArquivoConf = ".";
	private boolean bdOk;
	private boolean warOk;
	
	public boolean isDbPreconfigurado() {
		return dbPreconfigurado;
	}
	
	public void setDbPreconfigurado(boolean dbPreconfigurado) {
		this.dbPreconfigurado = dbPreconfigurado;
	}
	
	public int getDbSgbd() {
		return dbSgbd;
	}
	
	public void setDbSgbd(int dbSgbd) {
		this.dbSgbd = dbSgbd;
	}
	
	public String getDbServidor() {
		return dbServidor;
	}
	
	public void setDbServidor(String dbServidor) {
		this.dbServidor = dbServidor;
	}
	
	public int getDbPorta() {
		return dbPorta;
	}
	
	public void setDbPorta(int dbPorta) {
		this.dbPorta = dbPorta;
	}
	
	public String getDbNome() {
		return dbNome;
	}
	
	public void setDbNome(String dbNome) {
		this.dbNome = dbNome;
	}
	
	public String getDbUsuario() {
		return dbUsuario;
	}
	
	public void setDbUsuario(String dbUsuario) {
		this.dbUsuario = dbUsuario;
	}
	
	public String getDbSenha() {
		return dbSenha;
	}
	
	public void setDbSenha(String dbSenha) {
		this.dbSenha = dbSenha;
	}
	
	public String getPathArquivoConf() {
		return pathArquivoConf;
	}
	
	public void setPathArquivoConf(String pathArquivoConf) {
		this.pathArquivoConf = pathArquivoConf;
	}
	
	public boolean isBdOk() {
		return bdOk;
	}
	
	public void setBdOk(boolean bdOk) {
		this.bdOk = bdOk;
	}

	public boolean isWarOk() {
		return warOk;
	}
	
	public void setWarOk(boolean warOk) {
		this.warOk = warOk;
	}
}
