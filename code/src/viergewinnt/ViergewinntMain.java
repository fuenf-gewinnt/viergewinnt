package viergewinnt;

import db.AccessDB;
import gui.GUIinit;
import ki.Intelligence;

public class ViergewinntMain {

	public static void main(String[] args) {

		/*------------------------------------*\
			Intelligence enthält Spielfeld
		\*------------------------------------*/
		Intelligence ki = new Intelligence();
		AccessDB db = new AccessDB();

		/*------------------------------------*\
			Oberfläche
		\*------------------------------------*/
		GUIinit gui = new GUIinit(ki, db);

	}

}