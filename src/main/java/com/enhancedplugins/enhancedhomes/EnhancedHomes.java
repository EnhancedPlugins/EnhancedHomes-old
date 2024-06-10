package com.enhancedplugins.enhancedhomes;

import com.enhancedplugins.enhancedhomes.commands.HomesCommand;
import com.enhancedplugins.enhancedhomes.managers.HomeManager;
import com.enhancedplugins.enhancedhomes.utils.AnsiColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.Objects;

/**
 * The EnhancedHomes class is the main class of the EnhancedHomes plugin.
 * It extends the JavaPlugin class, which is the base class for all Bukkit plugins.
 * It handles the enabling and disabling of the plugin, and manages the plugin's configuration and home manager.
 */
public class EnhancedHomes extends JavaPlugin {
    private static final String PLUGIN_NAME = "EnhancedHomes";
    private static final String PLUGIN_ENABLED = AnsiColor.CYAN + PLUGIN_NAME + AnsiColor.GREEN + " enabled";
    private static final String PLUGIN_DISABLED = AnsiColor.CYAN + PLUGIN_NAME + AnsiColor.RED + " disabled";
    private static final String PLUGIN_ERROR_PREFIX = ChatColor.RED + "[" + ChatColor.WHITE + PLUGIN_NAME + ChatColor.RED + "] " + ChatColor.RESET;
    private static final String PLUGIN_PREFIX = ChatColor.AQUA + "[" + ChatColor.WHITE + PLUGIN_NAME + ChatColor.AQUA + "] " + ChatColor.RESET;
    private FileConfiguration config;
    private HomeManager homeManager;

    /**
     * This method is called when the plugin is enabled.
     * It saves the default configuration, loads the configuration, initializes the home manager, creates the homes directory if it does not exist, and registers the /homes command.
     */
    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.config = this.getConfig();
        this.homeManager = new HomeManager(this);
        File homesDir = new File(getDataFolder(), "homes");
        if (!homesDir.exists()) {
            homesDir.mkdirs();
        }

        // Register commands
        Objects.requireNonNull(getCommand("homes")).setExecutor(new HomesCommand(this, homeManager));

        getLogger().info(PLUGIN_ENABLED);
    }

    /**
     * This method is called when the plugin is disabled.
     * It logs that the plugin has been disabled.
     */
    @Override
    public void onDisable() { getLogger().info(PLUGIN_DISABLED); }

    /**
     * Retrieves the plugin prefix.
     *
     * @return The plugin prefix.
     */
    public String getPluginPrefix() { return PLUGIN_PREFIX; }

    /**
     * Retrieves the plugin error prefix.
     *
     * @return The plugin error prefix.
     */
    public String getPluginErrorPrefix() { return PLUGIN_ERROR_PREFIX; }

    /**
     * Retrieves the plugin configuration.
     *
     * @return The plugin configuration.
     */
    public FileConfiguration getPluginConfig() { return this.config; }

    /**
     * Retrieves the home manager.
     *
     * @return The home manager.
     */
    public HomeManager getHomeManager() { return this.homeManager; }
}