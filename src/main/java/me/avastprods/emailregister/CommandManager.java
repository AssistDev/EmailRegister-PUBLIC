package main.java.me.avastprods.emailregister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import main.java.me.avastprods.emailregister.email.SMTP;
import main.java.me.avastprods.emailregister.keydata.Data;
import main.java.me.avastprods.emailregister.keydata.Key;
import main.java.me.avastprods.emailregister.rewards.CommandReward;
import main.java.me.avastprods.emailregister.rewards.ItemReward;
import main.java.me.avastprods.emailregister.rewards.MoneyReward;
import net.milkbowl.vault.economy.EconomyResponse;

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

	HashMap<String, ArrayList<ItemStack>> notClaimed = new HashMap<String, ArrayList<ItemStack>>();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		Player s = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("register")) {
			Data data = new Data(this.clazz);

			if (args.length == 1) {
				if (isValid(args[0])) {
					if (s.hasPermission("emailregister.register")) {
						if (!data.getPending().contains(s.getName())) {
							if (!data.getRegistered().contains(s.getName())) {
								Key key = new Key();

								String theKey = key.generateKey();

								s.sendMessage(ChatColor.GREEN + "Sending email, please wait...");

								if (sendEmail(s, args[0], theKey)) {
									data.getPending().set(s.getName() + ".email", args[0]);
									data.getPending().set(s.getName() + ".key", theKey);
									data.save();

									s.sendMessage(ChatColor.GREEN + "Confirmation key sent to " + ChatColor.DARK_GREEN + args[0]);
									s.sendMessage(ChatColor.GREEN + "Type " + ChatColor.DARK_GREEN + "/register confirm <key> " + ChatColor.GREEN + "to finish your registration.");
								} else {
									s.sendMessage(ChatColor.RED + "An error occured while attempting to send email. Please contact a staff member.");
								}

								data.getPending().set(s.getName() + ".email", args[0]);
								data.getPending().set(s.getName() + ".key", theKey);
								data.save();

							} else {
								s.sendMessage(ChatColor.RED + "You have already registered!");
							}

						} else {
							s.sendMessage(ChatColor.RED + "Your email (" + ChatColor.DARK_RED + data.getPending().getString(s.getName() + ".email") + ChatColor.RED + ") is waiting for confirmation.");
							s.sendMessage(ChatColor.RED + "Type " + ChatColor.DARK_RED + " /register confirm <key> " + ChatColor.RED + "to finish your registration.");
							s.sendMessage(ChatColor.RED + "The email we sent you should contain the key. If you did not recieve this key, or entered in a wrong email address, then contact a staff member.");
						}

					} else {
						s.sendMessage(ChatColor.RED + "Insufficient permissions!");
					}

				} else if (args[0].equalsIgnoreCase("reset")) {
					if (s.hasPermission("emailregister.reset")) {
						if (data.getPending().contains(s.getName())) {
							if (!data.getRegistered().contains(s.getName())) {
								data.getPending().set(s.getName(), null);
								s.sendMessage(ChatColor.GREEN + "You have reset your email.");
							} else {
								s.sendMessage(ChatColor.RED + "You have already registered, and cannot reset a registered email. If you wish to do so, please contact a staff member.");
								return true;
							}

						} else {
							s.sendMessage(ChatColor.RED + "You have not set an email yet. Type " + ChatColor.DARK_RED + "/register <email>" + ChatColor.RED + " to do so.");
						}

					} else {
						s.sendMessage(ChatColor.RED + "Insufficient permissions!");
					}

				} else if (args[0].equalsIgnoreCase("resend")) {
					if (s.hasPermission("emailregister.resend")) {
						if (data.getPending().contains(s.getName())) {
							Key key = new Key();

							String theKey = key.generateKey();
							String email = data.getPending().getString(s.getName() + ".email");

							s.sendMessage(ChatColor.GREEN + "Sending email, please wait...");

							if (sendEmail(s, email, theKey)) {
								s.sendMessage(ChatColor.GREEN + "Confirmation key sent to " + ChatColor.DARK_GREEN + email);
								s.sendMessage(ChatColor.GREEN + "Type " + ChatColor.DARK_GREEN + "/register confirm <key> " + ChatColor.GREEN + "to finish your registration.");
							} else {
								s.sendMessage(ChatColor.RED + "An error occured while attempting to send email. Please contact a staff member.");
							}

						} else {
							s.sendMessage(ChatColor.RED + "You have not set an email yet. Type " + ChatColor.DARK_RED + "/register <email>" + ChatColor.RED + " to do so.");
						}

					} else {
						s.sendMessage(ChatColor.RED + "Insufficient permissions!");
					}

				} else if (args[0].equalsIgnoreCase("claim")) {
					if (s.hasPermission("emailregister.claim")) {
						if (notClaimed.containsKey(s.getName())) {
							StringBuilder sb = new StringBuilder();
							int slots = 0;

							for (int i = 0; i < s.getInventory().getSize(); i++) {
								if (s.getInventory().getItem(i) == null) {
									slots++;
								}
							}

							if (slots > notClaimed.get(s.getName()).size()) {
								for (ItemStack item : notClaimed.get(s.getName())) {
									s.getInventory().addItem(item);
									sb.append(item.getType()).append("[").append(item.getAmount()).append("]").append(", ");
								}

								s.sendMessage(ChatColor.GREEN + "You were given " + ChatColor.DARK_GREEN + sb.toString().toLowerCase() + ChatColor.GREEN + " as a gift.");
								notClaimed.remove(s.getName());
							} else {
								s.sendMessage(ChatColor.RED + "Unfortunately you do not have enough space in your inventory to receive your item gift. Empty your inventory, and type " + ChatColor.DARK_RED + " /register claim");
							}

						} else {
							s.sendMessage(ChatColor.RED + "You have no rewards to be claimed.");
						}

					} else {
						s.sendMessage(ChatColor.RED + "Insufficient permissions!");
					}

				} else if (args[0].equalsIgnoreCase("help")) {
					if (s.hasPermission("emailregister.help")) {
						s.sendMessage(ChatColor.DARK_GREEN + "=-=-=-= " + ChatColor.GREEN + "Page 1/2 " + ChatColor.DARK_GREEN + "=-=-=-=");
						s.sendMessage(ChatColor.DARK_GREEN + "/register <email> " + ChatColor.GREEN + "=> Sends a confirmation key to given email address.");
						s.sendMessage(ChatColor.DARK_GREEN + "/register confirm <key> " + ChatColor.GREEN + "=> Confirms your email address.");
						s.sendMessage(ChatColor.DARK_GREEN + "/register help [page] " + ChatColor.GREEN + "=> Displays the available commands.");

					} else {
						s.sendMessage(ChatColor.RED + "Insufficient permissions!");
					}

				} else {
					s.sendMessage(ChatColor.RED + "Please enter in a valid email address. " + ChatColor.DARK_RED + "example@gmail.com");
					return true;
				}

			} else if (args.length == 0 || args.length > 2) {
				s.sendMessage(ChatColor.RED + "You have entered in too many, or not enough arguments. Type " + ChatColor.DARK_RED + "/register help" + ChatColor.RED + " for help.");
			}

			if ((args.length == 2) && (args[0].equalsIgnoreCase("confirm"))) {
				if (s.hasPermission("emailregister.confirm")) {
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

									StringBuilder sb = new StringBuilder();

									if (slots > itemReward.getReward().size()) {
										for (ItemStack item : itemReward.getReward()) {
											s.getInventory().addItem(item);
											sb.append(item.getType()).append("[").append(item.getAmount()).append("]").append(", ");
										}

										s.sendMessage(ChatColor.GREEN + "You were given " + ChatColor.DARK_GREEN + sb.toString().toLowerCase() + ChatColor.GREEN + " as a gift.");

									} else {
										s.sendMessage(ChatColor.RED + "Unfortunately you do not have enough space in your inventory to receive your item gift. Empty your inventory, and type /register claim");
										notClaimed.put(s.getName(), itemReward.getReward());
									}
								}

								MoneyReward moneyReward = new MoneyReward(clazz);
								if (moneyReward.getReward() > 0) {
									EconomyResponse r = clazz.econ.depositPlayer(s.getName(), moneyReward.getReward());

									if (r.transactionSuccess()) {
										s.sendMessage(String.format("You were given $%s as a gift. You now have $%s", clazz.econ.format(r.amount), clazz.econ.format(r.balance)));
									} else {
										s.sendMessage(String.format("An error occured while attemping to deposit cash: %s", r.errorMessage));
									}
								}

								CommandReward commandReward = new CommandReward(clazz);
								if (commandReward.getReward().size() > 0) {
									for (String command : commandReward.getReward()) {
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

				} else {
					s.sendMessage(ChatColor.RED + "Insufficient permissions!");
				}
			}

			if ((args.length == 2) && (args[0].equalsIgnoreCase("reset"))) {
				if (s.hasPermission("emailregister.reset_other")) {
					Player target = Bukkit.getServer().getPlayer(args[1]);

					if (target == null || !target.isOnline()) {
						s.sendMessage(ChatColor.RED + "Invalid target.");
						return true;
					}

					if (data.getPending().contains(s.getName())) {
						data.getPending().set(target.getName(), null);
						s.sendMessage(ChatColor.GREEN + "You have reset " + ChatColor.DARK_GREEN + target.getName() + ChatColor.GREEN + "'s email.");
					} else {
						s.sendMessage(ChatColor.DARK_RED + target.getName() + ChatColor.RED + " has not set an email yet.");
					}

				} else {
					s.sendMessage(ChatColor.RED + "Insufficient permissions!");
				}
			}

			if ((args.length == 2) && (args[0].equalsIgnoreCase("help")) && (args[1].equalsIgnoreCase("2"))) {
				if (s.hasPermission("emailregister.help")) {
					s.sendMessage(ChatColor.DARK_GREEN + "=-=-=-= " + ChatColor.GREEN + "Page 2/2 " + ChatColor.DARK_GREEN + "=-=-=-=");
					s.sendMessage(ChatColor.DARK_GREEN + "/register reset " + ChatColor.GREEN + "=> Resets your email address.");
					s.sendMessage(ChatColor.DARK_GREEN + "/register reset <player> " + ChatColor.GREEN + "=> Resets target's email address.");
					s.sendMessage(ChatColor.DARK_GREEN + "/register resend " + ChatColor.GREEN + "=> Re-sends a confirmation key to your email address.");
					s.sendMessage(ChatColor.DARK_GREEN + "/register claim " + ChatColor.GREEN + "=> Claims your registration reward, if not already given.");

				} else {
					s.sendMessage(ChatColor.RED + "Insufficient permissions!");
				}
			}
		}

		return false;
	}

	public boolean isValid(String email) {
		Pattern pattern = Pattern.compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");
		return pattern.matcher(email).matches();
	}

	public boolean sendEmail(Player sender, String to, String key) {
		long old = System.currentTimeMillis();
		Variables vars = new Variables();

		try {
			SMTP google = new SMTP("smtp.gmail.com");
			google.starttls();
			google.login(vars.getFromHost(), vars.getFromPass());
			google.mail(vars.getFromHost());
			google.rcpt(to);
			google.sendMail(google.email().from(vars.getServerName(), vars.getFromHost()).to("", to).subject(vars.getSubject().replaceAll("%player%", sender.getName())).body("Hello " + sender.getName() + ",\n\n" + vars.getContent().replaceAll("%key%", key) + "\n\n" + vars.getServerName() + "\n"));
			google.close();
		} catch (Exception ex) {
			clazz.getServer().getConsoleSender().sendMessage("[EmailRegister] " + ChatColor.RED + "An error occured while attempting to send email!" + ex.getCause());
			return false;
		}

		System.out.println("Took " + (System.currentTimeMillis() - old) / 1000L + " seconds to send email.");
		return true;
	}
}
