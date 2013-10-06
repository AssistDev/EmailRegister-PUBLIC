package main.java.me.avastprods.emailregister;

import java.sql.SQLException;
import java.sql.Statement;

public class Register {
	private String name;
	private String email;
	private EmailRegister clazz;

	public Register(EmailRegister instance, String name, String email) {
		this.name = name;
		this.email = email;
		this.clazz = instance;
	}

	public void launch() throws SQLException {
		Statement statement = this.clazz.connection.createStatement();
		statement.executeUpdate("INSERT INTO emails ('name', 'email') VALUES ('" + this.name + "', '" + this.email + "');");
		statement.close();
	}
}
