package poeng;
import java.lang.reflect.Method;
import java.net.NetworkInterface;

import javax.swing.JOptionPane;

import java.util.Arrays;
import java.util.Enumeration;

import kabal.MySQL;

/**
 * Statisk klasse for å sjekke om vi trenger å laste ned
 * ny versjon av programmet. Sjekken er langt fra perfekt,
 * men den funker bra til sitt bruk!
 * 
 * @author Michael Johansen
 * @author Anja Rønning
 * @author Cathrine Holm
 * @version 0.2
 */
public class Versjonssjekk {

	private static final String[] browsere = { "firefox", "opera", "konqueror", "epiphany",
		"seamonkey", "galeon", "kazehakase", "mozilla", "netscape", "chromium" };

	/**
	 * Sjekker om vi har internett-tilkobling.
	 * 
	 * @return true/false True = Vi har nett.
	 */
	public static boolean harInternett()
	{
		try
		{
			// Looper gjennom nettverksinterfacene
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements())
			{
				NetworkInterface nettverksinterface = interfaces.nextElement();

				// Sjekker om en av dem er oppe og går
				if (nettverksinterface.isUp() && !nettverksinterface.isLoopback())
				{
					return true;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, "Internettilkoblingen og/eller\ndatabasetilkoblingen er nede.", "Kabal - Ingen tilkobling", JOptionPane.WARNING_MESSAGE, null);
		return false;
	}

	/**
	 * Sjekker om vi har rett passord til databasen, ved å prøve å
	 * åpne en tilkobling. Dersom vi ikke får koblet til har det
	 * mest sannsynligvis kommet en ny versjon.
	 * 
	 * @return true/false True = Vi har databasetilkobling
	 */
	public static boolean harNyesteVersjon()
	{
		if (Poengtabell.connect() == null)
		{
			int lastned = JOptionPane.showConfirmDialog(null,"Det er kommet en ny versjon av programmet,\ndu må ha den for å kunne koble til databasen.\n\nPrøve å laste ned ny versjon?","Kabal - Ny versjon ute!",JOptionPane.YES_NO_OPTION);
			if (lastned == JOptionPane.YES_OPTION)
			{
				visWebside(MySQL.getWebside());

				// Bruker svarte ja på å laste ned ny versjon, ikke start programmet
				System.exit(0);
			}
			return false;
		}
		return true;
	}

	/**
	 * Åpner URL for nedlasting.
	 * 
	 * Har funksjoner for å be OS'et om å åpne URL'en. Funksjonene
	 * er OS spesifikke, forskjellig for Mac OS, Windows og Unix.
	 * 
	 * @param webside
	 */
	public static void visWebside( String webside )
	{
		String operativsystem = System.getProperty("os.name");
		try
		{
			// Mac
			if (operativsystem.startsWith("Mac OS"))
			{
				Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] {String.class});
				openURL.invoke(null, new Object[] {webside});
			}

			// Windows
			else if (operativsystem.startsWith("Windows"))
			{
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + webside);
			}

			// Sikkert unix...
			else
			{
				boolean funnet = false;
				for (String browser : browsere)
				{
					if (!funnet)
					{
						funnet = Runtime.getRuntime().exec( new String[] {"which", browser}).waitFor() == 0;
						if (funnet)
						{
							Runtime.getRuntime().exec(new String[] {browser, webside});
						}
					}
				}
				if (!funnet)
				{
					throw new Exception(Arrays.toString(browsere));
				}
			}
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Klarte ikke å åpne nettleser!");
		}
	}

}