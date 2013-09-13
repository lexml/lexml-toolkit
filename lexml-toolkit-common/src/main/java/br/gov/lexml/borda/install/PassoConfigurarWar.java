package br.gov.lexml.borda.install;

import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

import br.gov.lexml.LexMLSystem;
import br.gov.lexml.LexMLUtil;
import br.gov.lexml.exceptions.InstalacaoException;
import de.schlichtherle.io.File;
import de.schlichtherle.io.FileInputStream;
import de.schlichtherle.io.FileOutputStream;
import de.schlichtherle.io.FileWriter;

public class PassoConfigurarWar extends PassoInstalacao<ContextoInstalacao> {

	private static ConfigDb configDb = ConfigDb.getInstance();

	@Override
	public String executa() throws Exception {
		
		try {
			configuraWar();
		}
		catch(Exception e) {
			throw new InstalacaoException("Falha configurar a aplicacao.", e);
		}
		
		return null;
	}

	private void configuraWar() throws Exception {
		
		String pathWar = encontraWar();
		
		System.out.println("\nAtualizando arquivo " + pathWar + " ...");
		
		File war = new File(pathWar);

		// lexml-db.properties
		File origem = new File(LexMLUtil.getPathJpaProperties());
		File destino = new File(war, "WEB-INF/classes/" + LexMLSystem.JPA_PROPERTIES_FILE);
		destino.delete();
		copyFile(origem, destino);
		
		// lexml_nbconfig.xml
		origem = new File(LexMLUtil.getPathPerfilNodoBorda());
		destino = new File(war, "WEB-INF/classes/" + LexMLSystem.PERFIL_NODO_BORDA_XML);
		copyFile(origem, destino);
		
		File.umount(war);
		
		contexto.setWarOk(true);
		
	}

	private String encontraWar() throws Exception {
		
		// Em produção "../oai" em desenv "oai"
		File dir = LexMLUtil.isDesenvolvimento()? 
				new File("oai"): new File("../oai");

		if(!dir.isDirectory()) {
			throw new FileNotFoundException("Diretorio \"oai\" nao encontrado.");
		}
		
		String[] files = dir.list(new FilenameFilter() {
			
			// Aceita qualquer oai*.war
			public boolean accept(java.io.File dir, String name) {
				return name.startsWith("oai") && name.endsWith(".war");
			}
			
		});
		
		if(files.length == 0) {
			throw new java.io.FileNotFoundException("Arquivo oai.war nao encontrado.");
		}
		
		return dir.getCanonicalPath() + File.separator + files[0];
	}
	
    public static void copyFile(File from, File to) throws IOException {
        byte[] buffer = new byte[102400];
        FileInputStream     in  = new FileInputStream(from);
        FileOutputStream    out = new FileOutputStream(to);
        int i = 0;
        while((i = in.read(buffer)) > 0) {
            out.write(buffer, 0, i);
        }
        in.close();
        out.close();
    }
	

}
