
package br.gov.lexml.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ManifestEditor {

    private static Log log = LogFactory.getLog(ManifestEditor.class);

    private static final String MANIFEST = "/META-INF/MANIFEST.MF";

    private Manifest manifest = new Manifest();

    public ManifestEditor(final ServletContext servletContext) throws IOException {
        loadManifest(servletContext);
    }

    public String getVersao() {
        return getValor("Implementation-Version", "*Versao*");
    }

    public String getBuild() {
        return getValor("Implementation-Build", "*Build*");
    }

    private String getValor(final String key, final String defaultStr) {
        return StringUtils.defaultString(manifest.getMainAttributes().getValue(key), defaultStr);
    }

    private void loadManifest(final ServletContext servletContext) throws IOException {

        log.info("Iniciando carga do MANIFEST.MF");

        if (servletContext != null) {
            InputStream is = servletContext.getResourceAsStream(MANIFEST);
            if (is != null) {
                log.info("Carregando MANIFEST.MF via ServletContext.");
                manifest.read(is);
                is.close();
                return;
            }
        }

        URL url = ManifestEditor.class.getResource(ManifestEditor.class.getSimpleName() + ".class");
        String classFileName = url.getFile();

        // Verifica se a classe está em um .jar
        if (classFileName.contains(".jar")) {
            log.info("Carregando MANIFEST.MF de .jar");
            String jarFileName = classFileName.substring(0, classFileName.indexOf(".jar") + 4);
            getManifestFromJarFile(jarFileName);
        }
        // Verifica se a classe está em um .war
        else if (classFileName.contains(".war")) {
            log.info("Carregando MANIFEST.MF de .war");
            String jarFileName = classFileName.substring(0, classFileName.indexOf(".war") + 4);
            getManifestFromJarFile(jarFileName);
        }
        else {
            log.error("Arquivo " + MANIFEST + " não encontrado.");
        }
    }

    private void getManifestFromJarFile(final String jarFileName) throws MalformedURLException, IOException {
        URL jarUrl = new URL("jar:" + jarFileName + "!/");
        JarURLConnection jarConnection = (JarURLConnection) jarUrl.openConnection();
        manifest = jarConnection.getManifest();
    }

}
