package com.enhancedplugins.enhancedhomes.commands;

import com.enhancedplugins.enhancedhomes.EnhancedHomes;
import com.enhancedplugins.enhancedhomes.managers.HomeManager;
import com.enhancedplugins.enhancedhomes.models.Home;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        String pluginPrefix;
        if (plugin.getPluginConfig().getBoolean("show-prefix")) {
            pluginPrefix = plugin.getLangMessage("prefix") + ChatColor.RESET + " ";
        } else {
            pluginPrefix = "";
        }

        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            String playerOnlyMessage = plugin.getLangMessage("commands.player-only");
            sender.sendMessage(pluginPrefix + playerOnlyMessage);
            return true;
        }

        // Determine the target player
        Player targetPlayer;
        if (args.length == 0 || args.length > 2) {
            // Check if not enough arguments were provided
            String usageMessage = plugin.getLangMessage("commands.delhome.usage");
            sender.sendMessage(pluginPrefix + usageMessage);
        }

        if (args.length == 2) {
            // Check if the sender has the necessary permissions
            if (!sender.getName().equalsIgnoreCase(args[0]) && !sender.hasPermission("enhancedhomes.delhome.other")) {
                String noPermissionMessage = plugin.getLangMessage("commands.delhome.other-error");
                sender.sendMessage(pluginPrefix + noPermissionMessage);
                return true;
            }
            // Get the target player
            targetPlayer = plugin.getServer().getPlayer(args[0]);
            if (targetPlayer == null) {
                // Check if the target player exists
                String playerNotFoundMessage = plugin.getLangMessage("commands.player-not-found");
                sender.sendMessage(pluginPrefix + playerNotFoundMessage);
                return true;
            }
            // Get the home to be deleted
            Home home = homeManager.getHome(targetPlayer, args[1]);
            if (home == null) {
                // Check if the home exists
                String homeNotFoundMessage = plugin.getLangMessage("commands.delhome.home-not-found");
                homeNotFoundMessage = homeNotFoundMessage.replace("%home%", args[1]);
                homeNotFoundMessage = homeNotFoundMessage.replace("%player%", targetPlayer.getName());
                sender.sendMessage(pluginPrefix + homeNotFoundMessage);
                return true;
            }
            // Delete the home
            homeManager.removeHome(targetPlayer, home.getName());
            // Send a success message
            String homeDeletedMessage = plugin.getLangMessage("commands.delhome.home-deleted");
            homeDeletedMessage = homeDeletedMessage.replace("%home%", home.getName());
            homeDeletedMessage = homeDeletedMessage.replace("%player%", targetPlayer.getName());
            sender.sendMessage(pluginPrefix + homeDeletedMessage);
        }
        else{
            // If only one argument is provided, the sender is the target player
            targetPlayer = (Player) sender;
            // Get the home to be deleted
            Home home = homeManager.getHome(targetPlayer, args[0]);
            if (home == null) {
                // Check if the home exists
                String homeNotFoundMessage = plugin.getLangMessage("commands.delhome.home-not-found");
                homeNotFoundMessage = homeNotFoundMessage.replace("%home%", args[0]);
                homeNotFoundMessage = homeNotFoundMessage.replace("%player%", targetPlayer.getName());
                sender.sendMessage(pluginPrefix + homeNotFoundMessage);
                return true;
            }
            // Delete the home
            homeManager.removeHome(targetPlayer, home.getName());

            // Send a success message
            String homeDeletedMessage = plugin.getLangMessage("commands.delhome.home-deleted");
            homeDeletedMessage = homeDeletedMessage.replace("%home%", home.getName());
            homeDeletedMessage = homeDeletedMessage.replace("%player%", targetPlayer.getName());
            sender.sendMessage(pluginPrefix + homeDeletedMessage);
        }

        return true;
    }
}