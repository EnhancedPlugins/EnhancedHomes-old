package com.enhancedplugins.enhancedhomes.commands;

import com.enhancedplugins.enhancedhomes.EnhancedHomes;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EnhancedHomesReloadCommand implements CommandExecutor {
    private final EnhancedHomes plugin;

    public EnhancedHomesReloadCommand(EnhancedHomes plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String pluginPrefix;
        if (plugin.getPluginConfig().getBoolean("show-prefix")) {
            pluginPrefix = plugin.getLangMessage("prefix") + ChatColor.RESET + " ";
        } else {
            pluginPrefix = "";
        }

        if (!sender.hasPermission("enhancedhomes.enhancedhomesreload")) {
            String noPermissionMessage = plugin.getLangMessage("commands.no-permission");
            sender.sendMessage(pluginPrefix + noPermissionMessage);
            return true;
        }

        plugin.reloadPluginConfig();
        plugin.reloadLangFile();
        plugin.getHomeManager().reloadHomes();

        String reloadMessage = plugin.getLangMessage("commands.reload");
        sender.sendMessage(pluginPrefix + reloadMessage);

        return true;
    }
}