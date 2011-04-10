package gui;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import data.Automatikk;
import data.Bord;
import data.Kort;
import data.Stokk;

import enums.Slag;

import kabal.Kabal;

/**
 * <h3>Lerret</h3>
 * 
 * <p>Objektklasse for Lerretet som extender Canvas, og implementerer mouselisteners.</p>
 * 
 * <p>Lerretet har sine egne bunker for trekkbunke, kastebunke, fundamentene og 
 * tablåene. Disse bunkene er kopier av stokkene som ligger i datapakken, og
 * skal derfor ikke endres på. Listene over kort som ligger i lerretets bunker
 * vil bli oppdatert hver gang noe endres. Fordelen med dette har vært for
 * debugging, slik at ikke bunkene som vises på lerretet kan rote til stokkene
 * som ligger i datapakken.</p>
 * 
 * @author Michael Johansen
 * @author Anja Rønning
 * @author Cathrine Holm
 * @version 0.9
 */
@SuppressWarnings("serial")
public class Lerret extends Canvas implements MouseListener, MouseMotionListener
{
	private Kabal kabal;
	private Bord bord;
	private Automatikk automatikk;

	// Grafiske bunker
	private Bunke trekkBunke;
	private Bunke kasteBunke;
	private Bunke[] fundament;
	private Bunke[] tabla;
	private Bunke draBunke;
	private Bunke fraBunke;

	// Til paint() for plassering av kantbilder
	public static final int KANTX = 5;
	public static final int KANTY = 5;

	// Til paint() for avstand mellom bunkene
	public static final int TOPPREKKE = 30;
	public static final int BUNNREKKE = 200;
	public static final int VENSTRE = 40;
	public static final int PLASS = 105;

	// Til paint() for offset i draBunke
	protected int xOffset = 0;
	protected int yOffset = 0;

	// Til paint() for bilder
	private String bildePakke;
	private HashMap<String, BufferedImage> bakgrunner;
	private BufferedImage bakgrunn = null;
	private Kort kortMedMusOverSeg;

	// Til pressBunke for initialisering av draBunke
	protected Kort kort;

	// Innstillinger
	private boolean grayscale = false;
	private boolean vinduErFokusert = true;

	/**
	 * Constructor
	 * @param kabal
	 * @param bord
	 */
	public Lerret( Kabal kabal, Bord bord )
	{
		this.kabal = kabal;
		this.bord = bord;
		this.automatikk = Automatikk.instans( kabal, this, true );

		// Lag bunker ut ifra stokker i datapakken
		this.trekkBunke = new Bunke( this, TOPPREKKE,VENSTRE, "trekkBunke", bord.getTrekkBunke() );
		this.kasteBunke = new Bunke( this, TOPPREKKE,VENSTRE+PLASS+PLASS/2, "kasteBunke", bord.getKasteBunke() );
		this.fundament = new Bunke[4];
		for (int i=0; i<4; i++)
		{
			this.fundament[i] = new Bunke( this, TOPPREKKE,VENSTRE+3*PLASS+i*PLASS, "fundament", bord.getFundament().get(i), i );
		}
		this.tabla = new Bunke[7];
		for (int i=0; i<7; i++)
		{
			this.tabla[i] = new Bunke( this, BUNNREKKE,VENSTRE+i*PLASS, "tabla", bord.getTabla().get(i), i );
		}
		this.draBunke = new Bunke(this, 1, 1, "draBunke", new Stokk(0));
		this.fraBunke = null;

		// Legg til MouseListener
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		// Hent bilder
		this.hentBilder();
	}

	/**
	 * <h3>hentBilder</h3>
	 * 
	 * <p>Bilder til paintmetoden.</p>
	 */
	public void hentBilder()
	{
		try {
			bildePakke = kabal.getBildePakke();
			bakgrunner = new HashMap<String, BufferedImage>();

			bakgrunner.put( "bakgrunn-plain", ImageIO.read( getClass().getResource( bildePakke + "bakgrunn-plain.jpg" )));
			bakgrunner.put( "bakgrunn-plain-gray", ImageIO.read( getClass().getResource( bildePakke + "bakgrunn-plain-gray.jpg" )));
			bakgrunner.put( "bakgrunn-oss", ImageIO.read( getClass().getResource( bildePakke + "bakgrunn-oss.jpg" )));
			bakgrunner.put( "bakgrunn-oss-gray", ImageIO.read( getClass().getResource( bildePakke + "bakgrunn-oss-gray.jpg" )));
			bakgrunner.put( "bunke-trekkbunke", ImageIO.read( getClass().getResource( bildePakke + "bunke-trekkbunke.png" )));
			bakgrunner.put( "bunke-kastebunke", ImageIO.read( getClass().getResource( bildePakke + "bunke-kastebunke.png" )));
			bakgrunner.put( "bunke-tabla", ImageIO.read( getClass().getResource( bildePakke + "bunke-tabla.png" )));
			bakgrunner.put( "bunke-ramme", ImageIO.read( getClass().getResource( bildePakke + "bunke-ramme.png" )));
			bakgrunner.put( "fundament"+"C", ImageIO.read( getClass().getResource( bildePakke + "fundament-clubs.png" )));
			bakgrunner.put( "fundament"+"D", ImageIO.read( getClass().getResource( bildePakke + "fundament-diamonds.png" )));
			bakgrunner.put( "fundament"+"H", ImageIO.read( getClass().getResource( bildePakke + "fundament-hearts.png" )));
			bakgrunner.put( "fundament"+"S", ImageIO.read( getClass().getResource( bildePakke + "fundament-spades.png" )));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * <h3>oppdaterMuseRektangler</h3>
	 * 
	 * <p>Oppdater museRektangler.</p>
	 */
	public void oppdaterMuseRektangler() {
		try {
			trekkBunke.oppdaterMuseRektangel();
			kasteBunke.oppdaterMuseRektangel();
			for (int i=0; i<fundament.length; i++)
				fundament[i].oppdaterMuseRektangel();
			for (int i=0; i<tabla.length; i++)
				tabla[i].oppdaterMuseRektangel();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * <h3>oppdaterLister</h3>
	 * 
	 * <p>Oppdaterer arraylists i bunkeobjektene
	 * slik at de stemmer med datapakkens stokker.</p>
	 */
	public void oppdaterLister() {
		try {
			trekkBunke.setListe( bord.getTrekkBunke() );
			kasteBunke.setListe( bord.getKasteBunke() );
			for (int i=0; i<4; i++)
				fundament[i].setListe( bord.getFundament().get(i) );
			for (int i=0; i<7; i++)
				tabla[i].setListe( bord.getTabla().get(i) );
			draBunke.setListe( bord.getDraBunke() );
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * <h3>oppdaterLerret</h3>
	 * 
	 * <p>Oppdaterer alle lister og visninger!</p>
	 */
	public void oppdaterLerret() {
		try {
			this.oppdaterLister();
			this.oppdaterMuseRektangler();
			this.repaint();
			kabal.oppdaterTittel();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * <h3>oppdaterVedAutomatiskFlytting</h3>
	 * 
	 * <p>Her ligger magien bak kortene som flyttes automagisk!</p>
	 * 
	 * <p>Denne metoden oppdaterer bildet mellom hver gang
	 * automatikken flytter på kort.</p>
	 */
	public void oppdaterVedAutomatiskFlytting() {
		try {
			this.oppdaterLister();
			this.update( getGraphics() );
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	//ABOVE///////////// OBJEKTMETODER ///////////////////////
	//////////////////////////////////////////////////////////
	//BELOW//////////// PAINT / UPDATE ///////////////////////

	/**
	 * Tegner lerretet
	 * @param g2
	 */
	public void paint(Graphics g)
	{
		kabal.oppdaterTittel();

		Graphics2D g2 = (Graphics2D)g;

		try {
			/* Tegn bakgrunner og kortbilder */

			// Sett bakgrunn
			if (kabal.isBilderAvOss()) {
				if (grayscale)
					bakgrunn = bakgrunner.get("bakgrunn-oss-gray");
				else
					bakgrunn = bakgrunner.get("bakgrunn-oss");
			}
			else {
				if (grayscale)
					bakgrunn = bakgrunner.get("bakgrunn-plain-gray");
				else
					bakgrunn = bakgrunner.get("bakgrunn-plain");
			}
			g2.drawImage(bakgrunn, 0, 0, null);

			// Tegn trekkBunke
			g2.drawImage( bakgrunner.get("bunke-trekkbunke"), trekkBunke.getX()-KANTX, trekkBunke.getY()-KANTY, null);
			trekkBunke.paint(g2);

			// Tegn kasteBunke
			g2.drawImage( bakgrunner.get("bunke-kastebunke"), kasteBunke.getX()-KANTX, kasteBunke.getY()-KANTY, null);
			kasteBunke.paint(g2);

			// Tegn fundament
			Slag[] slag = Slag.values();
			for(int i = 0; i < fundament.length; i++) {
				g2.drawImage( bakgrunner.get("fundament"+slag[i]), fundament[i].getX(), fundament[i].getY(), null);
				fundament[i].paint(g2);
			}
			// Tegn tabla
			for (int i = 0; i < tabla.length; i++) {
				g2.drawImage( bakgrunner.get("bunke-tabla"), tabla[i].getX()-KANTX, tabla[i].getY()-KANTY, null);
				tabla[i].paint(g2);
			}

			// Tegner bunken som blir dratt rundt, her kommer catch'en av
			// NullPointerException inn slik at programmet ikke stopper
			// når et kort er utenfor skjermen, - da mister vi kortet.
			if (draBunke != null)
			{
				if (draBunke.getListe().size() != 0)
				{
					for (int i = 0; i < draBunke.getListe().size(); i++)
					{
						//System.out.println("xOffset:"+xOffset+" | yOffset:"+yOffset+" | draBunke.size:"+draBunke.getListe().size());
						g2.drawImage( draBunke.getListe().get(i).getBilde(), getMousePosition().x-xOffset, getMousePosition().y-yOffset+i*Bunke.DRAMELLOMROM, null); 
					}
				}
			}
		}
		catch (NullPointerException ex) {
			// Her dras kortene utenfor lerretet, det trenger vi ikke bry oss
			// om ettersom kortene spretter tilbake straks brukeren slipper dem
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * <h3>update</h3>
	 * 
	 * <p>Opprettholder bildet mellom hver gang paint-metoden
	 * tegner, slik at ikke bildet "skipper" til svart skjerm
	 * når man utfører en kommando.</p>
	 * 
	 * @param graphics Grafikken som skal tegnes (vi henter et bilde
	 * 		av hvordan det så ut i forrige frame tegnet av paint() )
	 */
	public void update( Graphics graphics )
	{
		kabal.oppdaterTittel();

		Graphics graphicsOffScreen;
		Image image = null;
		Dimension dimension = getSize();

		// Vi lager offscreen buffer
		image = createImage( dimension.width, dimension.height );
		graphicsOffScreen = image.getGraphics();

		// Modifiser offscreen grafikken
		graphicsOffScreen.setColor( getBackground() );
		graphicsOffScreen.fillRect(0, 0, dimension.width, dimension.height);
		graphicsOffScreen.setColor( getForeground() );

		// Tegn offscreen bildet på skjermen
		paint( graphicsOffScreen );
		graphics.drawImage( image, 0, 0, this );
	}

	/////////////////// PAINT / UPDATE ///////////////////////
	//////////////////////////////////////////////////////////
	////////////////// GET BUNKE AT POINT ////////////////////

	/**
	 * Returnerer Bunke fra gitt Point
	 * 
	 * @param point Musens posisjon
	 * @param type "Klikk"/"Press"
	 * @return <b>Bunke</b> Bunken på pointet
	 */
	public Bunke getBunkeAtPoint( Point point, String type )
	{
		try {
			if (type.equals("klikk"))
			{
				if (trekkBunke.getMuseRektangel().contains( point ))
					return trekkBunke;

				for (int i=0; i<tabla.length; i++)
					if (tabla[i].getMuseRektangel().contains( point ))
						return tabla[i];

				return null;
			}
			else if (type.equals("press"))
			{
				if (kasteBunke.getMuseRektangel().contains( point ))
					return kasteBunke;

				for (int i=0; i<fundament.length; i++)
					if (fundament[i].getMuseRektangel().contains( point ))
						return fundament[i];

				for (int i=0; i<tabla.length; i++)
					if (tabla[i].getMuseRektangel().contains( point ))
						return tabla[i];

				return null;
			}
			else
			{
				System.out.println("FEIL! Lerret.getBunkeAtPoint() mottok en feilaktig 'String type'");
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	//ABOVE/////////// GET BUNKE AT POINT ////////////////////
	//////////////////////////////////////////////////////////
	//BELOW//////////// MOUSE LISTENERS //////////////////////

	/**
	 * <h3>mouseClicked</h3>
	 * 
	 * <p>Tar seg av klikk på trekkBunken og kasteBunken.</p>
	 * 
	 * <p>Vi finner hvilken bunke som er trykket på vha.
	 * museposisjon. Deretter utfører vi handlinger på
	 * trekkBunke og kasetBunke med klikkBunke().</p>
	 * 
	 * @param event Museklikket
	 */
	public void mouseClicked( MouseEvent event )
	{
		try {
			int museklikk = event.getClickCount();

			Point point = event.getPoint();
			Bunke klikketBunke = getBunkeAtPoint( point, "klikk" );

			if (museklikk == 1 || klikketBunke.toString().equals("trekkBunke"))
			{
				if (event.getButton() == MouseEvent.BUTTON1)
				{
					if (klikketBunke != null)
					{
						// Kjører klikkBunke-metoden
						if (klikkBunke( klikketBunke, 1 ))
						{
							// Oppdater hvis pressBunke returnerte true (noe er endret)
							this.oppdaterLerret();
						}
					}
				}
				else if (event.getButton() == MouseEvent.BUTTON3)
				{
					automatikk.leggOppKort( false );
					
					//TODO: testing
					//kabal.getBord().save();
				}
				else if (event.getButton() == MouseEvent.BUTTON2 && event.isControlDown())
				{
					kabal.setTesting( !kabal.isTesting() );
					kabal.restartKabal();
					///////////////////////////////////FOR TESTING AV POENGTABELL //TODO: rufl!
					//new Dialog( kabal, "poengtabell" );
					/////mer må da settes under Dialog.java for at det skal funke
				}
			}
			else if (2 <= museklikk && museklikk <= 5)
			{
				System.out.println("tester multiple clicks");
				//TODO FIX FIX FIX
				if (klikketBunke != null)
				{
					// Kjører klikkBunke-metoden
					if (klikkBunke( klikketBunke, 2 ))
					{
						// Oppdater hvis pressBunke returnerte true (noe er endret)
						this.oppdaterLerret();
					}
				}
			}
			else // Mange museklikk, si ifra til bruker at hun/han må slutte å mase.
			{
				// Ved for mange klikk på en bunke ...
				// ...som ikke er trekkbunken (for den kan man klikke i hytt og pine)!
				JOptionPane.showMessageDialog( kabal.getVindu(), "Slutt å mase! Jeg får vondt i knappen.\nTo klikk holder lenge...", "Slutt!", JOptionPane.ERROR_MESSAGE);
			}
		} catch (NullPointerException ex) {
			//TODO: Ingenting.
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * <h3>mousePressed</h3>
	 * 
	 * <p>Tar seg av kort som skal dras fra kasteBunken, fundament
	 * eller tabla</p>
	 * 
	 * <p>Vi finner hvilken bunke som er trykket på vha museposisjon.
	 * Så må vi trekke ut kort og legge dem i draBunken. Deretter
	 * utfører vi handlinger på disse med pressBunke().</p>
	 * 
	 * @param event Musehandlingen, knappen trykkes og holdes nede.
	 */
	public void mousePressed( MouseEvent event )
	{
		try {
			if (event.getButton() == MouseEvent.BUTTON1)
			{
				Point point = event.getPoint();
				Bunke bunke = getBunkeAtPoint( point, "press" );

				if (bunke != null)
				{
					// Kjør pressBunke-metoden
					if ( pressBunke( bunke, event ) )
					{
						// Oppdater hvis pressBunke returnerte true (noe er endret)
						this.oppdaterLerret();
						// Merk at pressBunke() oppdaterer X/Y-offsets på egen hånd
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * <h3>mouseReleased</h3>
	 * 
	 * <p>Tar seg av en bunke med kort som dras og blir sluppet.</p>
	 * 
	 * <p>Vi finner hvilken bunke som er dratt til vha. museposisjon.
	 * Så må vi legge kortene fra draBunken over til "tilBunken".
	 * Deretter rydder vi opp ved å nullstille draBunke/fraBunke.</p>
	 * 
	 * @param event Musehandlingen, knappen slippes etter å ha vært holdt nede
	 */
	public void mouseReleased( MouseEvent event )
	{
		try {
			// Passe på om vi må reversere draBunken
			boolean reverserDraBunken = true;

			if (event.getButton() == MouseEvent.BUTTON1)
			{
				if (draBunke.getListe().isEmpty() == false && fraBunke != null)
				{
					Point point = event.getPoint(); 
					Bunke tilBunke = getBunkeAtPoint( point, "press" );

					if (tilBunke != null)
					{
						// Kjør releaseBunke-metoden
						if (releaseBunke( tilBunke, event ) )
						{
							// releaseBunke returnerte true, vi trenger ikke reversere draBunken
							reverserDraBunken = false;

							// Sjekk at det ikke er samme tabla som dras fra og flyttes til...
							if (fraBunke.toString().equals(tilBunke.toString()))
							{
								// ...før trekk legges til
								if (fraBunke.getNummer() != tilBunke.getNummer())
								{
									bord.inkrementerTrekk();

									//TODO: testing
									//kabal.getBord().save();
								}
							}
							// Hvis det ikke er to tabla involvert er det bare å legge til trekk
							else
							{
								bord.inkrementerTrekk();

								//TODO: testing
								//kabal.getBord().save();
							}
						}
					}
				}
			}
			// Reverser bunker
			if (reverserDraBunken == true)
			{
				while ((kort = bord.getDraBunke().poll()) != null)
				{
					// draBunken gir tilbake kortene til kilden for "fraBunken"
					getSource(fraBunke).offer( kort );
				}
			}
			// Oppdater alt og nullstill fraBunke (merk at draBunke er en del av de av
			// de faste bunkene, og vil derfor tømmes for kort, - men IKKE nullstilles).
			this.oppdaterLerret();
			fraBunke = null;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * <h3>mouseDragged</h3>
	 * 
	 * <p>Tegner opp lerretet på nytt kontinuerlig når vi drar en bunke.</p>
	 * 
	 * @param event Musehandlingen, musen dras med en bunke
	 */
	public void mouseDragged(MouseEvent event) {
		try {
			if(draBunke != null) {
				repaint();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * <h3>mouseMoved</h3>
	 * 
	 * <p>Lyser opp et kort i tabla[i] når vi holder over det.</p>
	 * 
	 * <p>Vi finner hvilket tabla som holdes over vha. museposisjon.
	 * Så må vi finne det aktuelle kortet i tabla. Opprydning ved å
	 * nullstille "boolean musOverKort" gjøres av paint().</p>
	 * 
	 * @param event Musehandlingen, musen flyttes på
	 */
	public void mouseMoved(MouseEvent event)
	{
		try {
			// Skru av highlighting av forrige kort som hadde mus over seg
			if (kortMedMusOverSeg != null) {
				kortMedMusOverSeg.setMusOverKort( false );
				this.oppdaterLerret();
			}

			Point point = event.getPoint();
			Bunke klikketBunke = getBunkeAtPoint( point, "klikk" );

			// finn hvilket kort vi holder over
			if (klikketBunke != null)
			{
				// Kjører klikkBunke-metoden
				if (musFlyttet( klikketBunke, event ))
				{
					// Oppdater hvis pressBunke returnerte true (noe er endret)
					this.oppdaterLerret();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * <h3>mouseEntered</h3>
	 * 
	 * <p>Brukes kun til bakgrunnseffekter.</p>
	 */
	public void mouseEntered(MouseEvent e) {
		try {
			if (vinduErFokusert == false) {
				this.setGrayscale(false);
				this.repaint();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * <h3>mouseExited</h3>
	 * 
	 * <p>Brukes kun til bakgrunnseffekter.</p>
	 */
	public void mouseExited(MouseEvent e) {
		try {
			if (vinduErFokusert == false) {
				this.setGrayscale(true);
				this.repaint();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	//ABOVE//////////// MOUSE LISTENERS //////////////////////
	//////////////////////////////////////////////////////////
	//BELOW//////////// KLIKKFUNKSJONER //////////////////////

	/**
	 * <h3>klikkBunke</h3>
	 * 
	 * <p>Dersom vi har registrert et klikk er det enten trekkBunken eller
	 * tabla vi skal endre på. Ved klikk på trekkBunke skal enten et nytt
	 * kort trekkes, ellers kasteBunken snus. Ved klikk på tabla må vi
	 * sjekke om nederste kortet på det tablaet må flippes (dersom det er
	 * skjult).</p>
	 * 
	 * @param klikketBunke Bunken som er klikket
	 * @param clicks Antall klikk på bunken
	 * @return <b>true/false</b> True om noe er blitt utført
	 */
	public boolean klikkBunke( Bunke klikketBunke, int clicks )
	{
		try {
			// Hvis trekkBunken er klikket
			if (klikketBunke.toString().equals("trekkBunke"))
			{
				// Sjekk om den er tom, og snu kasteBunken
				if (bord.getTrekkBunke().isEmpty())
					bord.snuKasteBunken();

				// Dersom trekkBunken ikke er tom, trekk kort
				else
					bord.trekkKort();

				return true;
			}
			// Hvis kasteBunken er...
			if (klikketBunke.toString().equals("kasteBunke"))
			{
				// ...dobbeltklikket
				if (clicks == 2)
				{
					if (bord.getKasteBunke().isEmpty() == false)
					{
						kort = bord.getKasteBunke().peekAtEnd();

						for (int i=0; i<bord.getFundament().size(); i++)
						{
							if (kort.compareTo( bord.getFundament().get(i).peekAtEnd(), false ))
							{
								// Dytt kortet fra kastsebunken over til riktig fundament
								kort = bord.getKasteBunke().pop();
								bord.getFundament().get(i).push( kort );
							}
						}
					}
				}
			}
			// Hvis tabla[i] er klikket
			else if (klikketBunke.toString().equals("tabla"))
			{
				// Finn nummeret på tablaet
				int nummer = klikketBunke.getNummer();

				// Hvis tablaet ikke er tomt...
				if (klikketBunke.getListe().isEmpty() == false)
				{
					// ...og det øverste kortet er skjult...
					if (klikketBunke.getListe().get( klikketBunke.getListe().size() - 1 ).getVis() == false)
					{
						// ...så snu det øverste kortet
						kort = bord.getTabla().get(nummer).pop();
						kort.visKort();
						bord.getTabla().get(nummer).push( kort );

						// Dersom bruker ønsker at alle kortene skal legges opp automagisk
						if (automatikk.brukerVilLeggeOppAlleKort()) {
							if (automatikk.erDetFlerSkjulteKort() == false) {
								automatikk.leggOppAlleKort();
							}
						}
						return true;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * <h3>pressBunke</h3>
	 * 
	 * <p>Dersom vi har registrert at kasteBunken, fundamentet eller tabla
	 * blir <u>klikket og holdt inne</u> (les: presset) må vi sjekke hva
	 * som skal dras.</p>
	 * 
	 * <p>Fra kasteBunken og fundament[i] er det kun mulig å dra ett kort
	 * av gangen. Fra tabla[i] må vi sjekke hvor mange kort som skal dras
	 * og hvor musehendelsen skjedde slik at vi vet hvilke kort som skal
	 * dras.</p>
	 * 
	 * @param bunke Bunken som er klikket
	 * @param event Musehendelsen som ble registrert
	 * @return <b>true/false</b> True om noe er blitt utført
	 */
	public boolean pressBunke( Bunke bunke, MouseEvent event )
	{
		try {
			// Plukk øverste kort
			if (bunke.toString().equals("kasteBunke"))
			{
				// Flytt ett kort fra kasteBunken til draBunken
				bord.draEttKortFraKasteBunken();

				// Oppretter draBunke med kasteBunkens X/Y plassering
				draBunke = new Bunke(this, bunke.getY(), bunke.getX(), "draBunke", bord.getDraBunke());
				// Husker på fraBunken
				fraBunke = kasteBunke;

				// Oppdater offsets slik at musen holder tak rett sted på kortet
				xOffset = event.getX() - bunke.getListe().get( bunke.getListe().size()-1 ).getX();
				yOffset = event.getY() - bunke.getListe().get( bunke.getListe().size()-1 ).getY();

				return true;
			}
			// Plukk øverste kort fra fundament[i] til draBunke
			else if (bunke.toString().equals("fundament"))
			{
				// velg rett fundament
				int nummer = bunke.getNummer(); // 0 - 3

				// Flytt ett kort fra fundament[nummer] til draBunken
				bord.draEttKortFraFundament(nummer);

				// Oppretter draBunke med fundament[nummer]'s X/Y plassering
				draBunke = new Bunke(this, bunke.getX(), bunke.getY(), "draBunke", bord.getDraBunke());
				// Husker på fraBunken
				fraBunke = fundament[nummer];

				// Oppdater offsets slik at musen holder tak rett sted på kortet
				xOffset = event.getX() - bunke.getListe().get( bunke.getListe().size()-1 ).getX();
				yOffset = event.getY() - bunke.getListe().get( bunke.getListe().size()-1 ).getY();

				return true;
			}
			// Plukk flere kort fra tabla[i] til draBunke
			else if (bunke.toString().equals("tabla"))
			{
				// velg rett tabla
				int nummer = bunke.getNummer(); // 0 - 6

				// Finn antall kort som skal dras
				Kort toppKort = bord.getTabla().get(nummer).peekAtEnd();
				Rectangle rektangel = new Rectangle( toppKort.getPoint(), toppKort.getDimension() );
				Point point = event.getPoint();

				int fraKort = 0;
				int offset = 0;

				// Gå gjennom muserektangler for hvert kort. Dette gjøres
				// Slik at hvert kort får hele sitt bilde som museområde.
				for (int i = bord.getTabla().get(nummer).size() - 1; i > -1; i--)
				{
					// Sjekk om kortets rektangel har point fra klikket
					if (rektangel.contains(point))
					{
						// Sjekker at kortet vises
						if (bord.getTabla().get(nummer).get(i).getVis())
						{
							// Indeks i bunken
							fraKort = bord.getTabla().get(nummer).size() - i;
							offset = i;
							break;
						}
						// Kortet vises ikke, ikke lov å flytte
						else
						{
							return false;
						}
					}
					// Sjekk neste rektangel dersom vi ikke har funnet indeksen enda
					rektangel.y -= Bunke.KORTMELLOMROM;
				}

				// Legg til kortene fra og med 
				for (int i = 0; i < fraKort; i++)
				{
					kort = bord.getTabla().get(nummer).pop();
					bord.getDraBunke().add( 0,kort );
				}

				// Oppretter draBunke med kasteBunkens X/Y plassering
				draBunke = new Bunke(this, bunke.getY(), bunke.getX(), "draBunke", bord.getDraBunke());
				// Husker på fraBunken
				fraBunke = tabla[nummer];

				// Oppdater offsets slik at musen holder tak rett sted på kortet
				xOffset = event.getX() - bunke.getListe().get( offset ).getX();
				yOffset = event.getY() - bunke.getListe().get( offset ).getY();

				return true;
			}
		} catch (Exception ex) {
			//ex.printStackTrace();
		}
		return false;
	}

	/**
	 * <h3>releaseBunke</h3>
	 * 
	 * <p>Dersom vi har registrert at en bunke dras (mousePressed/pressBunke)
	 * må vi også passe på hva som skal skje når bunken slippes.</p>
	 * 
	 * <p>Dersom ett kort slippes på fundament[i] må dette sammenlignes med
	 * det øverste kortet i sin bunke. Dersom ett eller flere kort slippes
	 * på tabla[i] må dette også sammenlignes med øverste kort i sin bunke.</p>
	 * 
	 * @param tilBunke er bunken som <u>draBunke</u> slippes over
	 * @param event Musehendelsen som ble registrert
	 * @return <b>true/false</b> True om noe er blitt utført
	 */
	public boolean releaseBunke( Bunke tilBunke, MouseEvent event )
	{
		try {
			// Hvis man slipper kort på fundament
			if (tilBunke.toString().equals("fundament"))
			{
				// Kun ett kort av gangen
				if (draBunke.getListe().size() == 1)
				{
					// Velg rett fundament basert på informasjon i tilBunke objektet...
					// ...tilBunke er bunken som blir funnet av EventListenerene.
					int nummer = tilBunke.getNummer();

					// Ta en titt på første kortet i draBunken
					kort = bord.getDraBunke().peekAtStart();

					// Hvis fundamentet er tomt, sjekk om draBunke inneholder ett ess
					if (bord.getFundament().get(nummer).isEmpty())
					{
						// Hvis det første kortet er et ess
						if (kort.getNummer() == 0)
						{
							// Tvungne fundament er på, sjekk om kortet legges på rett fundament
							if (automatikk.tvungneFundament())
							{
								if (fundament[nummer].sjekkFundamentNummer(kort))
								{
									kort = bord.getDraBunke().poll();
									bord.getFundament().get(nummer).push( kort );

									// Dersom bruker ønsker at alle kortene skal legges opp automagisk
									if (automatikk.brukerVilLeggeOppAlleKort()) {
										if (automatikk.erDetFlerSkjulteKort() == false) {
											automatikk.leggOppAlleKort();
										}
									}

									kabal.spillFerdig();
									return true;
								}
							}
							// Ikke tvungne fundament, legg esset hvor som helst
							else
							{
								kort = bord.getDraBunke().poll();
								bord.getFundament().get(nummer).push( kort );

								// Dersom bruker ønsker at alle kortene skal legges opp automagisk
								if (automatikk.brukerVilLeggeOppAlleKort()) {
									if (automatikk.erDetFlerSkjulteKort() == false) {
										automatikk.leggOppAlleKort();
									}
								}
								return true;
							}
						}
					}
					// Hvis fundamentet ikke er tomt, sammenlign kortet i draBunke
					// med kortet på toppen av fundamentet
					else
					{
						// Ta en titt på siste kortet i fundament[i]
						Kort fundKort = bord.getFundament().get(nummer).peekAtEnd();

						// Sammenlign første kort i draBunke med siste kort i fundament[i]
						if (kort.compareTo(fundKort, false))
						{
							kort = bord.getDraBunke().poll();
							bord.getFundament().get(nummer).push( kort );

							// Dersom bruker ønsker at alle kortene skal legges opp automagisk
							if (automatikk.brukerVilLeggeOppAlleKort()) {
								if (automatikk.erDetFlerSkjulteKort() == false) {
									automatikk.leggOppAlleKort();
								}
							}
							return true;
						}
					}
				}
			}
			// Hvis man slipper kort på tabla
			else if (tilBunke.toString().equals("tabla"))
			{
				// Velg rett tabla basert på informasjonen i tilBunke objektet
				int nummer = tilBunke.getNummer();

				// Ta en titt på første kort i draBunken
				kort = bord.getDraBunke().peekAtStart();

				// Hvis tabla[i] er tomt...
				if (bord.getTabla().get(nummer).isEmpty())
				{
					// ...sjekk om draBunke starter med en konge...
					if (kort.getNummer() == 12)
					{
						// ...og flytt over alle kortene.
						while ((kort = bord.getDraBunke().poll()) != null)
						{
							bord.getTabla().get(nummer).push( kort );
						}

						// Dersom bruker ønsker at alle kortene skal legges opp automagisk
						if (automatikk.brukerVilLeggeOppAlleKort()) {
							if (automatikk.erDetFlerSkjulteKort() == false) {
								automatikk.leggOppAlleKort();
							}
						}
						return true;
					}
				}
				// Hvis tabla[i] inneholder kort...
				else
				{
					// ...sammenlign første kort i draBunke med øverste kort i tabla[i]...
					if (kort.compareTo( bord.getTabla().get(nummer).peekAtEnd(), true ))
					{
						// ...og flytt over kortene.
						while ((kort = bord.getDraBunke().poll()) != null)
						{
							bord.getTabla().get(nummer).push( kort );
						}

						// Dersom bruker ønsker at alle kortene skal legges opp automagisk
						if (automatikk.brukerVilLeggeOppAlleKort()) {
							if (automatikk.erDetFlerSkjulteKort() == false) {
								automatikk.leggOppAlleKort();
							}
						}

						return true;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * <h3>musFlyttet</h3>
	 * 
	 * <p>Dersom vi har registrert at musen flytter på seg så sjekker vi
	 * hvilken bunke den befinner seg over. Dersom bunken er et tabla sjekker
	 * vi hvilket av kortene i tablaet og lyser opp det aktuelle kortet.</p>
	 * 
	 * @param bunke Bunken musen befinner seg over
	 * @param event Musehendelsen som ble registrert
	 * @return <b>true/false</b> True om noe er blitt utført
	 */
	public boolean musFlyttet( Bunke bunke, MouseEvent event )
	{
		try {
			// Kun highlight hvis det er tabla
			if (bunke.toString().equals("tabla"))
			{
				if (bunke.getListe().isEmpty() == false)
				{
					// Hent nummeret på tablaet
					int nummer = bunke.getNummer(); // 0 - 6

					// Lag midlertidig muserektangel;
					Kort toppKort = bord.getTabla().get(nummer).peekAtEnd();
					Rectangle rektangel = new Rectangle( toppKort.getPoint(), toppKort.getDimension() );
					Point point = event.getPoint();

					int musOverDetteKortet = 0;

					// Gå gjennom muserektangler for hvert kort. Dette gjøres
					// Slik at hvert kort får hele sitt bilde som museområde.
					for (int i = bord.getTabla().get(nummer).size() - 1; i > -1; i--)
					{
						// Sjekk om kortets rektangel har point fra klikket
						if (rektangel.contains(point))
						{
							// Indeks i aktuell bunke
							musOverDetteKortet = i;

							// Setter på highlight på kortet
							kortMedMusOverSeg = bord.getTabla().get(nummer).get( musOverDetteKortet );
							kortMedMusOverSeg.setMusOverKort( true );
							// Tegningen gjøres i paint() metoden til bunken (Bunke.java)

							return true;
						}
						// Sjekk neste rektangel dersom vi ikke har funnet indeksen enda
						rektangel.y -= Bunke.KORTMELLOMROM;
					}
					return true;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	//ABOVE//////////// KLIKKFUNKSJONER //////////////////////
	//////////////////////////////////////////////////////////
	//BELOW/////////// GETTERS & SETTERS /////////////////////

	public Kabal getKabal() {
		return this.kabal;
	}

	public HashMap<String,BufferedImage> getBakgrunner() {
		return bakgrunner;
	}

	public boolean getGrayscale() {
		return this.grayscale;
	}
	public void setGrayscale( boolean b ) {
		this.grayscale = b;
	}

	public boolean getVinduErFokusert() {
		return this.vinduErFokusert;
	}
	public void setVinduErFokusert( boolean b ) {
		this.vinduErFokusert = b;
	}

	public Dimension getPreferredSize() {
		try {
			return new Dimension(500,553);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * <h3>getSource</h3>
	 * 
	 * <p>Brukes for å finne kilden til fraBunken.</p>
	 * @param bunke
	 * @return Returnerer kilden for gitte bunke
	 */
	public Stokk getSource( Bunke bunke ) {
		try {
			if (bunke.toString().equals("trekkBunke"))
				return bord.getTrekkBunke();

			if (bunke.toString().equals("kasteBunke"))
				return bord.getKasteBunke();

			if (bunke.toString().equals("fundament"))
				return bord.getFundament().get( bunke.getNummer() );

			if (bunke.toString().equals("tabla"))
				return bord.getTabla().get( bunke.getNummer() );
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}