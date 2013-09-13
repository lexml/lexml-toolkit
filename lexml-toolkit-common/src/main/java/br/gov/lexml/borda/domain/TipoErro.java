package br.gov.lexml.borda.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tipo_erro")
public class TipoErro implements Serializable {

	private static final long serialVersionUID = 3043041396241605027L;

	@Id
	@Column(name = "id_tipo_erro")
	private int idTipoErro;

	@Column(name = "no_tipo_erro", nullable = false, length = 1024)
	private String noTipoErro;

	public TipoErro() {
		super();
	}

	public int getIdTipoErro() {
		return idTipoErro;
	}

	public void setIdTipoErro(int idTipoErro) {
		this.idTipoErro = idTipoErro;
	}

	public String getNoTipoErro() {
		return noTipoErro;
	}

	public void setNoTipoErro(String noTipoErro) {
		this.noTipoErro = noTipoErro;
	}

}
