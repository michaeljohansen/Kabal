package data;

import gui.Lerret;

import javax.swing.JOptionPane;

import kabal.Kabal;

/**
 * <h3>Automatikk</h3>
 * 
 * <p>Dette er en objektklasse for metodene som utfører automatikken
 * i kabalprogrammet.</p>
 * 
 * <p>Klassen benytter seg av "singleton" designmønsteret. Mer om dette her:
 * <a href="http://radio.weblogs.com/0122027/stories/2003/10/20/implementingTheSingletonPatternInJava.html">
 * http://radio.weblogs.com/0122027/stories/2003/10/20/implementingTheSingletonPatternInJava.html</a></p>
 * 
 * @author Michael Johansen
 * @author Anja Rønning
 * @author Cathrine Holm
 * @version 0.5
 */
public class Automatikk
{
	/**
	 * En holder for den unike singleton instansen.
	 */
	static private Automatikk _instans = null;

	/*
	 * Objektvariabler
	 */
	private Kabal kabal;
	private Bord bord;
	private Lerret lerret;
	private boolean brukerVilLeggeOppAlleKort;
	private boolean tvungneFundament;
	private Kort kort;

	/**
	 * Singleton Constructor
	 * @param kabal
	 */
	private Automatikk( Kabal kabal, Lerret lerret ) {
		this.kabal = kabal;
		this.bord = kabal.getBord();
		this.lerret = lerret;
		this.brukerVilLeggeOppAlleKort = true;
		this.tvungneFundament = true;
		this.kort = null;
	}

	/**
	 * <h3>instans</h3>
	 * 
	 * <p>Oppretter automatikkobjektet dersom det ikke er gjort, ellers returneres
	 * objektet som allerede er laget. Metoden kan også tvinge gjennom at et nytt
	 * automatikkobjekt skal lages og returneres.</p>
	 * 
	 * @param kabal
	 * @param lerret
	 * @param reset
	 * @return
	 */
	public static Automatikk instans( Kabal kabal, Lerret lerret, boolean reset )
	{
		try {
			if (reset)
			{
				_instans = new Automatikk( kabal, lerret );
				return _instans;
			}
			else
			{
				if (_instans == null)
				{
					_instans = new Automatikk( kabal, lerret );
				}
				return _instans;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * <h3>erDetFlerSkjulteKort</h3>
	 * 
	 * <p>Sjekker tabla[i] for kort som ligger med bildesiden ned.
	 * Dersom ett finnes returneres true. Sjekken gjøres hovedsaklig
	 * for brukerVilLeggeOppKort() metoden.</p>
	 */
	public boolean erDetFlerSkjulteKort()
	{
		try {
			for (Stokk stokk : bord.getTabla())
			{
				if (stokk.isEmpty() == false)
				{
					for (Kort kort : stokk)
					{
						if (kort.getVis() == false)
						{
							return true;
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * <h3>flyttKortTilFundament</h3>
	 * 
	 * Flytter mottatt kort til mottatt fundament, og vil deretter
	 * - oppdatere tittelen i vinduet
	 * - inkrementere antall trekk
	 * - oppdatere tegningen av lerretet
	 * @param kort Kortet som skal flyttes
	 * @param fundament Fundamentet som kortet skal flyttes til
	 */
	public void flyttKortTilFundament( Kort kort, Stokk fundament )
	{
		try {
			// Få det flyttet
			fundament.push( kort );

			// Setter tittel og inkrementerer antall trekk
			kabal.getVindu().setTitle("Kabal, flytter "+kort);
			bord.inkrementerTrekk();

			// Oppdaterer tegningen
			lerret.oppdaterVedAutomatiskFlytting();

			// Vent bittelitt bare for syns skyld
			Thread.sleep(30);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * <h3>leggOppAlleKort</h3>
	 * 
	 * <p> Legger opp alle kortene automatisk, men kun dersom alle
	 * kortene i tablaene vises. Merk at det kan være kort igjen i
	 * trekkBunke og kasteBunke, disse vil også flippes og legges inn.</p>
	 * 
	 * <p>bruker erDetFlerSkjulteKort() metoden aktivt.</p>
	 * </p>
	 */
	public void leggOppAlleKort()
	{
		try {
			if (brukerVilLeggeOppAlleKort == true)
			{
				// Vi oppdaterer slik at draBunken blir "lagt ned og tegnet ordentlig"
				lerret.oppdaterLerret();

				if (JOptionPane.showConfirmDialog( kabal.getVindu(), "Legge opp kortene automagisk?", "Automatikk", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION )
				{
					leggOppKort( true );
				}
				// Dersom bruker svarer nei, ikke spør igjen.
				else 
				{
					brukerVilLeggeOppAlleKort = false;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/** 
	 * <h3>leggOppKort</h3>
	 * 
	 * <p>Ved LeggOppAlle: Legger opp alle kort fra kastebunke og tabla,
	 * samt flipper kort i trekkbunken på egen hånd.</p>
	 * 
	 * <p>Ved IKKE LeggOppAlle: Legger opp kort med bildesiden opp som
	 * ligger i tabla dersom høyre museknapp klikkes.</p>
	 */
	public void leggOppKort( boolean leggOppAlle )
	{
		try {
			for (int i = 0; i < 24; i++)
			{
				// Kjører gjennom trekkbunken
				if (leggOppAlle)
				{
					// Trekker nytt kort fra trekkbunken dersom
					// trekk/kaste-bunke ikke er tomme
					if (bord.getTrekkBunke().isEmpty() == false)
					{
						bord.trekkKort();
					}
					else {
						bord.snuKasteBunken();
					}

					// Oppdaterer tegningen
					lerret.oppdaterVedAutomatiskFlytting();
				}

				// Legger opp kort fra kastebunke
				for (Stokk fundament : bord.getFundament())
				{
					if (bord.getKasteBunke().isEmpty() == false)
					{
						// Hvis man legger et ess fra kastebunke på et tomt fundament
						if (bord.getKasteBunke().peekAtEnd().getNummer() == 0 && fundament.isEmpty())
						{
							// Hvis tvungne fundament må vi...
							if (tvungneFundament)
							{
								// ...legge esset på riktig fundament, ikke bare hvor som helst
								if (bord.getKasteBunke().peekAtEnd().finnRettFundamentNummer() == fundament.getNummer())
								{
									// Flytter kortet
									kort = bord.getKasteBunke().pop();
									flyttKortTilFundament( kort, fundament );
								}
							}
							// Ellers legg esset på hvilket som helst fundament
							else
							{
								// Flytter kortet
								kort = bord.getKasteBunke().pop();
								flyttKortTilFundament( kort, fundament );
							}
						}
						else if (fundament.isEmpty() == false)
						{
							if (bord.getKasteBunke().peekAtEnd().compareTo(fundament.peekAtEnd(), false))
							{
								// Flytter kortet
								kort = bord.getKasteBunke().pop();
								flyttKortTilFundament( kort, fundament );
							}
						}
					}
					// Sjekk om spillet er ferdig
					kabal.spillFerdig();
				}

				// Legger opp kort fra tablaene
				for (Stokk tabla : bord.getTabla())
				{
					for (Stokk fundament : bord.getFundament())
					{
						if (tabla.isEmpty() == false)
						{
							kort = tabla.peekAtEnd();

							if (kort.getVis())
							{
								// Hvis man legger et ess fra tabla på et tomt fundament
								if (tabla.peekAtEnd().getNummer() == 0 && fundament.isEmpty())
								{
									// Hvis tvungne fundament må vi...
									if (tvungneFundament)
									{
										// ...legge esset på riktig fundament, ikke bare hvor som helst 
										if (tabla.peekAtEnd().finnRettFundamentNummer() == fundament.getNummer())
										{
											// Flytter kortet
											kort = tabla.pop();
											flyttKortTilFundament( kort, fundament );
										}
									}
									// Ellers legg esset på hvilket som helst fundament
									else
									{
										// Flytter kortet
										kort = tabla.pop();
										flyttKortTilFundament( kort, fundament );
									}
								}
								else if (fundament.isEmpty() == false)
								{
									if (tabla.peekAtEnd().compareTo(fundament.peekAtEnd(), false))
									{
										// Flytter kortet
										kort = tabla.pop();
										flyttKortTilFundament( kort, fundament );
									}
								}
							}
						}
						// Sjekk om spillet er ferdig
						kabal.spillFerdig();
					}
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * Getters / Setters
	 */

	public boolean brukerVilLeggeOppAlleKort() {
		return brukerVilLeggeOppAlleKort;
	}

	public void setBrukerVilLeggeOppAlleKort(boolean brukerVilLeggeOppAlleKort) {
		this.brukerVilLeggeOppAlleKort = brukerVilLeggeOppAlleKort;
	}

	public boolean tvungneFundament() {
		return tvungneFundament;
	}

	public void setTvungneFundament(boolean tvungneFundament) {
		this.tvungneFundament = tvungneFundament;
	}

}
