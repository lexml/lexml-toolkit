package br.gov.lexml.borda.install;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class ConfigDb {

	private static ConfigDb INSTANCE = new ConfigDb();

	private final List<DbInfo> infos = new ArrayList<DbInfo>();

	private ConfigDb() {

		infos.add(new DbInfo(
			"MySQL (4.1 ou superior)",
			"org.hibernate.dialect.MySQLDialect",
			"com.mysql.jdbc.Driver",
			"jdbc:mysql://{0}:{1}/{2}?autoReconnect=true",
			3306));

		infos.add(new DbInfo(
			"Oracle (8i, 9i, 10g, 11g e 12c)",
			"org.hibernate.dialect.Oracle8iDialect",
			"oracle.jdbc.driver.OracleDriver",
			"jdbc:oracle:thin:@{0}:{1}:{2}",
			1521));

		infos.add(new DbInfo(
			"DB2 (8.x, 9.x)",
			"br.gov.lexml.util.hibernate.DB2Dialect",
			"com.ibm.db2.jcc.DB2Driver",
			"jdbc:db2://{0}:{1}/{2}",
			50000)); // outras portas default 446, 6789

		infos.add(new DbInfo(
			"Microsoft SQL Server (2000 e 2005)",
			"org.hibernate.dialect.SQLServerDialect",
			"net.sourceforge.jtds.jdbc.Driver",
			"jdbc:jtds:sqlserver://{0}:{1}/{2}",
			1433));

		infos.add(new DbInfo(
			"PostgreSQL (8.3, 8.4)",
			"org.hibernate.dialect.PostgreSQLDialect",
			"org.postgresql.Driver",
			"jdbc:postgresql://{0}:{1}/{2}",
			5432));

	}

	public static ConfigDb getInstance() {
		return INSTANCE;
	}

	public List<DbInfo> getDbInfos() {
		return infos;
	}

	public DbInfo getDbInfo(final int i) {
		return infos.get(i);
	}

	static class DbInfo {

		private final String sgbd;
		private final String dialect;
		private final String driver;
		private final String url;
		private final int defaultPort;

		public DbInfo(final String sgbd, final String dialect, final String driver, final String url, final int defaultPort) {
			this.sgbd = sgbd;
			this.driver = driver;
			this.dialect = dialect;
			this.url = url;
			this.defaultPort = defaultPort;
		}

		public String getSgbd() {
			return sgbd;
		}

		public String getDialect() {
			return dialect;
		}

		public String getDriver() {
			return driver;
		}

		public String getUrl(final String server, final int port, final String dbName) {
			return MessageFormat.format(url, server, Integer.toString(port), dbName);
		}

		public int getDefaultPort() {
			return defaultPort;
		}

	}

}
