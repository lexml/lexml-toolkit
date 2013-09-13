package br.gov.lexml.util.hibernate;

import java.sql.Types;

/**
 * Corrige problema de geração de Clob com tamanho fixo pelo org.hibernate.dialect.DB2Dialect 
 */
public class DB2Dialect extends org.hibernate.dialect.DB2Dialect {

    public DB2Dialect() {
        super();
        registerColumnType( Types.BLOB, "blob" );
        registerColumnType( Types.CLOB, "clob" );
    }

}
