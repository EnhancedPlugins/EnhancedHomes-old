package com.enhancedplugins.enhancedhomes.commands;

import com.enhancedplugins.enhancedhomes.EnhancedHomes;
import com.enhancedplugins.enhancedhomes.managers.HomeManager;
import com.enhancedplugins.enhancedhomes.models.Home;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

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

        if (args.length > 1) {
            String usageMessage = plugin.getLangMessage("commands.homes.usage");
            sender.sendMessage(pluginPrefix + usageMessage);
            return true;
        }

        // Determine the target player
        Player targetPlayer;
        if (args.length == 0) {
            targetPlayer = (Player) sender;
        } else {
            // Check if the sender has the necessary permissions
            if (!sender.getName().equalsIgnoreCase(args[0]) && !sender.hasPermission("enhancedhomes.homes.other")) {
                String noPermissionMessage = plugin.getLangMessage("commands.no-permission");
                sender.sendMessage(pluginPrefix + noPermissionMessage);
                return true;
            }
            targetPlayer = plugin.getServer().getPlayer(args[0]);
            if (targetPlayer == null) {
                String playerNotFoundMessage = plugin.getLangMessage("commands.player-not-found");
                String formattedMessage = playerNotFoundMessage.replace("%player%", args[0]);
                sender.sendMessage(pluginPrefix + formattedMessage);
                return true;
            }
        }

        // Retrieve the homes of the target player
        List<Home> homes = homeManager.getHomes(targetPlayer);
        if (homes.isEmpty()) {
            String noHomesFoundMessage = plugin.getLangMessage("commands.homes.homes-not-found");
            String formattedMessage = noHomesFoundMessage.replace("%player%", targetPlayer.getName());
            sender.sendMessage(pluginPrefix + formattedMessage);
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

        // Send a message to the sender with the list of homes
        String homesListTitle = plugin.getLangMessage("commands.homes.list-title");
        String formattedTitle;
        formattedTitle = homesListTitle.replace("%player%", targetPlayer.getName());
        formattedTitle = formattedTitle.replace("%current%", String.valueOf(homes.size()));
        formattedTitle = formattedTitle.replace("%max%", String.valueOf(maxHomes));
        sender.sendMessage(pluginPrefix + formattedTitle);

        homes.forEach(home -> {
            String worldName = home.getWorldName();
            String homeName = home.getName();
            String homesListItem = plugin.getLangMessage("commands.homes.list-item");
            String formattedItem = homesListItem.replace("%home%", homeName);
            formattedItem = formattedItem.replace("%world%", worldName);
            if (isCrossWorldTpEnabled) {
                formattedItem = formattedItem.replace("%world-color%", plugin.getLangMessage("commands.homes.accessible-world"));
            } else {
                formattedItem = formattedItem.replace("%world-color%", worldName.equalsIgnoreCase(((Player) sender).getWorld().getName()) ? plugin.getLangMessage("commands.homes.accessible-world") : plugin.getLangMessage("commands.homes.inaccessible-world"));
            }
            sender.sendMessage(formattedItem);
        });

        return true;
    }
}