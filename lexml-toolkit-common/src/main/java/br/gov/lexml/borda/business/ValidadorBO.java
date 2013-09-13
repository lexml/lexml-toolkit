
package br.gov.lexml.borda.business;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import br.gov.lexml.LexMLConfig;
import br.gov.lexml.LexMLSystem;
import br.gov.lexml.LexMLConfig.Perfil;
import br.gov.lexml.LexMLConfig.Publicador;
import br.gov.lexml.borda.dao.RegistroItemDAO;
import br.gov.lexml.borda.dao.LexMLDAO.CdValidacao;
import br.gov.lexml.borda.domain.RegistroItem;
import br.gov.lexml.borda.domain.RegistroItemErro;
import br.gov.lexml.borda.domain.TipoErro;
import br.gov.lexml.borda.helper.BOHelper;
import br.gov.lexml.coleta.validador.TipoErroValidacao;
import br.gov.lexml.coleta.validador.ValidadorRegistroItem;
import br.gov.lexml.coleta.validador.ValidadorService;
import br.gov.lexml.exceptions.ConfigFailedException;
import br.gov.lexml.oaiLexml.LexMLDocument;

/**
 * BusinessObject para as funcionalidades de validação
 * 
 * @author Gabriel Franklin
 */
public class ValidadorBO extends AbstractBO implements ValidadorService {

    private static Logger logger = Logger.getLogger(ValidadorBO.class.getName());

    /**
     * Tamanho dos buffers de gravação dos logs, erros e registros alterados.
     */

    private static int BUCKET_SIZE = 1000;

    private List<RegistroItemErro> errosList = new ArrayList<RegistroItemErro>();
    private List<RegistroItem> atualizados = new ArrayList<RegistroItem>();

    private final RegistroItemDAO dao;
    private final LexMLConfig config;
    private final BOHelper helper;

    private Set<String> nucleosValidos;

    private final ValidadorRegistroItem validador;

    private final Map<TipoErroValidacao , TipoErro> mapErros = new HashMap<TipoErroValidacao , TipoErro>();

    public ValidadorBO() throws ConfigFailedException {

        helper = BOHelper.getInstance();

        config = LexMLConfig.getInstance();

        dao = new RegistroItemDAO();

        validador = new ValidadorRegistroItem();
        validador.setValidadorService(this);

        mapErros.put(TipoErroValidacao.ERRO_GENERICO, helper.ERRO_GENERICO.tipo);
        mapErros.put(TipoErroValidacao.URN_INCOMPATIVEL, helper.ERRO_URN_INCOMPATIVEL.tipo);
        mapErros.put(TipoErroValidacao.URN_INVALIDO, helper.ERRO_URN_INVALIDO.tipo);
        mapErros.put(TipoErroValidacao.URN_MAL_FORMADO, helper.ERRO_URN_MAL_FORMADO.tipo);
        mapErros.put(TipoErroValidacao.XML_INVALIDO, helper.ERRO_XML_INVALIDO.tipo);
        mapErros.put(TipoErroValidacao.XML_MAL_FORMADO, helper.ERRO_XML_MAL_FORMADO.tipo);

        compilarPerfisValidos();
    }

    /**
     * Este metodo é usado tando na validacao dos registros do banco como durante a importacao a partir de arquivos
     * XMLs
     * <p/>
     * <u>Regras de Validação:</u> *
     * <li>RV#1: O ID_REGISTRO_ITEM <b>não</b> pode conter espaços em branco</li>
     * <li>RV#2: O TX_METADADO_XML <b>não</b> pode ser nulo</li>
     * <li>RV#3: O TX_METADADO_XML <b>deve</b> ser válido segundo o schema/xmlbeans</li>
     * <li>RV#4: O RegistroItem ri <b>não</b> pode ser nulo</li>
     * <li>RV#5: A URN de DocumentoIndividual deve ser válida para <b>todos</b> os idPublicador do registro</li>
     * <li>RV#6: A URN não é válida para DocumentoIndividual de acordo com o perfil, usando o idPublicador de Item</li>
     * <li>RV#7: A URN não é valida para Relacionamento de acordo com o perfil</li>
     * <li>RV#8: Se o Relacionamento não possuir idPublicador será considerado o idPublicador do primeiro Item do
     * registro</li>
     */
    @SuppressWarnings("unchecked")
    public boolean validarRegistroItem(final RegistroItem ri, final CorretorRegistroAntigo corretor) {
        LexMLDocument doc = null;

        if (null == ri) {
            logger.error("RV#4 Objeto RegistroItem ri passado é nulo");
            return false;
        }

        // Apaga último registro de erro
        dao.deleteRegistroItemErro(ri);

        // Comeca marcado com erro até passar por todas as validações, ou o campo CdValidação
        // assumir outro código
        ri.setCdValidacao(CdValidacao.ERRO);

        String xml = corretor.corrigeRegistro(ri.getTxMetadadoXml());

        if (validador.validar(ri.getIdRegistroItem(), xml, ri)) {
            ri.setCdValidacao(CdValidacao.OK);
            return true;
        }

        return false;
    }

    /**
     * Método que busca da DAO os registros indefinidos ainda não validados, valida e gera os relatãrios de erros e
     * de processamento.
     */
    public void validarRegistrosIndefinidos() {
        if (null == config) {
            logger.fatal("Ambiente nao configurado, verifique arquivo de configuracao:"
                + LexMLSystem.PERFIL_NODO_BORDA_XML);
            return;
        }

        List<RegistroItem> list = dao.listNotValid(null, BUCKET_SIZE);
        if (list.isEmpty()) {
            logger.warn("Não havia registros indefinidos para serem validados");
            return;
        }

        long total = dao.countNotValid();
        logger.info("Começando a validar " + total + " registros.");

        long sucessos = 0;
        long erros = 0;
        long subtotal;
        double per;

        DecimalFormat df = new DecimalFormat("#00.0");

        CorretorRegistroAntigo corretor = new CorretorRegistroAntigo();

        do {
            /* coloca a DAO em modo de transação, para economizar em commits */
            dao.beginTransaction();

            for (RegistroItem ri : list) {
                // System.out.println("----------------- ri: " + ri.getIdRegistroItem());

                if (validarRegistroItem(ri, corretor)) {
                    sucessos++;
                }
                else {
                    erros++;
                }

                /*
                 * coloca o RegistroItem processado na fila para ser atualizado de volta no banco de dados
                 */
                atualizados.add(ri);
            }

            // Salva listas de RegistroItem e RegistroItemErro
            saveLists();

            subtotal = sucessos + erros;
            per = (float) subtotal * 100 / total;
            logger.info("... " + subtotal + " (" + df.format(per) + "%)");

            // Próxima página
            String ultimoId = list.get(list.size() - 1).getIdRegistroItem();
            list = dao.listNotValid(ultimoId, BUCKET_SIZE);

        }
        while (!list.isEmpty());

        if (corretor.qtdRegistrosAntigos > 0) {
            logger.warn("-----------------------------------");
            logger.warn("Foram encontrados " + corretor.qtdRegistrosAntigos + " registros em formato antigo. "
                + "Veja o arquivo correcao-schema.txt");

        }

        logger.info("-----------------------------------");
        logger.info("Total de registros processados: " + total);
        logger.info("Processados com sucesso: " + sucessos);
        logger.info("Registros com erros: " + erros);

        per = (float) sucessos * 100 / total;
        String strPer = df.format(per);

        logger.info("Aproveitamento de " + strPer + "%");

        System.gc(); // a esta altura já deve haver algum lixo a coletar. Então executa-se um
        // garbage collector
    }

    /**
     * Percorre as listas (buffers) e descarrega no banco quando o tamanho da lista chegar a p_size
     * 
     * @param p_size
     */
    private void saveLists() {
        logger.debug("Iniciando COMMIT");
        logger.debug("de " + atualizados.size() + " registroItem");
        dao.updateCdValidacao(atualizados);
        atualizados = new ArrayList<RegistroItem>();

        logger.debug("de " + errosList.size() + " registroItemErro");
        dao.saveList(errosList);
        errosList = new ArrayList<RegistroItemErro>();

        dao.commit();
        dao.clear();
        System.gc();
        dao.beginTransaction();

        logger.debug("Commit finalizado.");
    }

    public boolean isNucleoValido(final String nucleo) {
        return nucleosValidos.contains(nucleo);
    }

    public void logError(final String idRegistroItem, final TipoErroValidacao tipoErroValidacao, final String msg,
        final Object ctx) {

        RegistroItem ri = (RegistroItem) ctx;

        TipoErro te = mapErros.get(tipoErroValidacao);

        RegistroItemErro erro = new RegistroItemErro(ri, te, msg);

        logger.error("[" + idRegistroItem + "] " + te.getNoTipoErro());
        logger.error(msg);

        errosList.add(erro);
    }

    /**
     * Gera um HashSet com os Nucleos de URN que são válidos neste provedor a partir de uma configuração já
     * carregada.
     * 
     * @throws ConfigFailedException
     */
    private void compilarPerfisValidos() {

        if (null != nucleosValidos) {
            return;
        }

        nucleosValidos = new HashSet<String>();

        for (Publicador publicador : config.getPublicadores()) {

            BigInteger idPublicador = publicador.getIdPublicador();

            ArrayList<Perfil> perfis = publicador.getPerfil();
            if (null != perfis) {
                for (Perfil perfil : perfis) {
                    String nucleo = perfil.getNucleoUrn();
                    String tipoPerfil = perfil.getTipoPerfil();
                    if (tipoPerfil.equals(TIPO_PERFIL_TODOS)) {
                        nucleosValidos.add(idPublicador + SEP + TIPO_PERFIL_DOCUMENTO_INDIVIDUAL + SEP + nucleo);
                        nucleosValidos.add(idPublicador + SEP + TIPO_PERFIL_RELACIONAMENTO + SEP + nucleo);
                    }
                    else {
                        nucleosValidos.add(idPublicador + SEP + tipoPerfil + SEP + nucleo);
                    }
                }
            }

        }

        logger.debug("TOTAL DE " + nucleosValidos.size() + " PERFIS ADICIONADOS");
    }

    private class CorretorRegistroAntigo {

        public int qtdRegistrosAntigos;

        public String corrigeRegistro(String xml) {

            if (xml != null && xml.contains("<lexml:LexML") && xml.contains("<DocumentoIndividual")) {

                qtdRegistrosAntigos++;

                xml = StringUtils.replace(xml, "<lexml:", "<");
                xml = StringUtils.replace(xml, "</lexml:", "</");
                xml = StringUtils.replace(xml, "xmlns:lexml=", "xmlns=");
            }

            return xml;
        }

    }
}
