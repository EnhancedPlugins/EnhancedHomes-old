package com.enhancedplugins.enhancedhomes.commands;

import com.enhancedplugins.enhancedhomes.EnhancedHomes;
import com.enhancedplugins.enhancedhomes.managers.HomeManager;
import com.enhancedplugins.enhancedhomes.models.Home;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The DelHomeCommand class implements the /delhome command.
 * This command allows a player to delete one of his home or a home of another player.
 */
public class DelHomeCommand implements CommandExecutor {
    private final EnhancedHomes plugin;
    private final HomeManager homeManager;

    /**
     * Constructor for the DelHomeCommand class.
     *
     * @param plugin The EnhancedHomes plugin.
     */
    public DelHomeCommand(EnhancedHomes plugin) {
        this.plugin = plugin;
        this.homeManager = plugin.getHomeManager();
    }

    /**
     * This method is called when the /delhome command is executed.
     * It checks if the sender is a player and if they have the necessary permissions.
     * It then retrieves the homes of the target player and sends a message to the sender with the list of homes.
     *
     * @param sender  The command sender.
     * @param command The command.
     * @param label   The command label.
     * @param args    The command arguments.
     * @return true if the command was executed successfully, false otherwise.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "Only players can use this command.");
            return true;
        }

        // Determine the target player
        Player targetPlayer;
        if (args.length == 0) {
            // Check if not enough arguments were provided
            sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "Not enough arguments. Usage: /delhome [name] || <player> [name].");
            return true;
        }
        if (args.length > 2) {
            // Check if too many arguments were provided
            sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "Too many arguments. Usage: /delhome [name] || <player> [name].");
            return true;
        }
        if (args.length == 2) {
            // Check if the sender has the necessary permissions
            if (!sender.getName().equalsIgnoreCase(args[0]) && !sender.hasPermission("enhancedhomes.delhome.other")) {
                sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "You do not have permission to delete other player's home.");
                return true;
            }
            // Get the target player
            targetPlayer = plugin.getServer().getPlayer(args[0]);
            if (targetPlayer == null) {
                // Check if the target player exists
                sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "Player not found.");
                return true;
            }
            // Get the home to be deleted
            Home home = homeManager.getHome(targetPlayer, args[1]);
            if (home == null) {
                // Check if the home exists
                sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "Home not found for " + targetPlayer.getName());
                return true;
            }
            // Delete the home
            homeManager.removeHome(targetPlayer, home.getName());
            // Send a success message
            sender.sendMessage(plugin.getPluginPrefix() + ChatColor.AQUA + "Home " + ChatColor.LIGHT_PURPLE +  home.getName() + ChatColor.AQUA + " deleted for " + ChatColor.LIGHT_PURPLE + targetPlayer.getName() + ChatColor.AQUA+ ".");
        }
        else{
            // If only one argument is provided, the sender is the target player
            targetPlayer = (Player) sender;
            // Get the home to be deleted
            Home home = homeManager.getHome(targetPlayer, args[0]);
            if (home == null) {
                // Check if the home exists
                sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "Home not found for " + targetPlayer.getName());
                return true;
            }
            // Delete the home
            homeManager.removeHome(targetPlayer, home.getName());
            // Send a success message
            sender.sendMessage(plugin.getPluginPrefix() + ChatColor.AQUA + "Home " + ChatColor.LIGHT_PURPLE +  home.getName() + ChatColor.AQUA + " deleted for " + ChatColor.LIGHT_PURPLE + targetPlayer.getName() + ChatColor.AQUA+ ".");
        }

        return true;
    }
}