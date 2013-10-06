package main.java.me.avastprods.emailregister.rewards;

import java.util.ArrayList;
import java.util.List;

import main.java.me.avastprods.emailregister.EmailRegister;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemReward {

	EmailRegister clazz;

	public ItemReward(EmailRegister instance) {
		this.clazz = instance;
	}
	
	public List<ItemStack> getReward() {
		List<ItemStack> rewardList = new ArrayList<ItemStack>();

		for (String item : clazz.getConfig().getStringList("item-reward")) {
			String[] split = item.split(":");
			Material mat = null;

			try {
				mat = Material.getMaterial(split[0]);
			} catch (NullPointerException npe) {
				clazz.getServer().getConsoleSender().sendMessage("[EmailRegister] " + ChatColor.RED + "An error occured while attempting to parse material from string.");
			}

			int amount = 0;

			try {
				amount = Integer.parseInt(split[1]);
			} catch (NumberFormatException nfx) {
				clazz.getServer().getConsoleSender().sendMessage("[EmailRegister] " + ChatColor.RED + "An error occured while attempting to parse integer from string.");
			}

			if (mat != null && amount != 0) {
				rewardList.add(new ItemStack(mat, amount));
			}
		}

		return rewardList;
	}
}
