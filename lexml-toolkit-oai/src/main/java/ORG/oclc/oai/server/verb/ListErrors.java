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

package ORG.oclc.oai.server.verb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringUtils;

import ORG.oclc.oai.server.catalog.AbstractCatalog;
import ORG.oclc.oai.server.crosswalk.Crosswalks;

/**
 * Representa o verbo ListErrors. Adaptada de {@link ListIdentifiers}
 * 
 * @author Fragomeni
 * @see ListIdentifiers
 */
public class ListErrors extends ServerVerb {

    private static final boolean debug = false;

    private static ArrayList validParamNames1 = new ArrayList();
    static {
        validParamNames1.add("verb");
        validParamNames1.add("from");
        validParamNames1.add("until");
        validParamNames1.add("identifier");
        validParamNames1.add("set");
    }

    private static ArrayList validParamNames2 = new ArrayList();
    static {
        validParamNames2.add("verb");
        validParamNames2.add("resumptionToken");
    }

    private static ArrayList requiredParamNames1 = new ArrayList();
    static {
        requiredParamNames1.add("verb");
    }

    private static ArrayList requiredParamNames2 = new ArrayList();
    static {
        requiredParamNames2.add("verb");
        requiredParamNames2.add("resumptionToken");
    }

    public static String construct(HashMap context, HttpServletRequest request, HttpServletResponse response,
                                   Transformer serverTransformer) throws OAIInternalServerError,
        TransformerException {
        Properties properties = (Properties) context.get("OAIHandler.properties");
        AbstractCatalog abstractCatalog = (AbstractCatalog) context.get("OAIHandler.catalog");
        boolean xmlEncodeSetSpec = "true".equalsIgnoreCase(properties.getProperty("OAIHandler.xmlEncodeSetSpec"));
        boolean urlEncodeSetSpec = !"false"
                .equalsIgnoreCase(properties.getProperty("OAIHandler.urlEncodeSetSpec"));
        String baseURL = properties.getProperty("OAIHandler.baseURL");
        if (baseURL == null) {
            try {
                baseURL = request.getRequestURL().toString();
            }
            catch (java.lang.NoSuchMethodError f) {
                baseURL = HttpUtils.getRequestURL(request).toString();
            }
        }
        StringBuffer sb = new StringBuffer();
        String oldResumptionToken = request.getParameter("resumptionToken");
        // String metadataPrefix = request.getParameter("metadataPrefix");

        // if (metadataPrefix != null && metadataPrefix.length() == 0) {
        // metadataPrefix = null;
        // }

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        String styleSheet = properties.getProperty("OAIHandler.styleSheet");
        if (styleSheet != null) {
            sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"");
            sb.append(styleSheet);
            sb.append("\"?>");
        }
        sb.append("<OAI-PMH xmlns=\"http://www.openarchives.org/OAI/2.0/\"");
        sb.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        sb.append(" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/");
        sb.append(" http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">");
        sb.append("<responseDate>");
        sb.append(createResponseDate(new Date()));
        sb.append("</responseDate>");
        // sb.append("<requestURL>");
        // sb.append(getRequestURL(request));
        // sb.append("</requestURL>");

        if (!abstractCatalog.isHarvestable()) {
            sb.append("<request verb=\"ListErrors\">");
            sb.append(baseURL);
            sb.append("</request>");
            sb.append("<error code=\"badArgument\">Database is unavailable for harvesting</error>");
        }
        else {
            ArrayList validParamNames = null;
            ArrayList requiredParamNames = null;
            Map ListErrorsMap = null;
            if (oldResumptionToken == null) {
                validParamNames = validParamNames1;
                requiredParamNames = requiredParamNames1;
                String identifier = request.getParameter("identifier");
                String from = StringUtils.defaultString(request.getParameter("from")).trim();
                String until = StringUtils.defaultString(request.getParameter("until")).trim();
                try {
                    if (from.length() > 0 && from.length() < 10) {
                        throw new BadArgumentException();
                    }
                    if (until.length() > 0 && until.length() < 10) {
                        throw new BadArgumentException();
                    }
                    if (from.length() == 0) {
                        from = "0001-01-01";
                    }
                    if (until.length() == 0) {
                        until = "9999-12-31";
                    }
                    from = abstractCatalog.toFinestFrom(from);
                    until = abstractCatalog.toFinestUntil(until);
                    if (from.compareTo(until) > 0) {
                        throw new BadArgumentException();
                    }
                    String set = request.getParameter("set");
                    if (set != null) {
                        if (set.length() == 0) {
                            set = null;
                        }
                        else if (urlEncodeSetSpec) {
                            set = set.replace(' ', '+');
                        }
                    }
                    Crosswalks crosswalks = abstractCatalog.getCrosswalks();
                    // if (metadataPrefix == null) {
                    // throw new BadArgumentException();
                    // }

                    // if (!crosswalks.containsValue(metadataPrefix)) {
                    // throw new CannotDisseminateFormatException(metadataPrefix);
                    // }
                    // else {
                    ListErrorsMap = abstractCatalog.listErrors(from, until, identifier, set);
                    // }
                }
                catch (NoItemsMatchException e) {
                    sb.append(getRequestElement(request, validParamNames, baseURL, xmlEncodeSetSpec));
                    sb.append(e.getMessage());
                }
                catch (BadArgumentException e) {
                    sb.append("<request verb=\"ListErrors\">");
                    // sb.append(HttpUtils.getRequestURL(request));
                    sb.append(baseURL);
                    sb.append("</request>");
                    sb.append(e.getMessage());
                    // } catch (BadGranularityException e) {
                    // sb.append(getRequestElement(request));
                    // sb.append(e.getMessage());
                }
                catch (CannotDisseminateFormatException e) {
                    sb.append(getRequestElement(request, validParamNames, baseURL, xmlEncodeSetSpec));
                    sb.append(e.getMessage());
                }
                catch (NoSetHierarchyException e) {
                    sb.append(getRequestElement(request, validParamNames, baseURL, xmlEncodeSetSpec));
                    sb.append(e.getMessage());
                }
            }
            else {
                validParamNames = validParamNames2;
                requiredParamNames = requiredParamNames2;
                if (hasBadArguments(request, requiredParamNames.iterator(), validParamNames)) {
                    sb.append(getRequestElement(request, validParamNames, baseURL, xmlEncodeSetSpec));
                    sb.append(new BadArgumentException().getMessage());
                }
                else {
                    try {
                        ListErrorsMap = abstractCatalog.listErrors(oldResumptionToken);
                    }
                    catch (BadResumptionTokenException e) {
                        sb.append(getRequestElement(request, validParamNames, baseURL, xmlEncodeSetSpec));
                        sb.append(e.getMessage());
                    }
                }
            }

            if (ListErrorsMap != null) {
                sb.append(getRequestElement(request, validParamNames, baseURL, xmlEncodeSetSpec));
                if (hasBadArguments(request, requiredParamNames.iterator(), validParamNames)) {
                    sb.append(new BadArgumentException().getMessage());
                }
                else {
                    sb.append("<ListErrors>");
                    Iterator errors = (Iterator) ListErrorsMap.get("errors");
                    while (errors.hasNext()) {
                        sb.append((String) errors.next());
                        sb.append("\n");
                    }

                    Map newResumptionMap = (Map) ListErrorsMap.get("resumptionMap");
                    if (newResumptionMap != null) {
                        String newResumptionToken = (String) newResumptionMap.get("resumptionToken");
                        String expirationDate = (String) newResumptionMap.get("expirationDate");
                        String completeListSize = (String) newResumptionMap.get("completeListSize");
                        String cursor = (String) newResumptionMap.get("cursor");
                        sb.append("<resumptionToken");
                        if (expirationDate != null) {
                            sb.append(" expirationDate=\"");
                            sb.append(expirationDate);
                            sb.append("\"");
                        }
                        if (completeListSize != null) {
                            sb.append(" completeListSize=\"");
                            sb.append(completeListSize);
                            sb.append("\"");
                        }
                        if (cursor != null) {
                            sb.append(" cursor=\"");
                            sb.append(cursor);
                            sb.append("\"");
                        }
                        sb.append(">");
                        sb.append(newResumptionToken);
                        sb.append("</resumptionToken>");
                    }
                    else if (oldResumptionToken != null) {
                        sb.append("<resumptionToken />");
                    }
                    sb.append("</ListErrors>");
                }
            }
        }
        sb.append("</OAI-PMH>");
        if (debug) {
            System.out.println("ListErrors.construct: returning: " + sb.toString());
        }
        return render(response, "text/xml; charset=UTF-8", sb.toString(), serverTransformer);
    }
}
