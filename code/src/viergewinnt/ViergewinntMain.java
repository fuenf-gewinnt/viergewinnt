package viergewinnt;

import db.AccessDB;
import gui.GUIinit;
import ki.Intelligence;

public class ViergewinntMain {

	public static void main(String[] args) {

		/*------------------------------------*\
			Intelligence enth�lt Spielfeld
		\*------------------------------------*/
		Intelligence ki = new Intelligence();
		AccessDB db = new AccessDB();

		/*------------------------------------*\
			Oberfl�che
		\*------------------------------------*/
		GUIinit gui = new GUIinit(ki, db);

	}

}