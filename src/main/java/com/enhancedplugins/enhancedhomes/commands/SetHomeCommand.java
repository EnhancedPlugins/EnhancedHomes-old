package com.enhancedplugins.enhancedhomes.commands;

import com.enhancedplugins.enhancedhomes.EnhancedHomes;
import com.enhancedplugins.enhancedhomes.managers.HomeManager;
import com.enhancedplugins.enhancedhomes.models.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
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
        String pluginPrefix;
        if (plugin.getPluginConfig().getBoolean("show-prefix")) {
            pluginPrefix = plugin.getLangMessage("prefix") + ChatColor.RESET + " ";
        } else {
            pluginPrefix = "";
        }

        // Check if the sender is a player
        if (!(sender instanceof Player player)) {
            String playerOnlyMessage = plugin.getLangMessage("commands.player-only");
            sender.sendMessage(pluginPrefix + playerOnlyMessage);
            return true;
        }

        // Check if the correct number of arguments was given
        if (args.length != 1) {
            String usageMessage = plugin.getLangMessage("commands.sethome.usage");
            sender.sendMessage(pluginPrefix + usageMessage);
            return true;
        }

        Player targetPlayer = player;
        Home home = homeManager.getHome(player, args[0]);
        boolean isHomeInReplaceHome = replaceHome.containsKey(player) && replaceHome.get(player).equals(args[0]);

        // Check if the home already exists
        if (home != null && !isHomeInReplaceHome) {
            replaceHome.put(player, args[0]);
            String homeExistsMessage = plugin.getLangMessage("commands.sethome.home-exists");
            homeExistsMessage = homeExistsMessage.replace("%home%", args[0]);
            sender.sendMessage(pluginPrefix + homeExistsMessage);

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
            List<String> sortedPermissions = sender.getEffectivePermissions().stream()
                    .map(PermissionAttachmentInfo::getPermission)
                    .filter(perm -> perm.startsWith("enhancedhomes.sethome.max"))
                    .sorted()
                    .toList();

            maxHomes = sortedPermissions.stream()
                    .map(perm -> Integer.parseInt(perm.replace("enhancedhomes.sethome.max.", "")))
                    .max(Integer::compareTo)
                    .orElse(maxHomes);
        }

        if (homeManager.getHomes(player).size() >= maxHomes) {
            String homeLimitReachedMessage = plugin.getLangMessage("commands.sethome.home-limit-reached");
            homeLimitReachedMessage = homeLimitReachedMessage.replace("%home%", args[0]);
            homeLimitReachedMessage = homeLimitReachedMessage.replace("%homes%", String.valueOf(homeManager.getHomes(player).size()));
            homeLimitReachedMessage = homeLimitReachedMessage.replace("%current%", String.valueOf(maxHomes));
            homeLimitReachedMessage = homeLimitReachedMessage.replace("%max%", String.valueOf(maxHomes));
            sender.sendMessage(pluginPrefix + homeLimitReachedMessage);
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

        String homeSetMessage = plugin.getLangMessage("commands.sethome.home-set");
        homeSetMessage = homeSetMessage.replace("%home%", args[0]);
        sender.sendMessage(pluginPrefix + homeSetMessage);
        return true;
    }
}