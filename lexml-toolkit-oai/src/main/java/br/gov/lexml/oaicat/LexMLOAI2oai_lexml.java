/**
 * This is a derivative work based on OCLC Oaicat software
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

import java.util.HashMap;
import java.util.Properties;

import ORG.oclc.oai.server.crosswalk.Crosswalk;
import br.gov.lexml.LexMLSystem;
import br.gov.lexml.oaicat.LexMLOAI.NI;

/**
 * Fornece o registro em oai_lexml
 * 
 */
public class LexMLOAI2oai_lexml extends Crosswalk {

	public LexMLOAI2oai_lexml(Properties properties) {
		super(LexMLSystem.OAI_LEXML_NAMESPACE.concat(" ").concat(LexMLSystem.OAI_LEXML_LOCATION_SCHEMA));

	}

	/**
	 * Informa se o registro neste formato de metadado pode ser fornecido em oai_dc
	 */
	public boolean isAvailableFor(Object nativeItem) {
		return true; // all records must support oai_dc according to the OAI spec.
	}

	/**
	 * Retorna a sequencia UTF-8 do xml armazenado em banco. 
	 */
	public String createMetadata(Object nativeItem) {
		HashMap table = (HashMap) nativeItem;
		return (String) table.get(NI.recordBytes);
	}
}
