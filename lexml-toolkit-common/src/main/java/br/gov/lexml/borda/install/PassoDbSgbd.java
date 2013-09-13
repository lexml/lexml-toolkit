
package br.gov.lexml.borda.install;

import java.util.List;

import br.gov.lexml.borda.install.ConfigDb.DbInfo;

public class PassoDbSgbd extends PassoInstalacaoInterativo<ContextoInstalacao> {

    private static ConfigDb configDb = ConfigDb.getInstance();

    @Override
    public void imprimePergunta() {

        System.out.println("Selecione o tipo do seu banco de dados:\n");

        List<DbInfo> dbs = configDb.getDbInfos();
        int i = 1;
        for (DbInfo db : dbs) {
            System.out.println("\t" + i++ + " - " + db.getSgbd());
        }
        System.out.print("\n[" + (contexto.getDbSgbd() + 1) + "]: ");
    }

    @Override
    public boolean validaResposta(final String resposta) {

        boolean sucesso = true;

        if (resposta.trim().equals("")) {
            contexto.setDbPorta(configDb.getDbInfo(contexto.getDbSgbd()).getDefaultPort());
            return true;
        }

        int sgbd = 0;
        try {
            sgbd = Integer.parseInt(resposta);
        }
        catch (Exception e) {
            sucesso = false;
        }

        int maxOpcao = configDb.getDbInfos().size();

        sucesso &= sgbd >= 1 && sgbd <= maxOpcao;

        if (!sucesso) {
            System.out.println("Selecione uma opcao valida entre 1 e " + maxOpcao + ".\n");
        }
        else {
            sgbd--;
            contexto.setDbSgbd(sgbd);
            contexto.setDbPorta(configDb.getDbInfo(sgbd).getDefaultPort());
        }

        return sucesso;
    }

}
