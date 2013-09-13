
package br.gov.lexml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import br.gov.lexml.borda.business.ToolKitBO;
import br.gov.lexml.exceptions.ConfigFailedException;
import br.gov.lexml.profileLexml.ConfiguracaoProvedorDocument;

/**
 * Provê métodos utilitários ao sistema em geral, inclusive a leitura de arquivos do diretório
 * definido por LEXML_HOME
 * 
 * @author Gabriel Franklin
 */
public class LexMLUtil {

    private static Logger logger = Logger.getLogger(LexMLUtil.class.getName());
    private static Properties jpa_properties = null;
    private static boolean desenvolvimento = !new File("../etc").isDirectory();

    /**
     * Retorna um long com o tempo em milisegundos GMT deste instante.
     * 
     * @return
     */
    public static long nowInMillisGMT() {
        // FIXME - Ainda não identificamos o Locale correto para pegar hora 0 GMT
        Calendar c = Calendar.getInstance(Locale.getDefault());
        return c.getTimeInMillis();
    }

    /**
     * Configura o JPA com o arquivo {LexMLSystem.JPA_PROPERTIES_FILE}
     * 
     * @return
     * @throws NamingException
     * @throws ConfigFailedException
     * @throws FileNotFoundException
     */
    public static Properties getJPAProperties() throws ConfigFailedException {
        if (null != jpa_properties) {
            return jpa_properties;
        }

        jpa_properties = new Properties();
        logger.debug("Carregando properties do arquivo: " + LexMLSystem.JPA_PROPERTIES_FILE);
        InputStream isProperties = null;
        try {
            isProperties = LexMLUtil.class.getResourceAsStream("/" + LexMLSystem.JPA_PROPERTIES_FILE);
            jpa_properties.load(isProperties);
            isProperties.close();
        }
        catch (FileNotFoundException e1) {
            logger.error("Arquivo não encontrado: " + LexMLSystem.JPA_PROPERTIES_FILE);
            throw new ConfigFailedException("Arquivo não encontrado: " + LexMLSystem.JPA_PROPERTIES_FILE, e1);
        }
        catch (IOException e2) {
            logger.error("Erro de I/O ao carregar arquivo " + LexMLSystem.JPA_PROPERTIES_FILE);
            throw new ConfigFailedException("Erro de I/O ao carregar arquivo " + LexMLSystem.JPA_PROPERTIES_FILE,
                                            e2);
        }
        if (null == isProperties) {
            logger.error("Não foi possível ler arquivo de properties a partir do getInputStreamFromLexMLHome");
            throw new ConfigFailedException(
                                            "Não foi possível ler arquivo de properties a partir do getInputStreamFromLexMLHome");
        }
        else {
            logger.debug("Arquivo de properties carregado com sucesso.");
        }

        // Carrega outras propriedades do hibernate... se existirem
        InputStream is = LexMLUtil.class.getResourceAsStream("/hibernate.properties");
        if (is != null) {
            try {
                jpa_properties.load(is);
                is.close();
            }
            catch (IOException e) {
                logger.error("Falha ao ler hibernate.properties", e);
            }
        }

        // jpa_properties.list(System.out);

        return jpa_properties;
    }

    public static ConfiguracaoProvedorDocument readConfiguracaoProvedor(final File file) throws NamingException,
                                                                                        FileNotFoundException,
                                                                                        ConfigFailedException {
        return readConfiguracaoProvedor(new FileInputStream(file), file.getName());
    }

    public static ConfiguracaoProvedorDocument readConfiguracaoProvedor(final String p_file)
                                                                                            throws NamingException,
                                                                                            FileNotFoundException,
                                                                                            ConfigFailedException {
        InputStream isPerfil = LexMLUtil.class.getResourceAsStream("/" + p_file);
        return readConfiguracaoProvedor(isPerfil, p_file);
    }

    /**
     * Lê e executa o parsing do arquivo passado como parametro na raiz do classpath ou no diretorio
     * LEXML_HOME
     */
    @SuppressWarnings("unchecked")
    public static ConfiguracaoProvedorDocument readConfiguracaoProvedor(final InputStream isPerfil,
                                                                        final String p_file)
                                                                                            throws NamingException,
                                                                                            FileNotFoundException,
                                                                                            ConfigFailedException {
        ConfiguracaoProvedorDocument confProvedor = null;

        if (null != isPerfil) {

            XmlOptions xmlOpt = new XmlOptions();
            xmlOpt.setLoadStripComments();
            xmlOpt.setSaveNoXmlDecl();
            xmlOpt.setSavePrettyPrintIndent(3);
            xmlOpt.setLoadStripProcinsts();
            xmlOpt.setLoadStripProcinsts();
            xmlOpt.setSaveAggressiveNamespaces();

            ArrayList validationErrors = new ArrayList();
            xmlOpt.setErrorListener(validationErrors);
            try {
                confProvedor = ConfiguracaoProvedorDocument.Factory.parse(isPerfil, xmlOpt);
                if (!confProvedor.validate(xmlOpt)) {
                    logger.error("Validate do XMLBeans falhou para: " + p_file);
                }

            }
            catch (XmlException e) {
                throw new ConfigFailedException("Erro de XML ao abrir o arquivo " + p_file, e);
            }
            catch (IOException e) {
                throw new ConfigFailedException("Não foi possível abrir o arquivo " + p_file, e);
            }
            finally {
                try {
                    isPerfil.close();
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                finally {
                    if (validationErrors.size() > 0) {
                        Iterator iter = validationErrors.iterator();
                        while (iter.hasNext()) {
                            logger.error(iter.next().toString());
                        }
                        throw new ConfigFailedException("O arquivo " + p_file
                                                        + " não é um arquivo de peril válido");
                    }
                }
            }
        }
        return confProvedor;
    }

    public static void copyFile(final File from, final File to) throws IOException {
        byte[] buffer = new byte[102400];
        FileInputStream in = new FileInputStream(from);
        FileOutputStream out = new FileOutputStream(to);
        int i = 0;
        while ((i = in.read(buffer)) > 0) {
            out.write(buffer, 0, i);
        }
        in.close();
        out.close();
    }

    /**
     * Verifica se está rodando de dentro do Eclipse (não existe a pasta ../etc)
     */
    public static boolean isDesenvolvimento() {
        return desenvolvimento;
    }

    public static File getPathJpaProperties() {
        // Em produção "../etc" em desenv "target/classes"
        File dir = isDesenvolvimento() ? new File("target/classes") : new File("../etc");
        return new File(dir, LexMLSystem.JPA_PROPERTIES_FILE);
    }

    public static File getPathPerfilNodoBorda() {
        // Em produção "../etc" em desenv "target/classes"
        File dir = LexMLUtil.isDesenvolvimento() ? new File("target/classes") : new File("../etc");
        return new File(dir, LexMLSystem.PERFIL_NODO_BORDA_XML);
    }

    public static void validaArquivoPerfil(final File file) throws Exception {
        // Verifica se o arquivo está correto
        ToolKitBO tkBO;
        try {
            LexMLUtil.readConfiguracaoProvedor(file);
        }
        catch (Exception e) {
            throw new Exception("Arquivo de configuracao " + LexMLSystem.PERFIL_NODO_BORDA_XML + " inválido.", e);
        }
    }

}
