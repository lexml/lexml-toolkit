
package br.gov.lexml.borda;

import java.io.File;
import java.io.IOException;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import br.gov.lexml.borda.business.ToolKitBO;
import br.gov.lexml.borda.business.ValidadorBO;
import br.gov.lexml.borda.dao.JPAUtil;
import br.gov.lexml.borda.install.ContextoInstalacao;
import br.gov.lexml.borda.install.GerenteInstalacao;
import br.gov.lexml.borda.install.PassoConfigurarWar;
import br.gov.lexml.borda.install.PassoDbInstalarSchema;
import br.gov.lexml.borda.install.PassoDbNome;
import br.gov.lexml.borda.install.PassoDbPorta;
import br.gov.lexml.borda.install.PassoDbSenha;
import br.gov.lexml.borda.install.PassoDbServidor;
import br.gov.lexml.borda.install.PassoDbSgbd;
import br.gov.lexml.borda.install.PassoDbUsuario;
import br.gov.lexml.borda.install.PassoDbVerificarPreconfiguracao;
import br.gov.lexml.borda.install.PassoNovoArquivoConf;
import br.gov.lexml.borda.install.PassoVerificarArquivoConf;
import br.gov.lexml.exceptions.ConfigFailedException;

/**
 * Comandos: importar - nodo borda, não apaga consumir - nodo meio, apaga agregar - nodo meio, não
 * apaga
 * <p>
 * <a href="Toolkit.html"><i>Código Fonte</i></a>
 * </p>
 */
public class Toolkit {

    private static Logger logger = Logger.getLogger(Toolkit.class);

    private static final boolean APAGAR_ARQUIVOS_IMPORTADOS_COM_SUCESSO = false;
    private static final boolean APAGAR_ARQUIVOS_CONSUMIDOS_COM_SUCESSO = true;

    public static void printHelp() {
        System.out
                .println("Forma de utilizacao\n"
                         + "\n"
                         + "     windows:     toolkit.bat <comando> <diretorio> [full]\n"
                         + "     linux:       toolkit.sh  <comando> <diretorio> [full]\n"
                         + "\n"
                         + "<comando>\n"
                         + "\n"
                         + "     importar     Importar arquivos em formato oai_lexml de um diretorio\n"
                         + "                  sem apagar os arquivos originais.\n"
                         + "\n"
                         + "     consumir     Importar os registros em formato oai_lexml de um diretorio\n"
                         + "                  apagando os arquivos originais.\n"
                         + "\n"
                         + "     validar      Validar os registros em formato oai_lexml da base de dados.\n"
                         + "\n"
                         + "     exportar     Exportar arquivos em formato oai_lexml para um diretorio.\n"
                         + "\n"
                         + "     exportar-sql Exportar arquivos em formato sql (comandos insert into) para um diretorio.\n"
                         + "\n"
                         + "<diretorio>\n"
                         + "\n"
                         + "     Caminho do diretorio com arquivos xml para os comandos importar,\n"
                         + "     consumir e exportar.\n"
                         + "\n"
                         + "full\n"
                         + "\n"
                         + "     Argumento opcional que indica que o diretorio deve ser importado/consumido\n"
                         + "     inteiro sem comparar a data do arquivo com a do registro no banco de dados.\n"
                         + "\n"
                         + "Exemplos:\n"
                         + "\n"
                         + "     toolkit.bat importar c:\\lexml full\n"
                         + "\n"
                         + "     toolkit.sh exportar ../lexml\n");
    }

    /*
     * (non-Javadoc)
     *
     * @see br.gov.lexml.borda.ToolkitInterface#validar(java.lang.String)
     */
    public void validar() throws ConfigFailedException {
        ValidadorBO bo;

        bo = new ValidadorBO();

        logger.info("Iniciando a validacao dos registros indefinidos.");

        bo.validarRegistrosIndefinidos();

        logger.info("Veja o resultado da validacao no arquivo 'log/toolkit.log'.\n");
    }

    /*
     * (non-Javadoc)
     *
     * @see br.gov.lexml.borda.ToolkitInterface#consumir(java.lang.String)
     */
    public void consumir(final String p_diretorio, boolean full) throws ConfigFailedException {
        ToolKitBO tkBO;

        tkBO = new ToolKitBO();
        try {
            tkBO.consumirPasta(p_diretorio, APAGAR_ARQUIVOS_CONSUMIDOS_COM_SUCESSO, full);
        }
        catch (IOException e) {
            logger.error("Erro de acesso ao diretorio ou aos seus arquivos durante a importacao.", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see br.gov.lexml.borda.ToolkitInterface#importar(java.lang.String)
     */
    public void importar(final String p_diretorio, boolean full) throws ConfigFailedException {
        ToolKitBO tkBO;

        tkBO = new ToolKitBO();
        try {
            tkBO.consumirPasta(p_diretorio, APAGAR_ARQUIVOS_IMPORTADOS_COM_SUCESSO, full);
        }
        catch (IOException e) {
            logger.error("Erro de acesso ao diretorio ou aos seus arquivos durante a importacao.", e);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see br.gov.lexml.borda.ToolkitInterface#exportar(java.lang.String)
     */
    public void exportar(final String p_diretorio) throws ConfigFailedException {

        ToolKitBO bo;

        logger.info("Iniciando o processo de exportacao dos registros.");

        bo = new ToolKitBO();
        try {
            bo.exportarParaPasta(p_diretorio);
        }
        catch (Exception e) {
            logger.error("Falha durante a exportacao.", e);
        }

    }

    /**
     * Exporta para comandos SQL de INSERT INTO
     * @param p_diretorio
     * @throws ConfigFailedException
     */
    public void exportarSQL(final String p_diretorio) throws ConfigFailedException {

        ToolKitBO bo;

        logger.info("Iniciando o processo de exportacao dos registros no formato SQL.");

        bo = new ToolKitBO();
        try {
            bo.exportarSQLParaPasta(p_diretorio);
        }
        catch (Exception e) {
            logger.error("Falha durante a exportacao.", e);
        }
    }

    private void instalar() {

        ContextoInstalacao ctx = new ContextoInstalacao();

        GerenteInstalacao<ContextoInstalacao> gerente = new GerenteInstalacao<ContextoInstalacao>(ctx);

        // Passos da instalacao
        gerente.addPasso(new PassoVerificarArquivoConf()).addPasso(new PassoNovoArquivoConf())
                .addPasso(new PassoDbVerificarPreconfiguracao()).addPasso(new PassoDbSgbd())
                .addPasso(new PassoDbServidor()).addPasso(new PassoDbPorta()).addPasso(new PassoDbNome())
                .addPasso(new PassoDbUsuario()).addPasso(new PassoDbSenha()).addPasso(new PassoDbInstalarSchema())
                .addPasso(new PassoConfigurarWar());

        System.out.println("\nInforme os dados para a instalacao.");

        try {
            gerente.executa();
        }
        catch (Throwable e) {
            System.out.println("\n" + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n"
                               + "Ocorreu um erro inesperado durante a instalacao do Toolkit.\n"
                               + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
            logger.fatal(e.getMessage(), e);
            System.out.println("\n" + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n" + "Por favor, tente novamente.\n"
                               + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }

        System.out.println();
        if (ctx.isWarOk()) {
            System.out.println("O Toolkit do LexML foi instalado com sucesso.\n");
        }
        else if (!ctx.isBdOk()) {
            System.out.println("Nao foi possivel instalar o banco de dados.\n");
        }
        else {
            System.out.println("Nao foi possivel configurar arquivo oai.war.\n");
        }

    }

    public void gerar200000() throws ConfigFailedException {
        ToolKitBO bo;

        bo = new ToolKitBO();

        bo.geraTests(1, 1000);
        bo.geraTests(1000, 1111);
//        bo.geraTests(10000, 50000);
//        bo.geraTests(50000, 100000);
//        bo.geraTests(100000, 150000);
//        bo.geraTests(150000, 190000);
//        bo.geraTests(190000, 250000);

        logger.info("!!! Fim !!!");
    }

    /**
     * @param args
     * @throws NamingException
     * @throws ConfigFailedException
     * @throws IOException
     */
    public static void main(final String[] args) throws NamingException {

        System.out.println("------------------------\n" + "Toolkit LexML versao 3.0\n"
                           + "------------------------\n");

        // Apresentação do help
        if (args.length < 1 || "-h".equals(args[0]) || "--help".equals(args[0]) || "help".equals(args[0])) {
            printHelp();
            return;
        }

        // Loga nova execução
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg + " ");
        }
        logger.info("---- Nova execução ---- Argumentos: " + sb);

        Toolkit tk = new Toolkit();

        String comando = args[0];

        if ("instalar".equalsIgnoreCase(comando)) {
            tk.instalar();
            return;
        }

        try {
            if ("validar".equalsIgnoreCase(comando) || "-v".equalsIgnoreCase(comando)) {
                tk.validar();
                return;
            }
            else if ("gerar".equals(comando)) {
                tk.gerar200000();
                return;
            }

            if (args.length < 2) {
                logger.error("Argumentos inválidos na chamada do toolkit.");
                printHelp();
                return;
            }

            // Comandos com 2 ou mais argumentos
            String diretorio = args[1];

            boolean full = args.length >= 3 && args[2].equalsIgnoreCase("full");

            File dir = new File(diretorio);
            if (!dir.isDirectory()) {
                logger.error("O argumento: " + diretorio + " passado nao foi encontrado ou nao eh um diretorio");
                printHelp();
            }
            else {
                if ("importar".equalsIgnoreCase(comando) || "-i".equalsIgnoreCase(comando)) {
                    tk.importar(diretorio, full);
                }
                else if ("consumir".equalsIgnoreCase(comando) || "-c".equalsIgnoreCase(comando)) {
                    tk.consumir(diretorio, full);
                }
                else if ("exportar".equals(comando) || "-e".equalsIgnoreCase(comando)) {
                    tk.exportar(diretorio);
                }
                else if ("exportar-sql".equals(comando) || "-sql".equalsIgnoreCase(comando)) {
                    tk.exportarSQL(diretorio);
                }
                else {
                    logger.error("Comando \"" + comando + "\" nao suportado.");
                    printHelp();
                }
            }

            JPAUtil.closeEntityManager();
        }
        catch (ConfigFailedException e) {
            logger.fatal(e.getMessage());
        }
    }

}
