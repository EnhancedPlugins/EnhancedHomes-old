package com.enhancedplugins.enhancedhomes.commands;

import java.util.HashMap;
import java.util.Map;

import com.enhancedplugins.enhancedhomes.models.Home;
import com.enhancedplugins.enhancedhomes.EnhancedHomes;
import com.enhancedplugins.enhancedhomes.managers.HomeManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

/**
 * This class handles the /home command in the EnhancedHomes plugin.
 * It implements the CommandExecutor interface, which means it provides
 * the onCommand method that is called when the /home command is executed.
 */
public class HomeCommand implements CommandExecutor {
    private final EnhancedHomes plugin;
    private final HomeManager homeManager;
    private final Map<Player, BukkitTask> teleportTasks = new HashMap<>();

    /**
     * Constructor for the HomeCommand class.
     *
     * @param plugin The instance of the EnhancedHomes plugin.
     */
    public HomeCommand(EnhancedHomes plugin) {
        this.plugin = plugin;
        this.homeManager = plugin.getHomeManager();
    }

    /**
     * This method is called when the /home command is executed.
     * It handles the logic for teleporting the player to their home.
     *
     * @param sender  The CommandSender who executed the command.
     * @param command The Command that was executed.
     * @param label   The alias of the command that was used.
     * @param args    The arguments that were provided with the command.
     * @return true if the command was executed successfully, false otherwise.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is a player
        if (!(sender instanceof Player senderPlayer)) {
            sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "Only players can use this command.");
            return true;
        }

        // Check if not enough arguments were provided
        if (args.length == 0) {
            sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "Not enough arguments. Usage: /home [name] || <player> [name].");
            return true;
        }

        // Check if too many arguments were provided
        if (args.length > 2) {
            sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "Too many arguments. Usage: /home [name] || <player> [name].");
            return true;
        }

        Player targetPlayer;

        // Check if the command is being used to teleport to another player's home
        if (args.length == 2) {
            if (!sender.hasPermission("enhancedhomes.home.other") && !sender.getName().equalsIgnoreCase(args[0])) {
                sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "You do not have permission to teleport to other players' homes.");
                return true;
            }

            targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null) {
                sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "Player not found.");
                return true;
            }
        } else {
            targetPlayer = senderPlayer;
        }

        // Check if the player is already teleporting to a home
        if (teleportTasks.containsKey(targetPlayer)) {
            sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "You are already teleporting to a home.");
            return true;
        }

        // Get the home from the HomeManager
        Home home = homeManager.getHome(targetPlayer, args[args.length - 1]);
        if (home == null) {
            sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "No home found with the name " + args[args.length - 1] + ".");
            return true;
        }

        // Check if the player is trying to teleport to a home in another world
        if (!targetPlayer.getWorld().getName().equals(home.getWorldName())) {
            if (!plugin.getConfig().getBoolean("cross-world-tp") && !sender.hasPermission("enhancedhomes.crossworldtp.bypass")) {
                sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "You do not have permission to teleport to homes in other worlds.");
                return true;
            }
        }

        // Get the location of the home
        Location homeLocation = new Location(Bukkit.getWorld(home.getWorldName()), home.getX(), home.getY(), home.getZ());

        // Check if a warmup is required before teleporting
        if (plugin.getConfig().getBoolean("warmup") && !sender.hasPermission("enhancedhomes.warmup.bypass")) {
            int warmupTime = plugin.getConfig().getInt("warmup-time");
            sender.sendMessage(plugin.getPluginPrefix() + ChatColor.AQUA + "Teleporting to home " + ChatColor.LIGHT_PURPLE + home.getName() + ChatColor.AQUA + " in " + ChatColor.LIGHT_PURPLE + warmupTime + ChatColor.AQUA + " seconds. Do not move.");
            Location playerLocation = senderPlayer.getLocation();
            BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (playerLocation.distanceSquared(senderPlayer.getLocation()) < 1) {
                    targetPlayer.teleport(homeLocation);
                    sender.sendMessage(plugin.getPluginPrefix() + ChatColor.AQUA + "Teleported to home " + ChatColor.LIGHT_PURPLE + home.getName() + ChatColor.AQUA + ".");
                } else {
                    sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "Teleportation cancelled because you moved.");
                }
                teleportTasks.remove(targetPlayer); // Remove the task from the HashMap once it's done
            }, warmupTime * 20L);
            teleportTasks.put(targetPlayer, task);
        } else {
            // Teleport the player immediately if no warmup is required
            targetPlayer.teleport(homeLocation);
            sender.sendMessage(plugin.getPluginPrefix() + ChatColor.AQUA + "Teleported to home " + ChatColor.LIGHT_PURPLE + home.getName() + ChatColor.AQUA + ".");
        }

        return true;
    }
}