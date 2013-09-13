
package br.gov.lexml;

import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import br.gov.lexml.borda.vo.AdministradorVO;
import br.gov.lexml.exceptions.ConfigFailedException;
import br.gov.lexml.profileLexml.ConfiguracaoProvedorDocument;
import br.gov.lexml.profileLexml.ConfiguracaoProvedorType;
import br.gov.lexml.profileLexml.PerfilType;
import br.gov.lexml.profileLexml.ProvedorType;
import br.gov.lexml.profileLexml.PublicadorType;
import br.gov.lexml.profileLexml.ResponsavelType;
import br.gov.lexml.profileLexml.PerfilType.TipoPerfil;
import br.gov.lexml.profileLexml.ProvedorType.Tipo.Enum;

public class LexMLConfig {

    private static ConfiguracaoProvedorDocument bordaConf = null;
    private ArrayList<Provedor> provedor;
    private final Logger logger = Logger.getLogger(LexMLConfig.class.getName());
    private ArrayList<Publicador> publicadores = new ArrayList<Publicador>();
    private final ArrayList<AdministradorVO> administradores = new ArrayList<AdministradorVO>();
    private static LexMLConfig instance = null;
    public static final long TEMPO_DE_SESSAO = 1000 * 20;// * 60 * 60 ; // 1 hora de sessao em
                                                         // millisegundos

    private LexMLConfig() throws ConfigFailedException {

        provedor = new ArrayList<Provedor>();

        try {
            setConfigFile(LexMLSystem.PERFIL_NODO_BORDA_XML);
        }
        catch (Throwable t) {
            t.printStackTrace();
            logger.error(t.getStackTrace().toString());
            throw new ConfigFailedException("Erro no arquivo de configuracao de perfil :" + getArquivoPerfil());
        }

        instance = this;
    }

    public static final LexMLConfig getInstance() throws ConfigFailedException {
        if (null == instance) {
            instance = new LexMLConfig();
        }
        return instance;
    }

    public String getArquivoPerfil() {
        return LexMLSystem.PERFIL_NODO_BORDA_XML;
    }

    // public final boolean validarAdministrador(final AdministradorVO adm) {
    // Iterator<AdministradorVO> iter = administradores.iterator();
    // while (iter.hasNext()) {
    // AdministradorVO tst = iter.next();
    // if (tst.getEmail().equals(adm.getEmail())) {
    // return tst.validateSenha(adm.getSenha());
    // }
    // }
    // return false;
    // }

    // public boolean matchLogin(final String emailTst, final String passwordTst) {
    // for (int i = 0; i < provedor.size(); i++) {
    // Provedor prov = provedor.get(i);
    // if (prov.matchLogin(emailTst, passwordTst)) {
    // return true;
    // }
    //
    // }
    // return false;
    // }

    private void parseProvedor(final ConfiguracaoProvedorDocument p_newConf) throws ConfigFailedException {
        if (null == p_newConf) {
            return;
        }

        ConfiguracaoProvedorType confProvedor = p_newConf.getConfiguracaoProvedor();

        if (null == confProvedor) {
            logger.error("Arquivo de configuração com erro no bloco ConfiguracaoProvedor");
        }
        else {
            if (1 > confProvedor.sizeOfProvedorArray()) {
                logger.error("Arquivo de configuração com erro no bloco Provedor");
                System.out.println(p_newConf.toString());
                throw new ConfigFailedException("Arquivo de configuração com erro no bloco Provedor");
            }
            
            for (int j = 0; j < confProvedor.sizeOfProvedorArray(); j++) {
                ProvedorType provedor_xml = confProvedor.getProvedorArray(j);
                Provedor newProvedor = new Provedor(provedor_xml);

                Enum newTipo = provedor_xml.getTipo();
                newProvedor.setTipo(newTipo.toString());
                newProvedor.setNome(provedor_xml.getNome());
                newProvedor.setIdProvedor(provedor_xml.getIdProvedor());

                ResponsavelType adm = provedor_xml.getAdministrador();
                if (null == adm) {
                    logger.error("Arquivo de configuração com erro Administrador de Provedor ausente");
                }
                else {
                    administradores.add(new AdministradorVO(provedor_xml.getIdProvedor(), adm.getIdResponsavel(),
                                                            adm.getEmail(), adm.getSenha()));
                }
                int publicadores = provedor_xml.sizeOfPublicadorArray();
                for (int i = 0; i < publicadores; i++) {
                    parsePublicador(provedor_xml.getPublicadorArray(i));
                }
                provedor.add(newProvedor);
            }
        }
    }

    private void parsePublicador(final PublicadorType publicador) {
        Publicador novo_publicador = new Publicador(publicador);

        for (int i = 0; i < publicador.sizeOfPerfilArray(); i++) {
            novo_publicador.addPerfil(new Perfil(publicador.getPerfilArray(i)));
        }
        ResponsavelType adm = publicador.getResponsavel();
        if (null != adm) {
            AdministradorVO publicadorAdm = new AdministradorVO(publicador.getIdPublicador(), adm
                    .getIdResponsavel(), adm.getEmail(), adm.getSenha());
            administradores.add(publicadorAdm);
        }
        publicadores.add(novo_publicador);
    }

    public void setConfigFile(final String configFile) throws ConfigFailedException, NamingException {
        ConfiguracaoProvedorDocument newConf;
        try {
            newConf = LexMLUtil.readConfiguracaoProvedor(configFile);
        }
        catch (FileNotFoundException e) {
            logger.error("Arquivo '" + configFile + "' não possui perfil de repositorio válido");
            throw new ConfigFailedException("Não foi possível abrir o arquivo " + configFile, e);
        }
        if (null != newConf) {
            parseProvedor(newConf);
            bordaConf = newConf;
        }
    }

    public class Provedor {

        private BigInteger idProvedor;
        private String nome;
        private String tipo;
        private Responsavel administrador;

        public Provedor(final ProvedorType provedor_xml) {
            tipo = provedor_xml.getTipo().toString();
            nome = provedor_xml.getNome();
            idProvedor = provedor_xml.getIdProvedor();
            administrador = new Responsavel(provedor_xml.getAdministrador());
        }

        public boolean matchLogin(final String emailTst, final String passwordTst) {
            if (administrador.matchLogin(emailTst, passwordTst)) {
                return true;
            }
            return false;
        }

        public BigInteger getIdProvedor() {
            return idProvedor;
        }

        public void setIdProvedor(final BigInteger idProvedor) {
            this.idProvedor = idProvedor;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(final String nome) {
            this.nome = nome;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(final String tipo) {
            this.tipo = tipo;
        }

        public Responsavel getAdministrador() {
            return administrador;
        }

        public void setAdministrador(final ResponsavelType administrador) {
            this.administrador = new Responsavel(administrador);
        }

    }

    public class Responsavel {

        private BigInteger idResponsavel;
        private String email;
        private String hash_senha;

        private Responsavel() {
        }

        private Responsavel(final ResponsavelType p_responsavel) {
            idResponsavel = p_responsavel.getIdResponsavel();
            email = p_responsavel.getEmail();
            hash_senha = p_responsavel.getSenha();
        }

        public String getEmail() {
            return email;
        }

        public boolean matchLogin(final String emailTst, final String passwordTst) {
            if (email.equals(emailTst) && hash_senha.equals(passwordTst)) {
                return true;
            }
            if (email.equals(emailTst)
                && hash_senha.equals(new LexMLCrypt.sha256New().hash(passwordTst.getBytes()))) {
                return true;
            }
            return false;
        }
    }

    public class Publicador {

        private BigInteger idPublicador;
        private String nome;
        private String sigla;
        private Responsavel responsavel;

        private ArrayList<Perfil> perfil;

        public Publicador(final BigInteger p_id, final String p_nome, final String p_sigla) {
            idPublicador = p_id;
            nome = p_nome;
            sigla = p_sigla;
        }

        public Publicador(final PublicadorType p_publicador) {
            idPublicador = p_publicador.getIdPublicador();
            nome = p_publicador.getNome();
            sigla = p_publicador.getSigla();
            responsavel = new Responsavel(p_publicador.getResponsavel());
        }

        public void addPerfil(final Perfil p_perfil) {
            if (null == perfil) {
                perfil = new ArrayList<Perfil>();
            }
            if (null != p_perfil) {
                perfil.add(p_perfil);
            }
        }

        public Responsavel getResponsavel() {
            return responsavel;
        }

        public BigInteger getIdPublicador() {
            return idPublicador;
        }

        public void setIdPublicador(final BigInteger idPublicador) {
            this.idPublicador = idPublicador;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(final String nome) {
            this.nome = nome;
        }

        public String getSigla() {
            return sigla;
        }

        public void setSigla(final String sigla) {
            this.sigla = sigla;
        }

        public ArrayList<Perfil> getPerfil() {
            return perfil;
        }

        public void setPerfil(final ArrayList<Perfil> perfil) {
            this.perfil = perfil;
        }
    }

    public class Perfil {

        public static final String SEP = ":";
        private String autoridade;
        private String localidade;
        private String tipoDocumento;
        private String tipoPerfil;

        public Perfil(final PerfilType p_perfil) {
            if (null != p_perfil) {
                autoridade = p_perfil.getAutoridade();
                localidade = p_perfil.getLocalidade();
                tipoDocumento = p_perfil.getTipoDocumento();
                br.gov.lexml.profileLexml.PerfilType.TipoPerfil.Enum tp = p_perfil.getTipoPerfil();
                /*
                 * se não foi informada o tipo da regra de perfil entao ela deve ser considerada "T", Todos
                 */
                if (null == tp) {
                    tp = TipoPerfil.T;
                }
                tipoPerfil = tp.toString();

            }
        }

        /**
         * Retorna o nucleo tipico de uma URN segundo esta regra de perfil.
         * 
         * @return
         */
        public String getNucleoUrn() {
            return localidade + SEP + autoridade + SEP + tipoDocumento;
        }

        @Override
        public String toString() {
            return autoridade + SEP + localidade + SEP + tipoDocumento;
        }

        public String getTipoPerfil() {
            return tipoPerfil;
        }

        public void setTipoPerfil(final String tipoPerfil) {
            this.tipoPerfil = tipoPerfil;
        }
    }

    public ArrayList<Publicador> getPublicadores() {
        return publicadores;
    }

    public void setPublicadores(final ArrayList<Publicador> publicadores) {
        this.publicadores = publicadores;
    }

    public void writePerfil(final StringBuffer p_sb) {
        if (null != bordaConf) {
            p_sb.append(bordaConf.toString().replaceAll("senha\\s*=\\s*\"[^>]*\"", "senha=\"*****\"").replaceAll("email\\s*=\\s*\"[^>]*\"", ""));
        }
    }
}
