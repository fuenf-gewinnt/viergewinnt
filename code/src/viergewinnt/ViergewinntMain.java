package viergewinnt;

import db.AccessDB;
import gui.GUIinit;
import ki.Intelligence;

public class ViergewinntMain {
	public static void main(String[] args) {

		/*------------------------------------*\
			Intelligence enthält Spielfeld
		\*------------------------------------*/
		AccessDB db = new AccessDB();
		Intelligence ki = new Intelligence(db);

		/*------------------------------------*\
			Oberfläche
		\*------------------------------------*/
		GUIinit gui = new GUIinit(ki, db);
	}
}