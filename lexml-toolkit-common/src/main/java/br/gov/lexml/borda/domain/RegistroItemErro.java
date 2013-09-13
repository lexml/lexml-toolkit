package br.gov.lexml.borda.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;

@Entity
@Table(name = "registro_item_erro")
public class RegistroItemErro implements Serializable {

	private static final long serialVersionUID = 6842957577267533780L;

	private static int nextId;

	@Id
	@Column(name = "id_registro_item_erro", nullable = false)
	private int idRegistroItemErro;

	@Column(name = "ts_registro_gmt", nullable = false)
	private Timestamp tsRegistroGmt;

	@Column(name = "ts_processamento", nullable = false)
	private Timestamp tsProcessamento;

	@Column(name = "de_detalhe_item_erro", nullable = false, length = 2048)
	private String deDetalheItemErro;

	@ManyToOne
	@JoinColumn(name = "id_registro_item")
	private RegistroItem registroItem;

	@ManyToOne
	@JoinColumn(name = "id_tipo_erro")
	private TipoErro tipoErro;

	public static void setNextId(final int p_ni) {
		nextId = p_ni;
	}

	public RegistroItemErro() {
		idRegistroItemErro = nextId++;
	}
	
    public RegistroItemErro(RegistroItem ri, final TipoErro te, String detalhe_erro) {
        this();
        setRegistroItem(ri);
        setDeDetalheItemErro(StringUtils.defaultString(detalhe_erro, "sem detalhe"));
        setTsRegistroGmt(ri.getTsRegistroGmt());
        setTipoErro(te);
    }

	public int getIdRegistroItemErro() {
		return idRegistroItemErro;
	}

	public void setIdRegistroItemErro(final int idRegistroItemErro) {
		this.idRegistroItemErro = idRegistroItemErro;
	}

	public Timestamp getTsRegistroGmt() {
		return tsRegistroGmt;
	}

	public void setTsRegistroGmt(final Timestamp tsRegistroGmt) {
		this.tsRegistroGmt = tsRegistroGmt;
	}

	public Timestamp getTsProcessamento() {
		return tsProcessamento;
	}

	public void setTsProcessamento(final Timestamp tsProcessamento) {
		this.tsProcessamento = tsProcessamento;
	}

	public String getDeDetalheItemErro() {
		return deDetalheItemErro;
	}

	public void setDeDetalheItemErro(final String deDetalheItemErro) {
		this.deDetalheItemErro = StringUtils.substring(deDetalheItemErro, 0, 2048);
	}

	public RegistroItem getRegistroItem() {
		return registroItem;
	}

	public void setRegistroItem(final RegistroItem registroItem) {
		this.registroItem = registroItem;
	}

	public TipoErro getTipoErro() {
		return tipoErro;
	}

	public void setTipoErro(final TipoErro tipoErro) {
		this.tipoErro = tipoErro;
	}

}
