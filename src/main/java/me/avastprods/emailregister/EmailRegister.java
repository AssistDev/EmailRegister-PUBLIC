package main.java.me.avastprods.emailregister;

import java.sql.Connection;
import java.util.Arrays;

import main.java.me.avastprods.emailregister.database.MySQL;
import main.java.me.avastprods.emailregister.keydata.Data;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class EmailRegister extends JavaPlugin {

	MySQL sql;
	Connection connection = null;

	Economy econ = null;

	public boolean usingEconomy = false;

	public void onEnable() {
		saveDefaultConfig();
		initConfig();
		initVars();

		Data data = new Data(this);
		data.reload();

		getCommand("register").setExecutor(new CommandManager(this));

		if (!setupEconomy()) {
			getServer().getConsoleSender().sendMessage("[EmailRegister] " + ChatColor.YELLOW + "Vault dependency not found - monetary rewards disabled.");
		} else {
			usingEconomy = true;
		}
	}

	public void initConfig() {
		if (!getConfig().contains("SERVER_NAME"))
			getConfig().set("SERVER_NAME", "My Server");

		if (!getConfig().contains("email.subject"))
			getConfig().set("email.subject", "Registration confirmation of %player%");
		if (!getConfig().contains("email.content"))
			getConfig().set("email.content", "Type /register confirm <key> in-game, to confirm your registration. Key: %key%");
		if (!getConfig().contains("email.from.host"))
			getConfig().set("email.from.host", "from@gmail.com");
		if (!getConfig().contains("email.from.password"))
			getConfig().set("email.from.password", "qwerty123");

		if (!getConfig().contains("mysql.host"))
			getConfig().set("mysql.host", "host");
		if (!getConfig().contains("mysql.port"))
			getConfig().set("mysql.port", "port");
		if (!getConfig().contains("mysql.user"))
			getConfig().set("mysql.user", "user");
		if (!getConfig().contains("mysql.pass"))
			getConfig().set("mysql.pass", "pass");
		if (!getConfig().contains("mysql.database"))
			getConfig().set("mysql.database", "database");

		if (!getConfig().contains("item-reward"))
			getConfig().set("item-reward", Arrays.asList("COOKIE:10", "CAKE:5", "DIAMOND_SWORD:1"));
		if (!getConfig().contains("money-reward"))
			getConfig().set("money-reward", 150);
		if (!getConfig().contains("command-reward"))
			getConfig().set("command-reward", Arrays.asList("say WoopWoop"));

		saveConfig();
	}

	public void initVars() {
		Variables.server = getConfig().getString("SERVER_NAME");

		Variables.subject = getConfig().getString("email.subject");
		Variables.content = getConfig().getString("email.content");

		Variables.fromHost = getConfig().getString("email.from.host");
		Variables.fromPass = getConfig().getString("email.from.password");

		Variables.host = getConfig().getString("mysql.host");
		Variables.port = getConfig().getString("mysql.port");
		Variables.user = getConfig().getString("mysql.user");
		Variables.pass = getConfig().getString("mysql.pass");
		Variables.database = getConfig().getString("mysql.database");
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}

		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

		if (rsp == null) {
			return false;
		}

		econ = rsp.getProvider();
		return econ != null;
	}
}
