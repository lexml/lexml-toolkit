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
package br.gov.lexml.oaicat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import ORG.oclc.oai.server.catalog.RecordFactory;

/*
 * NewFileRecordFactory converts native XML "items" to "record" Strings.
 * This factory assumes the native XML item looks exactly like the <record>
 * element of an OAI GetRecord response, with the possible exception that the
 * <metadata> element contains multiple metadataFormats from which to choose.
 */
/**
 * LexMLOAIRecodFactory provê os registros em formatos Strings prontos para serem incluídos em uma resposta OAI-PMH
 * 
 */
public class LexMLOAIRecordFactory extends RecordFactory implements LexMLOAI {

	private String repositoryIdentifier = null;

	/**
	 * Construct an NewFileRecordFactory capable of producing the Crosswalk(s) specified in the properties file.
	 * 
	 * @param properties
	 *           Contains information to configure the factory: specifically, the names of the crosswalk(s) supported
	 * @exception IllegalArgumentException
	 *               Something is wrong with the argument.
	 */
	public LexMLOAIRecordFactory(Properties properties) throws IllegalArgumentException {
		super(properties);

		// Não precisamos do repositoryIdentifier pois ele é gravado nos registros diretamente.
		// repositoryIdentifier = properties.getProperty("LexMLOAIRecordFactory.repositoryIdentifier");
		// if (repositoryIdentifier == null) {
		// throw new IllegalArgumentException("LexMLOAIRecordFactory.repositoryIdentifier is missing from the properties file");
		// }
	}

	/**
	 * Utility method to parse the 'local identifier' from the OAI identifier
	 * <p/>
	 * <b>LEXML ready</b>
	 * 
	 * @param identifier
	 *           OAI identifier (e.g. oai:oaicat.oclc.org:ID/12345)
	 * @return local identifier (e.g. ID/12345).
	 */
	public String fromOAIIdentifier(String identifier) {
		if (null == repositoryIdentifier)
			return identifier;

		try {
			StringTokenizer tokenizer = new StringTokenizer(identifier, ":");
			tokenizer.nextToken();
			tokenizer.nextToken();
			return tokenizer.nextToken();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Construct an OAI identifier from the native item
	 * <p/>
	 * <b>LEXML ready</b>
	 * 
	 * @param nativeItem
	 *           native Item object
	 * @return OAI identifier
	 */
	public String getOAIIdentifier(Object nativeItem) {
		if (null == repositoryIdentifier)
			return getLocalIdentifier(nativeItem);

		StringBuffer sb = new StringBuffer();
		sb.append("oai:");
		sb.append(repositoryIdentifier);
		sb.append(":");
		sb.append(getLocalIdentifier(nativeItem));
		return sb.toString();
	}

	/**
	 * Extract the local identifier from the native item
	 * <p/>
	 * <b>LEXML ready</b>
	 * 
	 * @param nativeItem
	 *           native Item object
	 * @return local identifier
	 */
	public String getLocalIdentifier(Object nativeItem) {
		return (String) ((HashMap) nativeItem).get(NI.localIdentifier);
	}

	/**
	 * get the datestamp from the item
	 * <p/>
	 * <b>LEXML ready</b>
	 * 
	 * @param nativeItem
	 *           a native item presumably containing a datestamp somewhere
	 * @return a String containing the datestamp for the item
	 * @exception IllegalArgumentException
	 *               Something is wrong with the argument.
	 */
	public String getDatestamp(Object nativeItem) throws IllegalArgumentException {
		return (String) ((HashMap) nativeItem).get(NI.lastModified);
	}

	/**
	 * get the setspec from the item
	 * <p/>
	 * <b>LEXML ready</b>
	 * 
	 * @param nativeItem
	 *           a native item presumably containing a setspec somewhere
	 * @return a String containing the setspec for the item
	 * @exception IllegalArgumentException
	 *               Something is wrong with the argument.
	 */
	public Iterator getSetSpecs(Object nativeItem) throws IllegalArgumentException {
		return (Iterator) ((HashMap) nativeItem).get(NI.setSpecs);
	}

	/**
	 * Get the about elements from the item
	 * 
	 * <p/>
	 * <b>LEXML ready</b>
	 * 
	 * @param nativeItem
	 *           a native item presumably containing about information somewhere
	 * @return a Iterator of Strings containing &lt;about&gt;s for the item
	 * @exception IllegalArgumentException
	 *               Something is wrong with the argument.
	 */
	public Iterator getAbouts(Object nativeItem) throws IllegalArgumentException {
		return null;
	}

	/**
	 * Is the record deleted?
	 * 
	 * <p/>
	 * <b>LEXML ready</b>
	 * 
	 * @param nativeItem
	 *           a native item presumably containing a possible delete indicator
	 * @return true if record is deleted, false if not
	 * @exception IllegalArgumentException
	 *               Something is wrong with the argument.
	 */
	public boolean isDeleted(Object nativeItem) throws IllegalArgumentException {
		String status = (String) ((HashMap) nativeItem).get(NI.status);

		if (null != status && "deleted".equalsIgnoreCase(status))
			return true;
		return false;
	}

	/**
	 * Allows classes that implement RecordFactory to override the default create() method. This is useful, for example, if the entire &lt;record&gt; is already packaged
	 * as the native record. Return null if you want the default handler to create it by calling the methods above individually.
	 * 
	 * <p/>
	 * <b>LEXML ready</b>
	 * 
	 * @param nativeItem
	 *           the native record
	 * @param schemaURL
	 *           the schemaURL desired for the response
	 * @param the
	 *           metadataPrefix from the request
	 * @return a String containing the OAI &lt;record&gt; or null if the default method should be used.
	 */
	public String quickCreate(Object nativeItem, String schemaLocation, String metadataPrefix) {
		return null;
	}
}
