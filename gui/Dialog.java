package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

import poeng.Poengtabell;
import poeng.Versjonssjekk;

import kabal.Kabal;

/**
 * Viser en dialog for bruker når spillet er ferdig.
 * 
 * Dialogen viser en melding og spør om bruker vil restarte
 * eller avslutte.
 * 
 * @author Michael Johansen
 * @author Anja Rønning
 * @author Cathrine Holm
 * @version 0.6
 */
@SuppressWarnings("serial")
public class Dialog extends JDialog implements ActionListener {

	private Kabal kabal;
	private Vindu vindu;

	private BufferedImage bilde;
	private String type;

	/**
	 * Constructor
	 * @param kabal
	 */
	public Dialog( Kabal kabal, String type, boolean spillFerdig )
	{
		// Oppretter dialog med bruker
		super( kabal.getVindu(), true );
		// true (for "modal") her tar over all input til programmet, dialogen dominerer

		this.kabal = kabal;
		this.vindu = kabal.getVindu();
		this.type = type;

		if (type.equals("gratulasjon"))
		{
			// Vis gratulasjon
			this.setTitle("Vi har en vinner!");
			this.add( this.visGratulasjonsPanel() );
		}
		else if (type.equals("innstillinger"))
		{
			// Vis innstillinger
			this.setTitle("Innstillinger");
			this.add( this.visInnstillingsPanel() );
		}
		else if (type.equals("poengtabell"))
		{
			// Sjekk at vi fant både internett og databasetilkobling når programmet
			// startet. Dobbeltsjekk at vi fremdeles har internett.
			if (kabal.isInternettOgDatabase() && Versjonssjekk.harInternett())
			{
				// Spill ikke ferdig
				if (spillFerdig == false)
				{
					// Tabell tom
					if (Poengtabell.isEmpty())
					{
						// Bare vis en meldingsboks
						// Må returne for å unngå at dialogen popper opp likevel når vi kommer til setVisible
						this.dispose();
						JOptionPane.showMessageDialog(kabal.getVindu(), "Poengtabellen er tom.\nSpill ferdig et spill!", "Poengtabell", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					else // Tabellen har innhold
					{
						this.setTitle("De beste spillerne");
						this.add( this.visPoengTabell( spillFerdig ) );
					}
				}
				else // Spill ferdig
				{
					// Testing ferdig
					if (kabal.isTesting())
					{
						// Bare vis en meldingsboks
						// Må returne for å unngå at dialogen popper opp likevel (når vi kommer til setvisible)
						this.dispose();
						JOptionPane.showMessageDialog( kabal.getVindu(), "Testing registreres ikke i poengtabellen.", "Poengtabell", JOptionPane.INFORMATION_MESSAGE );
						return;
					}
					else // Normalt ferdig
					{
						this.setTitle("De beste spillerne");
						this.add( this.visPoengTabell( spillFerdig ));
					}
				}
			}
			else // mangler internett og/eller databasetilkobling
			{
				// Bare vis en meldingsboks
				// Må returne for å unngå at dialogen popper opp likevel (når vi kommer til setvisible)
				this.dispose();
				JOptionPane.showMessageDialog(kabal.getVindu(), "Internettilkoblingen og/eller\ndatabasetilkoblingen er nede.", "Ingen tilkobling", JOptionPane.WARNING_MESSAGE, null);
				return;
			}
		}

		// Innstillinger for dialog
		this.setResizable( true ); //TODO:false
		this.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {

				// Hvis vi lukker poengtabellen skal kun den lukkes.
				if (Dialog.this.type.equals("poengtabell"))
				{
					// Lukk dialogen
					Dialog.this.dispose();
				}
				// Hvis vi lukker noe annet lukker vi hele programmet.
				else
				{
					// Lukk dialogen...
					Dialog.this.dispose();
					// ...kort pause...
					try {Thread.sleep(1100);} catch (InterruptedException e) {e.printStackTrace();}
					// ...og avslutt
					Dialog.this.kabal.avslutt();
				}
			}
		});

		// Pakk og vis
		this.pack();
		this.setLocationRelativeTo(vindu);
		this.setVisible(true);
	}

	/**
	 * <h3>visGratulasjonsPanel</h3>
	 * 
	 * <p>Lager innholdet for dialogen, her en gratulasjon.</p>
	 * 
	 * @return Panel med alt innholdet
	 */
	public JPanel visGratulasjonsPanel()
	{
		try {
			// Lag panelet
			JPanel panel = new JPanel( new GridBagLayout() );
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(5,20,5,20);

			// JEditorPane med teksten
			JEditorPane textarea = new JEditorPane( "text/html", null );
			String brukernavn = System.getProperty("user.name");
			String vis = "<html><div style=\"font-family:Verdana; font-size:11px;\"><br>"
				+ "<b>Gratulerer" + (brukernavn == null ? "" : " "+brukernavn) + ", du vant!</b><br>"
				+ "<br>"
				+ "Du har flyttet kort "+kabal.getBord().getAntallTrekk()+" ganger.<br>"
				+ "<br>"
				+ "Med vennlig hilsen:<br>"
				+ "<div style=\"font-size: 11px;\"><br>"
				+ "<i>Cathrine Holm,<br>"
				+ "Anja R&oslash;nning<br>"
				+ "& Michael Johansen<br>"
				+ "<br>"
				+ "Bachelor i Informatikk<br>"
				+ "NTNU, 2009</i><br>";
			
			textarea.setText( vis );
			textarea.setEditable( false );
			textarea.setOpaque(false);
			textarea.setBackground( new Color( 0,0,0 ));
			gbc.gridx = 0;
			gbc.gridy = 0;
			panel.add( textarea, gbc );

			// JLabel med NTNU bilde
			try {
				bilde = ImageIO.read( getClass().getResource(kabal.getBildePakke() + "ntnulogo.png") );
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			gbc.gridx = 1;
			gbc.gridy = 0;
			panel.add( new JLabel( new ImageIcon( bilde )), gbc );

			// Nye insets på GBC slik at knappene kommer nærmere hverandre
			gbc.insets = new Insets(10,10,10,10);

			// Restartknapp
			JButton restart = new JButton("Restart");
			restart.setFont( new Font("Calibri", 2, 16) );
			restart.addActionListener(this);
			gbc.anchor = GridBagConstraints.EAST;
			gbc.gridx = 0;
			gbc.gridy = 1;
			panel.add( restart, gbc );

			// Avsluttknapp
			JButton avslutt = new JButton("Avslutt");
			avslutt.setFont( new Font("Calibri", 2, 16) );
			avslutt.addActionListener(this);
			gbc.anchor = GridBagConstraints.WEST;
			gbc.gridx = 1;
			gbc.gridy = 1;
			panel.add( avslutt, gbc );

			return panel;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Lager innholdet for dialogen, her et innstillingspanel.
	 * 
	 * @return Panel med alt innholdet
	 */
	public JPanel visInnstillingsPanel( )
	{
		JPanel panel = new JPanel( new GridBagLayout() );
		GridBagConstraints gbc = new GridBagConstraints();

		final int antall = 3;

		JRadioButton[] radioKnapper = new JRadioButton[antall];

		final ButtonGroup gruppe = new ButtonGroup();

		final String ett = "Trekk ett";
		final String tre = "Trekk tre (gir "+(char)247+" "+kabal.getBord().getTrekk3Bonus()+" trekk i bonus)";
		final String fem = "Trekk fem (gir "+(char)247+" "+kabal.getBord().getTrekk5Bonus()+" trekk i bonus)";

		radioKnapper[0] = new JRadioButton( ett );
		radioKnapper[0].setActionCommand( ett );
		radioKnapper[0].setFont( panel.getFont() );

		radioKnapper[1] = new JRadioButton( tre );
		radioKnapper[1].setActionCommand( tre );
		radioKnapper[1].setFont( panel.getFont() );

		radioKnapper[2] = new JRadioButton( fem );
		radioKnapper[2].setActionCommand( fem );
		radioKnapper[2].setFont( panel.getFont() );

		for (int i = 0; i < antall; i++) {
			gruppe.add( radioKnapper[i] );
		}

		if (kabal.getTrekkKort() == 1)
			radioKnapper[0].setSelected( true );
		else if (kabal.getTrekkKort() == 3)
			radioKnapper[1].setSelected( true );
		else
			radioKnapper[2].setSelected( true );

		JButton lagreKnapp = new JButton("OK, Restart");
		JButton avbrytKnapp = new JButton("Avbryt");
		lagreKnapp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event)
			{
				String valg = gruppe.getSelection().getActionCommand();

				if (valg == ett)
				{
					// Setter ny innstilling
					kabal.setTrekkKort(1);
					// Restarter
					kabal.restartKabal();
				}
				else if (valg == tre)
				{
					// Setter ny innstilling
					kabal.setTrekkKort(3);
					// Restarter
					kabal.restartKabal();
				}
				else if (valg == fem)
				{
					// Setter ny innstilling
					kabal.setTrekkKort(5);
					// Restarter
					kabal.restartKabal();
				}
				return;
			}
		});
		avbrytKnapp.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Dialog.this.dispose();
				return;
			}
		});

		JTextArea textfield = new JTextArea(
				"- Gyldige flytt (kort/bunke som er dratt) koster ett trekk.\n"
				+ "- Automatisk flytting av kort med høyre museknapp koster ett trekk.\n"
				+ "- Poengberegning: Trekk + (Sekunder / 2)");
		textfield.setFont( panel.getFont() );
		textfield.setEditable( false );
		textfield.setOpaque(false);
		textfield.setBackground( new Color( 0,0,0 ));

		for (int i = 0; i < antall; i++)
		{
			gbc.insets = new Insets( (i==0?10:0),20,0,0 );
			gbc.gridx = 0;
			gbc.gridy = i;
			gbc.gridwidth = 2;
			gbc.anchor = GridBagConstraints.WEST;
			panel.add( radioKnapper[i], gbc );
		}

		gbc.insets = new Insets( 10,10,10,10 );
		gbc.gridx = 0;
		gbc.gridy = antall;
		gbc.gridwidth = 2;
		panel.add( textfield, gbc );

		gbc.insets = new Insets( 10,10,10,10 );
		gbc.gridx = 0;
		gbc.gridy = antall + 1;
		gbc.gridwidth = 1;
		//gbc.anchor = GridBagConstraints.PAGE_END;
		panel.add( lagreKnapp, gbc );

		gbc.insets = new Insets( 10,10,10,10 );
		gbc.gridx = 1;
		gbc.gridy = antall + 1;
		//gbc.anchor = GridBagConstraints.CENTER;
		panel.add( avbrytKnapp, gbc );

		return panel;
	}

	/**
	 * Lager innholdet for dialogen, her en poengtabell.
	 * 
	 * @return Panel med alt innholdet
	 */
	public JPanel visPoengTabell( boolean ferdig ) {
		if (ferdig)
		{
			Poengtabell poeng = new Poengtabell( kabal );
			poeng.leggTilResultat( kabal.getBord().getAntallTrekk() );
			return new Poengtabell( kabal );
		}
		else // ikke ferdig, har kun trykket på "poengtabell"
		{
			//try {Thread.sleep(150);} catch (InterruptedException e) {e.printStackTrace();}
			return new Poengtabell( kabal );
		}
	}

	/**
	 * Handlinger for alle knapper o.l. dialogen har
	 */
	@Override
	public void actionPerformed(ActionEvent event)
	{
		String item = event.getActionCommand();

		if (item.equals("Restart"))
		{
			this.dispose();
			kabal.restartKabal();
		}
		else if (item.equals("Avslutt"))
		{
			// Lukk dialogen...
			this.dispose();
			// ...kort pause...
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			// ...og avslutt
			kabal.avslutt();
		}
	}

}
