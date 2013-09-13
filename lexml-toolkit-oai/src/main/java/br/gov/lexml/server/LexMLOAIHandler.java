/**
 * Copyright 2006 OCLC Online Computer Library Center Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or
 * agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.gov.lexml.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import ORG.oclc.oai.server.catalog.AbstractCatalog;
import ORG.oclc.oai.server.verb.OAIInternalServerError;
import ORG.oclc.oai.server.verb.ServerVerb;

import br.gov.lexml.borda.dao.EMFFactory;
import br.gov.lexml.borda.dao.JPAUtil;
import br.gov.lexml.exceptions.ConfigFailedException;
import br.gov.lexml.oaicat.LexMLOAI;

/**
 * OAIHandler is the primary Servlet for OAICat.
 * 
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 */
public class LexMLOAIHandler extends HttpServlet {

    private static final Logger log = Logger.getLogger(LexMLOAIHandler.class);

    private static final long serialVersionUID = 1L;

    public static final String PROPERTIES_SERVLET_CONTEXT_ATTRIBUTE = LexMLOAIHandler.class.getName()
                                                                      + ".properties";

    private static boolean debug = log.isDebugEnabled();

    protected HashMap attributesMap = new HashMap();

    private String version;
    private String build;

    /**
     * Get the VERSION number
     */
    private String getVersion() {
    	if(version.contains("SNAPSHOT")) {
    		return version + ", build " + build;
    	}
    	return version;
    }

    /**
     * init is called one time when the Servlet is loaded. This is the place where one-time
     * initialization is done. Specifically, we load the properties file for this application, and
     * create the AbstractCatalog object for subsequent use.
     * 
     * @param config servlet configuration information
     * @exception ServletException there was a problem with initialization
     */
    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);

        getBuildVersion();

        try {
            // Trecho para inicializar o layer lexml para provimento
            // dos registros
            try {
                LexMLOAI.helper.inicializar();
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
                throw new ServletException(e.getMessage());
            }
            catch (Throwable e) {
                e.printStackTrace();
                throw new ServletException(e.getMessage());
            }
            // fim inicializacao do LexmL
            HashMap attributes = null;
            ServletContext context = getServletContext();
            Properties properties = (Properties) context.getAttribute(PROPERTIES_SERVLET_CONTEXT_ATTRIBUTE);
            if (properties == null) {
                final String PROPERTIES_INIT_PARAMETER = "properties";
                log.debug("OAIHandler.init(..): No '" + PROPERTIES_SERVLET_CONTEXT_ATTRIBUTE
                          + "' servlet context attribute. Trying to use init parameter '"
                          + PROPERTIES_INIT_PARAMETER + "'");

                InputStream in = getClass().getResourceAsStream("/oaicat.properties");
                if (in != null) {
                    properties = new Properties();
                    properties.load(in);
                    attributes = getAttributes(properties);
                }
                else {
                    log.error("Arquivo oaicat.properties nao encontrado.");
                }
            }
            else {
                log.debug("Load context properties");
                attributes = getAttributes(properties);
            }

            log.debug("Store global properties");
            attributesMap.put("global", attributes);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new ServletException(e.getMessage());
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new ServletException(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new ServletException(e.getMessage());
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new ServletException(e.getMessage());
        }
        catch (Throwable e) {
            e.printStackTrace();
            throw new ServletException(e.getMessage());
        }
        finally {
            JPAUtil.closeEntityManager();
        }
    }

    public HashMap getAttributes(final Properties properties) throws Throwable {
        HashMap attributes = new HashMap();
        Enumeration attrNames = getServletContext().getAttributeNames();
        while (attrNames.hasMoreElements()) {
            String attrName = (String) attrNames.nextElement();
            attributes.put(attrName, getServletContext().getAttribute(attrName));
        }
        attributes.put("OAIHandler.properties", properties);
        // String temp = properties.getProperty("OAIHandler.debug");
        // if ("true".equals(temp)) debug = true;
        String missingVerbClassName = properties.getProperty("OAIHandler.missingVerbClassName",
                                                             "ORG.oclc.oai.server.verb.BadVerb");
        Class missingVerbClass = Class.forName(missingVerbClassName);
        attributes.put("OAIHandler.missingVerbClass", missingVerbClass);
        if (!"true".equals(properties.getProperty("OAIHandler.serviceUnavailable"))) {
            attributes.put("OAIHandler.version", getVersion());
            AbstractCatalog abstractCatalog = AbstractCatalog.factory(properties, getServletContext());
            attributes.put("OAIHandler.catalog", abstractCatalog);
        }
        boolean forceRender = false;
        if ("true".equals(properties.getProperty("OAIHandler.forceRender"))) {
            forceRender = true;
        }
        String xsltName = properties.getProperty("OAIHandler.styleSheet");
        String appBase = properties.getProperty("OAIHandler.appBase");
        if (appBase == null) {
            appBase = "webapps";
        }
        if (xsltName != null
            && ("true".equalsIgnoreCase(properties.getProperty("OAIHandler.renderForOldBrowsers")) || forceRender)) {
            InputStream is;
            try {
                is = new FileInputStream(appBase + "/" + xsltName);
            }
            catch (FileNotFoundException e) {
                // This is a silly way to skip the context name in the xsltName
                is = new FileInputStream(getServletContext()
                        .getRealPath(xsltName.substring(xsltName.indexOf("/", 1) + 1)));
            }
            StreamSource xslSource = new StreamSource(is);
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer(xslSource);
            attributes.put("OAIHandler.transformer", transformer);
        }
        return attributes;
    }

    public HashMap getAttributes(final String pathInfo) {
        HashMap attributes = null;
        log.debug("pathInfo=" + pathInfo);
        if (pathInfo != null && pathInfo.length() > 0) {
            if (attributesMap.containsKey(pathInfo)) {
                log.debug("attributesMap containsKey");
                attributes = (HashMap) attributesMap.get(pathInfo);
            }
            else {
                log.debug("!attributesMap containsKey");
                try {
                    String fileName = pathInfo.substring(1) + ".properties";
                    log.debug("attempting load of " + fileName);
                    InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
                    if (in != null) {
                        log.debug("file found");
                        Properties properties = new Properties();
                        properties.load(in);
                        attributes = getAttributes(properties);
                    }
                    else {
                        log.debug("file not found");
                    }
                    attributesMap.put(pathInfo, attributes);
                }
                catch (Throwable e) {
                    log.debug("Couldn't load file", e);
                    // do nothing
                }
            }
        }
        if (attributes == null) {
            log.debug("use global attributes");
        }
        attributes = (HashMap) attributesMap.get("global");
        return attributes;
    }

    /**
     * Peform the http GET action. Note that POST is shunted to here as well. The verb widget is
     * taken from the request and used to invoke an OAIVerb object of the corresponding kind to do
     * the actual work of the verb.
     * 
     * @param request the servlet's request information
     * @param response the servlet's response information
     * @exception IOException an I/O error occurred
     */
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        // Força geração do cookie de sessão
        // (necessário para balanceamento de carga pelo pound do Prodasen)
        request.getSession();

        HashMap attributes = getAttributes(request.getPathInfo());
        if (!filterRequest(request, response)) {
            return;
        }
        log.debug("attributes = " + attributes);
        Properties properties = (Properties) attributes.get("OAIHandler.properties");
        boolean monitor = false;
        if (properties.getProperty("OAIHandler.monitor") != null) {
            monitor = true;
        }
        boolean serviceUnavailable = isServiceUnavailable(properties);
        String extensionPath = properties.getProperty("OAIHandler.extensionPath", "/extension");

        HashMap serverVerbs = ServerVerb.getVerbs(properties);
        HashMap extensionVerbs = ServerVerb.getExtensionVerbs(properties);

        Transformer transformer = (Transformer) attributes.get("OAIHandler.transformer");

        boolean forceRender = false;
        if ("true".equals(properties.getProperty("OAIHandler.forceRender"))) {
            forceRender = true;
        }

        request.setCharacterEncoding("UTF-8");

        Date then = null;
        if (monitor) {
            then = new Date();
        }
        if (debug) {
            Enumeration headerNames = request.getHeaderNames();
            log.debug("OAIHandler.doGet: ");
            while (headerNames.hasMoreElements()) {
                String headerName = (String) headerNames.nextElement();
                log.debug(headerName + ": " + request.getHeader(headerName));
            }
        }
        if (serviceUnavailable) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                               "Sorry. This server is down for maintenance");
        }
        else {
            try {
                String userAgent = request.getHeader("User-Agent");
                if (userAgent == null) {
                    userAgent = "";
                }
                else {
                    userAgent = userAgent.toLowerCase();
                }
                Transformer serverTransformer = null;
                if (transformer != null) {

                    // return HTML if the client is an old browser
                    if (forceRender || userAgent.indexOf("opera") != -1 || userAgent.startsWith("mozilla")
                        && userAgent.indexOf("msie 6") == -1
                    /* && userAgent.indexOf("netscape/7") == -1 */) {
                        serverTransformer = transformer;
                    }
                }
                String result = LexMLOAIHandler.getResult(attributes, request, response, serverTransformer,
                                                          serverVerbs, extensionVerbs, extensionPath);

                Writer out = LexMLOAIHandler.getWriter(request, response);
                out.write(result);
                out.flush();
                IOUtils.closeQuietly(out);
            }
            catch (FileNotFoundException e) {
                log.error("Falha no processamento.", e);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            }
            catch (Throwable e) {
                log.error("Falha no processamento.", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
        if (monitor) {
            StringBuffer reqUri = new StringBuffer(request.getRequestURI());
            String queryString = request.getQueryString(); // d=789
            if (queryString != null) {
                reqUri.append("?").append(queryString);
            }
            Runtime rt = Runtime.getRuntime();
            log.debug(rt.freeMemory() + "/" + rt.totalMemory() + " " + (new Date().getTime() - then.getTime())
                      + "ms: " + reqUri.toString());
        }
    }

    /**
     * Should the server report itself down for maintenance? Override this method if you want to do
     * this check another way.
     * 
     * @param properties
     * @return true=service is unavailable, false=service is available
     */
    protected boolean isServiceUnavailable(final Properties properties) {
        if (properties.getProperty("OAIHandler.serviceUnavailable") != null) {
            return true;
        }
        return false;
    }

    /**
     * Override to do any prequalification; return false if the response should be returned
     * immediately, without further action.
     * 
     * @param request
     * @param response
     * @return false=return immediately, true=continue
     */
    protected boolean filterRequest(final HttpServletRequest request, final HttpServletResponse response) {
        return true;
    }

    public static String getResult(final HashMap attributes, final HttpServletRequest request,
                                   final HttpServletResponse response, final Transformer serverTransformer,
                                   final HashMap serverVerbs, final HashMap extensionVerbs,
                                   final String extensionPath) throws Throwable {
        try {
            boolean isExtensionVerb = extensionPath.equals(request.getPathInfo());
            String verb = request.getParameter("verb");
            if (debug) {
                log.debug("OAIHandler.getResult: verb=>" + verb + "<");
            }
            String result;
            Class verbClass = null;
            if (isExtensionVerb) {
                verbClass = (Class) extensionVerbs.get(verb);
            }
            else {
                verbClass = (Class) serverVerbs.get(verb);
            }
            if (verbClass == null) {
                verbClass = (Class) attributes.get("OAIHandler.missingVerbClass");
            }
            Method construct = verbClass.getMethod("construct", new Class[]{ HashMap.class,
                                                                            HttpServletRequest.class,
                                                                            HttpServletResponse.class,
                                                                            Transformer.class });
            try {
                result = (String) construct.invoke(null, new Object[]{ attributes, request, response,
                                                                      serverTransformer });
            }
            catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
            if (debug) {
                log.debug(result);
            }
            return result;
        }
        catch (NoSuchMethodException e) {
            throw new OAIInternalServerError(e.getMessage());
        }
        catch (IllegalAccessException e) {
            throw new OAIInternalServerError(e.getMessage());
        }
    }

    /**
     * Get a response Writer depending on acceptable encodings
     * 
     * @param request the servlet's request information
     * @param response the servlet's response information
     * @exception IOException an I/O error occurred
     */
    public static Writer getWriter(final HttpServletRequest request, final HttpServletResponse response)
                                                                                                        throws IOException {
        Writer out;
        String encodings = request.getHeader("Accept-Encoding");
        if (debug) {
            log.debug("encodings=" + encodings);
        }
        if (encodings != null && encodings.indexOf("gzip") != -1) {
            response.setHeader("Content-Encoding", "gzip");
            out = new OutputStreamWriter(new GZIPOutputStream(response.getOutputStream()), "UTF-8");
        }
        else if (encodings != null && encodings.indexOf("deflate") != -1) {
            response.setHeader("Content-Encoding", "deflate");
            out = new OutputStreamWriter(new DeflaterOutputStream(response.getOutputStream()), "UTF-8");
        }
        else {
            out = response.getWriter();
        }
        return out;
    }

    /**
     * Peform a POST action. Actually this gets shunted to GET
     * 
     * @param request the servlet's request information
     * @param response the servlet's response information
     * @exception IOException an I/O error occurred
     */
    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        doGet(request, response);
    }

    @Override
    public void destroy() {
        super.destroy();

        try {
            EMFFactory.getEMF().close();
        }
        catch (ConfigFailedException e) {
            log.error("Falha ao fechar o EntityManagerFactory.", e);
        }
    }

    private void getBuildVersion() {
        ManifestEditor me;
        try {
            me = new ManifestEditor(getServletContext());
            version = me.getVersao();
            build = me.getBuild();
        }
        catch (IOException e) {
            log.error("Falha na leitura do MANIFEST.MF", e);
        }
    }

}
