
package br.gov.lexml.borda.helper;

import br.gov.lexml.util.JLineUserInputReader;
import br.gov.lexml.util.UserInputReader;
import br.gov.lexml.util.WindowsUserInputReader;

/**
 * Inicializadora do ambiente de execução e gravação de LOGS. Configura o LOG4J para gerar os
 * arquivos de erros, debug e log de acordo com o script executado.
 * 
 * @author Programador
 */
public class ToolKitHelper {

    public static final String LOG_SUFFIX = "_log.txt";
    public static final String DEBUG_SUFFIX = "_debug.txt";
    public static final String ERR_SUFFIX = "_erros.txt";
    private static String conversionPattern = "%d{ISO8601} [%-50c:%-4L] %-5p : %m%n";
    private static String minimoPattern = "%d{HH:mm:ss,SSS} %-5p [%-13C{1}:%4L] %m%n";
    private static boolean init = false;

    private static UserInputReader userInputReader;

    static {
        if (System.getProperty("os.name").startsWith("Windows")) {
            // Windows
            userInputReader = new WindowsUserInputReader();
        }
        else {
            // Outros
            userInputReader = new JLineUserInputReader();
        }
    }

    public static String readUserInput() throws Exception {
        return userInputReader.readLine().trim();
    }

}
