package poeng;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import kabal.Kabal;
import kabal.MySQL;

/**
 * Objektklasse for poengtabell.
 * 
 * Poengtabellen henter inn data fra MySQL, legger dette i en
 * tabell, som legges i et skrollepanel, som legges i et panel,
 * som returneres til Dialog (puh..puste inn).
 * 
 * @author Michael Johansen
 * @author Anja Rønning
 * @author Cathrine Holm
 * @version 0.7
 */
@SuppressWarnings("serial")
public class Poengtabell extends JPanel {

	private Kabal kabal;
	
	private static String mysqlTabell = MySQL.getTabell();

	/**
	 * Constructor 1
	 * Returnerer panelet med poengtabellen.
	 * 
	 * @param kabal
	 */
	public Poengtabell( Kabal kabal )
	{
		super();

		this.kabal = kabal;

		// Innstill panel
		this.setLayout( new GridBagLayout() );
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets( 10,10,10,10 );

		try {
			// Koble til databasen
			Connection tilkobling = connect();
			Statement statement = tilkobling.createStatement();
			String query = "SELECT * FROM "+mysqlTabell+" ORDER BY trekk ASC";
			ResultSet resultat = statement.executeQuery(query);

			// Sjekker hvor mange rader det finnes i databasen
			resultat.last();
			int antall = resultat.getRow();
			
			// Setter peker tilbake til første element
			resultat.first();

			String[] kolonneNavn = {"Navn","Trekk","Kommentar"};
			Object[][] matrise = new Object[antall][3];

			boolean fler = true;

			// Henter ut data (hvis det er noe å hente)
			if (antall != 0) {
				for (int i=0; i < 100 && fler; i++) {
					matrise[i][0] = resultat.getString("Navn");
					matrise[i][1] = "Trekk "+resultat.getInt("Kort")+":   "+resultat.getInt("Trekk");
					matrise[i][2] = resultat.getString("Kommentar");
					if (resultat.next() == false)
						fler = false;
				}
			}

			// Legger til data i tabell
			JTable tabell = new JTable( matrise, kolonneNavn );

			// Innstiller tabell
			tabell.setEnabled( false );
			TableColumn column = null;
			for (int i = 0; i < 3; i++) {
				column = tabell.getColumnModel().getColumn(i);
				if (i == 0)
					column.setPreferredWidth(35);
				if (i == 1)
					column.setPreferredWidth(35);
				if (i == 2)
					column.setPreferredWidth(170);
			}

			// Legger tabellen i et skrollepanel
			JScrollPane skrollpanel = new JScrollPane( tabell );
			tabell.setFillsViewportHeight( true );

			// Legger skrollepanel på panelet
			this.add( skrollpanel, gbc );

		} catch (Exception ex) {
			ex.printStackTrace();
		}		
	}

	/**
	 * Legger til et nytt resultat i databasen.
	 * 
	 * @param trekk
	 */
	public void leggTilResultat( int trekk ) {
		try {
			// Bestem score
			int poeng = trekk + kabal.getSekunderBrukt() / 2;
			
			// Hent brukernavn fra pålogget bruker
			String brukernavn = System.getProperty("user.name");
			if (brukernavn == null)
				brukernavn = JOptionPane.showInputDialog(null, "Skriv inn ditt navn for highscorelisten:\n", "Poengtabell", JOptionPane.QUESTION_MESSAGE);
			
			// Hent kommentar via input
			String kommentar = "Skriv her :)";
			boolean igjen = false;
			do {
				String melding = "Skriv inn en kommentar til poengtabellen:\n"
					+ "- Den vil stå ved navnet ditt\n"
					+ ( igjen ? "- Det var MER ENN 30 TEGN, vennligst kort det ned!\n" : "- Ikke mer enn 30 tegn\n")
					+ "- Du brukte "+kabal.getBord().getAntallTrekk()+" trekk\n"
					+ "- Du brukte "+kabal.getSekunderBrukt()+" sekunder\n"
					+ "- Poeng: "+poeng;

				kommentar = JOptionPane.showInputDialog( melding, kommentar );
				
				// Ingen kommentar, continue
				if (kommentar == null || kommentar.equals("Skriv her :)"))
					kommentar = ":)";
				// Godkjent kommentar, continue
				else if (kommentar.length() < 31)
					igjen = false;
				// Prøv igjen!
				else
					igjen = true;
			} while (kommentar.length() > 30 && igjen == true);

			// Legg til nytt resultat
			PreparedStatement sql;
			sql = connect().prepareStatement("INSERT INTO "+mysqlTabell+"(navn, trekk, kort, kommentar) VALUES(?,?,?,?)");
			sql.setString(1, brukernavn);
			sql.setInt(2, poeng);
			sql.setInt(3, kabal.getTrekkKort());
			sql.setString(4, kommentar);
			sql.executeUpdate();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Kobler til databasen.
	 * 
	 * @return Tilkoblingen
	 */
	public static Connection connect()
	{
		String server = MySQL.getServer();
		String brukernavn = MySQL.getBrukernavn();
		String passord = MySQL.getPassord();
		
		Connection tilkobling = null;
		try {
			Class.forName ("com.mysql.jdbc.Driver").newInstance ();
			tilkobling = DriverManager.getConnection( server, brukernavn, passord );
		} 
		catch (Exception ex) {
			System.out.println("Mislykket tilkobling");
			//ex.printStackTrace();
		}
		return tilkobling;
	}

	/**
	 * Sjekker om mysqlTabellen er tom.
	 * 
	 * @param mysqltabell
	 * @return true/false
	 */
	public static boolean isEmpty() {
		try {
			// Koble til databasen
			Connection tilkobling = connect();
			Statement statement = tilkobling.createStatement();
			String query = "SELECT * FROM "+mysqlTabell+" ORDER BY trekk ASC";
			ResultSet resultat = statement.executeQuery(query);

			// Sjekker hvor mange rader det finnes i databasen
			resultat.last();
			int antall = resultat.getRow();

			// Returnerer om settet er tomt eller ikke
			return antall == 0;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return true;
	}

}
