package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import data.Kort;
import data.Stokk;

import enums.Slag;


/**
 * Objektklasse for hver bunke.
 * 
 * Bunkeobjektene eksisterer kun for å tegnes til GUI, de endres ikke!
 * Alle endringer gjøres derimot i logikkpakken. Se Bord.java
 * 
 * Det er KRITISK å vite dette for å forstå hvordan programmet er satt
 * sammen. Det er Stokk.java som holder stokkene med de kortene i minnet,
 * Bunke.java brukes kun for å tegne stokkene!
 * 
 * @author Michael Johansen
 * @author Anja Rønning
 * @author Cathrine Holm
 * @version 0.7
 */
public class Bunke
{
	private Lerret lerret;

	// Kortliste
	private ArrayList<Kort> liste;

	// Plassering
	private int Y;
	private int X;

	// Nummer på stack
	private int nummer;

	// Størrelse
	public static final int KORTBREDDE = 71;
	public static final int KORTHOYDE = 96;
	public static final int KORTFORSKYVNING = 2;
	public static final int KORTMELLOMROM = 17;
	public static final int DRAMELLOMROM = 17;

	// Muserektangel
	private Rectangle museRektangel;

	// Typesjekking
	private String type;
	
	// Referanse til bildepakken
	String bildePakke;

	// Til paint
	Kort kort = null;
	int lolteller = 0;

	/**
	 * Constructor for trekkBunke og kasteBunke (arraylists)
	 * @param y Y-plassering på lerretet
	 * @param x X-plassering på lerretet
	 * @param type Hvilken type bunke dette er
	 * @param kortene ArrayList med kortene fra logikken
	 */
	public Bunke(Lerret lerret, int y, int x, String type, Stokk kortene)
	{
		this.lerret = lerret;
		this.Y = y;
		this.X = x;
		this.type = type;

		// Lån inn kortene fra logikken (listen skal kun vises)
		this.liste = new ArrayList<Kort>(kortene.getListe());

		// Printer ut innholdet i bunken når den er laget
		//testprint();
	}

	/**
	 * Constructor for fundament og tabla (arraylist og nummer)
	 * @param y Y-plassering på lerretet
	 * @param x X-plassering på lerretet
	 * @param type Hvilken type bunke dette er
	 * @param kortene ArrayList med kortene fra logikken
	 */
	public Bunke(Lerret lerret, int y, int x, String type, Stokk kortene, int nummer)
	{
		this.lerret = lerret;
		this.Y = y;
		this.X = x;
		this.type = type;
		this.nummer = nummer;

		// Lån inn kortene fra logikken (listen skal kun vises)
		this.liste = new ArrayList<Kort>(kortene.getListe());

		// Printer ut innholdet i bunken når den er laget
		//testprint();
	}

	/**
	 * Testprint
	 */
	public void testprint()
	{
		System.out.println("\nTESTPRINT AV "+this+", KORT:"+this.liste.size());
		for (Kort kort : liste) {
			System.out.println( kort );
		}
	}

	/**
	 * Tegner kortene på lerretet, kalles fra painten i lerretet!
	 * 
	 * Alle kortene må settes med sin egen X og Y.
	 * 
	 * X'en er som regel samme som bunken sin X, Y'en er gjerne variabel
	 * da flere kort skal vises nedover samme rekke.
	 * 
	 * @param g2
	 */
	public void paint( Graphics g )
	{
		Graphics2D g2 = (Graphics2D)g;
		
		try {
			bildePakke = lerret.getKabal().getBildePakke();
			
			for (int i = 0; i < liste.size(); i++)
			{
				kort = liste.get(i);

				if (type.equals("trekkBunke") || type.equals("kasteBunke"))
				{
					kort.setX( X+i*KORTFORSKYVNING );
					kort.setY( Y+i*KORTFORSKYVNING );
					g2.drawImage( kort.getBilde(), kort.getX(), kort.getY(), null );
				}
				else if (type.equals("fundament"))
				{
					kort.setX( X+i*KORTFORSKYVNING/2 );
					kort.setY( Y+i*KORTFORSKYVNING/2 );
					g2.drawImage( kort.getBilde(), kort.getX(), kort.getY(), null );
				}
				else if (type.equals("tabla"))
				{
					if (kort.getMusOverKort() == true)
						if (kort.getVis() == true)
							g2.drawImage( lerret.getBakgrunner().get("bunke-ramme"), getX()-Lerret.KANTX, getY()-Lerret.KANTY+i*KORTMELLOMROM, null);
					kort.setX( X );
					kort.setY( Y+i*KORTMELLOMROM );
					g2.drawImage( kort.getBilde(), kort.getX(), kort.getY(), null );
				}
				else if (type.equals("draBunke"))
				{
					kort.setX( X );
					kort.setY( Y+i*KORTMELLOMROM );
					// Kortene til draBunken tegnes av paint() metoden i lerretet
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Oppdaterer museRektangelet for hvert kort
	 * 
	 * Muserektanglene utvides med U (int) mot venstre og opp, og
	 * med UU mot høyre og ned. Dette gjør det lettere å treffe kortene
	 * når du flytter bunker rundt.
	 */
	public void oppdaterMuseRektangel()
	{
		// U er utvidelse av muserektangler på hver side av kortet
		int U = 15;
		int UU = U*2;
		
		if (liste.isEmpty())
		{
			museRektangel = new Rectangle( X,Y, KORTBREDDE,KORTHOYDE );
		}
		else if (type.equals("trekkBunke") || type.equals("kasteBunke"))
		{
			// flyttes KORTFORSKYVNING bortover X og Y for hvert kort
			museRektangel = new Rectangle( X,Y, KORTBREDDE+liste.size()*KORTFORSKYVNING,KORTHOYDE+liste.size()*KORTFORSKYVNING );
		}
		else if (type.equals("fundament"))
		{
			// forstørres KORTMELLOMROM nedover for hvert kort
			museRektangel = new Rectangle( X-U,Y-U, KORTBREDDE+UU,KORTHOYDE+UU );
		}
		else if (type.equals("tabla"))
		{
			// forstørres KORTMELLOMROM nedover for hvert kort
			museRektangel = new Rectangle( X-U,Y-U, KORTBREDDE+UU,KORTHOYDE+liste.size()*KORTMELLOMROM+UU );
		}
		else
		{
			System.out.println("FEIL: Bunke.java:oppdaterMuseRektangel");
		}
	}

	/**
	 * Returnerer fundamentnummer for et gitt slag
	 * 
	 * Brukes kun dersom bruker er tvunget til å legge et gitt slag
	 * på et gitt fundament (for å stemme med bildene i fundamentet.
	 * 
	 * @return int Fundamentnummeret
	 */
	public boolean sjekkFundamentNummer( Kort kort ) {
		switch( this.getNummer() ) {
		case 0: return kort.getSlag() == Slag.C;
		case 1: return kort.getSlag() == Slag.D;
		case 2: return kort.getSlag() == Slag.H;
		case 3: return kort.getSlag() == Slag.S;
		default: System.out.println("FEIL: Bunke.java:sjekkFundamentNummer()");
		}
		return false;
	}

	/*
	 * GETTERS / SETTERS
	 */

	public void setListe(Stokk liste) {
		this.liste = new ArrayList<Kort>(liste.getListe());
	}

	public ArrayList<Kort> getListe() {
		return liste;
	}

	public int getX() {
		return X;
	}

	public int getY() {
		return Y;
	}

	public int getNummer() {
		return nummer;
	}

	public Rectangle getMuseRektangel() {
		return museRektangel;
	}

	public String toString() {
		return type;
	}

}
