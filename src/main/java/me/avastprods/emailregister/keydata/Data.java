package main.java.me.avastprods.emailregister.keydata;

import java.io.File;
import java.io.IOException;

import main.java.me.avastprods.emailregister.EmailRegister;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Data {
	private FileConfiguration registered = null;
	private File registeredFile = null;

	private FileConfiguration pending = null;
	private File pendingFile = null;
	
	EmailRegister clazz;

	public Data(EmailRegister instance) {
		this.clazz = instance;
	}

	public void reload() {
		if (!new File(this.clazz.getDataFolder() + File.separator + "data").exists())
			new File(this.clazz.getDataFolder() + File.separator + "data").mkdir();

		if (this.registeredFile == null) {
			this.registeredFile = new File(this.clazz.getDataFolder() + File.separator + "data", "registered.yml");
		}

		if (this.pendingFile == null) {
			this.pendingFile = new File(this.clazz.getDataFolder() + File.separator + "data", "pending.yml");
		}

		this.registered = YamlConfiguration.loadConfiguration(this.registeredFile);
		this.pending = YamlConfiguration.loadConfiguration(this.pendingFile);
		save();
	}

	public FileConfiguration getRegistered() {
		if (this.registered == null) {
			reload();
		}

		return this.registered;
	}

	public FileConfiguration getPending() {
		if (this.pending == null) {
			reload();
		}

		return this.pending;
	}

	public void save() {
		if ((this.registered == null) || (this.registeredFile == null)) {
			return;
		}

		if ((this.pending == null) || (this.pendingFile == null)) {
			return;
		}
		try {
			getRegistered().save(this.registeredFile);
			getPending().save(this.pendingFile);
		} catch (IOException ex) {
			this.clazz.getServer().getConsoleSender().sendMessage("[EmailRegister] " + ChatColor.RED + "An error occured while attempting to save data.");
		}
	}
}
