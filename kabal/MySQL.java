package kabal;

public class MySQL {
	
	/**
	 * Dette er ikke en sikker måte å lagre
	 * informasjonen på, og overføringen foregår
	 * ikke kryptert. Bruk dette på egen risiko.
	 */
	private static String server = "jdbc:mysql://BRUKERNAVN.mysql.MINSERVER.ORG/DATABASE"; //TODO
	private static String tabell = "TABELL"; //TODO
	private static String brukernavn = "BRUKERNAVN"; //TODO
	private static String passord = "PASSORD"; //TODO
	/**
	 * Programmet tror at en ny versjon er ute
	 * når det ikke lenger klarer å koble til
	 * databasen. Da vil denne websiden vises.
	 */
	private static String webside = "http://www.MINSERVER.ORG/KABAL/"; //TODO
	
	public static String getServer() {
		return server;
	}
	public static String getTabell() {
		return tabell;
	}
	public static String getBrukernavn() {
		return brukernavn;
	}
	public static String getPassord() {
		return passord;
	}
	public static String getWebside() {
		return webside;
	}
	
}
