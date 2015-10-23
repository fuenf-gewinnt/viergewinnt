package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AccessDB {
	public final int ALLE = 0;
	public final int GEWONNEN = 1;
	public final int VERLOREN = -1;
	private Connection con;

	public AccessDB() {
		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("Treiberklasse nicht gefunden!");
			return;
		}

		try {
			con = DriverManager.getConnection("jdbc:hsqldb:file:src/db/MyDB;shutdown=true", "SA", "");
			System.out.println("Datenbank-Verbindung hergestellt.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String[] getGegner() {
		String[] gegner = null;
		try {
			Statement stmt = con.createStatement();
			String sql = "SELECT GEGNER FROM Spiel";
			ResultSet rs = stmt.executeQuery(sql);
			List<String> results = new ArrayList<String>();
			while (rs.next()) {
				results.add(rs.getString(1));
			}
			gegner = results.toArray(new String[results.size()]);
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return gegner;
	}

	public int pruefeSpieleGewonnen(String gegner) {
		/*------------------------------------*\
			return -2, wenn kein Spiel vorhanden
			return -1, wenn alle Spiele Sieger haben
			return X, Spiel_ID, von dem kein Gegner gibt.
		\*------------------------------------*/
		int tmp = -2;
		boolean spielNichtBeendet = false;

		try {
			Statement stmt = con.createStatement();
			String sql = "SELECT Spiel_ID, Sieger FROM Spiel WHERE Gegner = '" + gegner + "'";
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				Integer spiel_ID = rs.getInt(1);
				Integer sieger = rs.getInt(2);
				if (sieger != null && sieger != 0)
					tmp = -1;
				else {
					spielNichtBeendet = true;
					tmp = spiel_ID;
				}
			}

			rs.close();
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("Check, ob vom Gegner bereits Spiel: " + tmp);
		if (spielNichtBeendet)
			System.out.println("Spiel " + tmp + " wurde nicht zuende gespielt!");
		return tmp;
	}

	public int insertNeuesSpiel(String gegnerName) {
		int tmp = -1;
		try {
			Statement stmt = con.createStatement();
			PreparedStatement psInsert = con
					.prepareStatement("INSERT INTO SPIEL VALUES(null, '" + gegnerName + "', 0);");
			psInsert.executeUpdate();
			PreparedStatement psIdentity = con.prepareStatement("CALL IDENTITY()");
			ResultSet rs = psIdentity.executeQuery();
			rs.next();
			tmp = rs.getInt(1);
			rs.close();
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tmp;
	}

	public int getLetztesSpiel(String gegnerName) {
		int tmp = -1;
		try {
			Statement stmt = con.createStatement();
			String sql = "SELECT Spiel_ID FROM Spiel WHERE Sieger = '0' AND Gegner = '" + gegnerName + "'";
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				tmp = rs.getInt(1);
			}

			rs.close();
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tmp;
	}

	public int insertNeuenSatz(String gegnerName, int spiel_id) {
		int tmp = -1;
		try {
			Statement stmt = con.createStatement();
			PreparedStatement psInsert = con
					.prepareStatement("INSERT INTO Satz VALUES(null, '" + spiel_id + "', -1, 0);");
			psInsert.executeUpdate();
			PreparedStatement psIdentity = con.prepareStatement("CALL IDENTITY()");
			ResultSet rs = psIdentity.executeQuery();
			rs.next();
			tmp = rs.getInt(1);
			rs.close();
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tmp;
	}

	public int getCountSaetze(int spiel_id, int spieler) {
		int tmp = 0;
		try {
			Statement stmt = con.createStatement();
			String sql = null;

			if (spieler == ALLE) {
				// Alle S�tze dieses Spieles
				sql = "SELECT COUNT(*) FROM Satz WHERE Spiel_ID = '" + spiel_id + "'";
			} else if (spieler == VERLOREN) {
				// Alle S�tze, die wir verloren haben (Punkte)
				// verarbeitung s.u.
				sql = "SELECT COUNT(*) FROM Satz WHERE Punkte >= 0 AND Spiel_ID = '" + spiel_id + "'";
			} else {
				// Alle S�tze, die wir gewonnen haben (Punkte)
				sql = "SELECT SUM(Punkte) FROM Satz WHERE Punkte >= 0 AND Spiel_ID = '" + spiel_id + "'";
			}

			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				tmp = rs.getInt(1);
			}
			rs.close();
			stmt.close();

			if (spieler == VERLOREN) {
				// Alle Punkte minus unsere = Gegner-Punkte
				int unserePunkte = 0;
				String tmpSql = "SELECT SUM(Punkte) FROM Satz WHERE Punkte >= 0 AND Spiel_ID = '" + spiel_id + "'";
				Statement tmpStmt = con.createStatement();
				ResultSet tmpRs = tmpStmt.executeQuery(tmpSql);
				while (tmpRs.next()) {
					unserePunkte = tmpRs.getInt(1);
				}
				tmpRs.close();
				tmpStmt.close();
				tmp = (2 * tmp) - unserePunkte;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tmp;
	}

	public void insertNeuenZug(int zug, int satz_id, int satz, int spalte, int zeile, int spieler) {
		// Zug_ID Zug Satz_ID Satz Spalte Zeile Zug_Spieler
		int tmp = -1;
		try {
			Statement stmt = con.createStatement();
			PreparedStatement psInsert = con.prepareStatement("INSERT INTO Zug VALUES(null, " + zug + ", " + satz_id
					+ ", " + satz + ", " + spalte + ", " + zeile + ", " + spieler + ");");
			psInsert.executeUpdate();
			PreparedStatement psIdentity = con.prepareStatement("CALL IDENTITY()");
			ResultSet rs = psIdentity.executeQuery();
			rs.next();
			tmp = rs.getInt(1);
			rs.close();
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Zug (ID:" + tmp + ") in DB eingef�gt.");
	}

	public void close() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void commit() {
		try {
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateSatzPunkte(int satz_id, int punkte) {
		try {
			Statement stmt = con.createStatement();
			String sql = "UPDATE Satz SET Punkte = " + punkte + " WHERE Satz_ID = " + satz_id;
			int rs = stmt.executeUpdate(sql);
			System.out.println(rs + " Zeile(n) der Satz-Tabelle ver�ndert. (Punkte)");
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void cleanSatzUndZuege(int satz_id) {

		try {
			// Erst Z�ge l�schen
			Statement stmt2 = con.createStatement();
			String sql2 = "DELETE FROM Zug WHERE Satz_ID = " + satz_id;
			int rs2 = stmt2.executeUpdate(sql2);
			System.out.println(rs2 + " Zeile(n) der Zug-Tabelle gel�scht.");
			stmt2.close();

			// Dann Satz l�schen
			Statement stmt1 = con.createStatement();
			String sql1 = "DELETE FROM Satz WHERE Satz_ID = " + satz_id;
			int rs1 = stmt1.executeUpdate(sql1);
			System.out.println(rs1 + " Zeile(n) der Satz-Tabelle gel�scht.");
			stmt1.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateSatzStartspieler(int satz_id, int startSpieler) {
		try {
			Statement stmt = con.createStatement();
			String sql = "UPDATE Satz SET Startspieler = " + startSpieler + " WHERE Satz_ID = " + satz_id;
			int rs = stmt.executeUpdate(sql);
			System.out.println(rs + " Zeile(n) der Satz-Tabelle ver�ndert. (Startspieler)");
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateZugSatz(int satz_id, int satzKumuliert) {
		try {
			Statement stmt = con.createStatement();
			String sql = "UPDATE Zug SET Satz = " + satzKumuliert + " WHERE Satz_ID = " + satz_id;
			int rs = stmt.executeUpdate(sql);
			System.out.println(rs + " Zeile(n) der Zug-Tabelle ver�ndert. (Satz)");
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String[] getSpiele(String tmpGegner) {
		String[] spiele = null;
		try {
			Statement stmt = con.createStatement();
			String sql = "SELECT Spiel_ID FROM Spiel WHERE Gegner = '" + tmpGegner + "'";
			ResultSet rs = stmt.executeQuery(sql);
			List<String> results = new ArrayList<String>();
			while (rs.next()) {
				results.add(rs.getString(1));
			}
			spiele = results.toArray(new String[results.size()]);
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return spiele;
	}

	public String[] getSaetze(int tmpSpielID) {
		String[] saetze = null;
		try {
			Statement stmt = con.createStatement();
			String sql = "SELECT Satz_ID FROM Satz WHERE Spiel_ID = " + tmpSpielID;
			ResultSet rs = stmt.executeQuery(sql);
			List<String> results = new ArrayList<String>();
			while (rs.next()) {
				results.add(rs.getString(1));
			}
			saetze = results.toArray(new String[results.size()]);
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return saetze;
	}

	public Integer[][] getStatsSpielfeld(int satz_id) {
		Integer[][] spielfeld = new Integer[6][7];

		try {
			Statement stmt = con.createStatement();
			String sql = "SELECT Spalte, Zeile, Spieler FROM Zug WHERE Satz_ID = " + satz_id;
			ResultSet rs = stmt.executeQuery(sql);

			int colCount = rs.getMetaData().getColumnCount();
			List<Integer[]> table = new ArrayList<>();
			while (rs.next()) {
				Integer[] row = new Integer[colCount];
				for (int iCol = 1; iCol <= colCount; iCol++) {
					row[iCol - 1] = rs.getInt(iCol);
				}
				table.add(row);
			}

			for (Integer[] integers : table) {
				spielfeld[5 - integers[1]][integers[0]] = integers[2];
			}

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return spielfeld;
	}

	public String getGegner(int tmpSatzID) {
		String gegner = null;
		try {
			Statement stmt = con.createStatement();
			String sql = "SELECT Gegner FROM Satz JOIN Spiel ON Satz.Spiel_ID = Spiel.Spiel_ID WHERE Satz.Satz_ID = "
					+ tmpSatzID;
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				gegner = rs.getString(1);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return gegner;
	}
}