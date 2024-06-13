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
        String pluginPrefix;
        if (plugin.getPluginConfig().getBoolean("show-prefix")) {
            pluginPrefix = plugin.getLangMessage("prefix") + ChatColor.RESET + " ";
        } else {
            pluginPrefix = "";
        }

        // Check if the sender is a player
        if (!(sender instanceof Player senderPlayer)) {
            String playerOnlyMessage = plugin.getLangMessage("commands.player-only");
            sender.sendMessage(pluginPrefix + playerOnlyMessage);
            return true;
        }

        // Check if not enough arguments are provided
        if (args.length == 0 || args.length > 2) {
            String usageMessage = plugin.getLangMessage("commands.home.usage");
            sender.sendMessage(pluginPrefix + usageMessage);
            return true;
        }

        Player targetPlayer;

        // Check if the command is being used to teleport to another player's home
        if (args.length == 2) {
            if (!sender.hasPermission("enhancedhomes.home.other") && !sender.getName().equalsIgnoreCase(args[0])) {
                String noPermissionMessage = plugin.getLangMessage("commands.home.other-error");
                sender.sendMessage(pluginPrefix + noPermissionMessage);
                return true;
            }

            targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null) {
                String playerNotFoundMessage = plugin.getLangMessage("commands.player-not-found");
                playerNotFoundMessage = playerNotFoundMessage.replace("%player%", args[0]);
                sender.sendMessage(pluginPrefix + playerNotFoundMessage);
                return true;
            }
        } else {
            targetPlayer = senderPlayer;
        }

        // Check if the player is already teleporting to a home
        if (teleportTasks.containsKey(targetPlayer)) {
            String alreadyTeleportingMessage = plugin.getLangMessage("commands.home.already-teleporting");
            sender.sendMessage(pluginPrefix + alreadyTeleportingMessage);
            return true;
        }

        // Get the home from the HomeManager
        Home home = homeManager.getHome(targetPlayer, args[args.length - 1]);
        if (home == null) {
            String noHomeFoundMessage = plugin.getLangMessage("commands.home.home-not-found");
            noHomeFoundMessage = noHomeFoundMessage.replace("%home%", args[args.length - 1]);
            noHomeFoundMessage = noHomeFoundMessage.replace("%player%", targetPlayer.getName());
            sender.sendMessage(pluginPrefix + noHomeFoundMessage);
            return true;
        }

        // Check if the player is trying to teleport to a home in another world
        if (!targetPlayer.getWorld().getName().equals(home.getWorldName())) {
            if (!plugin.getConfig().getBoolean("cross-world-tp") && !sender.hasPermission("enhancedhomes.crossworldtp.bypass")) {
                String crossWorldErrorMessage = plugin.getLangMessage("commands.home.cross-world-error");
                sender.sendMessage(pluginPrefix + crossWorldErrorMessage);
                return true;
            }
        }

        // Check si le monde existe sur le serveur
        if (Bukkit.getWorld(home.getWorldName()) == null) {
            String worldNotFoundMessage = plugin.getLangMessage("commands.home.world-not-found");
            worldNotFoundMessage = worldNotFoundMessage.replace("%world%", home.getWorldName());
            sender.sendMessage(pluginPrefix + worldNotFoundMessage);
            return true;
        }

        // Get the location of the home
        Location homeLocation = new Location(Bukkit.getWorld(home.getWorldName()), home.getX(), home.getY(), home.getZ());

        // Check if a warmup is required before teleporting
        if (plugin.getConfig().getBoolean("warmup") && !sender.hasPermission("enhancedhomes.warmup.bypass")) {
            int warmupTime = plugin.getConfig().getInt("warmup-time");
            String warmupMessage = plugin.getLangMessage("commands.home.warmup-message");
            warmupMessage = warmupMessage.replace("%time%", String.valueOf(warmupTime));
            warmupMessage = warmupMessage.replace("%home%", home.getName());
            warmupMessage = warmupMessage.replace("%player%", targetPlayer.getName());
            sender.sendMessage(pluginPrefix + warmupMessage);
            Location playerLocation = senderPlayer.getLocation();
            BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (playerLocation.distanceSquared(senderPlayer.getLocation()) < 1) {
                    targetPlayer.teleport(homeLocation);

                    // Play a sound effect when teleporting
                    targetPlayer.playSound(targetPlayer.getLocation(), "entity.enderman.teleport", 1.0f, 1.0f);

                    String teleportMessage = plugin.getLangMessage("commands.home.teleported-message");
                    teleportMessage = teleportMessage.replace("%home%", home.getName());
                    teleportMessage = teleportMessage.replace("%player%", targetPlayer.getName());
                    teleportMessage = teleportMessage.replace("%world%", home.getWorldName());
                    sender.sendMessage(pluginPrefix + teleportMessage);
                } else {
                    String movementCancelledMessage = plugin.getLangMessage("commands.home.movement-cancelled");
                    sender.sendMessage(pluginPrefix + movementCancelledMessage);
                }
                teleportTasks.remove(targetPlayer); // Remove the task from the HashMap once it's done
            }, warmupTime * 20L);
            teleportTasks.put(targetPlayer, task);
        } else {
            // Teleport the player immediately if no warmup is required
            targetPlayer.teleport(homeLocation);

            // Play a sound effect when teleporting
            targetPlayer.playSound(targetPlayer.getLocation(), "entity.enderman.teleport", 1.0f, 1.0f);

            // Send a message to the sender
            String teleportMessage = plugin.getLangMessage("commands.home.teleported-message");
            teleportMessage = teleportMessage.replace("%home%", home.getName());
            teleportMessage = teleportMessage.replace("%player%", targetPlayer.getName());
            teleportMessage = teleportMessage.replace("%world%", home.getWorldName());
            sender.sendMessage(pluginPrefix + teleportMessage);
        }

        return true;
    }
}