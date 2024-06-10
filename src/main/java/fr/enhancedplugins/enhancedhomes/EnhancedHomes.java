package fr.enhancedplugins.enhancedhomes;

import fr.enhancedplugins.enhancedhomes.utils.AnsiColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class EnhancedHomes extends JavaPlugin {
    private static final String PLUGIN_NAME = "EnhancedHomes";
    private static final String PLUGIN_ENABLED = AnsiColor.CYAN + PLUGIN_NAME + AnsiColor.GREEN + " enabled";
    private static final String PLUGIN_DISABLED = AnsiColor.CYAN + PLUGIN_NAME + AnsiColor.RED + " disabled";
    private FileConfiguration config;

    @Override
    public void onEnable() {
        this.config = this.getConfig();
        getLogger().info(PLUGIN_ENABLED);
    }

    @Override
    public void onDisable() { getLogger().info(PLUGIN_DISABLED); }

    public FileConfiguration getPluginConfig() { return this.config; }
}
