package file;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import gui.GUIinit;
import ki.Intelligence;

public class FileImpl implements FileController {

	public static FileReader fileReader;
	public static FileWriter agentWriter;
	public String agentPfad;
	public String serverPfad;
	public String spielerangabe;
	private String[] content;
	private Boolean isReady;
	public boolean fileFound;

	public FileImpl(Intelligence ki, String pfad, String spielerangabe) {
		// Übergabe der Daten um Datei(pfad) zu finden
		this.spielerangabe = spielerangabe;
		if (!pfad.endsWith("\\"))
			pfad = pfad + "\\";
		agentPfad = pfad + "server2spieler" + spielerangabe + ".xml";
		serverPfad = pfad + "spieler" + spielerangabe + "2server.txt";
		Thread t = new ChangeListener(this, ki);
		t.start();
		try {
			Thread.sleep(40);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		GUIinit.btnStart.setEnabled(false);
	}

	@Override
	public void send(int z) {
		try {
			File agentFile = new File(serverPfad);
			agentFile.createNewFile();
			// System.out.println("Agentfile erfolgreich angelegt");

			// FileWriter Object erstellen:
			agentWriter = new FileWriter(agentFile);
			// System.out.println("FileWriter erfolgreich angelegt");

			agentWriter.write(Integer.toString(z));
			agentWriter.flush();
			agentWriter.close();
			System.out.println("Erfolgreiche Eingabe in File");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void recieve() {
		try {
			// content[0] = freigabe
			// content[1] = satzstatus
			// content[2] = gegnerzug
			// content[3] = sieger
			content = new String[4];
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = dBuilder.parse(agentPfad);
			Element element = doc.getDocumentElement();

			NodeList nodes = element.getChildNodes();
			int a = 0;
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);

				if (node instanceof Element) {
					Element child = (Element) node;

					content[a] = child.getTextContent();
					a++;
				}

			}

		} catch (Exception e) {
			System.out.println("bad");
		}

	}

	private void delete() {
		try {
			File file = new File(agentPfad);
			file.delete();
		} catch (Exception e) {
			System.out.println("bad to delete");
		}

	}

	public String[] getContent() {
		return content;
	}

	public boolean isNew() {
		try {
			fileReader = new FileReader(agentPfad);
			recieve();
			fileReader.close();
			delete();
			return true;

		} catch (IOException e) {
			System.out.print(".");
			return false;
		}

	}

}
