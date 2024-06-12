package com.enhancedplugins.enhancedhomes.commands;

import com.enhancedplugins.enhancedhomes.EnhancedHomes;
import com.enhancedplugins.enhancedhomes.managers.HomeManager;
import com.enhancedplugins.enhancedhomes.models.Home;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * This class handles the /sethome command in the EnhancedHomes plugin.
 * It allows players to set their home at their current location.
 */
public class SetHomeCommand implements CommandExecutor {
    private final EnhancedHomes plugin;
    private final HomeManager homeManager;
    private Map<Player, String> replaceHome = new HashMap<>();

    /**
     * Constructor for the SetHomeCommand class.
     *
     * @param plugin The instance of the EnhancedHomes plugin.
     */
    public SetHomeCommand(EnhancedHomes plugin) {
        this.plugin = plugin;
        this.homeManager = plugin.getHomeManager();
    }

    /**
     * This method is called when the /sethome command is executed.
     *
     * @param sender  The sender of the command.
     * @param command The command that was executed.
     * @param label   The alias of the command that was used.
     * @param args    The arguments that were given with the command.
     * @return true if the command was executed successfully, false otherwise.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is a player
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "Only players can use this command.");
            return true;
        }

        // Check if the correct number of arguments was given
        if (args.length == 0) {
            sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "Not enough arguments. Usage: /sethome [name] || <player> [name].");
            return true;
        }

        if (args.length > 1) {
            sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "Too many arguments. Usage: /sethome <name>");
            return true;
        }

        Player targetPlayer = player;
        Home home = homeManager.getHome(player, args[0]);
        boolean isHomeInReplaceHome = replaceHome.containsKey(player) && replaceHome.get(player).equals(args[0]);

        // Check if the home already exists
        if (home != null && !isHomeInReplaceHome) {
            replaceHome.put(player, args[0]);
            sender.sendMessage(plugin.getPluginPrefix() + ChatColor.YELLOW + "A home with the name " + ChatColor.LIGHT_PURPLE + args[0] + ChatColor.YELLOW + " already exists. Please use /sethome " + args[0] + " again to replace it (within 5 minutes).");

            // Remove the home from the replaceHome map after 5 minutes
            new BukkitRunnable() {
                @Override
                public void run() {
                    replaceHome.remove(player);
                }
            }.runTaskLater(plugin, 20 * 60 * 5);

            return true;
        }

        // If the home is in the replaceHome map, remove it
        if (isHomeInReplaceHome) {
            assert home != null;
            homeManager.removeHome(player, home.getName());
            replaceHome.remove(player);
        }

        // Check if the player has reached their home limit
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

        if (homeManager.getHomes(player).size() >= maxHomes) {
            sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "Home limit reached. You can only set " + maxHomes + " homes.");
            return true;
        }

        // Set the home at the player's current location
        Location targetLocation = targetPlayer.getLocation();
        String worldName = targetLocation.getWorld().getName();
        float targetX = targetLocation.getBlockX();
        float targetY = targetLocation.getBlockY();
        float targetZ = targetLocation.getBlockZ();
        home = new Home(args[0], worldName, targetX, targetY, targetZ);
        homeManager.addHome(targetPlayer, home);

        sender.sendMessage(plugin.getPluginPrefix() + ChatColor.AQUA + "Home " + ChatColor.LIGHT_PURPLE + args[0] + ChatColor.AQUA + " set.");
        return true;
    }
}