package data;

import java.util.ArrayList;
import java.util.Collections;

import kabal.Kabal;

import enums.Slag;

/**
 * <h3>Bord</h3>
 * 
 * <p>Oppretter minneplasser til de forskjellige kortbunkene</p>
 * 
 * <p>trekkBunke og kasteBunke er enkle arraylists med queue- og
 * stack-egenskaper. fundament og tabla er arraylists med flere
 * av disse queue/stack-arraylistene i seg.</p>
 * 
 * @author Michael Johansen
 * @author Anja Rønning
 * @author Cathrine Holm
 * @version 0.8
 */
public class Bord 
{
	private Kabal kabal;

	// Bordets objekter
	private Stokk trekkBunke;
	private Stokk kasteBunke;
	private ArrayList<Stokk> fundament;
	private ArrayList<Stokk> tabla;
	private Stokk draBunke;
	
	// Antall trekk, og bonuser
	private int antallTrekk;
	private int trekk3bonus = 25;
	private int trekk5bonus = 50;

	//TODO Bordets liste med alle utførte trekk for save/load
	//private Stack<Bord> lagreStakk;

	// Midlertidige variabler
	private Kort kort;

	/**
	 * <h3>Constructor</h3>
	 * 
	 * <p>Oppretter trekkBunke, kasteBunke, fundament, tabla og draBunke</p>
	 * 
	 * @param kabal Klassen som kalte denne
	 */
	public Bord( Kabal kabal )
	{
		this.kabal = kabal;

		// Oppretter arraylists med queue- og stack-egenskaper
		trekkBunke = new Stokk(0);
		kasteBunke = new Stokk(0);
		fundament = new ArrayList<Stokk>(4);
		tabla = new ArrayList<Stokk>(7);
		draBunke = new Stokk(0);

		for (int i=0; i<4; i++)
			fundament.add(new Stokk(i));
		for (int i=0; i<7; i++)
			tabla.add( new Stokk(i) );

		// Lager kort og deler ut til arraylistene vi har laget
		lagKortOgDelUt();
		
		// Bestem start for antall trekk (bonus avregnes her)
		if (kabal.getTrekkKort() == 5)
			antallTrekk = 0 - trekk5bonus;
		else if (kabal.getTrekkKort() == 3)
			antallTrekk = 0 - trekk3bonus;
		else
			antallTrekk = 0;
		
		// Initialiserer lagreStakken og lagrer første state for save/load
		//TODO lagreStakk = new Stack<Bord>();
		//lagreStakk.push( this );
	}

	/**
	 * <h3>Constructor for duplisering</h3>
	 * 
	 * <p>Oppretter et nytt bordobjekt med trekkBunke, kasteBunke,
	 * fundament, tabla og draBunke fra et annet bord.</p>
	 * 
	 * @param bord Bordet som skal dupliseres
	 */
	public Bord( Bord bord )
	{
		// Oppretter arraylists med queue- og stack-egenskaper
		trekkBunke = new Stokk(0);
		kasteBunke = new Stokk(0);
		fundament = new ArrayList<Stokk>(4);
		tabla = new ArrayList<Stokk>(7);
		draBunke = new Stokk(0);

		for (int i=0; i<4; i++)
			fundament.add(new Stokk(i));
		for (int i=0; i<7; i++)
			tabla.add( new Stokk(i) );

		// Dupliserer over i lagrekartet
		for (Kort kort : bord.getTrekkBunke())
			trekkBunke.add(kort);
		for (Kort kort : bord.getKasteBunke())
			kasteBunke.add(kort);
		for (Stokk stokk : bord.getFundament())
			for (Kort kort : stokk)
				fundament.get(stokk.getNummer()).add(kort);
		for (Stokk stokk : bord.getTabla())
			for (Kort kort : stokk)
				tabla.get(stokk.getNummer()).add(kort);
		
		// Dupliser over antall trekk
		antallTrekk = bord.getAntallTrekk();
	}

	/**
	 * <h3>Opprett kortene med nummer og slag</h3>
	 * 
	 * <p>Den <u>viktigste</u> metoden i Bord.java</p>
	 * 
	 * <p>
	 * - Lager en kortstokk med de 52 kortene<br>
	 * - Stokker kortstokken<br>
	 * - Deler kortene ut til trekkBunke og tabla'ene</p>
	 */
	public void lagKortOgDelUt()
	{
		// Lag en kortstokk 
		ArrayList<Kort> kortStokk = new ArrayList<Kort>();

		// Lager alle kortene og legger dem i kortstokken.
		// Kortene legges inn med fronten ned så
		// bilder av oss vises dersom den bool'en er true.
		for (Slag slag : Slag.values()) {
			for (int i=0; i<=12; i++) {
				kortStokk.add( new Kort( kabal, slag, i ) );
			}
		}

		if (kabal.isTesting())
		{
			// START TESTING
			for (Stokk stokk : tabla)
				for (int k=0; k<13; k++)
					if (!kortStokk.isEmpty())
						stokk.push(kortStokk.remove(kortStokk.size()-1));
			// END TESTING
		}
		else
		{
			// Stokke, stokke, stokke
			for (int i=0; i<100; i++)
				Collections.shuffle( kortStokk );

			// Send de 24 øverste kortene til trekkBunken
			for (int i=0; i<24; i++) {
				Kort kort = kortStokk.remove( kortStokk.size()-1 );
				trekkBunke.offer( kort );
			}

			// Send de 28 resterende kortene til tabla
			for (int i=6; i>-1; i--) {
				for (int j=0; j<=i; j++) {
					Kort kort = kortStokk.remove( kortStokk.size()-1 );
					tabla.get(i).push( kort );
				}
			}
		}

		/*
		 * Alle kortene er nå delt ut til trekkBunken og tablaet,
		 * og alle kortene ligger med fronten ned. Vi må derfor
		 * flippe det nederste kortet på hvert tabla.
		 */

		if (kabal.isTesting())
		{
			// START TESTING
			for (Stokk stakk : tabla)
				for (Kort kort : stakk)
					kort.visKort();
			// END TESTING
		}
		else
		{
			// Vis de nederste kortene på hver stakk i tablaet
			for (Stokk stakk : tabla) {
				// pop fra stakk
				Kort kort = stakk.pop();
				// flipp kortet
				kort.visKort();
				// push tilbake til stakk
				stakk.push( kort );
			}
		}
	}

	/**
	 * Overfører ett kort fra trekkBunken til kasteBunken
	 */
	public void trekkKort() {
		for (int i = 0; i < kabal.getTrekkKort(); i++)
		{
			// Poll fra trekkBunke
			kort = trekkBunke.pop();
			if (kort != null)
			{
				kort.visKort();
				// Offer til kasteBunke
				kasteBunke.offer( kort );
			}
		}
	}

	/**
	 * Alle kort legges fra kasteBunken tilbake til trekkBunken
	 */
	public void snuKasteBunken() {
		while ((kort = kasteBunke.pop()) != null)
		{
			kort.skjulKort();
			trekkBunke.offer( kort );
		}
	}

	/**
	 * Flytter ett kort fra kasteBunken til draBunken
	 */
	public void draEttKortFraKasteBunken() {
		// Plukk av det øverste kortet
		kort = kasteBunke.remove( kasteBunke.size() - 1 );
		// Dytt det inn i draBunken
		draBunke.push( kort );
	}

	/**
	 * Flytter ett kort fra fundament[nummer] til draBunken
	 */
	public void draEttKortFraFundament( int nummer ) {
		// Plukk av det øverste kortet
		kort = fundament.get(nummer).pop();
		// Dytt det inn i draBunken
		draBunke.push( kort );
	}

//	public void save() {
//		try {
//			// Push duplikat av bordet til lagreStakken
//			lagreStakk.push( new Bord(this) );
//			System.out.println("saving... "+lagreStakk.size());
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}
//	
//	public void load() {
//		try {
//			if (lagreStakk.isEmpty() == false)
//			{
//				System.out.println("loading... "+lagreStakk.size());
//				
//				// Hent ut bordet fra forrige state
//				Bord gammeltBord = lagreStakk.pop();
//
//				// Tøm bunkene i det nåværende bordet
//				this.trekkBunke.purge();
//				this.kasteBunke.purge();
//				for (int i=0; i<4; i++)
//					fundament.get(i).purge();
//				for (int i=0; i<7; i++)
//					tabla.get(i).purge();
//				
//				// Fyll bunkene i nåværende bord med innholdet fra forrige state
//				for (Kort kort : gammeltBord.getTrekkBunke())
//					trekkBunke.add(kort);
//				for (Kort kort : gammeltBord.getKasteBunke())
//					kasteBunke.add(kort);
//				for (Stokk stokk : gammeltBord.getFundament())
//					for (Kort kort : stokk)
//						fundament.get(stokk.getNummer()).add(kort);
//				for (Stokk stokk : gammeltBord.getTabla())
//					for (Kort kort : stokk) {
//						if (kort.getVis())
//							kort.visKort();
//						else
//							kort.skjulKort();
//						tabla.get(stokk.getNummer()).add(kort);
//					}
//				
//				// Henter trekkteller fra forrige state
//				antallTrekk = gammeltBord.getAntallTrekk();
//				
//				// Oppdater lerretet for å vise endringen
//				kabal.getVindu().getLerret().oppdaterLerret();
//			}
//			else
//			{
//				System.out.println("Tomt");
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}
	
	/**
	 * Overrider toString
	 * 
	 * @return String
	 */
	public String toString() {
		return "Bordobjekt";
	}

	/*
	 * Getters
	 */

	public Stokk getTrekkBunke() {
		return trekkBunke;
	}

	public Stokk getKasteBunke() {
		return kasteBunke;
	}

	public ArrayList<Stokk> getFundament() {
		return fundament;
	}

	public ArrayList<Stokk> getTabla() {
		return tabla;
	}

	public Stokk getDraBunke() {
		return draBunke;
	}

	public int getAntallTrekk() {
		return antallTrekk;
	}

	public void setAntallTrekk( int i ) {
		this.antallTrekk = i;
	}
	
	public void inkrementerTrekk() {
		this.antallTrekk++;
	}
	
	public int getTrekk3Bonus() {
		return trekk3bonus;
	}
	
	public int getTrekk5Bonus() {
		return trekk5bonus;
	}
	
}
