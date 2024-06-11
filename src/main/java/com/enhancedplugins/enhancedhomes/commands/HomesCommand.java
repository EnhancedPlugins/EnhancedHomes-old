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
 * The HomesCommand class implements the /homes command.
 * This command allows a player to list their homes or the homes of another player.
 */
public class HomesCommand implements CommandExecutor {
    private final EnhancedHomes plugin;
    private final HomeManager homeManager;

    /**
     * Constructor for the HomesCommand class.
     *
     * @param plugin The EnhancedHomes plugin.
     */
    public HomesCommand(EnhancedHomes plugin) {
        this.plugin = plugin;
        this.homeManager = plugin.getHomeManager();
    }

    /**
     * This method is called when the /homes command is executed.
     * It checks if the sender is a player and if they have the necessary permissions.
     * It then retrieves the homes of the target player and sends a message to the sender with the list of homes.
     *
     * @param sender The command sender.
     * @param command The command.
     * @param label The command label.
     * @param args The command arguments.
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
            targetPlayer = (Player) sender;
        } else {
            // Check if the sender has the necessary permissions
            if (!sender.getName().equalsIgnoreCase(args[0]) && !sender.hasPermission("enhancedhomes.homes.other")) {
                sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "You do not have permission to view other players' homes.");
                return true;
            }
            targetPlayer = plugin.getServer().getPlayer(args[0]);
            if (targetPlayer == null) {
                sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "Player not found.");
                return true;
            }
        }

        // Retrieve the homes of the target player
        List<Home> homes = homeManager.getHomes(targetPlayer);
        if (homes.isEmpty()) {
            sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "No homes found for " + targetPlayer.getName());
            return true;
        }

        // Check if cross-world teleportation is enabled
        boolean isCrossWorldTpEnabled;
        if (sender.hasPermission("enhancedhomes.crossworldtp.bypass")) {
            isCrossWorldTpEnabled = true;
        } else {
            isCrossWorldTpEnabled = plugin.getPluginConfig().getBoolean("cross-world-tp");
        }

        // Determine the maximum number of homes
        boolean isHomeLimitEnabled = plugin.getPluginConfig().getBoolean("homes-limit");
        int maxHomes = isHomeLimitEnabled ? plugin.getPluginConfig().getInt("max-homes") : 100;

        if (sender.hasPermission("enhancedhomes.sethome.unlimited")) {
            maxHomes = 100;
        } else {
            for (int i = 1; i <= 100; i++) {
                if (sender.hasPermission("enhancedhomes.sethome.max." + i)) {
                    maxHomes = i;
                    break;
                }
            }
        }

        // Send a message to the sender with the list of homes
        sender.sendMessage(plugin.getPluginPrefix() + ChatColor.LIGHT_PURPLE + targetPlayer.getName() + ChatColor.AQUA + "'s homes (" + ChatColor.LIGHT_PURPLE + homeManager.getHomes(targetPlayer).size() + ChatColor.AQUA + "/" + ChatColor.LIGHT_PURPLE + maxHomes + ChatColor.AQUA + "):");
        homes.forEach(home -> {
            String worldName = home.getWorldName();
            String homeName = home.getName();
            String homeWorldColor = String.valueOf(isCrossWorldTpEnabled ? ChatColor.GREEN : worldName.equals(targetPlayer.getWorld().getName()) ? ChatColor.GREEN : ChatColor.RED);
            sender.sendMessage(ChatColor.AQUA + "- " + ChatColor.LIGHT_PURPLE + homeName + ChatColor.AQUA + " (" + homeWorldColor + worldName + ChatColor.AQUA + ")");
        });

        return true;
    }
}