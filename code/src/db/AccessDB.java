package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AccessDB {
	private Connection con;

	public AccessDB() {
		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("Treiberklasse nicht gefunden!");
			return;
		}

		/*------------------------------------*\
			Insert with Auto Increment
			String sql = "INSERT INTO Spiel VALUES (null, 'Test1', 0)";
		\*------------------------------------*/
		// while (rs.next()) {
		// rs.getArray(0);
		// String id = rs.getString(1);
		// String firstName = rs.getString(2);
		// String lastName = rs.getString(3);
		// System.out.println(id + "\t|\t" + firstName + "\t|\t" +
		// lastName);
		// }
	}

	public String[] getGegner() {

		String[] gegner = null;

		try {
			con = DriverManager.getConnection("jdbc:hsqldb:file:src/db/MyDB;shutdown=true", "SA", "");

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
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return gegner;
	}
}