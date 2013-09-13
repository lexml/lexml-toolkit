package br.gov.lexml.oaicat;

import org.apache.log4j.Logger;

import ORG.oclc.oai.server.verb.OAIInternalServerError;

import br.gov.lexml.LexMLConfig;
import br.gov.lexml.borda.domain.ConjuntoItem;
import br.gov.lexml.exceptions.ConfigFailedException;

public interface LexMLOAI {

	/**
	 * NativeItem
	 * 
	 */
	public interface NI {
		public static final String localIdentifier = "localIdentifier";
		public static final String lastModified = "lastModified";
		public static final String setSpecs = "setSpecs";
		public static final String status = "status";
		public static final String recordBytes = "recordBytes";
	}

	public abstract class helper {
		private static Logger logger = Logger.getLogger(LexMLOAI.class.getName());
		private static LexMLConfig config = null;

		public static final void inicializar() throws OAIInternalServerError {
//			try {
//				ToolKitHelper.initLog4J();
//			} catch (IOException e2) {
//				e2.printStackTrace();
//			}
			try {
				config = LexMLConfig.getInstance();
			} catch (ConfigFailedException e) {
				logger.error(e);
				e.printStackTrace();
				throw new OAIInternalServerError("Erro ao carregar arquivo de configuração.");
			}
		}

		public static final String ConjuntoItem2Sets(final ConjuntoItem ci) {
			if (null != ci) {
				String setSpec = ci.getIdConjuntoItem();
				String setName = ci.getDeConjuntoItem();
				if (null != setSpec && null != setName) {
					return "<set><setSpec>".concat(setSpec).concat("</setSpec><setName>").concat(setName).concat("</setName></set>");
				}
			}
			return null;
		}

		public static void writePerfil(final StringBuffer p_sb) {
			config.writePerfil(p_sb);
		}
	}
}
