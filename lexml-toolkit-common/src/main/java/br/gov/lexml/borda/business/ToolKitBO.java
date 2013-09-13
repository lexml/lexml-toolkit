package br.gov.lexml.borda.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import br.gov.lexml.LexMLConfig;
import br.gov.lexml.borda.dao.LexMLDAO;
import br.gov.lexml.borda.dao.LexMLDAO.CdValidacao;
import br.gov.lexml.borda.dao.RegistroItemDAO;
import br.gov.lexml.borda.domain.RegistroItem;
import br.gov.lexml.exceptions.ConfigFailedException;

public class ToolKitBO extends AbstractBO {

	private static Logger logger = Logger.getLogger(ToolKitBO.class.getName());
	private static final int TAMANHO_LISTA = 300;
	private RegistroItemDAO registroItemDao;
	private List<String> arquivos = new ArrayList<String>();
	private boolean delete_imported_files = false;
	private int arquivos_processados = 0;
	private int sucessos = 0;
	private int falhas = 0;
	private final String exportarSQLNomeArquivo = "lexml_registro_item_dump.sql";

	// Para recuperação de falhas ao importar
	List<RegistroItem> toSave = new ArrayList<RegistroItem>();

	private int gcCount;

	/**
	 * Utilize um dos modos de inicializacao a partir de LexMLConfig.MODE_*
	 *
	 * @see LexMLConfig
	 * @param p_modo
	 * @throws ConfigFailedException
	 */
	public ToolKitBO() throws ConfigFailedException {
		init();
	}

	/**
	 * Utilize um dos modos de inicializacao a partir de LexMLConfig.MODE_*
	 *
	 * @see LexMLConfig
	 * @throws ConfigFailedException
	 */
	public void init() throws ConfigFailedException {
		if (null == registroItemDao) {
			registroItemDao = new RegistroItemDAO();
		}

	}

	/**
	 * Recebe a localização e nome do arquivo XML contendo o registro em formato
	 * oai_lexml gravado pelo JOAI, em seguida processa o registro extraído do
	 * arquivo e realiza a atualização/inserção no banco de dados do agregador /
	 * nodo central
	 *
	 * @param p_path
	 * @param p_arquivo
	 * @throws IOException
	 */
	private void processarArquivo(final String p_path, final String p_arquivo,
			boolean full) throws IOException {

		File arquivo = new File(p_path + File.separator + p_arquivo);

		// DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");//
		// 2001-12-17T09:30:47.0Z
		// format.setTimeZone(TimeZone.getTimeZone("GMT"));
		boolean sucesso = false;

		if (arquivo.isFile() && arquivo.canRead()) {

			try {
				// Extrai o id
				String id = URLDecoder.decode(p_arquivo, "UTF-8");
				id = id.substring(0, id.length() - 4); // Retira ".xml" do final

				boolean insert = true;
				Date tsBanco = registroItemDao.getTsRegistroGmt(id);
				if (tsBanco != null) {
					// Verifica se já foi carregado
					if (!full && arquivo.lastModified() < tsBanco.getTime()) {
						return;
					}
					insert = false;
				}

				if (logger.isDebugEnabled()) {
					logger.debug("Extraido o Identifier OAI:" + id
							+ " do arquivo: " + p_arquivo + " no diretorio "
							+ p_path);
				}

				RegistroItem ri = new RegistroItem();
				ri.setIdRegistroItem(id);

				// Obtém conteúdo do arquivo
				FileInputStream fis = new FileInputStream(arquivo);
				String xml = IOUtils.toString(fis);
				IOUtils.closeQuietly(fis);
				ri.setTxMetadadoXml(xml);

				ri.setCdStatus(LexMLDAO.CdStatus.DEFAULT_CD_STATUS);
				ri.setCdValidacao(LexMLDAO.CdValidacao.DEFAULT_VALIDACAO_STATUS);

				if (insert) {
					registroItemDao.save(ri);
				} else {
					registroItemDao.update(ri);
				}
				toSave.add(ri);

				arquivos_processados++;
				logger.info("Arquivo " + p_arquivo + " processado com sucesso.");
				checkListsSizes(TAMANHO_LISTA);

				sucessos++;

				sucesso = true;
			} catch (Throwable t) {
				logger.error("Arquivo " + p_arquivo
						+ " falhou durante processamento.", t);
				falhas++;

				recuperaDeFalha();
			}

			if (sucesso) {
				deletar(arquivo.getCanonicalPath(), TAMANHO_LISTA);
			}
		}
	}

	private void recuperaDeFalha() {

		registroItemDao.rollback();

		registroItemDao.beginTransaction();

		if (toSave.isEmpty()) {
			return;
		}

		// Salva os registros que estavam ok cuja transação foi desfeita.
		for (RegistroItem ri : toSave) {
			registroItemDao.saveOrUpdate(ri);
		}

		commitAndBeginTransaction();
	}

	/**
	 * Verifica se a lista de arquivos processados é menor que o parametro
	 * passado Se for maior ou igual ela é esvaziada no banco de dados e
	 * "comitada".
	 *
	 * @param tamanhoLista
	 */
	private void checkListsSizes(final int tamanhoLista) {
		if (arquivos_processados >= tamanhoLista) {
			logger.debug("Commit de " + arquivos_processados
					+ " registros importados");
			commitAndBeginTransaction();
		}
	}

	private void commitAndBeginTransaction() {
		registroItemDao.commit();
		registroItemDao.clear();
		toSave.clear();
		limpaMemoria();
		registroItemDao.beginTransaction();
		arquivos_processados = 0;
	}

	private void limpaMemoria() {
		if (gcCount > 50) {
			System.gc();
			gcCount = 0;
		} else {
			gcCount++;
		}
	}

	/**
	 * Metodo Temporario, a ser refatorado ou retirado no futuro
	 */
	public void geraTests(final int p_i, final int p_j) {
		String id = "oai:novoteste.br:";
		int k = 0;
		registroItemDao.beginTransaction();
		for (int i = p_i; i < p_j; i++) {
			k++;
			RegistroItem ri = new RegistroItem();
			ri.setConjuntoItem(null);
			ri.setIdRegistroItem(id + i);
			ri.setTxMetadadoXml("<lexml:LexML xsi:schemaLocation=\"http://projeto.lexml.gov.br/esquemas/oai_lexml.xsd\" xmlns:lexml=\"http://www.lexml.gov.br/oai_lexml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
					+ "  <Item formato=\"text/html\" idPublicador=\"1\" tipo=\"conteudo\"> http://www6.senado.gov.br/legislacao/ListaPublicacoes.action?id=132554 </Item>"
					+ "  <Item formato=\"text/html\" idPublicador=\"1\" tipo=\"metadado\"> http://www6.senado.gov.br/legislacao/DetalhaDocumento.action?id=132554 </Item>"
					+ "  <DocumentoIndividual>urn:lex:br:federal:lei:1988-12-02;"
					+ i
					+ "@inicio.vigencia;publicacao;1988-12-05~texto;pt-br</DocumentoIndividual>"
					+ "  <Epigrafe>Lei nº 7.682, de 02 de dezembro de 1988</Epigrafe>"
					+ "  <Ementa>ALTERA O DECRETO-LEI 2.406, DE 5 DE JANEIRO DE 1988, E DA OUTRAS PROVIDENCIAS.</Ementa>"
					+
					// "  <Relacionamento tipo=\"publicacao.oficial\">urn:lex:br:imprensa.nacional:publicacao.oficial;diario.oficial.uniao:1988-12-05@1988-12-05;publicacao;1988-12-05~texto;pt-br</Relacionamento>"
					// +
					"</lexml:LexML>");
			ri.setCdStatus("N");
			ri.setCdValidacao("I");
			registroItemDao.saveOrUpdate(ri);
			if (k > 1999) {
				registroItemDao.commit();
				registroItemDao.beginTransaction();
				k = 0;
			}
		}
		registroItemDao.commit();

	}

	/**
	 * Recebe o nome de um arquivo e o tamanho limite para a lista de deleção.
	 * Se a lista chegar ou superar o tamanho passado então todos os arquivos da
	 * lista são deletados e a lista é zerada.
	 *
	 * @param p_arquivo
	 * @param p_tam
	 */
	private void deletar(final String p_arquivo, final int p_tam) {
		if (!delete_imported_files) {
			return;
		}

		if (p_arquivo != null) {
			arquivos.add(p_arquivo);
		}

		if (arquivos.size() >= p_tam || p_arquivo == null) {
			System.out.println("Apagando " + arquivos.size() + " arquivo(s)");
			for (int i = 0; i < arquivos.size(); i++) {
				String nome_arquivo = arquivos.get(i);
				logger.info("Apagando o arquivo " + i + " " + nome_arquivo);
				File arquivo = new File(nome_arquivo);
				if (!arquivo.delete()) {
					logger.error("Falha na deleção.");
				}
			}
			arquivos = new ArrayList<String>();
		}
	}

	/**
	 * Processa o diretório e diretórios internos a procura de registros com a
	 * extensão `.xml`
	 *
	 * @param p_dir
	 * @throws IOException
	 */
	private void processarDiretorio(final String p_dir, boolean full)
			throws IOException {
		File dir = new File(p_dir);

		if (dir.isDirectory()) {
			String[] children = dir.list();
			if (children == null) {
				logger.info("Diretório não pode ser acessado ou está vazio");
			} else {
				List<String> fileNames = new ArrayList<String>(
						Arrays.asList(children));
				Collections.sort(fileNames);

				for (String fileName : fileNames) {
					if (fileName.endsWith(".xml")) {
						processarArquivo(p_dir, fileName, full);
					} else {
						processarDiretorio(p_dir + File.separator + fileName,
								full);
					}
				}
			}
		}
	}

	/**
	 * Varre toda a estrutura de arquivos, pastas e subpastas a procura de
	 * arquivos XML <br/>
	 * para serem inseridos no banco de dados se p_delete_imported_files for
	 * true <br/>
	 * os arquivos importados com sucesso serão apagados.
	 *
	 * @param p_dir
	 * @param p_delete_imported_files
	 * @throws IOException
	 */
	public void consumirPasta(final String p_dir,
			final boolean p_delete_imported_files, boolean full)
			throws IOException {

		registroItemDao.beginTransaction();
		toSave.clear();

		sucessos = 0;
		falhas = 0;
		delete_imported_files = p_delete_imported_files;
		logger.debug("Iniciando o processamento do diretório " + p_dir);
		if (delete_imported_files) {
			logger.info("Os arquivos importados com sucesso serão apagados ao final do processamento");
		}

		processarDiretorio(p_dir, full);
		checkListsSizes(0);
		registroItemDao.commit();

		toSave.clear();

		deletar(null, 0);
		logger.info("---------------------------------------");
		logger.info("Foram importados " + sucessos + " registros com sucesso.");
		logger.info("Foram importados " + falhas + " registros com falha.");
	}

	public void exportarParaPasta(final String p_dir) throws Exception {

		File dir = new File(p_dir);
		final String INICIO_XML = "<?xml";
		if (!dir.isDirectory()) {
			System.out.println("Pasta não encontrada.");
			logger.error("Diretório informado não foi encontrado");

		} else {

			Date dtInicio = null;
			Date dtFim = new Date();

			int contagem = registroItemDao.countListByTimeWindowAndSet(
					dtInicio, dtFim, null);
			logger.info("Preparando para exportar " + contagem
					+ " registros na pasta:" + p_dir);

			int c = 0;
			String lastId = null;
			List<RegistroItem> listagem = registroItemDao
					.listByTimeWindowAndSet(dtInicio, dtFim, null, lastId,
							TAMANHO_LISTA);

			while (!listagem.isEmpty()) {

				for (RegistroItem registro : listagem) {
					c++;

					String xml = registro.getTxMetadadoXml();
					String pi = xml.substring(0, 5);

					String newFile = URLEncoder.encode(
							registro.getIdRegistroItem(), "UTF-8").concat(
							".xml");
					String arquivo = p_dir + File.separator + newFile;

					logger.info("gravando registro #" + c + " para o arquivo: "
							+ arquivo);

					OutputStreamWriter out = new OutputStreamWriter(
							new FileOutputStream(arquivo), "UTF-8");

					if (!INICIO_XML.equals(pi)) {
						out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
					}

					try {
						out.write(xml);
					} catch (Exception e) {
						logger.error("Falha ao gravar arquivo.", e);
					}

					out.close();
				}

				lastId = listagem.get(listagem.size() - 1).getIdRegistroItem();
				listagem = registroItemDao.listByTimeWindowAndSet(dtInicio,
						dtFim, null, lastId, TAMANHO_LISTA);
			}

			logger.info("Finalizada a exportação");
		}

	}

	public void exportarSQLParaPasta(final String p_dir) throws Exception {

        File dir = new File(p_dir);

        final String SQL_PADRAO=
        	"insert into registro_item"+
        		"(id_registro_item, ts_registro_gmt, cd_status, cd_validacao, tx_metadado_xml, id_conjunto_item) values " +
        		"(''{0}'', current_timestamp, ''{1}'', ''"+CdValidacao.OK+"'', ''{2}'', {3});\n";

        if (!dir.isDirectory()) {
            System.out.println("Pasta não encontrada.");
            logger.error("Diretório informado não foi encontrado");

        }
        else {

            Date dtInicio = null;
            Date dtFim =  new Date();

            //obtendo a contagem dos dados
            int contagem = registroItemDao.countListByTimeWindowAndSet(dtInicio, dtFim, null);

            if (contagem<= 0){
                logger.info("Nenhum registro a exportar.");
            } else {

				logger.info("Preparando para exportar " + contagem
						+ " registros na pasta:" + p_dir);

				// obtendo os dados
				int c = 0;
				String lastId = null;
				List<RegistroItem> listagem = registroItemDao
						.listByTimeWindowAndSet(dtInicio, dtFim, null, lastId,
								TAMANHO_LISTA);

				// preparando aquivo de saida
				String arquivo = p_dir + File.separator + this.exportarSQLNomeArquivo;
				OutputStreamWriter out = new OutputStreamWriter(
						new FileOutputStream(arquivo, false), "UTF-8");

				while (!listagem.isEmpty()) {

					for (RegistroItem registro : listagem) {
						c++;

						// preparando XML: duplicando as aspas simples
						String xml = registro.getTxMetadadoXml();
						if (xml == null) {
							xml = "";
						} else {
							xml = xml.replace("'", "''");
						}

						//preparando o idConjuntoItem, que pode nao existir
						String idConjuntoItem;
						if ( (registro.getConjuntoItem()== null) || (registro.getConjuntoItem().getIdConjuntoItem()== null) ) {
							idConjuntoItem="NULL";
						} else {
							idConjuntoItem=registro.getConjuntoItem().getIdConjuntoItem();
						}

						// montando nova linha
						String novaLinha = MessageFormat.format(SQL_PADRAO,
								registro.getIdRegistroItem(), //0
								registro.getCdStatus(), //1
								xml, //2
								idConjuntoItem); //3

						logger.info("gravando registro #" + c + " em nova linha");

						// escrevendo no arquivo
						try {
							out.write(novaLinha);
						} catch (Exception e) {
							logger.error("Falha ao gravar arquivo.", e);
						}

					}

					lastId = listagem.get(listagem.size() - 1)
							.getIdRegistroItem();
					listagem = registroItemDao.listByTimeWindowAndSet(dtInicio,
							dtFim, null, lastId, TAMANHO_LISTA);
				}

				// fechando arquivo de saida
				out.close();
			}

            logger.info("Finalizada a exportação");
        }

    }

	public int getSucessos() {
		return sucessos;
	}

	public void setSucessos(final int sucessos) {
		this.sucessos = sucessos;
	}

	public int getFalhas() {
		return falhas;
	}

	public void setFalhas(final int falhas) {
		this.falhas = falhas;
	}

	public static void main(String[] args) throws Exception {
		// FileInputStream fis = new
		// FileInputStream("/home/fragomeni/oai%3Aacordao.stf.jus.br%3Aaco%2F140851.xml");
		FileInputStream fis = new FileInputStream(
				"/tmp/p17/oai%3Aacordao.stf.jus.br%3Aaco%2F140851.xml");
		String conteudo = IOUtils.toString(fis);
		fis.close();
		for (int i = 0; i < conteudo.length(); i++) {
			char c = conteudo.charAt(i);
			if (!Character.isDefined(c)) {
				System.out.println("\nCaractere inválido: "
						+ conteudo.charAt(i));
			} else {
				System.out.println(c + "\t" + (int) c + "\t0x"
						+ Integer.toHexString(c) + "\t" + Character.getType(c));
			}
		}
		System.out.println("Fim.");
	}

}
