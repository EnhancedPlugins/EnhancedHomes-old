package com.enhancedplugins.enhancedhomes;

import com.enhancedplugins.enhancedhomes.commands.DelHomeCommand;
import com.enhancedplugins.enhancedhomes.commands.HomeCommand;
import com.enhancedplugins.enhancedhomes.commands.HomesCommand;
import com.enhancedplugins.enhancedhomes.commands.SetHomeCommand;
import com.enhancedplugins.enhancedhomes.commands.EnhancedHomesReloadCommand;
import com.enhancedplugins.enhancedhomes.managers.HomeManager;
import com.enhancedplugins.enhancedhomes.utils.AnsiColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
    private FileConfiguration langConfig;
    private HomeManager homeManager;
    private File langFile;

    /**
     * This method is called when the plugin is enabled.
     * It saves the default configuration, loads the configuration, initializes the home manager, creates the homes directory if it does not exist, and registers the /homes command.
     */
    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadLangConfig();

        this.config = this.getConfig();

        config.addDefault("show-prefix", true);
        config.addDefault("cross-world-tp", false);
        config.addDefault("warmup", true);
        config.addDefault("warmup-time", 3);
        config.addDefault("homes-limit", true);
        config.addDefault("max-homes", 5);
        config.options().copyDefaults(true);
        saveConfig();

        this.homeManager = new HomeManager(this);

        File homesDir = new File(getDataFolder(), "homes");
        if (!homesDir.exists()) {
            homesDir.mkdirs();
        }

        this.langFile = new File(getDataFolder(), "lang.yml");
        if (!langFile.exists()) {
            saveResource("lang.yml", false);
        }
        this.langConfig = YamlConfiguration.loadConfiguration(langFile);

        // Register commands
        Objects.requireNonNull(getCommand("homes")).setExecutor(new HomesCommand(this));
        Objects.requireNonNull(getCommand("home")).setExecutor(new HomeCommand(this));
        Objects.requireNonNull(getCommand("sethome")).setExecutor(new SetHomeCommand(this));
        Objects.requireNonNull(getCommand("delhome")).setExecutor(new DelHomeCommand(this));
        Objects.requireNonNull(getCommand("enhancedhomesreload")).setExecutor(new EnhancedHomesReloadCommand(this));

        getLogger().info(PLUGIN_ENABLED);
    }

    /**
     * This method is called when the plugin is disabled.
     * It logs that the plugin has been disabled.
     */
    @Override
    public void onDisable() { getLogger().info(PLUGIN_DISABLED); }

    /**
     * Retrieves the plugin configuration.
     *
     * @return The plugin configuration.
     */
    public FileConfiguration getPluginConfig() { return this.config; }

    /**
     * Reloads the plugin configuration.
     */
    public void reloadPluginConfig() {
        reloadConfig();
        this.config = getConfig();
        saveDefaultConfig();
    }

    /**
     * Retrieves the home manager.
     *
     * @return The home manager.
     */
    public HomeManager getHomeManager() { return this.homeManager; }

    /**
     * Loads the language configuration file.
     */
    public void loadLangConfig() {
        File langFile = new File(getDataFolder(), "lang.yml");
        if (!langFile.exists()) {
            saveResource("lang.yml", false);
        }

        this.langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    /**
     * Retrieves a language message from the language configuration file.
     * The message is retrieved based on the specified path.
     * The message is then translated to include color codes.
     *
     * @param path The path to the message in the language configuration file.
     * @return The translated message.
     */
    public String getLangMessage(String path) {
        String message = this.langConfig.getString(path);
        if (message == null) {
            return "Message not found for path: " + path;
        }
        String translatedMessage = ChatColor.translateAlternateColorCodes('&', message);
        return translatedMessage;
    }

    /**
     * Reloads the language configuration file.
     */
    public void reloadLangFile() {
        this.langConfig = YamlConfiguration.loadConfiguration(langFile);
        if (!this.langFile.exists()) {
            saveResource("lang.yml", false);
            this.langConfig = YamlConfiguration.loadConfiguration(langFile);
        }
    }
}