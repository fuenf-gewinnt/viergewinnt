package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccessDB {
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

			/*------------------------------------*\
				Insert with Auto Increment
				String sql = "INSERT INTO Spiel VALUES (null, 'Test1', 0)";
			\*------------------------------------*/
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Spiel";
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String id = rs.getString(1);
				String firstName = rs.getString(2);
				String lastName = rs.getString(3);
				System.out.println(id + "\t|\t" + firstName + "\t|\t" + lastName);
			}

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
	}
}