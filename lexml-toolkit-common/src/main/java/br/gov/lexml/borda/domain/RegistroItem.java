package br.gov.lexml.borda.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;

import br.gov.lexml.borda.dao.LexMLDAO;

@Entity
@BatchSize(size = 200)
@Table(name = "registro_item")
@org.hibernate.annotations.Table(indexes = { @Index(name = "idx_validacao_e_ts", columnNames = { "cd_validacao", "ts_registro_gmt" }),@Index(name = "idx_status", columnNames = { "cd_status"}) }, appliesTo = "registro_item")
public class RegistroItem implements Serializable {
	private static final long serialVersionUID = 5062000945013396268L;	

	/**
	 * O Registro item possui:
	 * 
	 * <br/>
	 * 
	 * ID_REGISTRO_ITEM, TS_REGISTRO_GMT, CD_STATUS, CD_VALIDACAO e TX_METADADO_XML
	 * 
	 */
	public RegistroItem() {
		super();
	}

	@Id
	@Column(name = "id_registro_item", nullable = false, length = 255)
	private String idRegistroItem;

	@Column(name = "ts_registro_gmt", nullable = false)
	@OrderBy
	 @IndexColumn(name = "validacao_e_ts")
	private Timestamp tsRegistroGmt;

	@Column(name = "cd_status", nullable = false, length = 1)
	@IndexColumn(name = "idx_status")
	private String cdStatus = LexMLDAO.CdStatus.DEFAULT_CD_STATUS;

	@Column(name = "cd_validacao", nullable = false, length = 1)
	 @IndexColumn(name = "idx_validacao_e_ts")
	private String cdValidacao = LexMLDAO.CdValidacao.DEFAULT_VALIDACAO_STATUS;

	@Column(name = "tx_metadado_xml", nullable = true, length = 0)
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private String txMetadadoXml;

	@OneToMany(mappedBy = "registroItem", fetch = FetchType.LAZY)
	private Set<RegistroItemErro> registroItemErroCollection;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_conjunto_item", nullable = true)
	private ConjuntoItem conjuntoItem;

	public String getIdRegistroItem() {
		/* Fazemos o trim da string para evitar erros futuros ao PROAIDriver */
		return idRegistroItem;
	}

	public void setIdRegistroItem(final String idRegistroItem) {
		/* Fazemos o trim da string para evitar erros futuros ao PROAIDriver */
		this.idRegistroItem = idRegistroItem;
	}

	public Timestamp getTsRegistroGmt() {
		return tsRegistroGmt;
	}

	public void setTsRegistroGmt(final Timestamp tsRegistroGmt) {
		this.tsRegistroGmt = tsRegistroGmt;
	}

	public String getCdStatus() {
		return cdStatus;
	}

	/**
	 * Sao permitidos os cdStatus: "N" = Novo ou "D" = Deletado
	 * 
	 * @param cdStatus
	 */
	public void setCdStatus(final String cdStatus) {
		this.cdStatus = cdStatus;
	}

	/**
	 * Sao permitidos os cdValidacao: "E"=ERRO; "O"=OK; "I"=Indefinido; "P"=PROCESSADO
	 * 
	 * @return
	 */
	public String getCdValidacao() {
		return cdValidacao;
	}

	public void setCdValidacao(final String cdValidacao) {
		this.cdValidacao = cdValidacao;
	}

	public String getTxMetadadoXml() {
		return txMetadadoXml;
	}

	public void setTxMetadadoXml(final String txMetadadoXml) {
		this.txMetadadoXml = txMetadadoXml;
	}

	public Set<RegistroItemErro> getRegistroItemErroCollection() {
		return registroItemErroCollection;
	}

	public void setRegistroItemErroCollection(final Set<RegistroItemErro> registroItemErroCollection) {
		this.registroItemErroCollection = registroItemErroCollection;
	}

	public ConjuntoItem getConjuntoItem() {
		return conjuntoItem;
	}

	public void setConjuntoItem(final ConjuntoItem conjuntoItem) {
		this.conjuntoItem = conjuntoItem;
	}
}
