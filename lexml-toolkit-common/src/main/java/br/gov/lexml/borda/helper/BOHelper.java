
package br.gov.lexml.borda.helper;

import org.apache.log4j.Logger;

import br.gov.lexml.borda.dao.ConjuntoItemDAO;
import br.gov.lexml.borda.dao.TipoErroDAO;
import br.gov.lexml.borda.domain.ConjuntoItem;
import br.gov.lexml.borda.domain.TipoErro;

public class BOHelper {

    private static Logger logger = Logger.getLogger(BOHelper.class.getName());

    public static final String CONJUNTO_ITEM_ID_DEFAULT = "lexml";
    public static final String CONJUNTO_ITEM_DE_DEFAULT = "Registros oai_lexml";

    private static BOHelper instance = null;
    public Erro ERRO_GENERICO = null;
    public Erro ERRO_XML_MAL_FORMADO = null;
    public Erro ERRO_XML_INVALIDO = null;
    public Erro ERRO_URN_MAL_FORMADO = null;
    public Erro ERRO_URN_INVALIDO = null;
    public Erro ERRO_URN_INCOMPATIVEL = null;
    private ConjuntoItem CONJUNTO_DEFAULT = null;
    private boolean inicializada = false;

    /**
     * Retorna o singleton com todas os erros pré-definidos prontos para uso
     * 
     * @return
     */
    public static BOHelper getInstance() {
        if (null == instance) {
            instance = new BOHelper();
        }
        return instance;
    }

    private BOHelper() {
        inicializar();
    }

    private void inicializar() {

        if (inicializada) {
            return;
        }
        logger
                .debug("Inicializando as tabelas com os erros padrão e o conjunto padrão para importação de registros");

        if (false) {
            if (null == CONJUNTO_DEFAULT) {
                ConjuntoItemDAO c_dao = new ConjuntoItemDAO();

                ConjuntoItem cj = c_dao.load(CONJUNTO_ITEM_ID_DEFAULT);
                if (null == cj) {
                    cj = new ConjuntoItem();
                    cj.setIdConjuntoItem(CONJUNTO_ITEM_ID_DEFAULT);
                    cj.setDeConjuntoItem(CONJUNTO_ITEM_DE_DEFAULT);
                    c_dao.save(cj);
                }

                CONJUNTO_DEFAULT = cj;
            }
        }

        TipoErroDAO m_dao = new TipoErroDAO();
        m_dao.beginTransaction();

        if (null == ERRO_GENERICO) {
            ERRO_GENERICO = new Erro(1, "Um erro nao previsto ocorreu durante a validacao", "Erro Generico", m_dao);
        }

        if (null == ERRO_XML_MAL_FORMADO) {
            ERRO_XML_MAL_FORMADO = new Erro(2, "Falha na validacao por motivo de XML mal formado",
                                            "XML mal formado", m_dao);
        }

        if (null == ERRO_XML_INVALIDO) {
            ERRO_XML_INVALIDO = new Erro(3, "Falha de XML invalido", "XML invalido", m_dao);
        }

        if (null == ERRO_URN_MAL_FORMADO) {
            ERRO_URN_MAL_FORMADO = new Erro(4, "Falha no DocumentoIndividual: URN mal formado", "URN mal formado",
                                            m_dao);
        }

        if (null == ERRO_URN_INVALIDO) {
            ERRO_URN_INVALIDO = new Erro(5, "Falha no DocumentoIndividual: URN invalida", "URN invalido", m_dao);
        }

        if (null == ERRO_URN_INCOMPATIVEL) {
            ERRO_URN_INCOMPATIVEL = new Erro(
                                             6,
                                             "Falha no DocumentoIndividual: URN incompati�vel com o perfil do provedor local",
                                             "URN incompativel com o perfil do provedor", m_dao);
        }

        m_dao.commit();

        inicializada = true;
    }

    /**
     * Erro de validação
     * 
     * @author Gabriel Franklin
     */
    public class Erro {

        public TipoErro tipo;
        public String msg;

        public Erro(final int cod, final String msgUsuario, final String msgErro, final TipoErroDAO p_dao) {

            msg = msgUsuario;
            TipoErro tipo2 = p_dao.load(cod);
            if (null == tipo2) {
                tipo2 = new TipoErro();
                tipo2.setIdTipoErro(cod);
                tipo2.setNoTipoErro(msgErro);
                p_dao.save(tipo2);
            }
            tipo = tipo2;
        }
    }
}
