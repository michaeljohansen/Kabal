package data;

import javax.imageio.ImageIO;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import kabal.Kabal;

import enums.*;
import gui.Bunke;

/**
 * <h3>Objektklasse for hvert Kort.</h3>
 * 
 * <p>Har metoder for å hente navn og filnavn for forskjellige kort,
 * samt flere metoder for å sammenligne to kort mot hverandre.</p>
 * 
 * @author Michael Johansen
 * @author Anja Rønning
 * @author Cathrine Holm
 * @version 0.9
 */
public class Kort
{
	private Kabal kabal;

	private int nummer;
	private Slag slag;
	private Vis vis;
	private BufferedImage bilde;
	private int X,Y;
	private boolean musOverKort;
	private String bildePakke;

	/**
	 * Constructor for de 52 kortbildene
	 * 
	 * Alle kortene har i tillegg en X og Y variabel som beskriver hvor
	 * på lerretet det skal tegnes. Den X og Y'en blir satt av bunkens
	 * paint metode, ettersom hvert bunkeobjekt vet hvor kortene skal
	 * være i lerretet. paint() metoden (i bunke) setter da også offset
	 * for hvert kort i forhold til bunkens størrelse.
	 * 
	 * @param nummer
	 * @param slag
	 */
	public Kort( Kabal kabal, Slag slag, int nummer ) 
	{
		this.kabal = kabal;

		this.nummer = nummer;
		this.slag = slag;
		this.vis = Vis.NEI;
		this.musOverKort = false;
		this.bildePakke = kabal.getBildePakke();
		skjulKort();
	}

	/**
	 * Viser kortets bilde
	 */
	public void visKort()
	{
		this.vis = Vis.JA;
		try {
			this.setBilde( ImageIO.read( getClass().getResource( bildePakke + toFilename() )));
		}
		catch (IOException e) {
			System.out.println(e.toString());
		}
	}

	/**
	 * Skjuler kortets bilde
	 * 
	 * Husk at denne kjøres på alle kort som ligger ned når spillet startes.
	 */
	public void skjulKort()
	{
		this.vis = Vis.NEI;

		String[] oss = {"anja","cathrine","michael"};
		int tilfeldig = (int)((Math.random())*3);
		try {
			if (kabal.isBilderAvOss())
				this.setBilde( ImageIO.read( getClass().getResource( bildePakke + oss[tilfeldig] + ".bmp" )));
			/*TODO: REMOVED  else if (kabal.isTregData())
				this.setBilde( ImageIO.read( getClass().getResource( bildePakke + "baksideTreg.jpg" ) ));*/
			else
				this.setBilde( ImageIO.read( getClass().getResource( bildePakke + "bakside.bmp" ) ));
		}
		catch(Exception e) {
			System.out.println(e.toString());
		}
	}

	/**
	 * @return "Diamonds"
	 */
	public String navnForSlagbokstav() {
		Slag eiEnum = this.getSlag();
		switch (eiEnum) {
		case C: return "Clubs";
		case D: return "Diamonds";
		case H: return "Hearts";
		case S: return "Spades";
		default: return "ERROR: ugyldig bokstav i kortet";
		}
	}

	/**
	 * @return "Eight"
	 */
	public String navnForNummer() {
		int nummer = this.getNummer();
		switch (nummer) {
		case 0: return "Ace";
		case 1: return "Two";
		case 2: return "Three";
		case 3: return "Four";
		case 4: return "Five";
		case 5: return "Six";
		case 6: return "Seven";
		case 7: return "Eight";
		case 8: return "Nine";
		case 9: return "Ten";
		case 10: return "Knight";
		case 11: return "Queen";
		case 12: return "King";
		default: return "ERROR: ugyldig nummer i kortet";
		}
	}

	/**
	 * @return "The Eight of Diamonds"
	 */
	public String toString() {
		String slagNavn = this.navnForSlagbokstav();
		String nummerNavn = this.navnForNummer();
		return (nummerNavn + " of " + slagNavn);
	}

	/**
	 * @return "13.bmp"
	 */
	public String toFilename() {
		int filenumber = 0;
		Slag eiEnum = this.getSlag();
		switch (eiEnum) {
		case C: filenumber = 0; break; 
		case D: filenumber = 13; break; 
		case H: filenumber = 26; break; 
		case S: filenumber = 39; break; 
		}
		filenumber += this.getNummer();
		filenumber++; // bildene starter på "1.jpg", ikke "0.jpg".
		return filenumber+".bmp";
	}

	/**
	 * Sjekker for kortslag om man kan legge this på kort.
	 * @return boolean
	 */
	public boolean compareSlag( Kort kort, boolean tabla ) {
		if (tabla)
		{
			switch (this.slag) {
			case D:;
			case H: return ( kort.slag == Slag.C || kort.slag == Slag.S );
			case C:;
			case S: return ( kort.slag == Slag.D || kort.slag == Slag.H );
			}
			return false;
		}
		else // fundament
		{
			return this.getSlag() == kort.getSlag();
		}
	}

	/**
	 * Sjekker for kortnummer om man kan legge this på kort.
	 * @return boolean
	 */
	public boolean compareNummer( Kort kort, boolean tabla ) {
		if (tabla)
		{
			return this.nummer == kort.nummer - 1;
		}
		else // fundament
		{
			return this.nummer == kort.nummer + 1;
		}
	}

	/**
	 * Bruker både compareSlag og compareNummer.
	 * @return boolean
	 */
	public boolean compareTo( Kort kort, boolean tabla ) {
		if (tabla)
		{
			return (compareSlag( kort, true ) && compareNummer( kort, true ));
		}
		else // fundament
		{
			return (compareSlag( kort, false) && compareNummer( kort, false ));
		}
	}

	/**
	 * Finner nummeret for et fundament, av hensyn til den automatiske
	 * oppleggingen som må vite hvor den skal legge essene.
	 * @return Nummeret på fundamentet
	 */
	public int finnRettFundamentNummer() {
		switch (this.slag) {
		case C: return 0;
		case D: return 1;
		case H: return 2;
		case S: return 3;
		default: System.out.println("FEIL: Kort.finnRettFundamentNummer returnerer feil!"); return 0;
		}
	}

	/*
	 * Getters / Setters
	 */

	public int getNummer() {
		return this.nummer;
	}
	public Slag getSlag() {
		return this.slag;
	}

	public boolean getVis() {
		return (vis == Vis.JA);
	}

	public void setBilde( BufferedImage bilde ) {
		this.bilde = bilde;
	}
	public BufferedImage getBilde() {
		return bilde;
	}

	public int getX() {
		return this.X;
	}
	public void setX(int x) {
		this.X = x;
	}
	public int getY() {
		return this.Y;
	}
	public void setY(int y) {
		this.Y = y;
	}

	public boolean getMusOverKort() {
		return this.musOverKort;
	}
	public void setMusOverKort( boolean b ) {
		this.musOverKort = b;
	}

	public Point getPoint() {
		return new Point( X,Y );
	}
	public Dimension getDimension() {
		return new Dimension( Bunke.KORTBREDDE, Bunke.KORTHOYDE );
	}
}
