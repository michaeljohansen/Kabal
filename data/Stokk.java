package data;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <h3>Stokk</h3>
 * 
 * <p>Egen implementasjon av ArrayList&lt;Kort&gt; slik at vi kan
 * gi listen queue og stack egenskaper som offer(), peek(), poll(),
 * push() og pop() med fler.</p>
 * 
 * <p><b>Hvorfor ikke extende arraylist?</b><br>
 * Man bør aldri extende innebygde typer i Java, det er
 * ikke god kodeskikk, da typene kan endres. Det er derfor
 * bedre å ha en arraylist med i objektet, og heller la
 * metodene til Stokk-objektet virke direkte på arraylisten.</p>
 * 
 * <p><b>Hvorfor implementere Iterable?</b><br>
 * For da kan vi bruke for-løkker av denne typen:
 * for (Kort kort : stokk) på Stokk-objekter.</p>
 * 
 * @author Michael Johansen
 * @author Anja Rønning
 * @author Cathrine Holm
 * @version 0.5
 */
public class Stokk implements Iterable<Kort>
{
	private int nummer;
	private ArrayList<Kort> arraylist;
	
	/**
	 * <h3>Constructor</h3>
	 * 
	 * <p>Vi har med nummer av hensyn til den automatiske oppleggingen
	 * som må kunne kjenne igjen nummeret på fundamentene.</p>
	 */
	public Stokk( int n ) {
		this.nummer = n;
		this.arraylist = new ArrayList<Kort>();
	}
	
	/**
	 * <h3>Legger til kort i arraylisten</h3>
	 * @param kort
	 */
	public void add( Kort kort ) {
		arraylist.add( kort );
	}
	public void add( int i, Kort kort ) {
		arraylist.add( i, kort );
	}
	
	/**
	 * <h3>Tar ut et kort i arraylisten</h3>
	 * @param nummer
	 */
	public Kort remove( int i ) {
		return arraylist.remove( i );
	}
	
	/**
	 * <h3>Henter et kort i arraylisten</h3>
	 * @param nummer
	 */
	public Kort get( int i ) {
		return arraylist.get( i );
	}
	
	/**
	 * <h3>Returnerer størrelsen på stokken</h3>
	 * @return arraylist.size()
	 */
	public int size() {
		return arraylist.size();
	}
	
	/**
	 * <h3>Sjekk om arraylisten er tom</h3>
	 * @return arraylist.isEmpty()
	 */
	public boolean isEmpty() {
		return arraylist.isEmpty();
	}
	
	/**
	 * <h3>Offer kort til køen</h3>
	 * @param kort
	 */
	public void offer(Kort kort) {
		arraylist.add( kort );
	}
	
	/**
	 * <h3>Peek på neste kort i køen</h3>
	 * 
	 * <p>Her må det try/catches fordi "get(int)" metoden thrower exception</p>
	 * 
	 * @return Kort
	 */
	public Kort peekAtStart() {
		try {
			return arraylist.get( 0 );
		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
	}
	
	/**
	 * <h3>Peek på siste kort i køen</h3>
	 * 
	 * <p>Her må det try/catches fordi "get(int)" metoden thrower exception</p>
	 * 
	 * @return Kort
	 */
	public Kort peekAtEnd() {
		try {
			return arraylist.get( arraylist.size()-1 );
		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
	}
	
	/**
	 * <h3>Poll neste kort fra køen</h3>
	 * 
	 * <p>Her må det try/catches fordi "remove(int)" metoden thrower exception.</p>
	 * 
	 * @return Kort
	 */
	public Kort poll() {
		try {
			return arraylist.remove( 0 );
		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
	}
	
	/**
	 * <h3>Pop neste kort fra køen</h3>
	 * 
	 * <p>Dette er en stack-metode. Den må være her slik at vi kan trekke
	 * fra toppen av trekkBunken i stedet for bunnen.</p>
	 * 
	 * @return Kort
	 */
	public Kort pop() {
		try {
			return arraylist.remove( arraylist.size()-1 );
		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
	}
	
	/**
	 * <h3>Push kort til køen</h3>
	 * 
	 * <p>Dette er en stack-metode. Den må være her slik at vi kan pushe
	 * til toppen av trekkBunken i stedet for bunnen.</p>
	 */
	public void push( Kort kort ) {
		arraylist.add( arraylist.size(), kort );
	}
	
	/**
	 * <h3>Purge alle kort fra stokken</h3>
	 * 
	 * <p>Dette er en hjemmelaga metode. Den er her pga save/load
	 * funksjonene til bordet.</p>
	 */
	public void purge() {
		try {
			while ( arraylist.size() != 0)
				arraylist.remove( arraylist.size() - 1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/*
	 * Iterator
	 */

	@Override
	public Iterator<Kort> iterator() {
		return arraylist.iterator();
	}
	
	/*
	 * Getters / Setters
	 */
	
	public int getNummer() {
		return this.nummer;
	}
	
	public ArrayList<Kort> getListe() {
		return this.arraylist;
	}

}
