package main.java.me.avastprods.emailregister.rewards;

import java.util.List;

import main.java.me.avastprods.emailregister.EmailRegister;

public class CommandReward {

	EmailRegister clazz;

	public CommandReward(EmailRegister instance) {
		this.clazz = instance;
	}
	
	public List<String> getReward() {
		return clazz.getConfig().getStringList("command-reward");
	}
}
