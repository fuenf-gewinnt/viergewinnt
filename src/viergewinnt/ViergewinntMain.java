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
		@SuppressWarnings("unused")
		AccessDB db = new AccessDB();

		/*------------------------------------*\
			Oberfl�che
		\*------------------------------------*/
		@SuppressWarnings("unused")
		GUIinit gui = new GUIinit(ki);

	}

}