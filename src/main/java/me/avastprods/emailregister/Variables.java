package main.java.me.avastprods.emailregister;

public class Variables {
	
	static String subject = null;

	static String content = null;

	static String fromHost = null;

	static String fromPass = null;

	String host = null;

	String port = null;

	String database = null;

	String user = null;

	String pass = null;

	public String getSubject() {
		return subject;
	}

	public String getContent() {
		return content;
	}

	public String getFromHost() {
		return fromHost;
	}

	public String getFromPass() {
		return fromPass;
	}

	public String getHost() {
		return this.host;
	}

	public String getPort() {
		return this.port;
	}

	public String getDatabase() {
		return this.database;
	}

	public String getUser() {
		return this.user;
	}

	public String getPass() {
		return this.pass;
	}
}
