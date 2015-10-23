package file;

import ki.Intelligence;

public class ChangeListener extends Thread {

	private FileImpl fc;
	private Intelligence ki;
	private final int timeOut = 300;

	public ChangeListener(FileImpl fc, Intelligence ki) {
		this.fc = fc;
		this.ki = ki;
	}

	@Override
	public void run() {
		// So lange spiel nicht beendet:
		while (!ki.spielBeendet()) {
			/*
			 * Spielsteuerung
			 */
			if (!fc.fileFound)
				System.out.print("Suche File " + fc.agentPfad + " ");
			fc.fileFound = true;
			if (fc.isNew()) {

				System.out.println("\r");

				long dif = System.currentTimeMillis();
				ki.handle(fc.getContent());
				if (ki.unserZug()) {
					System.out.println("Wir sind dran!");
					int tmp = ki.getZug();
					fc.send(tmp);
					System.out.print("Dauer des Zugs: ");
					System.out.print((System.currentTimeMillis() - dif));
					System.out.print("ms\n");
					// ki.checkIfBeendet();
				} else
					System.out.println("Wir sind nicht dran!");

				fc.fileFound = false;
			}

			// Leerlauf!
			long l = System.currentTimeMillis() + timeOut;
			while (System.currentTimeMillis() < l) {
			}
		}
	}

}
