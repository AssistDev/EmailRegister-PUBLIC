package main.java.me.avastprods.emailregister;

import java.util.regex.Pattern;

import main.java.me.avastprods.emailregister.email.Email;
import main.java.me.avastprods.emailregister.keydata.Data;
import main.java.me.avastprods.emailregister.keydata.Key;
import main.java.me.avastprods.emailregister.rewards.CommandReward;
import main.java.me.avastprods.emailregister.rewards.ItemReward;
import main.java.me.avastprods.emailregister.rewards.MoneyReward;
import net.milkbowl.vault.economy.EconomyResponse;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandManager implements CommandExecutor {
	EmailRegister clazz;

	public CommandManager(EmailRegister instance) {
		this.clazz = instance;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		Player s = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("register")) {
			Data data = new Data(this.clazz);

			if (args.length == 1) {
				if (isValid(args[0])) {
					if (!data.getPending().contains(s.getName())) {
						if (!data.getRegistered().contains(s.getName())) {
							Variables vars = new Variables();
							Key key = new Key();

							String theKey = key.generateKey();

							s.sendMessage(ChatColor.GREEN + "Sending email, please wait...");
							long old = System.currentTimeMillis();

							Email email = new Email(Email.Provider.GMAIL, vars.getFromHost(), vars.getFromPass());
							email.sendEmail(args[0], vars.getContent().replaceAll("%key%", theKey), vars.getSubject().replaceAll("%player%", s.getName()));

							data.getPending().set(s.getName() + ".email", args[0]);
							data.getPending().set(s.getName() + ".key", theKey);
							data.save();

							System.out.println("Took " + (System.currentTimeMillis() - old) / 2000L + " seconds to send email.");
							s.sendMessage(ChatColor.GREEN + "Confirmation key sent to " + ChatColor.DARK_GREEN + args[0]);
							s.sendMessage(ChatColor.GREEN + "Type " + ChatColor.DARK_GREEN + "/register confirm <key> " + ChatColor.GREEN + "to finish your registration.");

						} else {
							s.sendMessage(ChatColor.RED + "You have already registered!");
						}

					} else {
						s.sendMessage(ChatColor.RED + "Your email (" + ChatColor.DARK_RED + data.getPending().getString(new StringBuilder(String.valueOf(s.getName())).append(".email").toString()) + ChatColor.RED + ") is waiting for confirmation.");
						s.sendMessage(ChatColor.RED + "Type " + ChatColor.DARK_RED + " /register confirm <key> " + ChatColor.RED + "to finish your registration.");
						s.sendMessage(ChatColor.RED + "The email we sent you should contain the key. If you did not recieve this key, or entered in a wrong email address, then contact a staff member.");
					}

				} else {
					s.sendMessage(ChatColor.RED + "Please enter in a valid email address. " + ChatColor.DARK_RED + "example@gmail.com");
				}
			}

			if ((args.length == 2) && (args[0].equalsIgnoreCase("confirm"))) {
				if (!data.getRegistered().contains(s.getName())) {
					if (data.getPending().contains(s.getName())) {
						if (data.getPending().getString(s.getName() + ".key").equals(args[1])) {
							data.getRegistered().set(s.getName() + ".email", data.getPending().getString(s.getName() + ".email"));
							data.getPending().set(s.getName(), null);
							data.save();

							s.sendMessage(ChatColor.GREEN + "Succesfully finished registration!");
							
							ItemReward itemReward = new ItemReward(clazz);
							if (itemReward.getReward().size() > 0) {
								int slots = 0;

								for (int i = 0; i < s.getInventory().getSize(); i++) {
									if (s.getInventory().getItem(i) == null) {
										slots++;
									}
								}

								if (slots > itemReward.getReward().size()) {
									for (ItemStack item : itemReward.getReward()) {
										s.getInventory().addItem(item);
									}
									
									s.sendMessage((ChatColor.GREEN + "You were given " + ChatColor.DARK_GREEN + StringUtils.join(itemReward.getReward().toArray(), ", ", 0, itemReward.getReward().toArray().length).toLowerCase() + ChatColor.GREEN + " as a gift.").replaceAll("ItemStack", ""));

								} else {
									s.sendMessage(ChatColor.RED + "Unfortunately you do not have enough space in your inventory to receive your item gift. Empty your inventory, and type /register claim");
								}
							}

							MoneyReward moneyReward = new MoneyReward(clazz);
							if (moneyReward.getReward() > 0) {
								EconomyResponse r = clazz.econ.depositPlayer(s.getName(), moneyReward.getReward());
								
								if(r.transactionSuccess()) {
					                s.sendMessage(String.format("You were given $%s as a gift. You now have $%s", clazz.econ.format(r.amount), clazz.econ.format(r.balance)));
					            } else {
					                s.sendMessage(String.format("An error occured while attemping to deposit cash: %s", r.errorMessage));
					            }
							}
							
							CommandReward commandReward = new CommandReward(clazz);
							if(commandReward.getReward().size() > 0) {
								for(String command : commandReward.getReward()) {
									Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%p", s.getName()));
								}
							}
							
							Bukkit.getServer().broadcastMessage(ChatColor.DARK_GREEN + s.getName() + ChatColor.GREEN + " has registered using " + ChatColor.DARK_GREEN + "/register <email>" + ChatColor.GREEN + " !");

						} else {
							s.sendMessage(ChatColor.RED + "The key you entered is not valid.");
						}

					} else {
						s.sendMessage(ChatColor.RED + "You have not set an email yet. Type " + ChatColor.DARK_RED + "/register <email>" + ChatColor.RED + " to do so.");
					}

				} else {
					s.sendMessage(ChatColor.RED + "You have already registered!");
				}
			}

		}

		return false;
	}

	public boolean isValid(String email) {
		Pattern pattern = Pattern.compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");
		return pattern.matcher(email).matches();
	}
}
