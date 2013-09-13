
package br.gov.lexml.borda.install;

import java.io.File;

import br.gov.lexml.LexMLSystem;
import br.gov.lexml.LexMLUtil;

public class PassoNovoArquivoConf extends PassoInstalacaoInterativo<ContextoInstalacao> {

    @Override
    public void imprimePergunta() {
        System.out.print("Caminho para o arquivo de configuracao " + LexMLSystem.PERFIL_NODO_BORDA_XML + ":\n"
                         + "\n" + "[" + contexto.getPathArquivoConf() + "]: ");
    }

    @Override
    public boolean validaResposta(final String resposta) throws Exception {

        String strPath = resposta.equals("") ? contexto.getPathArquivoConf() : resposta;

        File path = new File(strPath);
        File file = new File(path, LexMLSystem.PERFIL_NODO_BORDA_XML);
        String pathCompleto = file.getCanonicalPath();

        if (!file.isFile()) {
            System.out.println("Arquivo " + pathCompleto + " nao encontrado.\n");
            return false;
        }

        // Verifica se o arquivo está correto
        LexMLUtil.validaArquivoPerfil(file);

        // Move para posição correta
        File arqDestino = LexMLUtil.getPathPerfilNodoBorda();
        if (!file.equals(arqDestino)) {
            arqDestino.delete();
            LexMLUtil.copyFile(file, arqDestino);
        }

        return true;
    }

    @Override
    public String executaPasso(final String resposta) {
        if (!resposta.equals("")) {
            contexto.setPathArquivoConf(resposta);
        }
        return null;
    }

}
