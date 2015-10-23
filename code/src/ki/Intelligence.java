package ki;

import java.util.Random;

import db.AccessDB;
import gui.GUIinit;

public class Intelligence {

	private int globalSpalten = 7;
	private int globalZeilen = 6;
	private Integer zug = null;
	private Boolean istBeendet = false;
	private boolean unserZug;
	private AccessDB db;
	private Integer[][] spielfeld = new Integer[globalZeilen][globalSpalten];
	private int zugKumuliert = 0;
	public int startSpieler = 0;
	/*
	 * >> Gegner: -1 <<>> Wir: 1 <<
	 */

	public void reset() {
		zug = null;
		istBeendet = false;
		spielfeld = new Integer[globalZeilen][globalSpalten];
		zugKumuliert = 0;
		startSpieler = 0;
		GUIinit.update(spielfeld);
	}

	public Intelligence(AccessDB db) {
		this.db = db;
	}

	public void handle(String[] content) {
		/*
		 * Gewinn-�berpr�fung fehlt?
		 */
		if (Integer.parseInt(content[2]) >= 0) {
			/*
			 * Gegnerzug ohne �berpr�fung!
			 */
			int spalte = Integer.parseInt(content[2]);
			System.out.println("Gegner schmeisst in: " + spalte);
			fuegeInSpalteEin(spalte, -1);
			GUIinit.update(spielfeld);

			if (zugKumuliert == 0) {
				// Gegner ist Startspieler
				startSpieler = -1;
			}
			zugKumuliert++;

			db.insertNeuenZug(zugKumuliert, GUIinit.satz_id, db.getCountSaetze(GUIinit.spiel_id, db.ALLE), spalte,
					getZeilennummer(spalte) - 1, -1);
		}
		if (content[0].equals("true")) {
			/*
			 * Unser Zug!
			 */
			if (Integer.parseInt(content[2]) < 0)
				zug = startzug();
			else
				zug = legLosKI_gibGas();
			fuegeInSpalteEin(zug, 1);
			GUIinit.update(spielfeld);
			unserZug = true;

			if (zugKumuliert == 0) {
				// Gegner ist Startspieler
				startSpieler = 1;
			}
			zugKumuliert++;

			db.insertNeuenZug(zugKumuliert, GUIinit.satz_id, db.getCountSaetze(GUIinit.spiel_id, db.ALLE), zug,
					getZeilennummer(zug) - 1, 1);
		} else if (!content[3].equals("offen")) {
			/*
			 * Gewinner
			 */
			System.out.println(content[3] + " hat gewonnen.");
			GUIinit.satzendePopup(content[3], db, this);
			GUIinit.btnStart.setEnabled(true);
			unserZug = false;
			istBeendet = true;

			// Ist Spielende?
			// GUIinit.spielendePopup();
			// in db eintragen
		} else {
			/*
			 * Fehler
			 */
			System.out.println("Freigabe: false");
			GUIinit.satzendePopup(GUIinit.gegnerName, db, this);
			GUIinit.btnStart.setEnabled(true);
			unserZug = false;
			istBeendet = true;

			// Ist Spielende?
			// GUIinit.spielendePopup();
			// in db eintragen
		}
	}

	Integer[] moeglicheZuege = new Integer[globalSpalten];
	/*
	 * Verbot = -1 Ja = 1 Egal = 0
	 */
	int[] ja_array = new int[globalSpalten];
	int[] unserJa_array = new int[globalSpalten];
	int[] egal_array = new int[globalSpalten];
	int[] verbot_array = new int[globalSpalten];

	/*
	 * Allgemeine Vorgehensweise der KI: Wir setzen fiktiv einen Stein in jede
	 * Spalte des Spielfelds. Zun�chst unseren ( 1 ), dann einen vom Gegner ( -1
	 * ) Dann geht er die M�glichkeiten aus der KLasse "CheckSpielfeld" durch
	 * 
	 * Prio 1: Stein '1' wird in das Spielfeld gesetzt und m�glichkeiten
	 * getestet bei true -> Stein wird in die Spalte gesetzt --> UNSER SIEG bei
	 * false -> Prio 2
	 * 
	 * Prio 2: Stein '-1' wird in das Spielfeld gesetzt und m�glichkeiten
	 * getestet bei true -> Stein wird in die Spalte gesetzt --> GEGNERSIEG
	 * verhindert bei false -> Prio 3
	 * 
	 * Prio 3: Versuchen den Gegner zu blocken oder eine eigene Zwickm�hle bauen
	 * Dabei wird unterschieden, zwischen Spaltenw�rfe die so etwas tun und
	 * Einw�rfe, die verboten sind verboten hei�t, dieser Zug w�rde den Gegner
	 * im n�chsten Zug zum Sieg verhelfen
	 * 
	 * Und abschlie�end wird aus den 'ja'-m�glichkeiten random ausgew�hlt falls
	 * nicht vorhanden aus den 'egal'-m�glichkeiten random ausgew�hlt ganz zu
	 * schluss aus der 'verboten'-M�glichkeiten ausgew�hlt -> es g�be nicht mehr
	 * die m�glichkeit zu gewinnen
	 */

	// falls wir anfangen-> mach das; ansonsten mach legLosKI_gibGas()
	public int startzug() {
		// random.nextInt(max - min + 1) + min;
		Random random = new Random();
		int i = random.nextInt(3) + 2;
		return i;
	}

	// Spieler 1 = wir
	// Spieler -1 = Gegner
	public int legLosKI_gibGas() {
		// --> Prio1: Pr�fen ob wir gewinnen k�nnen
		for (int spalte = 0; spalte < 7; spalte++) {
			// testen!!!!!! outofbound
			int zeilenNummer = getZeilennummer(spalte);

			if (zeilenNummer < 6 && analysiereSpielfeld(zeilenNummer, spalte, 3, 1)) {
				return spalte;
			}
		}

		// --> Prio2: Pr�fen ob gegner im n�chsten Zug gewinnen kann
		for (int spalte = 0; spalte < 7; spalte++) {

			int zeilenNummer = getZeilennummer(spalte);

			if (zeilenNummer < 6 && analysiereSpielfeld(zeilenNummer, spalte, 3, -1)) {
				return spalte;
			}
		}

		// --> Prio3: Keiner kann im n�chsten Zug gewinnen, Gegner das Leben
		// schwer machen oder Zwickm�hle bauen
		// -->Gegnerzug �berpr�fen
		for (int spalte = 0; spalte < 7; spalte++) {
			int zeilenNummer = getZeilennummer(spalte);

			if (zeilenNummer < 6 && analysiereSpielfeld(zeilenNummer, spalte, 2, -1)) {
				// ja = voerst m�glicher und sinnvoller Zug, der aber noch
				// �berpr�ft werden muss
				moeglicheZuege[spalte] = 1;
			}
		}

		// --> Prio3: Keiner kann im n�chsten Zug gewinnen, Gegner das Leben
		// schwer machen oder Zwickm�hle bauen
		// --> eigenen zug �berpr�fen
		for (int spalte = 0; spalte < 7; spalte++) {
			int zeilenNummer = getZeilennummer(spalte);

			if (zeilenNummer < 6 && analysiereSpielfeld(zeilenNummer, spalte, 2, 1)) {
				// ja = voerst m�glicher und sinnvoller Zug, der aber noch
				// �berpr�ft werden muss
				moeglicheZuege[spalte] = 2;
			}
		}

		// --> Prio3.1: �berpr�fung der Z�ge aus Prio3, ob Gegner durch den Zug
		// gewinnen kann

		for (int spalte = 0; spalte < 7; spalte++) {
			if (moeglicheZuege[spalte] != null && moeglicheZuege[spalte] == 1) {
				int zeilenNummer = getZeilennummer(spalte);

				if (zeilenNummer < 6 && analysiereSpielfeld(zeilenNummer, spalte, 3, -1)) {
					moeglicheZuege[spalte] = -1;
				}
			}
		}

		// --> Auswahl des n�chsten Spielzugs aus Prio3.
		// Random aus den "ja-Z�gen". Wenn kein ja dann aus den "egal-Z�gen".
		// Zuletzt aus den "verboten-Zuegen"

		// schau ob wir zwei Steine nebeneinander haben -> 3. einf�gen
		int a = 0;
		for (int i = 0; i < moeglicheZuege.length; i++) {
			if (moeglicheZuege[i] != null && moeglicheZuege[i] == 2) {
				unserJa_array[a] = i;
				a++;
			}

		}
		if (a > 0) {
			Random random = new Random();
			int spalte = random.nextInt(a - 1 - 0 + 1) + 0;
			return unserJa_array[spalte];
		}

		// schau ob gegner zwei Steine nebeneinander hat -> Fr�hzeitig blocken
		int j = 0;
		for (int i = 0; i < moeglicheZuege.length; i++) {
			if (moeglicheZuege[i] != null && moeglicheZuege[i] == 1) {
				ja_array[j] = i;
				j++;
			}

		}
		if (j > 0) {
			Random random = new Random();
			int spalte = random.nextInt(j - 1 - 0 + 1) + 0;
			return ja_array[spalte];
		}

		// wenn beide vorherigen Arrays null -> nimm random eins aus dem
		// egal_array
		int jj = 0;
		for (int i = 0; i < moeglicheZuege.length; i++) {
			if ((moeglicheZuege[i] == null || moeglicheZuege[i] == 0)
					&& (moeglicheZuege[i] != null && getZeilennummer(moeglicheZuege[i]) < globalZeilen)) {
				egal_array[jj] = i;
				jj++;
			}

		}
		if (jj > 0) {
			Random random = new Random();
			int spalte = random.nextInt(jj);
			return egal_array[spalte];

		}

		// letzte m�glichkeit: stein einwerfen, obwohl gegner gewinnen kann
		int jjj = 0;
		int spalte = 1;
		for (int i = 0; i < moeglicheZuege.length; i++) {
			if (moeglicheZuege[i] != null && moeglicheZuege[i] == -1) {
				verbot_array[jjj] = i;
				jjj++;
			}
		}
		if (jjj > 0) {
			Random random = new Random();
			spalte = random.nextInt(jjj - 1 - 0 + 1) + 0;

		}
		return verbot_array[spalte];

	}

	public int hatJemandGewonnen() {
		int zeilenNummer = 0;
		for (int spalte = 0; spalte < 7; spalte++) {
			if (zeilenNummer < 6 && analysiereSpielfeld(zeilenNummer, spalte, 3, 1)) {
				zeilenNummer++;

				// wir haben gewonnen
				return 1;
			}
		}
		zeilenNummer = 0;
		for (int spalte = 0; spalte < 7; spalte++) {
			if (zeilenNummer < 6 && analysiereSpielfeld(zeilenNummer, spalte, 3, -1)) {
				zeilenNummer++;

				// Gegner hat gewonnen
				return -1;
			}
		}
		// 0 = kein Gewinn liegt vor
		return 0;
	}

	private void fuegeInSpalteEin(int spalte, int spieler) {
		for (int i = globalZeilen; i > 0; i--) {
			if (spielfeld[i - 1][spalte] == null || spielfeld[i - 1][spalte] == 0) {
				spielfeld[i - 1][spalte] = spieler;
				break;
			}
		}
	}

	public Integer getZug() {
		// Liefere Controller unseren neuen Zug
		return zug;
	}

	public boolean unserZug() {
		return unserZug;
	}

	public boolean spielBeendet() {
		return istBeendet;
	}

	// R�ckgabe ist die Anzahl der Steine in der entsprechenden Spalte
	protected int getZeilennummer(int spalte) {
		int anzahl = 0;
		for (int i = 0; i < 6; i++) {
			if (!(spielfeld[globalZeilen - 1 - (i)][spalte] == null
					|| spielfeld[globalZeilen - 1 - (i)][spalte] == 0)) {
				anzahl++;
			}
		}
		System.out.println("getZeilennummer(" + spalte + ") = " + anzahl);
		return anzahl;
	}

	// �berpr�fung 1: Waagrechte Siegm�glichkeit?
	private boolean pruefeWaagrecht(int zeile, int spalte, int anzahl, int spieler) {
		for (int r = 0; r <= anzahl; r++) {
			int gleicheSteine = 0;

			for (int l = 0; l <= anzahl; l++) {
				if (istDieSpalteNochImSpielfeld(spalte, l, r, 0)
						&& spielfeld[globalZeilen - 1 - (zeile)][spalte + l - r] != null
						&& spielfeld[globalZeilen - 1 - (zeile)][spalte + l - r] == spieler) {
					gleicheSteine++;
				}
			}

			if (gleicheSteine == anzahl) {
				return true;
			}
		}
		return false;

	}

	// �berpr�fung 2: Senkrechte Siegm�glichkeit?
	private boolean pruefeSenkrecht(int zeile, int spalte, int anzahl, int spieler) {
		for (int r = 0; r <= anzahl; r++) {
			int gleicheSteine = 0;

			for (int l = 0; l <= anzahl; l++) {
				if (istDieZeileNochImSpielfeld(zeile, l, r)
						&& spielfeld[globalZeilen - 1 - (zeile + l - r)][spalte] != null
						&& spielfeld[globalZeilen - 1 - (zeile + l - r)][spalte] == spieler) {
					gleicheSteine++;
				}
			}
			if (gleicheSteine == anzahl) {
				return true;
			}
		}
		return false;

	}

	// �berpr�fung 3: Diagonale Siegm�glichkeit? (Links unten nach rechts oben)
	private boolean pruefeDiagonalLinksUnten(int zeile, int spalte, int anzahl, int spieler) {
		for (int r = 0; r <= anzahl; r++) {
			int gleicheSteine = 0;

			for (int l = 0; l <= anzahl; l++) {
				if (istDieSpalteNochImSpielfeld(spalte, l, r, 0) && istDieZeileNochImSpielfeld(zeile, l, r)
						&& spielfeld[globalZeilen - 1 - (zeile + l - r)][spalte + l - r] != null
						&& spielfeld[globalZeilen - 1 - (zeile + l - r)][spalte + l - r] == spieler) {
					gleicheSteine++;
				}
			}
			if (gleicheSteine == anzahl) {
				return true;
			}

		}
		return false;
	}

	// �berpr�fung 4: Diagonale Siegm�glichkeit? (Rechts unten nach Links oben)
	private boolean pruefeDiagonalRechtsUnten(int zeile, int spalte, int anzahl, int spieler) {
		for (int r = 0; r <= anzahl; r++) {
			int gleicheSteine = 0;

			for (int l = 0; l <= anzahl; l++) {
				if (istDieSpalteNochImSpielfeld(spalte, l, r, 1) && istDieZeileNochImSpielfeld(zeile, l, r)
						&& spielfeld[globalZeilen - 1 - (zeile + l - r)][spalte - l + r] != null
						&& spielfeld[globalZeilen - 1 - (zeile + l - r)][spalte - l + r] == spieler) {
					gleicheSteine++;
				}
			}

			if (gleicheSteine == anzahl) {
				return true;
			}

		}
		return false;
	}

	// Boolean-Methode um zu �brpr�fen, ob die Spalte noch im SPielfeld ist
	private boolean istDieSpalteNochImSpielfeld(int spalte, int links, int rechts, int i) {
		if (i == 1) {
			/*
			 * Sonderfall (diagonal rechts unten)
			 */
			if ((spalte - links + rechts) >= 0 && (spalte - links + rechts) < 7)
				return true;

		} else {
			/*
			 * Standard
			 */
			if ((spalte + links - rechts) >= 0 && (spalte + links - rechts) < 7)
				return true;

		}
		return false;
	}

	// Boolean-Methode um zu �brpr�fen, ob die Zeile noch im Spielfeld ist
	private boolean istDieZeileNochImSpielfeld(int zeile, int links, int rechts) {
		if ((zeile + links - rechts) >= 0 && (zeile + links - rechts) < 6) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Die wichtigste Methode, die alle anderen Methoden dieser Klasse vereint
	 * Hier wird geschaut, ob ieine der oberen M�glichkeiten gegeben ist und
	 * entsprehend ein true gesetzt true bedeutet f�r uns: Stein setzen, wir
	 * gewinnen! true bedeutet f�r gegner: Stein setzen, sonst gewinnt der
	 * Gegner! false: diese Spalte f�hrt nicht zum Sieg, (eig) uninteressant
	 */
	protected boolean analysiereSpielfeld(int zeile, int spalte, int anzahl, int spieler) {
		if (pruefeWaagrecht(zeile, spalte, anzahl, spieler)) {
			return true;
		} else if (pruefeSenkrecht(zeile, spalte, anzahl, spieler)) {
			return true;
		} else if (pruefeDiagonalLinksUnten(zeile, spalte, anzahl, spieler)) {
			return true;
		} else if (pruefeDiagonalRechtsUnten(zeile, spalte, anzahl, spieler)) {
			return true;
		} else {
			return false;
		}

	}

	public boolean checkIfBeendet() {
		if (hatJemandGewonnen() != 0) {
			System.out.println("Spiel zuende.");
			istBeendet = true;
			return true;
		} else
			return false;
	}

}
