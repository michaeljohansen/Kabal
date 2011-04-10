package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import data.Bord;

import poeng.Versjonssjekk;

import kabal.Kabal;

/**
 * Objektklasse for Vinduet som extender JFrame.
 * 
 * @author Michael Johansen
 * @author Anja Rønning
 * @author Cathrine Holm
 * @version 0.9
 */
@SuppressWarnings("serial")
public class Vindu extends JFrame implements WindowFocusListener
{
	private Kabal kabal;
	private Lerret lerret;

	/**
	 * Constructor kaller inn JFrame
	 * @param kabal  
	 * @param bord
	 * @param tittel 
	 */
	public Vindu(Kabal kabal, Bord bord, String tittel)
	{
		super( tittel );
		this.kabal = kabal;
		setTitle("Norwegian University of Science and Technology");

		// Konfigurer vindu
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener( new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Vindu.this.dispose();
				System.exit(0);
			}
		});

		// Konfigurer komponenter
		lerret = new Lerret( kabal, bord );
		lerret.oppdaterMuseRektangler();
		Knapper knapper = new Knapper( kabal );

		// Legg til greier til contentpane
		this.getContentPane().add( lerret, BorderLayout.PAGE_START );
		this.getContentPane().add( new JSeparator(), BorderLayout.CENTER );
		this.getContentPane().add( knapper, BorderLayout.PAGE_END );

		// Legg til windowFocusListeneren
		this.addWindowFocusListener(this);

		// Pakk og vis
		this.pack();
		this.setSize(800,623);
		this.sentrerVindu();
		this.setResizable(false);
		this.setVisible(true);
	}
	
	/**
	 * <h3>sentrerVindu</h3>
	 * 
	 * <p>Sentrerer vinduet på skjermen.</p>
	 */
	public void sentrerVindu() {
		Dimension skjerm = Toolkit.getDefaultToolkit().getScreenSize();
		Point point = new Point( skjerm.width / 2 - this.getWidth() / 2, skjerm.height / 3 - this.getHeight() / 3 );
		this.setLocation( point );
	}

	/**
	 * <h3>windowGainedFocus</h3>
	 * 
	 * <p>Brukes kun til bakgrunnseffekter.</p>
	 * 
	 * @param event Vindu fikk fokus
	 */
	public void windowGainedFocus(WindowEvent event) {
		try {
			lerret.setGrayscale(false);
			lerret.setVinduErFokusert(true);
			lerret.repaint();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * <h3>windowLostFocus</h3>
	 * 
	 * <p>Brukes kun til bakgrunsseffekter.</p>
	 * 
	 * @param event Vindu mistet fokus
	 */
	public void windowLostFocus(WindowEvent event) {
		try {
			lerret.setGrayscale(true);
			lerret.setVinduErFokusert(false);
			lerret.repaint();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * Getters / Setters
	 */

	public Kabal getKabal() {
		return kabal;
	}

	public Vindu getVindu() {
		return this;
	}

	public Lerret getLerret() {
		return lerret;
	}
}



/**
 * Oppretter knappene
 * 
 * Knappene plasseres i en GridLayout for å få lik størrelse
 */
@SuppressWarnings("serial")
class Knapper extends JPanel implements ActionListener
{
	private Kabal kabal;
	
	private String knapp1 = "Restart";
	private String knapp2 = "Angre Trekk";
	private String knapp3 = "Studentmodus";
	private String knapp4 = "Innstillinger";
	private String knapp5 = "Om Kabalen";
	private String knapp6 = "Poengtabell";
	private String knapp7 = "Avslutt";

	/**
	 * Constructor for knappene
	 * Kabalobjektet finnes i vindu.kabal
	 * @param kabal
	 */
	public Knapper( Kabal kabal )
	{
		super();

		this.kabal = kabal;

		// Et nett for plassering av knappene
		this.setLayout( new GridBagLayout() );
		GridBagConstraints gbc = new GridBagConstraints();

		boolean forste = true;
		int rekke = 0;
		
		// Operasjoner på alle knappene
		for(JButton knapp : new JButton[] {new JButton(knapp1),new JButton(knapp2),new JButton(knapp3),new JButton(knapp4),new JButton(knapp5),new JButton(knapp6),new JButton(knapp7)}) {
			gbc.insets = new Insets( 5,(forste?5:0),4,5 );
			gbc.gridx = rekke++;
			gbc.gridy = 0;
			gbc.weightx = 0.5;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			knapp.addActionListener(this);
			knapp.setFont( new Font("Calibri", 2, 16) );
			// TODO, ANGRE TREKK KNAPPEN ER IKKE MED ENDA
			if (knapp.getText().equals("Angre Trekk") == false)
				this.add(knapp, gbc);
			forste = false;
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		// Hent kommando
		String item = event.getActionCommand();

		// Eksekver, eksvekvekkelol
		if (item.equals( knapp1 )) // Restart
		{
			// Start ny kabal
			kabal.restartKabal();
		}
		else if (item.equals( knapp2 )) // Angre trekk
		{
			// Angre funksjon
			//TODO: Ikke implementert skikkelig enda.
			//kabal.getBord().load();
		}
		else if (item.equals( knapp3 )) // Studentmodus
		{
			if (kabal.isBilderAvOss() == true)
			{
				// Skru av bilderAvOss
				kabal.setBilderAvOss( false );
				// Sett ny tittel
				kabal.setOppdateringAvTittel( true );
				kabal.setTittel("Kabal");
				// Start ny kabal
				kabal.restartKabal();
			}
			else
			{
				// Skru på bilderAvOss
				kabal.setBilderAvOss( true );
				// Sett ny tittel
				kabal.setOppdateringAvTittel( false );
				kabal.setTittel("Julegave fra Cathrine Holm, Anja Rønning & Michael Johansen");
				// Start ny kabal
				kabal.restartKabal();
			}
		}
		else if (item.equals( knapp4 )) // Innstillinger
		{
			new Dialog( kabal, "innstillinger", false );
		}
		else if (item.equals( knapp5 )) // Om Kabal
		{
			omKabalDialog( kabal );
		}
		else if (item.equals( knapp6)) // Poengtabell
		{
			if (Versjonssjekk.harInternett())
				new Dialog( kabal, "poengtabell", false );
		}
		else if (item.equals( knapp7 )) // Avslutt
		{
			// Kverk rammen
			kabal.getVindu().dispose();
			// Avslutt
			System.exit(0);
		}
	}

	/**
	 * En advarselboks dersom bruker prøver å kombinere visning av
	 * studentene og treg-data modus. Dette er ulovlig fordi det
	 * rett og slett ser stygt ut.
	 */
	public void omKabalDialog( Kabal kabal )
	{
		JEditorPane omKabalTekst = new JEditorPane("text/html",null);
		omKabalTekst.setText(
				"<html>"
				+"<div style=\"font-family:Verdana; font-size:11px;\">"
				
				+"&quot;Kabal&quot; kommer av det nyhebraiske ordet kabbala, betegenelsen p&aring; <br>"
				+"middelalderens j&oslash;diske mystikk. P&aring; tysk betyr Kabala, som p&aring; fransk, <br>"
				+"intrige eller komplott. <br>"
				+"<br>"
				
				+"Programmet ble til slutt delt opp i seks pakker, en for data, en for <br>"
				+"GUI, en kun for mainmetoden, en for poengtabellen, en for bildene og <br>"
				+"en siste for enums. <br>"
				+"<br>"
				
				+"Vi fikk mye inspirasjon fra kilder via Google. Den viktigste <br>"
				+"kilden er Alexander Bjerkan og Espen Jacobssons implementasjon <br>"
				+"fra fjor&aring;ret. Den kan finnes p&aring;: <br>"
				+"<div style=\"color:blue;\"><a href=\"http://svn2.assembla.com/svn/planetsphere/src/\""
				+"http://svn2.assembla.com/svn/planetsphere/src/ </a><br>"  // TODO: TEST HER \t
				+"</div>"
				+"<br>"
				
				+"<div style=\"margin-left:50px;\">"
				+"Med vennlig hilsen <br>"
				+"<div style=\"margin-left:70px; font-style:italic;\">"
				+"Cathrine Holm, Anja R&oslash;nning & Michael Johansen <br>"
				+"IT2103 - Objektorientert systemutvikling <br>"
				+"</div>"
				+"</div>"
			);
		
		omKabalTekst.setEditable(false);
		omKabalTekst.setOpaque(false);
		omKabalTekst.setBackground( new Color( 0,0,0 ));
		
		JOptionPane.showMessageDialog( kabal.getVindu(), omKabalTekst, "Om Kabalen", JOptionPane.INFORMATION_MESSAGE );
	}
}
