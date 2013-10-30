package main.java.me.avastprods.emailregister.rewards;

import main.java.me.avastprods.emailregister.EmailRegister;

public class MoneyReward {

	EmailRegister clazz;

	public MoneyReward(EmailRegister instance) {
		this.clazz = instance;
	}
	
	public double getReward() {
		if(clazz.usingEconomy) {
			return clazz.getConfig().getDouble("money-reward");
		}
		
		return 0;
	}
}
