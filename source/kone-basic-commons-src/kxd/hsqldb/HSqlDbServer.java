package kxd.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.hsqldb.server.Server;

public class HSqlDbServer {
	Server server;

	public HSqlDbServer(String[] DatabaseNames) {
		server = new Server();
		for (int i = 0; i < DatabaseNames.length; i++)
			server.setDatabaseName(i, DatabaseNames[i]);
		server.setSilent(true);
		server.setTrace(false);
		server.start();
	}

	public void start() {
		server.start();
	}

	public void stop() {
		server.stop();
	}

	public static void main(String[] args) {
		new HSqlDbServer(new String[] { "test" });
		System.out.println("-----------------------------");
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			Connection connection = DriverManager.getConnection(
					"jdbc:hsqldb:mem:test", "sa", "");
			Statement stmt = null;
			ResultSet rs = null;
			stmt = connection.createStatement();
			String sql1 = "create table dong_test(xh varchar(10));";
			String sql2 = "insert into dong_test(xh) values('Tom'); insert into dong_test(xh) values('Mary')";
			String sql3 = "select limit 1 1 * from dong_test";
			stmt.executeUpdate(sql1);
			stmt.executeUpdate(sql2);
			rs = stmt.executeQuery(sql3);
			while (rs.next()) {
				System.out.println(">>> " + rs.getString(1));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
