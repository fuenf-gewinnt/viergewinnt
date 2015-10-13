/**
 * 4 Gewinnt GUI-Entwurf
 * 
 * Autor: Marcel Cornesse
 * 
 * Versionsverlauf:
 * v0.1  rudimentärer GUI-Entwurf
 * v0.12 Infofenster Spielende / Satzende hinzugefügt
 * v0.15 Spielfeld angelegt, Array-Tabelle agiert im Hintergrund eines png Spielfeldes
 * v0.2	 Steuerungslogik hinterlegt, save via FileWriter
 * 
 * v0.4	added Button "showStats"
 */

package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import db.AccessDB;
import file.FileImpl;
import ki.Intelligence;
import pusher.PusherImpl;
import viergewinnt.CommunicationController;

public class GUIinit {

	JTextField txtGegnername;
	JPasswordField pwdSchlssel;
	String schnittstelle;
	JTextField txtPath;
	public static JButton btnStart;
	private JTable game;
	private CommunicationController comControl;
	private Intelligence ki;
	private JRadioButton rdbtnPush;
	private JRadioButton rdbtnFile;
	private AccessDB db;
	private Object[][] data_stat;
	private JLabel lblSetgegner;
	public static String gegnerName = "";
	static String spielerwahl;
	private static JFrame frame;
	private static Object[][] data;
	private static JPanel panel1;

	/**
	 * Erzeuge Applikation.
	 * 
	 * @param db
	 */
	public GUIinit(Intelligence ki, AccessDB db) {
		this.ki = ki;
		this.db = db;
		initialize();
	}

	// CODE START NUR ZU TESTZWECKEN cornemrc
	public static void satzendePopup(String gegnerName) {
		// Code für schlechte Zeiten
		// String name = JOptionPane.showInputDialog(frame, "Wer hat
		// gewonnen?");

		int auswahl = JOptionPane.showOptionDialog(frame, "Wer hat gewonnen?", "Satzende",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				new String[] { "Fungi", gegnerName, "Unentschieden", "Satz wiederholen!" }, "Fungi");
		System.out.println(auswahl);
	}

	private void spielendePopup(String gewinner) {
		// Siegervariable ergänzen!
		JOptionPane.showMessageDialog(frame, "Gewonnen hat: " + gewinner, "Spielende", 1);
	}

	private void savePopup() {
		// Siegervariable ergänzen!
		JOptionPane.showMessageDialog(frame, "Konfiguration gespeichert!", "Information", 1);
	}
	// CODE ENDE NUR ZU TESTZWECKEN cornemrc

	/**
	 * Initialisiere die Inhalte des frames
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1280, 720);
		frame.setResizable(false);
		frame.setTitle("4 Gewinnt v0.3 alpha - Powered by Fungi Software Solutions");
		ImageIcon fungilogo = new ImageIcon("pictures/fungi_atompilz.png");
		frame.setIconImage(fungilogo.getImage());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		// Tabs
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		tabbedPane.setBounds(0, 0, 1274, 655);

		// Tab1: SPIEL
		panel1 = new JPanel();
		panel1.setBackground(new java.awt.Color(100, 200, 250));
		tabbedPane.addTab("Spiel", panel1);
		panel1.setLayout(null);

		// 4w Silhouette
		ImageIcon img_silhouette = new ImageIcon("pictures/4w_silhouette.png");
		JLabel lblSilhouette = new JLabel(img_silhouette);
		lblSilhouette.setBounds(10, 100, 921, 500);
		panel1.add(lblSilhouette);

		JLabel lblSatz = new JLabel("Satz:");
		lblSatz.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 25));
		lblSatz.setBounds(1001, 27, 54, 34);
		panel1.add(lblSatz);

		JLabel lblSetsatz = new JLabel("set_satz");
		lblSetsatz.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 25));
		lblSetsatz.setBounds(1135, 27, 88, 34);
		panel1.add(lblSetsatz);

		// Ergebnisanzeige Start
		ImageIcon img_chip_red = new ImageIcon("pictures/red.png");
		JLabel lbl_chip_red = new JLabel(img_chip_red);
		lbl_chip_red.setBounds(150, 10, 57, 50);
		panel1.add(lbl_chip_red);

		JLabel lblSpielstand = new JLabel("Fungi                       vs.");
		lblSpielstand.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 25));
		lblSpielstand.setBounds(217, 16, 260, 34);
		panel1.add(lblSpielstand);

		JLabel lblSetstandfungi = new JLabel("set_stand_fungi");
		lblSetstandfungi.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 25));
		lblSetstandfungi.setBounds(217, 64, 194, 34);
		panel1.add(lblSetstandfungi);

		ImageIcon img_chip_yellow = new ImageIcon("pictures/yellow.png");
		JLabel lbl_chip_yellow = new JLabel(img_chip_yellow);
		lbl_chip_yellow.setBounds(510, 10, 57, 50);
		panel1.add(lbl_chip_yellow);

		lblSetgegner = new JLabel();
		lblSetgegner.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 25));
		lblSetgegner.setBounds(580, 16, 125, 34);
		panel1.add(lblSetgegner);

		JLabel lblSetstand = new JLabel("set_stand_gegner");
		lblSetstand.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 25));
		lblSetstand.setBounds(580, 64, 205, 34);
		panel1.add(lblSetstand);
		// Ergebnisanzeige Ende

		// Buttons Start
		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				startPlaying();
				btnStart.setEnabled(false);
			}
		});
		btnStart.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		btnStart.setBounds(1128, 529, 120, 30);
		panel1.add(btnStart);

		JButton btnBeenden = new JButton("Beenden");
		btnBeenden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0); // 0 da keine Fehler aufgetreten sind
			}
		});
		btnBeenden.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		btnBeenden.setBounds(1128, 575, 120, 30);
		panel1.add(btnBeenden);
		// Buttons Ende

		// BEGINN DER SPIELTABELLE
		data = new Object[][] { { null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null }, { null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null }, { null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null } };

		// Die Titel der Spalten
		String[] data_title = new String[] { "0", "1", "2", "3", "4", "5", "6" };

		game = new JTable(data, data_title);
		ColorRenderer cR = new ColorRenderer(data);
		game.setDefaultRenderer(Object.class, cR);
		game.setBounds(158, 108, 590, 475);
		game.setRowHeight(79);
		game.setEnabled(false); // nicht anklickbar
		panel1.add(game);
		// ENDE DER SPIELTABELLE

		// Tab2: OPTIONEN
		JPanel panel2 = new JPanel();
		panel2.setOpaque(true);
		// panel2.setBackground(Color.orange);
		tabbedPane.addTab("Einstellungen", panel2);
		panel2.setLayout(null);

		// Anlegen eines FileWriter Objektes zum Speichern der Konfiguration
		ConfigFile cFW = new ConfigFile(this);

		// --- Beginn der Konfiguration ---
		txtGegnername = new JTextField();
		txtGegnername.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		txtGegnername.setBounds(224, 72, 200, 35);
		panel2.add(txtGegnername);
		txtGegnername.setColumns(10);

		pwdSchlssel = new JPasswordField();
		pwdSchlssel.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		pwdSchlssel.setBounds(224, 123, 200, 35);
		panel2.add(pwdSchlssel);

		JLabel lblSchnittstelle = new JLabel("Schnittstelle:");
		lblSchnittstelle.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 22));
		lblSchnittstelle.setBounds(45, 209, 150, 30);
		panel2.add(lblSchnittstelle);

		rdbtnPush = new JRadioButton("Push");
		rdbtnPush.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		rdbtnPush.setBounds(224, 205, 77, 39);
		panel2.add(rdbtnPush);

		rdbtnFile = new JRadioButton("File");
		rdbtnFile.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		rdbtnFile.setBounds(381, 205, 65, 39);
		panel2.add(rdbtnFile);

		// Schnittstellen-Buttons gruppieren
		ButtonGroup groupSchnittstelle = new ButtonGroup();
		groupSchnittstelle.add(rdbtnPush);
		groupSchnittstelle.add(rdbtnFile);

		JLabel lblSpielerwahl = new JLabel("Spielerwahl:");
		lblSpielerwahl.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 22));
		lblSpielerwahl.setBounds(45, 270, 150, 30);
		panel2.add(lblSpielerwahl);

		JRadioButton rdbtnSpielerO = new JRadioButton("Spieler O");
		rdbtnSpielerO.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		rdbtnSpielerO.setBounds(224, 266, 123, 39);
		panel2.add(rdbtnSpielerO);

		JRadioButton rdbtnSpielerX = new JRadioButton("Spieler X");
		rdbtnSpielerX.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		rdbtnSpielerX.setBounds(381, 266, 119, 39);
		panel2.add(rdbtnSpielerX);

		// Spieler-Buttons gruppieren
		ButtonGroup groupSpieler = new ButtonGroup();
		groupSpieler.add(rdbtnSpielerO);
		groupSpieler.add(rdbtnSpielerX);

		JButton btnSpeichern = new JButton("Speichern");
		btnSpeichern.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Konfigurationsdatei erstellen/aktualisieren
				if (rdbtnPush.isSelected() == true) {
					schnittstelle = "Push";
				}
				if (rdbtnFile.isSelected() == true) {
					schnittstelle = "File_" + txtPath.getText();
				}
				if (rdbtnSpielerO.isSelected() == true) {
					spielerwahl = "o";
				}
				if (rdbtnSpielerX.isSelected() == true) {
					spielerwahl = "x";
				}
				gegnerName = txtGegnername.getText();
				lblSetgegner.setText(gegnerName);
				cFW.schreiben();
				savePopup();
			}
		});
		btnSpeichern.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		btnSpeichern.setBounds(1128, 529, 120, 30);
		panel2.add(btnSpeichern);

		JLabel lblSchlssel = new JLabel("Schl\u00FCssel:");
		lblSchlssel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 22));
		lblSchlssel.setBounds(45, 128, 150, 30);
		panel2.add(lblSchlssel);

		JLabel lblGegnername = new JLabel("Gegnername:");
		lblGegnername.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 22));
		lblGegnername.setBounds(45, 77, 150, 30);
		panel2.add(lblGegnername);

		JButton btnPopupTestSpielende = new JButton("Popup Test Spielende");
		btnPopupTestSpielende.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				spielendePopup("");
			}
		});
		btnPopupTestSpielende.setBounds(1054, 72, 200, 29);
		panel2.add(btnPopupTestSpielende);
		// CODE ENDE NUR ZU TESTZWECKEN cornemrc

		txtPath = new JTextField();
		txtPath.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		txtPath.setText("Bei File bitte Pfad angeben");
		txtPath.setBounds(534, 206, 600, 35);
		txtPath.setColumns(10);
		panel2.add(txtPath);

		// Tab3: STATISTIK
		JPanel panel3 = new JPanel();
		panel3.setOpaque(true);
		panel3.setBackground(Color.orange);
		tabbedPane.addTab("Statistik", panel3);
		panel3.setLayout(null);

		// 4w Silhouette
		ImageIcon img_silhouetteStat = new ImageIcon("pictures/4w_silhouette.png");
		JLabel lblSilhouetteStat = new JLabel(img_silhouetteStat);
		lblSilhouetteStat.setBounds(10, 100, 921, 500);
		panel3.add(lblSilhouetteStat);

		// Ergebnisanzeige Start
		ImageIcon img_chip_red_stat = new ImageIcon("pictures/red.png");
		JLabel lbl_chip_red_stat = new JLabel(img_chip_red_stat);
		lbl_chip_red_stat.setBounds(150, 10, 57, 50);
		panel3.add(lbl_chip_red_stat);

		JLabel lblSpielstandStat = new JLabel("Fungi                       vs.");
		lblSpielstandStat.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 25));
		lblSpielstandStat.setBounds(217, 16, 260, 34);
		panel3.add(lblSpielstandStat);

		JLabel lblSetstandfungiStat = new JLabel("set_stand_fungi");
		lblSetstandfungiStat.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 25));
		lblSetstandfungiStat.setBounds(217, 64, 194, 34);
		panel3.add(lblSetstandfungiStat);

		ImageIcon img_chip_yellow_stat = new ImageIcon("pictures/yellow.png");
		JLabel lbl_chip_yellow_stat = new JLabel(img_chip_yellow_stat);
		lbl_chip_yellow_stat.setBounds(510, 10, 57, 50);
		panel3.add(lbl_chip_yellow_stat);

		JLabel lblSetgegnerStat = new JLabel("set_gegner");
		lblSetgegnerStat.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 25));
		lblSetgegnerStat.setBounds(580, 16, 125, 34);
		panel3.add(lblSetgegnerStat);

		JLabel lblSetstandStat = new JLabel("set_stand_gegner");
		lblSetstandStat.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 25));
		lblSetstandStat.setBounds(580, 64, 205, 34);
		panel3.add(lblSetstandStat);
		// Ergebnisanzeige Ende

		// BEGINN DER STATISTIK SPIELTABELLE
		data_stat = new Object[][] { { null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null }, { null, null, null, null, null, null, null },
				{ null, null, "x", "o", null, null, null }, { null, null, "x", "o", null, null, null },
				{ null, null, "x", "o", null, null, null } };

		// Die Titel der Spalten
		String[] data_title_stat = new String[] { "0", "1", "2", "3", "4", "5", "6" };

		JTable game_stat = new JTable(data_stat, data_title_stat);
		ColorRenderer cR_stat = new ColorRenderer(data_stat);
		game_stat.setDefaultRenderer(Object.class, cR_stat);
		game_stat.setBounds(158, 108, 590, 475);
		game_stat.setRowHeight(79);
		game_stat.setEnabled(false); // nicht anklickbar
		panel3.add(game_stat);
		// ENDE DER STATISTIK SPIELTABELLE

		// GEGNER Selektionsbox
		JComboBox comboBox1 = new JComboBox(db.getGegner());
		comboBox1.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		comboBox1.setBounds(1008, 100, 200, 35);
		panel3.add(comboBox1);

		// Array für SPIEL Selektionsbox
		String[] spielArray = new String[3];
		spielArray[0] = "Spiel 1";
		spielArray[1] = "Spiel 2";
		spielArray[2] = "Spiel 3";

		JComboBox comboBox2 = new JComboBox(spielArray);
		comboBox2.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		comboBox2.setBounds(1008, 150, 200, 35);
		panel3.add(comboBox2);

		// Array für SATZ Selektionsbox
		String[] satzArray = new String[3];
		satzArray[0] = "Satz 1";
		satzArray[1] = "Satz 2";
		satzArray[2] = "Satz 3";

		JComboBox comboBox3 = new JComboBox(satzArray);
		comboBox3.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		comboBox3.setBounds(1008, 200, 200, 35);
		panel3.add(comboBox3);

		JButton btnShowStats = new JButton("Zeigen!");
		btnShowStats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showStats();
			}
		});
		btnShowStats.setBounds(1008, 250, 200, 35);
		panel3.add(btnShowStats);

		// JTabbedPane wird dem frame hinzugefügt
		frame.getContentPane().add(tabbedPane);

		// Fußzeilen-Info
		JLabel lblGewinntV = new JLabel("4 Gewinnt v0.3 alpha - Powered by Fungi Software Solutions");
		lblGewinntV.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblGewinntV.setBounds(830, 655, 430, 20);
		frame.getContentPane().add(lblGewinntV);

		// Einlesen der vorhandenen Konfigurationsdatei
		// Auslagerung des Programmcodes in ConfigFile-Klasse
		// Die einzelnen Radiobuttons werden anschließend entsprechend der
		// Variablen vorselektiert
		try {
			cFW.lesen();

			if (schnittstelle.equals("Push")) {
				rdbtnPush.setSelected(true);
			} else {
				rdbtnFile.setSelected(true);
			}
			if (spielerwahl.equals("x")) {
				rdbtnSpielerX.setSelected(true);
			} else {
				rdbtnSpielerO.setSelected(true);
			}
		} catch (Exception e) {
			System.err.println("Möglicherweise ist die Konfigurationsdatei beschädigt.");
		}

		frame.setVisible(true);
	}

	protected void showStats() {

	}

	public Boolean BtnSelected(String comp) {
		if (comp.equals("rdbtnPush"))
			return rdbtnPush.isSelected();
		else
			return rdbtnFile.isSelected();
	}

	private void startPlaying() {
		/*------------------------------------*\
			Auswahl der Schnittstelle
		\*------------------------------------*/
		if (BtnSelected("rdbtnPush")) {

			/*
			 * PUSHER
			 */
			comControl = new PusherImpl(ki, pwdSchlssel.getPassword());

		} else {

			/*
			 * FILE
			 */
			comControl = new FileImpl(ki, txtPath.getText(), spielerwahl);

		}
	}

	public static void update(Integer[][] spielfeld) {

		/*
		 * Konvertiere Spielfeld zu Object[][] data!
		 */
		String gegnerwahl;
		if (spielerwahl.equals("x"))
			gegnerwahl = "o";
		else
			gegnerwahl = "x";

		for (int zeile = 0; zeile < spielfeld.length; zeile++) {
			for (int spalte = 0; spalte < spielfeld[zeile].length; spalte++) {
				if (spielfeld[zeile][spalte] != null && spielfeld[zeile][spalte].intValue() == -1) {
					data[zeile][spalte] = gegnerwahl;
				} else if (spielfeld[zeile][spalte] != null && spielfeld[zeile][spalte] == 1) {
					data[zeile][spalte] = spielerwahl;
				} else
					data[zeile][spalte] = null;
			}
		}
		panel1.repaint();

	}

	public void setGegner(String gegner) {
		gegnerName = gegner;
		txtGegnername.setText(gegner);
		lblSetgegner.setText(gegner);
	}
}
