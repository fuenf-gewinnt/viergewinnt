package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigFile {
	FileWriter writer;
	File file;
	FileReader reader;
	GUIinit gui;

	public ConfigFile(GUIinit init) {
		this.gui = init;
	}

	public void schreiben() {

		// Datei anlegen
		file = new File("config.ini");
		try {
			// falls die Datei bereits existiert wird diese überschrieben
			writer = new FileWriter(file);

			// Text wird in den Stream geschrieben
			writer.write(gui.txtGegnername.getText());
			// Plattformunabhängiger Zeilenumbruch
			writer.write(System.getProperty("line.separator"));
			writer.write(gui.pwdKey.getPassword());
			writer.write(System.getProperty("line.separator"));
			writer.write(gui.pwdSecret.getPassword());
			writer.write(System.getProperty("line.separator"));
			writer.write(gui.pwdAppID.getPassword());
			writer.write(System.getProperty("line.separator"));
			writer.write(gui.schnittstelle);
			writer.write(System.getProperty("line.separator"));
			writer.write(GUIinit.spielerwahl);

			// Schreibe Stream in Datei
			writer.flush();

			// Schließe Stream
			writer.close();
		}

		catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Beginn LESEN
	// Buffered Reader liest die einzelnen Zeilen der ini Datei aus
	// Die einzelnen Variablen werden anschließend entsprechend befüllt

	public void lesen() throws IOException {
		try {
			reader = new FileReader("config.ini");
			BufferedReader br = new BufferedReader(reader);

			gui.setGegner(br.readLine());
			gui.pwdKey.setText(br.readLine());
			gui.pwdSecret.setText(br.readLine());
			gui.pwdAppID.setText(br.readLine());
			String zeile3 = br.readLine();
			if (zeile3.equals("Push")) {
				gui.schnittstelle = "Push";
				gui.txtPath.setText("Bei File bitte Pfad angeben");
			} else {
				String[] splitarray = zeile3.split("_");
				gui.schnittstelle = "File";
				gui.txtPath.setText(splitarray[1]);
			}
			String zeile4 = br.readLine();
			if (zeile4.equals("x")) {
				gui.spielerwahl = "x";
			} else {
				gui.spielerwahl = "o";
			}

			br.close();
		} catch (FileNotFoundException e) {
			System.err.println("Keine Konfigurationsdatei zum Einlesen vorhanden!");
		}

	}

}