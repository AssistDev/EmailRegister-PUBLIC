package main.java.me.avastprods.emailregister.keydata;

import java.util.Random;

public class Key {
	
	public String generateKey() {
		String alpha = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random r = new Random();

		StringBuilder sb = new StringBuilder(10);

		for (int i = 0; i < 10; i++) {
			sb.append(alpha.charAt(r.nextInt(alpha.length())));
		}

		return sb.toString();
	}
}
