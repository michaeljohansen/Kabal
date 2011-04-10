package kabal;

import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import data.Bord;

import gui.Dialog;
import gui.Vindu;

/**
 * <h3>Her starter kabalprogrammet.</h3>
 * 
 * <div style="font:arial;font-size:14px">
 * Sjekkliste:<br>
 * [x] Integrerte bilder i .JAR<br>
 * [x] Runnable .JAR fil med refererte bibliotek<br>
 * [x] WindowListener med intuitiv grayscaling<br>
 * [x] Beskyttelse mot feil museinput<br>
 * [x] Skikkelig JavaDoc<br>
 * [x] Highlighting av kortet du holder over<br>
 * [x] Innstillinger for både raske og treger maskiner<br>
 *       - Fjernet da paint() metodene senere ble optimalisert<br>
 * [x] Automatisk fullføring når alle kort er snudd<br>
 * [x] Automatisk opplegging med høyre museknapp<br>
 * [x] Dialog med avslutt / restart når kabalen er fullført<br>
 * [x] Innstillinger for trekking av kort<br>
 * [x] Bedre dialogbokser vha. JDialog<br>
 * [x] Poengtabell med database som lett kan byttes<br>
 * [x] Poengberegning ut ifra instillinger for trekking av kort<br>
 * [x] Beskyttelse av MySQL tilkoblings-informasjon<br>
 * [x] Trekkteller i tittelen<br>
 * [x] Tegn lerretet på en bedre måte, fjern "treg data-modus"<br>
 * [x] Poengberegning basert på både tid og trekk<br>
 * [x] Lucky Restart funksjon så man slipper å restarte flere ganger<br>
 * 	     - Kommentert ut inntil videre, litt for "cheaty".<br>
 * [x] Nettsjekking og versjonssjekking<br>
 * [x] Utvidete muserektangler for lettere treff på kort<br>
 * [x] Lag et "singleton" designmønster for automatikken<br>
 * [x] HTML i swing komponenter<br>
 * [ ] Forenkle koden før utgivelse og sørg for at den er kommentert<br>
 * - angrefunksjon, bør kunne ta både enkle trekk og automatiske opplegginger.<br>
 * - google i vei for bedre beskyttelse av MySQL info.<br/>
 * - bedre restartfunksjon (kun lerret, ikke hele vinduet).<br>
 * - dobbeltklikk legger opp kort.<br>
 * - muligheter for å resize vinduet.<br>
 * That's it, jeg kommer ikke på noe mer akkurat nå.
 * </div>
 * 
 * @author Michael Johansen
 * @author Anja Rønning
 * @author Cathrine Holm
 * @version 0.8
 */
public class Kabal
{
	private Bord bord;
	private Vindu vindu;

	private String tittel;
	private String bildePakke;
	private int trekkKort;
	private boolean internettOgDatabase;
	private boolean bilderAvOss;
	private boolean oppdateringAvTittel;
	private boolean testing;
	private Date startTime;
	private Date stoppTime;
	private int sekunderBrukt;

	/**
	 * Main
	 * @param args
	 */
	public static void main (String[] args)
	{
		try {
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		boolean harInternett = true;
		boolean nyesteVersjon = true;

		// Sjekk om vi har nett
		if ((harInternett = poeng.Versjonssjekk.harInternett()) == true)
		{
			// Sjekk om det er ny versjon ute
			nyesteVersjon = poeng.Versjonssjekk.harNyesteVersjon();
		}

		// Start kabal
		new Kabal( harInternett && nyesteVersjon );
	}

	/**
	 * Constructor
	 */
	public Kabal( boolean harInternettOgNyesteVersjon )
	{
		// Sette egne variabler
		tittel = "Kabal";
		bildePakke = "/bilder/";
		trekkKort = 1;
		internettOgDatabase = harInternettOgNyesteVersjon;
		bilderAvOss = false;
		oppdateringAvTittel = true;
		testing = false;

		// Lag ny data
		bord = new Bord( this );
		// Start timer
		timer(true);
		// Lag ny gui
		vindu = new Vindu( this, bord, tittel );
	}

	/**
	 * Restart kabal
	 */
	public void restartKabal()
	{
		// Kast gammelt vindu
		vindu.dispose();
		// Lag ny data
		bord = new Bord( this );
		// Lag og vis ny gui
		vindu = new Vindu( this, bord, tittel );
		// Start timer
		timer(true);
	}

	/**
	 * Aces' Restart kabal ("Lucky Restart")
	 */
	/*public void acesRestart()
	{
		// Kast gammelt vindu
		vindu.dispose();

		// TODO: Burde ha en dialog her slik at ikke brukeren sitter med tom skjerm

		// Aces' start
		boolean aces = false;
		while (aces == false) {
			bord = new Bord(this);
			int ess = 0;
			for (Stokk stokk : bord.getTabla())
				if (stokk.peekAtEnd().getNummer() == 0)
					ess++;
			if (ess >= 2)
				aces = true;
		}

		// Lag og vis ny gui
		vindu = new Vindu( this, bord, tittel );
		// Start timer
		timer(true);
	}*/

	/**
	 * Sjekker om spillet er ferdig og gratulerer.
	 * 
	 * Må sjekkes ved:
	 * - Gyldig dratt kort til foundation
	 * - Automatisk flyttet kort til foundation (dobbelklikk)
	 */
	public void spillFerdig()
	{
		// Nullstiller tittelen på vinduet
		this.oppdaterTittel();

		for(int i = 0; i < bord.getFundament().size(); i++)
			if (bord.getFundament().get(i).size() != 13)
				return;

		// Stopper timer
		timer(false);

		// Viser poengtabell
		if (internettOgDatabase == true)
			new Dialog( this, "poengtabell", true);
		else
			JOptionPane.showMessageDialog(this.getVindu(), "Internettilkoblingen og/eller\ndatabasetilkoblingen er nede.\n\nFår ikke lagt til dine poeng i databasen!", "Ingen tilkobling", JOptionPane.WARNING_MESSAGE, null);

		// Kort pause
		try {Thread.sleep(700);} catch (InterruptedException e) {e.printStackTrace();}

		// Viser gratulasjon og NTNU logo
		new Dialog( this, "gratulasjon", false );
	}

	/**
	 * Avslutter programmet
	 */
	public void avslutt() {
		vindu.dispose();
		System.exit(0);
	}

	/**
	 * Måler antall sekunder brukt
	 * @param start true/false start/stopp
	 */
	public void timer( boolean start ) {
		if (start == true)
			startTime = new Date();
		if (start == false) {
			stoppTime = new Date();
			long lang = stoppTime.getTime() - startTime.getTime();
			sekunderBrukt = (int)(lang / 1000);
		}
	}

	/**
	 * Oppdaterer tittel med trekk og sekunder.
	 * Sekunder er ikke live dessverre! Trenger vel ny Thread da? til sjekkliste->[ ].
	 */
	public void oppdaterTittel() {
		try {
			if (oppdateringAvTittel)
			{
				// Må sjekkes dessverre
				if (vindu != null)
				{
					// Må sjekkes dessverre
					if (vindu.getLerret() != null)
					{
						this.vindu.setTitle("Kabal, trekk "+bord.getAntallTrekk()/*+", sekunder "+getSekunder()*/);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * GETTERS / SETTERS
	 */

	public Bord getBord() {
		return bord;
	}

	public Vindu getVindu() {
		return vindu;
	}

	public int getTrekkKort() {
		return trekkKort;
	}
	public void setTrekkKort( int i ) {
		this.trekkKort = i;
	}

	public boolean isInternettOgDatabase() {
		return internettOgDatabase;
	}

	public boolean isBilderAvOss() {
		return bilderAvOss;
	}
	public void setBilderAvOss( boolean b ) {
		this.bilderAvOss = b;
	}

	public boolean isOppdateringAvTittel() {
		return oppdateringAvTittel;
	}
	public void setOppdateringAvTittel( boolean b ) {
		this.oppdateringAvTittel = b;
	}

	public boolean isTesting() {
		return testing;
	}
	public void setTesting( boolean b ) {
		this.testing = b;
	}

	public int getSekunder() {
		stoppTime = new Date();
		long lang = stoppTime.getTime() - startTime.getTime();
		int sekunder = (int)(lang / 1000);
		return sekunder;
	}
	public int getSekunderBrukt() {
		return sekunderBrukt;
	}

	public void setTittel(String tittel) {
		this.tittel = tittel;
	}

	public String getBildePakke() {
		return bildePakke;
	}

}

/*
class TimerThread extends Thread
{
	Kabal kabal = null;

	public TimerThread()
	{
		super();
	}

	public void run() {
		if (kabal != null)
			kabal.oppdaterTittel();
		try {
			TimerThread.sleep((int)(Math.random() * 1000));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setObject( Kabal kabal ) {
		this.kabal = kabal;
	}
}
 */
