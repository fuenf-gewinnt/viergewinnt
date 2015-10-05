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

	public void schreiben() {

		// Datei anlegen
		file = new File("config.ini");
		try {
			// falls die Datei bereits existiert wird diese überschrieben
			writer = new FileWriter(file);

			// Text wird in den Stream geschrieben
			writer.write(GUIinit.txtGegnername.getText());
			// Plattformunabhängiger Zeilenumbruch
			writer.write(System.getProperty("line.separator"));
			writer.write(GUIinit.pwdSchlssel.getPassword());
			writer.write(System.getProperty("line.separator"));
			writer.write(GUIinit.schnittstelle);
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

			GUIinit.txtGegnername.setText(br.readLine());
			GUIinit.pwdSchlssel.setText(br.readLine());
			String zeile3 = br.readLine();
			if (zeile3.equals("Push")) {
				GUIinit.schnittstelle = "Push";
				GUIinit.txtPath.setText("Bei File bitte Pfad angeben");
			} else {
				String[] splitarray = zeile3.split("_");
				GUIinit.schnittstelle = "File";
				GUIinit.txtPath.setText(splitarray[1]);
			}
			String zeile4 = br.readLine();
			if (zeile4.equals("x")) {
				GUIinit.spielerwahl = "x";
			} else {
				GUIinit.spielerwahl = "o";
			}

			br.close();
		} catch (FileNotFoundException e) {
			System.err.println("Keine Konfigurationsdatei zum Einlesen vorhanden!");
		}

	}

}