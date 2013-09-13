/**
 * this is a work based on OAICat Software,
 *
 *
 * Copyright 2006 OCLC Online Computer Library Center Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or
 * agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.gov.lexml.oaicat;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import ORG.oclc.oai.server.catalog.AbstractCatalog;
import ORG.oclc.oai.server.verb.BadResumptionTokenException;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;
import ORG.oclc.oai.server.verb.IdDoesNotExistException;
import ORG.oclc.oai.server.verb.NoItemsMatchException;
import ORG.oclc.oai.server.verb.NoMetadataFormatsException;
import ORG.oclc.oai.server.verb.NoSetHierarchyException;
import ORG.oclc.oai.server.verb.OAIInternalServerError;
import ORG.oclc.oai.util.OAIUtil;

import br.gov.lexml.borda.dao.ConjuntoItemDAO;
import br.gov.lexml.borda.dao.LexMLDAO.CdStatus;
import br.gov.lexml.borda.dao.LexMLDAO.CdValidacao;
import br.gov.lexml.borda.dao.RegistroItemDAO;
import br.gov.lexml.borda.domain.ConjuntoItem;
import br.gov.lexml.borda.domain.RegistroItem;
import br.gov.lexml.borda.domain.RegistroItemErro;

/**
 * Catalogo LexML, baseado no trabalho de Jeff Young NewFileSystemOAICatalog
 */

public class LexMLOAICatalog extends AbstractCatalog implements LexMLOAI {

    public class InternalResumptionToken {

        public String from;
        public String until;
        public String set;
        public String format;
        public String lastId;
        public int offset;
        public int total;

        public InternalResumptionToken(final String p_from, final String p_until, final String p_set,
                                       final String p_format, final String p_lastId, final int p_offset,
                                       final int p_total) {
            from = p_from;
            until = p_until;
            set = p_set;
            lastId = p_lastId;
            offset = p_offset;
            format = p_format;
            total = p_total;
        }

        public InternalResumptionToken(final InternalResumptionToken oldIRT, final String p_lastId,
                                       final int p_offset) {
            from = oldIRT.from;
            until = oldIRT.until;
            set = oldIRT.set;
            lastId = p_lastId;
            offset = oldIRT.offset + p_offset;
            format = oldIRT.format;
            total = oldIRT.total;
        }

    }

    static boolean debug = false;

    /**
     * Use the current date as the basis for the resumptiontoken
     * <p/>
     * <b>LEXML ready</b>
     * 
     * @return a long integer version of the current time
     */
    private synchronized static String getRSName() {
        Date now = new Date();
        return Long.toString(now.getTime());
    }

    private final SimpleDateFormat sdfData = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat sdfDataHora = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private final HashMap resumptionTokens = new HashMap();
    private RegistroItemDAO m_ri_dao = null;
    private ConjuntoItemDAO m_ci_dao = null;
    private ArrayList sets = null;

    private final int maxListSize;

    public LexMLOAICatalog(final Properties properties) throws IOException {

        String temp;
        temp = properties.getProperty("LexMLOAICatalog.maxListSize");
        if (temp == null) {
            throw new IllegalArgumentException("LexMLOAICatalog."
                                               + "maxListSize não foi informado no arquivo de properties.");
        }
        maxListSize = Integer.parseInt(temp);

        // TimeZone tz = TimeZone.getTimeZone("UTC");
        // sdfData.setTimeZone(tz);
        // sdfDataHora.setTimeZone(tz);

        prepare();
    }

    /**
     * close the repository
     */
    @Override
    public void close() {
    }

    /**
     * Utility method to construct a Record object for a specified metadataFormat from a native
     * record
     * 
     * @param nativeItem native item from the dataase
     * @param metadataPrefix the desired metadataPrefix for performing the crosswalk
     * @return the <record/> String
     * @exception CannotDisseminateFormatException the record is not available for the specified
     *            metadataPrefix.
     */
    private String constructRecord(final HashMap nativeItem, final String metadataPrefix)
                                                                                         throws CannotDisseminateFormatException {
        String schemaURL = null;
        Iterator setSpecs = getSetSpecs(nativeItem);
        Iterator abouts = getAbouts(nativeItem);

        if (metadataPrefix != null) {
            if ((schemaURL = getCrosswalks().getSchemaURL(metadataPrefix)) == null) {
                throw new CannotDisseminateFormatException(metadataPrefix);
            }
        }
        return getRecordFactory().create(nativeItem, schemaURL, metadataPrefix, setSpecs, abouts);
    }

    private String constructRecord(final RegistroItem ri, final String metadataPrefix)
                                                                                      throws CannotDisseminateFormatException {
        return constructRecord(RegistroItem2NativeRecord(ri), metadataPrefix);
    }

    private String constructError(final RegistroItem ri) throws CannotDisseminateFormatException {

        StringBuffer xmlRec = new StringBuffer();

        xmlRec.append("<record><header");
        if (ri.getCdStatus() == CdStatus.DELETADO) {
            xmlRec.append(" status=\"deleted\"");
        }
        xmlRec.append("><identifier>");
        xmlRec.append(OAIUtil.xmlEncode(ri.getIdRegistroItem()));
        xmlRec.append("</identifier><datestamp>");
        xmlRec.append(dateToString(ri.getTsRegistroGmt()));
        xmlRec.append("</datestamp>");
        xmlRec.append("</header>");

        xmlRec.append("<metadata>");

        // Status
        String cdStatus = ri.getCdStatus();
        String status = cdStatus.equals(CdStatus.DELETADO) ? "Deletado" : "Novo";
        xmlRec.append("<Status>" + status + "</Status>");

        if (cdStatus.equals(CdStatus.NOVO)) {

            // Resultado da validação
            String cdValidacao = ri.getCdValidacao();
            String validacao = null;
            if (cdValidacao.equals(CdValidacao.ERRO)) {
                validacao = "Erro";
            }
            else if (cdValidacao.equals(CdValidacao.INDEFINIDO)) {
                validacao = "Ainda não validado";
            }
            else if (cdValidacao.equals(CdValidacao.OK)) {
                validacao = "Valiado com sucesso";
            }
            else if (cdValidacao.equals(CdValidacao.FALHA)) {
                validacao = "Falha";
            }
            else {
                validacao = "Código de validação '" + cdValidacao + "' desconhecido.";
            }
            xmlRec.append("<Validacao>" + validacao + "</Validacao>");

            // Mensagem de erro
            Set<RegistroItemErro> ries = ri.getRegistroItemErroCollection();
            if (!ries.isEmpty()) {
                RegistroItemErro rie = ries.iterator().next();
                xmlRec.append("<Mensagem>" + StringEscapeUtils.escapeXml(rie.getDeDetalheItemErro())
                              + "</Mensagem>");
            }
        }
        xmlRec.append("</metadata>");

        xmlRec.append("</record>");

        return xmlRec.toString();
    }

    private String dateToString(final Date date) {
        return sdfDataHora.format(date);
    }

    /**
     * get an Iterator containing the abouts for the nativeItem
     * 
     * @param rs ResultSet containing the nativeItem
     * @return an Iterator containing the list of about values for this nativeItem
     */
    private Iterator getAbouts(final HashMap nativeItem) {
        return null;
    }

    /**
     * <b>LEXML ready</b>
     * 
     * @return
     */
    private HashMap getNativeHeader(final RegistroItem ri) {

        HashMap recordMap = null;
        ArrayList setSpecs = new ArrayList();

        recordMap = new HashMap();
        recordMap.put(NI.localIdentifier, ri.getIdRegistroItem());
        recordMap.put(NI.lastModified, dateToString(ri.getTsRegistroGmt()));
        ConjuntoItem ci = ri.getConjuntoItem();
        if (null != ci) {
            setSpecs.add(ci.getIdConjuntoItem());
            recordMap.put(NI.setSpecs, setSpecs.iterator());
        }
        if ("D".equalsIgnoreCase(ri.getCdStatus().substring(0, 1))) {
            recordMap.put(NI.status, "deleted");
        }

        return recordMap;
    }

    /*
     * Retrieve a list of Identifiers that satisfy the criteria parameters
     *
     * @param from beginning date in the form of YYYY-MM-DD or null if earliest date is desired
     *
     * @param until ending date in the form of YYYY-MM-DD or null if latest date is desired
     *
     * @param set set name or null if no set is desired
     *
     * @return a Map object containing an optional "resumptionToken" key/value pair and an "identifiers" Map object. The "identifiers" Map contains OAI identifier keys
     * with corresponding values of "true" or null depending on whether the identifier is deleted or not.
     *
     * @exception OAIBadRequestException signals an http status code 400 problem
     */

    private HashMap getNativeRecord(final String identifier, final String metadataFormat) throws IOException {
        RegistroItem ri = m_ri_dao.load(identifier);

        if (null == ri) {
            return null;
        }
        return RegistroItem2NativeRecord(ri);

    }

    /**
     * Retrieve the specified metadata for the specified oaiIdentifier
     * 
     * @param oaiIdentifier the OAI identifier
     * @param metadataPrefix the OAI metadataPrefix
     * @return the Record object containing the result.
     * @exception CannotDisseminateFormatException signals an http status code 400 problem
     * @exception IdDoesNotExistException signals an http status code 404 problem
     * @exception OAIInternalServerError signals an http status code 500 problem
     */
    @Override
    public String getRecord(final String oaiIdentifier, final String metadataPrefix)
                                                                                    throws IdDoesNotExistException,
                                                                                    CannotDisseminateFormatException,
                                                                                    OAIInternalServerError {
        HashMap nativeItem = null;
        try {
            String localIdentifier = getRecordFactory().fromOAIIdentifier(oaiIdentifier);

            nativeItem = getNativeRecord(localIdentifier, metadataPrefix);
            if (nativeItem == null) {
                throw new IdDoesNotExistException(oaiIdentifier);
            }
            return constructRecord(nativeItem, metadataPrefix);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new OAIInternalServerError("Database Failure");
        }
    }

    /**
     * Retrieve a list of schemaLocation values associated with the specified oaiIdentifier. We get
     * passed the ID for a record and are supposed to return a list of the formats that we can
     * deliver the record in. Since we are assuming that all the records in the directory have the
     * same format, the response to this is static;
     * 
     * @param oaiIdentifier the OAI identifier
     * @return a Vector containing schemaLocation Strings
     * @exception OAIBadRequestException signals an http status code 400 problem
     * @exception OAINotFoundException signals an http status code 404 problem
     */
    @Override
    public Vector getSchemaLocations(final String oaiIdentifier) throws IdDoesNotExistException,
                                                                NoMetadataFormatsException {
        // ArrayList extensionList = null;

        String localIdentifier = getRecordFactory().fromOAIIdentifier(oaiIdentifier);
        // extensionList = getExtensionList(localIdentifier);

        ArrayList extensionList = new ArrayList();
        // extensionList.add("oai_dc");
        extensionList.add("oai_lexml");

        if (extensionList != null) {
            return getRecordFactory().getSchemaLocations(extensionList);
        }
        else {
            throw new IdDoesNotExistException(oaiIdentifier);
        }
    }

    /**
     * <b>LEXML ready</b>
     * 
     * @return lista dos sets da base lexml-db
     */
    private ArrayList getSets() {
        TreeMap treeMap = new TreeMap();
        String propertyPrefix = "Sets.";
        List<ConjuntoItem> lista = m_ci_dao.list();
        if (null != lista) {
            Iterator<ConjuntoItem> iter = lista.iterator();
            int i = 0;
            while (iter.hasNext()) {
                i++;
                treeMap.put(propertyPrefix + i, helper.ConjuntoItem2Sets(iter.next()));
            }
        }
        return new ArrayList(treeMap.values());
    }

    /**
     * get an Iterator containing the setSpecs for the nativeItem
     * 
     * @param rs ResultSet containing the nativeItem
     * @return an Iterator containing the list of setSpec values for this nativeItem
     */
    private Iterator getSetSpecs(final HashMap nativeItem) {
        return (Iterator) nativeItem.get(NI.setSpecs);
    }

    // #############################################################################################################################

    @Override
    public Map listIdentifiers(final String from, final String until, final String set, final String metadataPrefix)
                                                                                                                    throws NoItemsMatchException,
                                                                                                                    CannotDisseminateFormatException {
        List<RegistroItem> ri_list = m_ri_dao.listByTimeWindowAndSet(toDate(from), toDate(until), set, null,
                                                                     maxListSize);
        int total = m_ri_dao.countListByTimeWindowAndSet(toDate(from), toDate(until), set);

        String lastId = getLastId(ri_list);
        InternalResumptionToken irt = new InternalResumptionToken(from, until, set, metadataPrefix, lastId,
                                                                  ri_list.size(), total);
        return internalListIdentifiers(ri_list, irt);
    }

    @Override
    public Map listIdentifiers(final String resumptionId) throws BadResumptionTokenException {
        InternalResumptionToken irt = (InternalResumptionToken) resumptionTokens.get(resumptionId);
        if (null == irt) {
            throw new BadResumptionTokenException();
        }

        List<RegistroItem> ri_list = m_ri_dao.listByTimeWindowAndSet(toDate(irt.from), toDate(irt.until), irt.set,
                                                                     irt.lastId, maxListSize);

        try {
            return internalListIdentifiers(ri_list, irt);
        }
        catch (CannotDisseminateFormatException e) {
            throw new BadResumptionTokenException();
        }
        catch (NoItemsMatchException e) {
            // Se a lista retornar vazia é só o fim da paginacao.
        }
        return null;
    }

    private Map internalListIdentifiers(final List<RegistroItem> ri_list, final InternalResumptionToken irt)
                                                                                                            throws CannotDisseminateFormatException,
                                                                                                            NoItemsMatchException {
        purge(); // clean out old resumptionTokens

        Map listIdentifiersMap = new HashMap();
        ArrayList headers = new ArrayList();
        ArrayList identifiers = new ArrayList();

        int numRows = ri_list.size();
        Iterator<RegistroItem> ri_iter = ri_list.iterator();

        if (numRows == 0) {
            throw new NoItemsMatchException();
        }

        while (ri_iter.hasNext()) {
            String[] header = getRecordFactory().createHeader(getNativeHeader(ri_iter.next()));
            headers.add(header[0]);
            identifiers.add(header[1]);
        }

        // Verifica se temos que informar um ResumptionToken
        if (numRows >= maxListSize) {
            String resumptionId = LexMLOAICatalog.getRSName();
            String lastId = getLastId(ri_list);
            InternalResumptionToken novoIRT = new InternalResumptionToken(irt, lastId, numRows);
            resumptionTokens.put(resumptionId, novoIRT);
            listIdentifiersMap.put("resumptionMap", getResumptionMap(resumptionId, irt.total, irt.offset));
        }

        listIdentifiersMap.put("headers", headers.iterator());
        listIdentifiersMap.put("identifiers", identifiers.iterator());

        return listIdentifiersMap;

    }

    // ###########################################################################################################################
    @Override
    public Map<String , Object> listErrors(final String from, final String until, final String identifier,
                                           final String set) throws NoItemsMatchException,
                                                            CannotDisseminateFormatException {

        List<RegistroItem> ri_list;
        InternalResumptionToken irt = null;

        if (StringUtils.isEmpty(identifier)) {
            ri_list = m_ri_dao.listErrorsByTimeWindowAndSet(toDate(from), toDate(until), set, null, maxListSize);
            int total = m_ri_dao.countListErrorsByTimeWindowAndSet(toDate(from), toDate(until), set);
            String lastId = getLastId(ri_list);
            irt = new InternalResumptionToken(from, until, set, null, lastId, ri_list.size(), total);
        }
        else {
            RegistroItem ri = m_ri_dao.load(identifier);
            if (ri == null) {
                throw new NoItemsMatchException();
            }
            ri_list = new ArrayList<RegistroItem>();
            ri_list.add(ri);
        }

        return internalErrors(ri_list, irt);
    }

    @Override
    public Map<String , Object> listErrors(final String resumptionId) throws BadResumptionTokenException {
        InternalResumptionToken irt = (InternalResumptionToken) resumptionTokens.get(resumptionId);
        if (null == irt) {
            throw new BadResumptionTokenException();
        }

        List<RegistroItem> ri_list = m_ri_dao.listErrorsByTimeWindowAndSet(toDate(irt.from), toDate(irt.until),
                                                                           irt.set, irt.lastId, maxListSize);

        try {
            return internalErrors(ri_list, irt);
        }
        catch (CannotDisseminateFormatException e) {
            throw new BadResumptionTokenException();
        }
        catch (NoItemsMatchException e) {
            // Se a lista retornar vazia é só o fim da paginacao.
        }
        return null;
    }

    private Map internalErrors(final List<RegistroItem> ri_list, final InternalResumptionToken irt)
                                                                                                   throws CannotDisseminateFormatException,
                                                                                                   NoItemsMatchException {

        purge(); // clean out old resumptionTokens

        Map listIdentifiersMap = new HashMap();
        ArrayList errors = new ArrayList();

        int numRows = ri_list.size();
        Iterator<RegistroItem> ri_iter = ri_list.iterator();

        if (numRows == 0) {
            throw new NoItemsMatchException();
        }

        while (ri_iter.hasNext()) {
            errors.add(constructError(ri_iter.next()));
            // String[] header = getRecordFactory().createHeader(getNativeHeader(ri_iter.next()));
            // errors.add(header[0]);
            // identifiers.add(header[1]);
        }

        // Verifica se temos que informar um ResumptionToken
        if (numRows >= maxListSize) {
            String resumptionId = LexMLOAICatalog.getRSName();
            String lastId = getLastId(ri_list);
            InternalResumptionToken novoIRT = new InternalResumptionToken(irt, lastId, numRows);
            resumptionTokens.put(resumptionId, novoIRT);
            listIdentifiersMap.put("resumptionMap", getResumptionMap(resumptionId, irt.total, irt.offset));
        }

        listIdentifiersMap.put("errors", errors.iterator());

        return listIdentifiersMap;

    }

    // ###########################################################################################################################

    @Override
    public Map listRecords(final String from, final String until, final String set, final String metadataPrefix)
                                                                                                                throws CannotDisseminateFormatException,
                                                                                                                OAIInternalServerError,
                                                                                                                NoItemsMatchException {
        List<RegistroItem> ri_list = m_ri_dao.listByTimeWindowAndSet(toDate(from), toDate(until), set, null,
                                                                     maxListSize);
        int total = m_ri_dao.countListByTimeWindowAndSet(toDate(from), toDate(until), set);

        String lastId = getLastId(ri_list);
        InternalResumptionToken irt = new InternalResumptionToken(from, until, set, metadataPrefix, lastId,
                                                                  ri_list.size(), total);
        return internalListRecords(ri_list, irt);
    }

    private String getLastId(final List<RegistroItem> list) {
        if (!list.isEmpty()) {
            return list.get(list.size() - 1).getIdRegistroItem();
        }
        return null;
    }

    @Override
    public Map listRecords(final String resumptionId) throws BadResumptionTokenException {
        InternalResumptionToken irt = (InternalResumptionToken) resumptionTokens.get(resumptionId);
        if (null == irt) {
            throw new BadResumptionTokenException();
        }

        List<RegistroItem> ri_list = m_ri_dao.listByTimeWindowAndSet(toDate(irt.from), toDate(irt.until), irt.set,
                                                                     irt.lastId, maxListSize);

        try {
            return internalListRecords(ri_list, irt);
        }
        catch (CannotDisseminateFormatException e) {
            throw new BadResumptionTokenException();
        }
        catch (NoItemsMatchException e) {
            // A falta de itens indica o fim da paginação e não um erro.
        }
        return null;
    }

    private Map internalListRecords(final List<RegistroItem> ri_list, final InternalResumptionToken irt)
                                                                                                        throws CannotDisseminateFormatException,
                                                                                                        NoItemsMatchException {
        purge(); // clean out old resumptionTokens
        Map listRecordsMap = new HashMap();
        ArrayList records = new ArrayList();

        int numRows = ri_list.size();
        Iterator<RegistroItem> ri_iter = ri_list.iterator();

        if (numRows == 0) {
            throw new NoItemsMatchException();
        }

        while (ri_iter.hasNext()) {
            records.add(constructRecord(ri_iter.next(), irt.format));
        }
        // Verifica se temos que informar um ResumptionToken
        if (numRows >= maxListSize) {
            String resumptionId = LexMLOAICatalog.getRSName();
            String lastId = getLastId(ri_list);
            InternalResumptionToken novoIRT = new InternalResumptionToken(irt, lastId, numRows);
            resumptionTokens.put(resumptionId, novoIRT);
            listRecordsMap.put("resumptionMap", getResumptionMap(resumptionId, irt.total, irt.offset));
        }
        listRecordsMap.put("records", records.iterator());
        return listRecordsMap;

    }

    /**
     * Não faremos paginação de sets
     * <p/>
     * <b>LEXML ready</b>
     */
    @Override
    public Map listSets() throws NoSetHierarchyException {
        // throw new NoSetHierarchyException();
        if (sets.size() == 0) {
            throw new NoSetHierarchyException();
        }
        Map listSetsMap = new LinkedHashMap();
        listSetsMap.put("sets", sets.iterator());
        return listSetsMap;
    }

    /**
     * Nunca faremos paginação de sets
     * <p/>
     * <b>LEXML ready</b>
     */
    @Override
    public Map listSets(final String resumptionToken) throws BadResumptionTokenException {
        throw new BadResumptionTokenException();
    }

    /**
     * <b>LEXML ready</b>
     */
    private void prepare() {
        setHarvestable(false);
        if (null == m_ri_dao) {
            m_ri_dao = new RegistroItemDAO();
        }
        if (null == m_ci_dao) {
            m_ci_dao = new ConjuntoItemDAO();
        }
        if (null == sets) {
            sets = getSets();
        }
        if (null != m_ri_dao && null != m_ci_dao) {
            setHarvestable(true);
        }

    }

    /**
     * Purge tokens that are older than the time-to-live.
     * <p/>
     * <b>LEXML ready</b>
     */
    private void purge() {
        ArrayList old = new ArrayList();
        Date then, now = new Date();
        Iterator keySet = resumptionTokens.keySet().iterator();
        String key;

        while (keySet.hasNext()) {
            key = (String) keySet.next();
            then = new Date(Long.parseLong(key) + getMillisecondsToLive());
            if (now.after(then)) {
                old.add(key);
            }
        }
        Iterator iterator = old.iterator();
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            resumptionTokens.remove(key);
        }
    }

    private HashMap RegistroItem2NativeRecord(final RegistroItem ri) {
        HashMap recordMap = getNativeHeader(ri);
        if (recordMap != null) {
            // Remove diretiva xml
            String metadado = ri.getTxMetadadoXml();
            if (metadado.startsWith("<?xml")) {
                int i = metadado.indexOf("?>");
                if (i != -1) {
                    metadado = metadado.substring(i + 2);
                }
            }
            recordMap.put(NI.recordBytes, metadado);
            return recordMap;
        }
        return null;
    }

    /**
     * <p/>
     * <b>LEXML ready</b>
     * 
     * @param strDate
     * @return
     * @throws ParseException
     */
    private Date toDate(final String strDate) {
        if (StringUtils.isEmpty(strDate)) {
            return null;
        }
        try {
            if (strDate.length() == 10) {
                return sdfData.parse(strDate);
            }
            // Data e hora
            return sdfDataHora.parse(strDate);
        }
        catch (ParseException e) {
            throw new RuntimeException("Falha ao converter a data '" + strDate + "'", e);
        }
    }
}
