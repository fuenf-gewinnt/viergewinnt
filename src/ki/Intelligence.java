package ki;

import gui.GUIinit;

public class Intelligence {

	private int spalten = 7;
	private int zeilen = 6;
	private Integer zug = null;
	private Integer[][] spielfeld = new Integer[zeilen][spalten];
	/*
	 * >> Gegner: -1 <<>> Wir: 1 <<
	 */
	private boolean unserZug;

	public void handle(String[] content) {
		if (Integer.parseInt(content[2]) >= 0) {
			/*
			 * Gegnerzug ohne Überprüfung
			 */
			System.out.println("Gegner schmeisst in: " + Integer.parseInt(content[2]));
			fuegeInSpalteEin(Integer.parseInt(content[2]), -1);
			GUIinit.update(spielfeld);
		}
		if (content[0].equals("true")) {
			/*
			 * Unser Zug!
			 */
			calculate();
			fuegeInSpalteEin(zug, 1);
			GUIinit.update(spielfeld);
			unserZug = true;
		} else if (!content[3].equals("offen")) {
			System.out.println(content[3] + " hat gewonnen.");
			// öffne Popup
			// aktiviere Button!
			unserZug = false;
		} else {
			System.out.println("Freigabe: false");
			// öffne Popup
			// aktiviere Button!
			unserZug = false;
		}
	}

	public void calculate() {
		zug = null;
		// kalkuliere...
		zug = 1;
	}

	private void fuegeInSpalteEin(int spalte, int spieler) {
		for (int i = zeilen; i > 0; i--) {
			if (spielfeld[i - 1][spalte] == null || spielfeld[i - 1][spalte] == 0) {
				spielfeld[i - 1][spalte] = spieler;
				break;
			}
		}
	}

	@SuppressWarnings("unused")
	private int zufall() {
		Double random = Math.random() * 10;
		long round = Math.round(random);
		int cut = (int) round;
		if (cut >= 0 && cut < spalten) {
			return cut;
		} else {
			return zufall();
		}
	}

	public Integer getZug() {
		// Liefere Controller unseren neuen Zug
		return zug;
	}

	public boolean unserZug() {
		return unserZug;
	}
}
