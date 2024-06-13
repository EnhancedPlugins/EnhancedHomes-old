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
        if (!sender.hasPermission("enhancedhomes.enhancedhomesreload")) {
            sender.sendMessage(plugin.getPluginErrorPrefix() + ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        plugin.reloadPluginConfig();
        plugin.reloadLangFile();
        plugin.getHomeManager().reloadHomes();

        sender.sendMessage(plugin.getPluginPrefix() + ChatColor.GREEN + "Configuration, language file and homes reloaded successfully.");

        return true;
    }
}