package main.java.me.avastprods.emailregister.email;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
 
public class Email {
 
  private Provider provider;
	private String owner;
	private String password;
 
	public Email(Provider provider, String owner, String password) {
		this.provider = provider;
		this.owner = owner;
		this.password = password;
	}
 
	public void sendEmail(String to, String content, String subject, int port) {
 
		String HOST = provider.getHost();
 
		String PASSWORD = Base64Coder.encodeString(password);
		String USER = Base64Coder.encodeString(owner);
 
		try {
			Socket socket = new Socket(HOST, port);
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			Scanner scanner = new Scanner(socket.getInputStream());
 
			dos.writeBytes("HELO\r\n");
			dos.writeBytes("AUTH LOGIN");
			dos.writeBytes("\r\n");
			dos.writeBytes(USER);
			dos.writeBytes("\r\n");
			dos.writeBytes(PASSWORD);
			dos.writeBytes("\r\n");
			dos.writeBytes("MAIL FROM:<" + owner + ">\r\n");
			dos.writeBytes("\r\n");
			dos.writeBytes("RCPT TO: <" + to + ">\r\n");
			dos.writeBytes("DATA\r\n");
			dos.writeBytes("Subject: " + subject + "\r\n");
			dos.writeBytes(content);
			dos.writeBytes("\r\n.\r\n");
			dos.writeBytes("QUIT\r\n");
 
			dos.flush();
 
			String responseline;
			while ((responseline = scanner.next()) != null) {
				System.out.println(responseline);
			}
 
			scanner.close();
			dos.close();
			socket.close();
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}
 
	public void sendEmail(String to, String content, String subject) {
		sendEmail(to, content, subject, 25); // Use default port
	}
 
	public enum Provider {
 
		YAHOO("smtp.mail.yahoo.com"),
		GMAIL("smtp.gmail.com");
		private String host;
 
		Provider(String host) {
			this.host = host;
 
		}
 
		public String getHost() {
			return host;
		}
	}
 
	/*
	 * Base 64 encoding implementation from http://www.source-code.biz/base64coder/java/
	 */
	static class Base64Coder {
 
		private static final char[] map1 = new char[64];
 
		static {
			int i = 0;
			for (char c = 'A'; c <= 'Z'; c++) {
				map1[i++] = c;
			}
			for (char c = 'a'; c <= 'z'; c++) {
				map1[i++] = c;
			}
			for (char c = '0'; c <= '9'; c++) {
				map1[i++] = c;
			}
			map1[i++] = '+';
			map1[i++] = '/';
		}
		// Mapping table from Base64 characters to 6-bit nibbles.
		private static final byte[] map2 = new byte[128];
 
		static {
			for (int i = 0; i < map2.length; i++) {
				map2[i] = -1;
			}
			for (int i = 0; i < 64; i++) {
				map2[map1[i]] = (byte) i;
			}
		}
 
		public static String encodeString(String s) {
			return new String(encode(s.getBytes()));
		}
 
		public static char[] encode(byte[] in) {
			return encode(in, 0, in.length);
		}
 
		public static char[] encode(byte[] in, int iLen) {
			return encode(in, 0, iLen);
		}
 
		public static char[] encode(byte[] in, int iOff, int iLen) {
			int oDataLen = (iLen * 4 + 2) / 3;
			int oLen = ((iLen + 2) / 3) * 4;
			char[] out = new char[oLen];
			int ip = iOff;
			int iEnd = iOff + iLen;
			int op = 0;
			while (ip < iEnd) {
				int i0 = in[ip++] & 0xff;
				int i1 = ip < iEnd ? in[ip++] & 0xff : 0;
				int i2 = ip < iEnd ? in[ip++] & 0xff : 0;
				int o0 = i0 >>> 2;
				int o1 = ((i0 & 3) << 4) | (i1 >>> 4);
				int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
				int o3 = i2 & 0x3F;
				out[op++] = map1[o0];
 
				out[op++] = map1[o1];
				out[op] = op < oDataLen ? map1[o2] : '=';
				op++;
				out[op] = op < oDataLen ? map1[o3] : '=';
				op++;
			}
			return out;
		}
 
		private Base64Coder() {
		}
	}
}