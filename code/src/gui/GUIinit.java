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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
	private boolean initialStart = true;
	public static int satz_id;
	public static int spiel_id;
	private JLabel lblSetsatz;
	private JLabel lblSetstandfungi;
	private JLabel lblSetstand;
	public static String gegnerName = "";
	static String spielerwahl;
	private static JFrame frame;
	private static Object[][] data;
	private static JPanel panel1;
	private final String sep = " (ID ";
	private final static String unserName = "Fungi";

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

	public static void satzendePopup(String gegnerName, AccessDB db, Intelligence ki) {
		int auswahl = JOptionPane.showOptionDialog(frame, "Wer hat gewonnen?", "Satzende",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				new String[] { unserName, gegnerName, "Unentschieden", "Satz wiederholen!" }, "Fungi");
		switch (auswahl) {
		case 0:
			db.updateSatzPunkte(satz_id, 2);
			break;
		case 1:
			db.updateSatzPunkte(satz_id, 0);
			break;
		case 2:
			db.updateSatzPunkte(satz_id, 1);
			break;
		case 3:
			db.cleanSatzUndZuege(satz_id);
			break;
		default:
			satzendePopup(gegnerName, db, ki);
			break;
		}
		if (auswahl < 3 && auswahl >= 0) {
			// Startspieler setzen
			if (ki.startSpieler != 0)
				db.updateSatzStartspieler(satz_id, ki.startSpieler);
			else
				System.out.println("Startspieler konnte nicht ermittelt werden.");

			// Nummer des Satzes in Zug-Tabelle updaten
			int satzKumuliert = db.getCountSaetze(spiel_id, db.ALLE);
			db.updateZugSatz(satz_id, satzKumuliert);

			// Gewinner setzen
			int tmpPunkte = db.getCountSaetze(spiel_id, db.GEWONNEN);
			int tmpAnzahlSaetze = db.getCountSaetze(spiel_id, db.ALLE);
			if ((tmpAnzahlSaetze == 2 && tmpPunkte >= 3) || (tmpAnzahlSaetze > 2 && tmpPunkte > 3)) {
				// gewonnen!
				db.updateSpielPunkte(spiel_id, 1);
				spielendePopup(unserName);
			} else if ((tmpAnzahlSaetze == 2 && tmpPunkte <= 1) || (tmpAnzahlSaetze > 2 && tmpPunkte < 3)) {
				// verloren!
				db.updateSpielPunkte(spiel_id, -1);
				spielendePopup(gegnerName);
			}
		}
		db.commit();
	}

	private static void spielendePopup(String gewinner) {
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
		frame.setTitle("4 Gewinnt v1.0 - Powered by Fungi Software Solutions");
		ImageIcon fungilogo = new ImageIcon("pictures/fungi_atompilz.png");
		frame.setIconImage(fungilogo.getImage());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.out.println("Programm über Fenster-X geschlossen.");
				db.commit();
				db.close();
				System.exit(0);
			}
		});

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

		lblSetsatz = new JLabel("");
		lblSetsatz.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 25));
		lblSetsatz.setBounds(1135, 27, 88, 34);
		panel1.add(lblSetsatz);

		// Ergebnisanzeige Start
		ImageIcon img_chip_red = new ImageIcon("pictures/red.png");
		JLabel lbl_chip_red = new JLabel(img_chip_red);
		lbl_chip_red.setBounds(150, 10, 57, 50);
		panel1.add(lbl_chip_red);

		JLabel lblSpielstand = new JLabel("Fungi              vs.");
		lblSpielstand.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 25));
		lblSpielstand.setBounds(217, 16, 260, 34);
		panel1.add(lblSpielstand);

		// set_stand_fungi
		lblSetstandfungi = new JLabel("0");
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

		// set_stand_gegner
		lblSetstand = new JLabel("0");
		lblSetstand.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 25));
		lblSetstand.setBounds(580, 64, 205, 34);
		panel1.add(lblSetstand);
		// Ergebnisanzeige Ende

		// Buttons Start
		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (!initialStart) {
					reset();
					System.out.println("Programm wurde zurückgesetzt.");
				}
				initialStart = false;
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
				db.commit();
				db.close();
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

		// JButton btnPopupTestSatzende = new JButton("Popup Test Satzende");
		// btnPopupTestSatzende.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// satzendePopup("gegner", db, ki);
		// }
		// });
		// btnPopupTestSatzende.setBounds(854, 72, 200, 29);
		// panel2.add(btnPopupTestSatzende);

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

		JLabel lblSpielstandStat = new JLabel("Fungi              vs.");
		lblSpielstandStat.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 25));
		lblSpielstandStat.setBounds(217, 16, 260, 34);
		panel3.add(lblSpielstandStat);

		// set_stand_fungi
		JLabel lblSetstandfungiStat = new JLabel("");
		lblSetstandfungiStat.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 25));
		lblSetstandfungiStat.setBounds(217, 64, 194, 34);
		panel3.add(lblSetstandfungiStat);

		ImageIcon img_chip_yellow_stat = new ImageIcon("pictures/yellow.png");
		JLabel lbl_chip_yellow_stat = new JLabel(img_chip_yellow_stat);
		lbl_chip_yellow_stat.setBounds(510, 10, 57, 50);
		panel3.add(lbl_chip_yellow_stat);

		// set_gegner
		JLabel lblSetgegnerStat = new JLabel("");
		lblSetgegnerStat.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 25));
		lblSetgegnerStat.setBounds(580, 16, 125, 34);
		panel3.add(lblSetgegnerStat);

		// set_stand_gegner
		JLabel lblSetstandStat = new JLabel("");
		lblSetstandStat.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 25));
		lblSetstandStat.setBounds(580, 64, 205, 34);
		panel3.add(lblSetstandStat);
		// Ergebnisanzeige Ende

		// BEGINN DER STATISTIK SPIELTABELLE
		data_stat = new Object[][] { { null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null }, { null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null }, { null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null } };

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
		spielArray[0] = null;

		JComboBox comboBox2 = new JComboBox(spielArray);
		comboBox2.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		comboBox2.setBounds(1008, 150, 200, 35);
		panel3.add(comboBox2);

		// Array für SATZ Selektionsbox
		String[] satzArray = new String[3];
		satzArray[0] = null;

		JComboBox comboBox3 = new JComboBox(satzArray);
		comboBox3.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		comboBox3.setBounds(1008, 200, 200, 35);
		panel3.add(comboBox3);

		JButton btnShowStats = new JButton("Zeigen!");
		btnShowStats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Box 3 verändert -> Statistik zeigen!
				if ((String) comboBox3.getSelectedItem() != null && !((String) comboBox3.getSelectedItem()).isEmpty()) {
					// Satz ID
					String tmp3 = ((String) comboBox3.getSelectedItem());
					String tmp4 = tmp3.substring(tmp3.indexOf(sep) + (sep).length(), tmp3.length() - 1);
					int tmpSatzID = Integer.parseInt(tmp4);

					// Statistik
					updateStats(db.getStatsSpielfeld(tmpSatzID));
					panel3.repaint();

					// Gegner
					lblSetgegnerStat.setText(db.getGegner(tmpSatzID));
				}
			}
		});
		btnShowStats.setBounds(1008, 250, 200, 35);
		panel3.add(btnShowStats);

		// JTabbedPane wird dem frame hinzugefügt
		frame.getContentPane().add(tabbedPane);

		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (tabbedPane.getSelectedIndex() == 2) {
					// Statistik-Tab ausgewählt
					comboBox1.removeAllItems();
					String[] tmp = db.getGegner();
					for (String line : tmp) {
						comboBox1.addItem(line);
					}
				}
			}
		});

		comboBox1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Box 1 verändert -> Box 2 füllen!
				JComboBox cb = (JComboBox) e.getSource();
				if ((String) cb.getSelectedItem() != null && !((String) cb.getSelectedItem()).isEmpty()) {
					String tmpGegner = (String) cb.getSelectedItem();
					comboBox2.removeAllItems();
					String[] tmpSpiele = db.getSpiele(tmpGegner);
					for (int i = 0; i < tmpSpiele.length; i++) {
						comboBox2.addItem("Spiel " + (i + 1) + sep + tmpSpiele[i] + ")");
					}
				}
			}
		});

		comboBox2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Box 2 verändert -> Box 3 füllen!
				JComboBox cb = (JComboBox) e.getSource();
				if ((String) cb.getSelectedItem() != null && !((String) cb.getSelectedItem()).isEmpty()) {
					String tmp = ((String) cb.getSelectedItem());
					String tmp2 = tmp.substring(tmp.indexOf(sep) + (sep).length(), tmp.length() - 1);
					int tmpSpielID = Integer.parseInt(tmp2);
					comboBox3.removeAllItems();
					String[] tmpSaetze = db.getSaetze(tmpSpielID);
					for (int i = 0; i < tmpSaetze.length; i++) {
						comboBox3.addItem("Satz " + (i + 1) + sep + tmpSaetze[i] + ")");
					}
				}
			}
		});

		// Fußzeilen-Info
		JLabel lblGewinntV = new JLabel("4 Gewinnt v1.0 - Powered by Fungi Software Solutions");
		lblGewinntV.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblGewinntV.setBounds(880, 655, 430, 20);
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

	public Boolean BtnSelected(String comp) {
		if (comp.equals("rdbtnPush"))
			return rdbtnPush.isSelected();
		else
			return rdbtnFile.isSelected();
	}

	private void reset() {
		ki.reset();
	}

	private void startPlaying() {
		/*------------------------------------*\
			Berechnung Satz/Spiel
		\*------------------------------------*/
		// Spiel
		int tmp = db.pruefeSpieleGewonnen(gegnerName);
		if (tmp == -2 || tmp == -1) {
			// Neues Spiel anlegen, da kein Spiel vorhanden ist oder alle haben
			// Sieger haben
			spiel_id = db.insertNeuesSpiel(gegnerName);
			if (spiel_id == -1)
				System.out.println("Fehler beim Anlegen des Spiels.");
		} else {
			// Benutze altes Spiel
			spiel_id = db.getLetztesSpiel(gegnerName);
			if (spiel_id == -1)
				System.out.println("Fehler beim Suchen des letzten Spiels.");
		}
		System.out.println("Spiel_ID: " + spiel_id);

		// Satz
		satz_id = db.insertNeuenSatz(gegnerName, spiel_id);
		System.out.println("Satz_ID: " + satz_id);

		/*------------------------------------*\
			Updaten der GUI & Datenbank
		\*------------------------------------*/
		lblSetsatz.setText(Integer.toString(db.getCountSaetze(spiel_id, db.ALLE)));
		lblSetstandfungi.setText(Integer.toString(db.getCountSaetze(spiel_id, db.GEWONNEN)));
		lblSetstand.setText(Integer.toString(db.getCountSaetze(spiel_id, db.VERLOREN)));

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

	public void updateStats(Integer[][] spielfeld) {
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
					data_stat[zeile][spalte] = gegnerwahl;
				} else if (spielfeld[zeile][spalte] != null && spielfeld[zeile][spalte] == 1) {
					data_stat[zeile][spalte] = spielerwahl;
				} else
					data_stat[zeile][spalte] = null;
			}
		}
	}

	public void setGegner(String gegner) {
		gegnerName = gegner;
		txtGegnername.setText(gegner);
		lblSetgegner.setText(gegner);
	}

	public int getSatz_id() {
		return satz_id;
	}
}
